import java.util.ArrayList;
import java.util.List;

public class Inode {
    private int flags;
    private int fileSize;
    private int numberOfExtents;
    List<Extent> pointers;

    // region Constructor
    public Inode() {
        numberOfExtents = 0;
        pointers = new ArrayList<>();
    }

    public Inode(int flags, int fileSize, int numberOfExtents, List<Extent> pointers) {
        this.flags = flags;
        this.fileSize = fileSize;
        this.numberOfExtents = numberOfExtents;
        this.pointers = pointers;
    }

    // endregion


    // region Getters and Setters

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getNumberOfExtents() {
        return numberOfExtents;
    }

    public void setNumberOfExtents(int numberOfExtents) {
        this.numberOfExtents = numberOfExtents;
    }

    public List<Extent> getPointers() {
        return pointers;
    }

    public void setPointers(List<Extent> pointers) {
        this.pointers = pointers;
    }

    // endregion


    public void addPointer(Extent extent) {
        pointers.add(extent);
        numberOfExtents++;
    }

    @Override
    public String toString() {
        return "Inode{" +
                "flags=" + flags +
//                ", fileSize=" + fileSize +
                "numberOfExtents=" + numberOfExtents +
                ", pointer=" + pointers +
                '}';
    }
}
