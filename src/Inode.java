import java.util.LinkedList;
import java.util.List;

public class Inode {
    private int flags;
    private int fileSize;
    // TODO: 8.11.2019. add size and time modified
    private LinkedList<Extent> pointers;

    // region Constructor
    public Inode() {
        pointers = new LinkedList<>();
    }

    public Inode(int flags, int fileSize, LinkedList<Extent> pointers) {
        this.flags = flags;
        this.fileSize = fileSize;
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
        return pointers.size();
    }

    public void setNumberOfExtents(int numberOfExtents) {
    }

    public LinkedList<Extent> getPointers() {
        return pointers;
    }

    public void setPointers(LinkedList<Extent> pointers) {
        this.pointers = pointers;
    }

    // endregion


    public void addPointer(Extent extent) {
        pointers.add(extent);
    }

    @Override
    public String toString() {
        return "Inode{" +
                "flags=" + flags +
//                ", fileSize=" + fileSize +
                "numberOfExtents=" + pointers.size() +
                ", pointer=" + pointers +
                '}';
    }
}
