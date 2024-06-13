public class ArrayBuffer {

    private byte[] array;

    private int availableCount = 0;

    public static void main(String[] args) {
        ArrayBuffer buf = new ArrayBuffer(20);
        buf.write(new byte[] { 1, 2, 3, 4, 5});

        byte b1 = buf.peek(0);
        byte b2 = buf.peek(1);
        byte b3 = buf.peek(2);

        byte[] r = new byte[3];
        buf.read(r);
        System.out.println("");
    }

    public ArrayBuffer(int bufferSize) {
        this.array = new byte[bufferSize];
    }

    public void write(byte[] bytes) {
        if (availableCount + bytes.length > array.length) {
            throw new IllegalArgumentException("Cannot write " + bytes.length + "bytes into buffer with remaining capacity " + (array.length - availableCount));
        } else {
            System.arraycopy(bytes, 0, array, availableCount, bytes.length);
            availableCount += bytes.length;
        }
    }

    public int available() {
        return availableCount;
    }

    public void read(byte[] target) {
        // read the next available bytes into the target
        int byteReadCount = Math.min(availableCount, target.length);
        System.arraycopy(array, 0, target, 0, byteReadCount);
        // now shift the remaining elements to the beginning of the buffer and zero the rest
        int byteShiftCount = availableCount - byteReadCount;
        System.arraycopy(array, byteReadCount, array, 0, byteShiftCount);
        availableCount = byteShiftCount;
    }

    public byte peek(int index) {
        if (index >= availableCount) {
            throw new IllegalArgumentException("Index " + index + " is beyond available index " + (availableCount - 1));
        } else {
            return array[index];
        }
    }

}
