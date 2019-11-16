import java.io.Serializable;
import java.util.LinkedList;

public class Inode implements Serializable {
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

    public void bytesToExtents(byte[] data, int startOfFree) {
        int written = 0;
        int sizeOfData = data.length;
        int sizeOfDataInBlocks = sizeOfData/5 + 1;
        int extentStartIndex = startOfFree;
        while (written < sizeOfDataInBlocks) {
            int extentStart = extentStartIndex;
            Block old = new Block();
            short extentSize = 0;
            for (int i = extentStart; written < sizeOfDataInBlocks;) {
                Disk.read(i, old);
                if (i == old.getNext() - 1 && written < sizeOfDataInBlocks) {
                    System.out.println("sljedeci je");
                    i++;
                    written++;
                    extentSize++;
                    if (written == sizeOfDataInBlocks) {
                        System.out.println("velicina dosegnuta");
                        Extent extent = new Extent(extentStart, extentSize);
                        this.addPointer(extent);
                        System.out.println(extent);
                    }
                } else {
                    written++;
                    extentSize++;
                    i = old.getNext();
                    System.out.println("nije sljedeci");
                    Extent extent = new Extent(extentStart, extentSize);
                    this.addPointer(extent);
                    extentStart = i;
                    extentSize = 0;
                    System.out.println(extent);
                }
            }
        }

    }

    @Override
    public String toString() {
        return "Inode{" +
                "flags=" + flags +
//                ", fileSize=" + fileSize +
                ", numberOfExtents=" + pointers.size() +
                ", pointer=" + pointers +
                '}';
    }
}
