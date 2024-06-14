package org.idiologue.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;


public class MultipartParser {

    private enum State {
        SEEKING_INITIAL_BOUNDARY,
        GATHERING_PART_HEADERS,
        COLLECTING_BODY
    }

    private State currentState = State.SEEKING_INITIAL_BOUNDARY;

    private byte[] initialBoundaryBytes;

    private byte[] subsequentBoundaryBytes;

    private static final byte[] DOUBLE_NEWLINE_BYTES = new byte[] { 10, 10 };

    private ArrayBuffer buffer = new ArrayBuffer(64000);

    private MultipartParserListener listener;

    private Pattern headerPattern = Pattern.compile("([^:]+): (.+)");

    private static final Logger LOG = LogManager.getLogger(MultipartParser.class);

    public MultipartParser(String boundary, MultipartParserListener listener) {
        LOG.info("Created multipart parser with boundary {}", boundary);
        this.initialBoundaryBytes = ("--" + boundary).getBytes(UTF_8);
        this.subsequentBoundaryBytes = ("\n--" + boundary).getBytes(UTF_8);
        this.listener = listener;
    }

    Mono<Void> parse(Flux<DataBuffer> bufferFlux) {
        return bufferFlux.mapNotNull(dataBuffer -> {
            try {
                process(dataBuffer);
            } catch (Exception er) {
                er.printStackTrace();
            }
            return null;
        }).then();
    }

    private void process(DataBuffer dataBuffer) {
        int byteCount = dataBuffer.readableByteCount();
        System.out.println("Data buffer is adding " + byteCount + " bytes");
        buffer.write(dataBuffer);
        DataBufferUtils.release(dataBuffer);
        processBuffer();
    }

    private void processBuffer() {
        boolean continuationDesired = false;
        LOG.info("Processing with current state {}", currentState);

        if (currentState.equals(State.SEEKING_INITIAL_BOUNDARY)) {
            ArrayBuffer.MatchResult result = buffer.match(0, initialBoundaryBytes);

            if (result instanceof ArrayBuffer.NoMatchResult noMatch) {
                System.out.println("No match :(");
            } else if (result instanceof ArrayBuffer.PartialMatchResult partialMatch) {
                System.out.println("Partial match: " + result);
            } else if (result instanceof ArrayBuffer.CompleteMatchResult completeMatch) {
                System.out.println("Complete boundary match: " + result);
                if (completeMatch.startPosition == 0) {
                    // end position is the position of the last boundary character, so we add 1 to position past the
                    // boundary and then 1 more to position past the newline that follows
                    int skipCount = completeMatch.endPosition + 2;

                    // remove the boundary from the buffer
                    buffer.skip(skipCount);

                    this.currentState = State.GATHERING_PART_HEADERS;
                    System.out.println("Dropped boundary");
                    continuationDesired = true;
                } else {
                    throw new RuntimeException("Malformed boundary! " + completeMatch);
                }
            }
        } else if (currentState.equals(State.GATHERING_PART_HEADERS)) {
            ArrayBuffer.MatchResult result = buffer.match(0, DOUBLE_NEWLINE_BYTES);
            if (result instanceof ArrayBuffer.CompleteMatchResult completeMatch) {
                System.out.println("Complete header match " + completeMatch);

                int count = completeMatch.endPosition + 1;
                byte[] headerBytes = new byte[count];
                buffer.read(headerBytes);
                String headerString = new String(headerBytes).trim();
                List<String> lines = Arrays.stream(headerString.split("\n")).toList();
                HttpHeaders headers = new HttpHeaders();
                lines.forEach(line -> {
                    Matcher m = headerPattern.matcher(line);
                    if (m.matches()) {
                        String headerName = m.group(1);
                        String headerValue = m.group(2);
                        headers.set(headerName, headerValue);
                    }
                });
                listener.partStarted(headers);
                this.currentState = State.COLLECTING_BODY;
                continuationDesired = true;
            }
        } else if (currentState.equals(State.COLLECTING_BODY)) {
            ArrayBuffer.MatchResult result = buffer.match(0, subsequentBoundaryBytes);
            if (result instanceof ArrayBuffer.NoMatchResult noMatch) {
                System.out.println("No match :( " + buffer.available());
                byte[] bodyBytes = new byte[buffer.available()];
                buffer.read(bodyBytes);
                listener.bodyBytesRead(bodyBytes);
            } else if (result instanceof ArrayBuffer.PartialMatchResult partialMatch) {
                System.out.println("Partial match: " + result);
            } else if (result instanceof ArrayBuffer.CompleteMatchResult completeMatch) {
                System.out.println("Complete body match " + completeMatch);
                byte[] bodyBytes = new byte[completeMatch.startPosition];
                buffer.read(bodyBytes);
                listener.bodyBytesRead(bodyBytes);
                buffer.skip(1);
                listener.partEnded();
                this.currentState = State.SEEKING_INITIAL_BOUNDARY;
                continuationDesired = true;
            }
        }
        if (continuationDesired) {
            processBuffer();
        }
    }

}
