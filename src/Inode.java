import java.util.LinkedList;

public class Inode {
    private int flags;
    private int fileSize;
    private LinkedList<Extent> pointers;
    private long timestamp;


    // region Constructor
    public Inode() {
        pointers = new LinkedList<>();
        timestamp = System.currentTimeMillis();
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

    public long getTimestamp() {
        return timestamp;
    }

    // TODO: 8.11.2019. consider removing this
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // endregion


    public void createRootDir() {

    }

    public void addPointer(Extent extent) {
        pointers.add(extent);
    }

    // returns size in bytes
    public double size() {
        double size = 0;
        for (Extent e : pointers) {
            size += e.getSize();
        }
        return size / 5;
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
