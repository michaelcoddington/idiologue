package org.idiologue.server.repository.impl;

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
        COLLECTING_BODY,
        SEEKING_SUBSEQUENT_BOUNDARY,
        END_MULTIPART
    }

    private State currentState = State.SEEKING_INITIAL_BOUNDARY;

    private byte[] initialBoundaryBytes;

    private byte[] subsequentBoundaryBytes;

    private static final byte[] MULTIPART_LINE_SEPARATOR_BYTES = new byte[] { 13, 10, 13, 10 };

    private static final byte[] MULTIPART_END_BYTES = new byte[] { 45, 45, 13, 10 };

    private ArrayBuffer buffer = new ArrayBuffer(64000);

    private MultipartParserListener listener;

    private Pattern headerPattern = Pattern.compile("([^:]+): (.+)");

    private static final Logger LOG = LogManager.getLogger(MultipartParser.class);

    public MultipartParser(String boundary, MultipartParserListener listener) {
        LOG.info("Created multipart parser with boundary {}", boundary);
        this.initialBoundaryBytes = ("--" + boundary + "\r\n").getBytes(UTF_8);
        this.subsequentBoundaryBytes = ("\r\n--" + boundary).getBytes(UTF_8); // improve this to not include \r\n this way
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
                    int skipCount = completeMatch.endPosition;

                    // remove the boundary from the buffer
                    buffer.skip(skipCount);
                    System.out.println("Dropped boundary");


                    this.currentState = State.GATHERING_PART_HEADERS;
                    continuationDesired = true;

                } else {
                    throw new RuntimeException("Malformed boundary! " + completeMatch);
                }
            }
        } else if (currentState.equals(State.GATHERING_PART_HEADERS)) {
            ArrayBuffer.MatchResult result = buffer.match(0, MULTIPART_LINE_SEPARATOR_BYTES);
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
            } else {
                LOG.warn("Got match result {} while gathering part headers", result);
            }
        } else if (currentState.equals(State.COLLECTING_BODY)) {
            LOG.debug("Collecting body");
            ArrayBuffer.MatchResult result = buffer.match(0, subsequentBoundaryBytes);
            if (result instanceof ArrayBuffer.NoMatchResult noMatch) {
                System.out.println("No match :( " + buffer.available());
                byte[] bodyBytes = new byte[buffer.available()];
                buffer.read(bodyBytes);
                listener.bodyBytesRead(bodyBytes);
            } else if (result instanceof ArrayBuffer.PartialMatchResult partialMatch) {
                System.out.println("Partial match: " + result);
                if (partialMatch.startPosition > 0) {
                    byte[] bodyBytes = new byte[partialMatch.startPosition];
                    buffer.read(bodyBytes);
                    listener.bodyBytesRead(bodyBytes);
                }
            } else if (result instanceof ArrayBuffer.CompleteMatchResult completeMatch) {
                System.out.println("Complete body match " + completeMatch);
                byte[] bodyBytes = new byte[completeMatch.startPosition];
                buffer.read(bodyBytes);
                listener.bodyBytesRead(bodyBytes);
                listener.partEnded();
                this.currentState = State.SEEKING_SUBSEQUENT_BOUNDARY;
                continuationDesired = true;
            }
        } else if (currentState.equals(State.SEEKING_SUBSEQUENT_BOUNDARY)) {
            ArrayBuffer.MatchResult result = buffer.match(0, subsequentBoundaryBytes);
            if (result instanceof ArrayBuffer.CompleteMatchResult completeMatch) {
                // move the read pointer to one character past the boundary
                int skipCount = completeMatch.endPosition + 1;

                // remove the boundary from the buffer
                buffer.skip(skipCount);
                System.out.println("Dropped subsequent boundary");

                // this may be the ending boundary, so check that...
                ArrayBuffer.MatchResult endResult = buffer.match(0, new byte[] { 13, 10 });

                if (endResult instanceof ArrayBuffer.CompleteMatchResult completeResult) {
                    if (completeResult.startPosition == 0) { // if the boundary is followed by CRLF, there's more content
                        this.currentState = State.GATHERING_PART_HEADERS;
                        continuationDesired = true;
                    } else if (completeResult.startPosition == 2) { // if boundary is followed by --CRLF, that's the end
                        this.currentState = State.END_MULTIPART;
                        this.listener.parseEnded();
                    }
                }
            }
        }

        if (continuationDesired) {
            processBuffer();
        }
    }

}
