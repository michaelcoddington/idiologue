import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;


public class MultipartParser {

    private enum State {
        SEEKING_INITIAL_BOUNDARY,
        GATHERING_PART_HEADERS,
        COLLECTING_BODY
    }

    private State currentState = State.SEEKING_INITIAL_BOUNDARY;

    private String boundary;

    private byte[] initialBoundaryBytes;

    private byte[] subsequentBoundaryBytes;

    private static final byte[] DOUBLE_NEWLINE_BYTES = new byte[] { 10, 10 };

    private ByteBuffer byteBuffer = ByteBuffer.allocate(4000);

    private int bufferSize = 0;

    private ByteBufferSequenceMatcher matcher = new ByteBufferSequenceMatcher();

    public MultipartParser(String boundary) {
        this.boundary = boundary;
        this.initialBoundaryBytes = boundary.getBytes(UTF_8);
        this.subsequentBoundaryBytes = ("\n" + boundary).getBytes(UTF_8);
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
        dataBuffer.toByteBuffer(0, byteBuffer, bufferSize, byteCount);
        bufferSize += byteCount;

        if (currentState.equals(State.SEEKING_INITIAL_BOUNDARY)) {
            ByteBufferSequenceMatcher.MatchResult result = matcher.match(byteBuffer, 0, bufferSize, initialBoundaryBytes);

            if (result instanceof ByteBufferSequenceMatcher.NoMatchResult noMatch) {
                System.out.println("No match :(");
            } else if (result instanceof ByteBufferSequenceMatcher.PartialMatchResult partialMatch) {
                System.out.println("Partial match: " + result);
            } else if (result instanceof ByteBufferSequenceMatcher.CompleteMatchResult completeMatch) {
                System.out.println("Complete boundary match: " + result);
                if (completeMatch.startPosition == 0) {
                    // end position is the position of the last boundary character, so we add 1 to position past the
                    // boundary and then 1 more to position past the newline that follows
                    int newStartPosition = completeMatch.endPosition - completeMatch.startPosition + 2;

                    // remove the boundary from the buffer
                    byteBuffer.position(newStartPosition);
                    byteBuffer.compact();
                    bufferSize -= newStartPosition;

                    this.currentState = State.GATHERING_PART_HEADERS;
                    System.out.println("Dropped boundary");
                } else {
                    throw new RuntimeException("Malformed boundary! " + completeMatch);
                }
            }
        } else if (currentState.equals(State.GATHERING_PART_HEADERS)) {
            ByteBufferSequenceMatcher.MatchResult result = matcher.match(byteBuffer, 0, bufferSize, DOUBLE_NEWLINE_BYTES);
            if (result instanceof ByteBufferSequenceMatcher.CompleteMatchResult completeMatch) {
                System.out.println("Complete header match " + completeMatch);
                byteBuffer.position(0);
                int headerEndPosition = completeMatch.endPosition + 1;
                byte[] headerBytes = new byte[headerEndPosition];
                byteBuffer.get(headerBytes, 0, headerEndPosition);
                byteBuffer.compact();
                bufferSize -= headerEndPosition;
                System.out.println(new String(headerBytes));
                this.currentState = State.COLLECTING_BODY;
            }
        } else if (currentState.equals(State.COLLECTING_BODY)) {
            ByteBufferSequenceMatcher.MatchResult result = matcher.match(byteBuffer, 0, bufferSize, subsequentBoundaryBytes);
            if (result instanceof ByteBufferSequenceMatcher.NoMatchResult noMatch) {
                System.out.println("No match :(");
            } else if (result instanceof ByteBufferSequenceMatcher.PartialMatchResult partialMatch) {
                System.out.println("Partial match: " + result);
            } else if (result instanceof ByteBufferSequenceMatcher.CompleteMatchResult completeMatch) {
                System.out.println("Complete body match " + completeMatch);
                byteBuffer.position(0);
                byte[] bodyBytes = new byte[completeMatch.startPosition];
                byteBuffer.get(bodyBytes, 0, bodyBytes.length);
                System.out.println(new String(bodyBytes));
                // at this point we've stopped just before the newline, so read that...
                byteBuffer.get();
                // and compact
                byteBuffer.compact();
                this.currentState = State.SEEKING_INITIAL_BOUNDARY;
            }
        }
    }

}
