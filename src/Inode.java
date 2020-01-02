import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

public class Inode implements Serializable {
    private int flags;
    private int fileSize;
    private LinkedList<Extent> pointers;
    private long timestamp;
    private static final long serialVersionUID = 1L;


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

    public void bytesToExtents(byte[] data, SuperBlock superBlock) {
        int written = 0;
        int sizeOfData = data.length;
        int sizeOfDataInBlocks = sizeOfData/5 + 1;
        int extentStartIndex = superBlock.getStartOfFree();
        while (written < sizeOfDataInBlocks) {
            int extentStart = extentStartIndex;
            Block old = new Block();
            short extentSize = 0;
            for (int i = extentStart; written < sizeOfDataInBlocks;) {
                DISC.read(i, old);
                superBlock.setStartOfFree(old.getNext());
                if (i == old.getNext() - 1 && written < sizeOfDataInBlocks) {
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

    public void writeExtents(byte[] data) {
        int written = 0;
        for (Extent e : pointers) {
            // 5 is the size of a block
            int size = e.getSize() * 5;
            byte[] buffer = Arrays.copyOfRange(data, written, size + written);
            written += size;
            DISC.write(e.getStartIndex(), buffer);
        }
    }

    public byte[] readExents() {
        int read = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Extent e : pointers) {
            byte[] buffer = new byte[e.getSize() * 5];
            read += size();
            DISC.read(e.getStartIndex(), buffer);
            try {
                baos.write(buffer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return baos.toByteArray();
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
