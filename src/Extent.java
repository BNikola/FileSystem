import java.io.Serializable;

public class Extent implements Serializable {
    // TODO: 27.9.2019. change modifiers
    private int startIndex;
    private short size;       // number of 5B blocks
    private static final long serialVersionUID = 1L;

    public Extent(int startIndex, short size) {
        this.startIndex = startIndex;
        this.size = size;
    }

    // region Getters and Setters

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public short getSize() {
        return size;
    }

    public void setSize(short size) {
        this.size = size;
    }

    // endregion

    @Override
    public String toString() {
        return "Extent{" +
                "startIndex=" + startIndex +
                ", size=" + size +
                '}';
    }
}
