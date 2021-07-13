package apex.gl;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class VAOModelAttribute {

    private final int index;
    private final int data;

    public VAOModelAttribute(int index, int size, int stride) {
        this.index = index;
        this.data = size << 16 | stride * Float.BYTES;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return data >> 16;
    }

    public int getStride() {
        return data & 0xFFFF;
    }

    public static VAOModelAttribute create(int index, int size, int stride) {
        return new VAOModelAttribute(index, size, stride);
    }

}
