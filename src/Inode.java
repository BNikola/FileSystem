import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class Inode implements Serializable {
    private int flags;
    private int fileSize;
    private ArrayList<Extent> pointers;
    private long timestamp;
    private static final long serialVersionUID = 1L;

    // region Constructor
    public Inode() {
        pointers = new ArrayList<>();
        timestamp = System.currentTimeMillis();
    }

    public Inode(int flags) {
        this.pointers = new ArrayList<>();
        this.flags = flags;
        timestamp = System.currentTimeMillis();
    }

    public Inode(int flags, int fileSize, ArrayList<Extent> pointers) {
        this.flags = flags;
        this.fileSize = fileSize;
        this.pointers = pointers;
        timestamp = System.currentTimeMillis();
    }

    public Inode(Inode inode) {
        this.flags = inode.flags;
        this.fileSize = inode.fileSize;
        this.timestamp = System.currentTimeMillis();
        this.pointers = new ArrayList<>();
    }

    // TODO: 5.1.2020. remove this afterwards
    public void showMeTheMoney() {
        int i = 0;
        for (i = 400000; i < 406000; i++) {
            Block b = new Block();
            DISC.read(i, b);
            System.out.println(i + " - " + b);
        }
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

    public ArrayList<Extent> getPointers() {
        return pointers;
    }

    public void setPointers(ArrayList<Extent> pointers) {
        this.pointers = pointers;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // endregion


    public void addPointer(Extent extent) {
        pointers.add(extent);
        Collections.sort(pointers, Comparator.comparing(Extent::getStartIndex));

    }

    // returns size in bytes
    public double size() {
        double size = 0;
        Collections.sort(pointers, Comparator.comparing(Extent::getStartIndex));
        for (Extent e : pointers) {
            size += e.getSize();
        }
        return size / 5;
    }

    public void bytesToExtents(byte[] data, SuperBlock superBlock) {
        int written = 0;
        int sizeOfData = data.length;
        int sizeOfDataInBlocks = (sizeOfData%5 == 0)? sizeOfData/5:sizeOfData/5 + 1;
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
                        Extent extent = new Extent(extentStart, extentSize);
                        this.addPointer(extent);
                    }
                } else {
                    written++;
                    extentSize++;
                    i = old.getNext();
                    Extent extent = new Extent(extentStart, extentSize);
                    this.addPointer(extent);
                    extentStart = i;
                    extentSize = 0;
                }
            }
        }
    }

    public void writeExtents(byte[] data) {
        int written = 0;
        Collections.sort(pointers, Comparator.comparing(Extent::getStartIndex));
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
        Collections.sort(pointers, Comparator.comparing(Extent::getStartIndex));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Extent e : pointers) {
            byte[] buffer = new byte[e.getSize() * 5];
            read += e.getSize();
            DISC.read(e.getStartIndex(), buffer);
            try {
                baos.write(buffer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    public void resetExtents() {
        Integer oldSOF = DISC.superBlock.getStartOfFree();
        for (int i = 0; i < pointers.size(); i++) {
            Extent extent = pointers.get(i);
            DISC.superBlock.setStartOfFree(extent.getStartIndex());

            int j = 0;
            for (j = extent.getStartIndex(); j <= extent.getStartIndex() + extent.getSize() - 1; j++) {
                try {
                    if (j == extent.getStartIndex() + extent.getSize() - 1) {
//                        showMeTheMoney(extent);
                        if (i == pointers.size() - 1) {
                            DISC.getDisk().seek(j*5);
                            DISC.getDisk().writeInt(oldSOF);
                            DISC.getDisk().writeBoolean(false);
//                            showMeTheMoney(extent);
//                            break;
                        } else {
                            DISC.getDisk().seek(j*5);
                            DISC.getDisk().writeInt(pointers.get(i+1).getStartIndex());
                            DISC.getDisk().writeBoolean(false);
                            break;
                        }
                    } else {
                        DISC.getDisk().seek(j*5);
                        DISC.getDisk().writeInt(j+1);
                        DISC.getDisk().writeBoolean(false);
                        Block b = new Block();
                        DISC.read(j, b);
                    }
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }
//            showMeTheMoney();

        }
        DISC.superBlock.setStartOfFree(pointers.get(0).getStartIndex());
    }

    public Inode append(byte[] newData) {
        byte [] oldData = readExents();
        this.resetExtents();
        Inode newInode = new Inode(this);
        newInode.bytesToExtents(newData, DISC.superBlock);
        newInode.writeExtents(newData);
        return newInode;
    }

    @Override
    public String toString() {
        return "Inode{" +
                "flags=" + flags +
//                ", fileSize=" + fileSize +
                "\nnumberOfExtents=" + pointers.size() +
                "\npointers=" + pointers +
                "\ntime=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(timestamp)) +
                '}';
    }
}
