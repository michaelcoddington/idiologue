import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class ByteBufferSequenceMatcher {

    public static void main(String[] args) {

        String body = " who am your walr";
        String test = "walrus";

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        byte[] testBytes = test.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(2046);
        byteBuffer.put(bodyBytes);

        MatchResult match = new ByteBufferSequenceMatcher().match(byteBuffer, 0, bodyBytes.length, testBytes);
        System.out.println("Found match: " + match);

        String append = "us and whatever";
        byte[] appendBytes = append.getBytes(StandardCharsets.UTF_8);
        byteBuffer.put(appendBytes);

        MatchResult match2 = new ByteBufferSequenceMatcher().match(byteBuffer, 0, bodyBytes.length + appendBytes.length, testBytes);
        System.out.println("Found match: " + match2);
    }

    public MatchResult match(ByteBuffer buffer, int bufferStartPosition, int bufferReadLimit, byte[] testSequence) {
        byte start = testSequence[0];
        for (int i = bufferStartPosition; i < bufferReadLimit; i++) {
            if (buffer.get(i) == start) {
                //System.out.println("Match on sequence start, buffer index " + i);
                int matchCount = 1; // we already found the first byte
                boolean mismatch = false;
                int followPosition = 1;
                for (; followPosition< testSequence.length && !mismatch; followPosition++) {
                    int bufferPosition = i + followPosition;
                    if (bufferPosition < bufferReadLimit) {
                        if (buffer.get(bufferPosition) == testSequence[followPosition]) {
                            matchCount += 1;
                        } else {
                            mismatch = true;
                        }
                    } else {
                        return new PartialMatchResult(i);
                    }
                }
                if (matchCount == testSequence.length) {
                    return new CompleteMatchResult(i, i + matchCount - 1);
                }
            }
        }
        return new NoMatchResult();
    }

    class MatchResult { }
    class NoMatchResult extends MatchResult { }
    class CompleteMatchResult extends MatchResult {
        int startPosition;
        int endPosition;

        CompleteMatchResult(int startPos, int endPos) {
            this.startPosition = startPos;
            this.endPosition = endPos;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", CompleteMatchResult.class.getSimpleName() + "[", "]")
                    .add("startPosition=" + startPosition)
                    .add("endPosition=" + endPosition)
                    .toString();
        }
    }
    class PartialMatchResult extends MatchResult {
        int startPosition;

        public PartialMatchResult(int startPosition) {
            this.startPosition = startPosition;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", PartialMatchResult.class.getSimpleName() + "[", "]")
                    .add("startPosition=" + startPosition)
                    .toString();
        }
    }

}
