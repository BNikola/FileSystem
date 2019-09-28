import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Disk {
    // the size in bytes of each disk block
    public static final int BLOCK_SIZE = 5;
    // size of the disc in bytes
    public static final int SIZE = 20_000_000;
    // the number of disk blocks in the system
    public static final int NUMBER_OF_BLOCKS = 4_000_000;

    // number of reads and writes to the file system
    private static int readCount = 0;
    private static int writeCount = 0;


    // file that represents the file system
    private File fileName;
    private static RandomAccessFile disk;

    public static Logger LOGGER = Logger.getLogger("Logger");

    static {
        try {
            FileHandler fileHandler = new FileHandler("error.log");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Disk() {
        try {
            fileName = new File("DISK");
            disk = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    private static void seek(int blockNumber) throws IOException {
        if (blockNumber < 0 || blockNumber > NUMBER_OF_BLOCKS) {
            throw new RuntimeException("Block number: " + blockNumber + " is out of range");
        }
        disk.seek((long) (blockNumber * BLOCK_SIZE));
    }

    // region Read methods

    public static void read(int blockNumber, byte [] outputBuffer) {
//        if (outputBuffer.length != BLOCK_SIZE) {
//            throw new RuntimeException("Read: bad buffer length: " + outputBuffer.length);
//        }
        try {
            seek(blockNumber);
            disk.read(outputBuffer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        readCount++;
    }

    public static void read(int blocknum, SuperBlock block) {
        try {
            seek(blocknum);
            block.startOfINode = disk.readInt();
            block.startOfFree = disk.readInt();
            block.numberOfInodes = disk.readInt();
        }
        catch (EOFException e) {
            if (blocknum != 0) {
                System.err.println(e);
                System.exit(1);
            }
            block.startOfINode = block.startOfFree = 0;
        }
        catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        readCount++;
    }

    public static void read(int blockNumber, InodeBlock block) {
        try {
            seek(blockNumber);
            block.setSize(disk.readInt());
            for (int i = 0; i < block.getInodeList().size(); i++) {
                block.getInodeList().get(i).setNumberOfExtents(disk.readInt());
                for (int j = 0; j < block.getInodeList().get(i).getNumberOfExtents(); j++) {
                    block.getInodeList().get(i).pointers.set(j, new Extent(disk.readInt(), disk.readShort()));
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    public static void read(int blockNumber, Block block) {
        try {
            seek(blockNumber);
            block.setNext(disk.readInt());
            block.setFree(disk.readBoolean());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    // endregion


    // region Write methods

    public static void write(int blocknum, SuperBlock block) {
        try {
            seek(blocknum);
            disk.writeInt(block.startOfINode);
            disk.writeInt(block.startOfFree);
            disk.writeInt(block.numberOfInodes);
        }
        catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        writeCount++;
    }

    public static void write(int blockNumber, InodeBlock block) {
        try {
            seek(blockNumber);
            disk.writeInt(block.getSize());
            for (int i = 0; i < block.getInodeList().size(); i++) {
                disk.writeInt(block.getInodeList().get(i).getNumberOfExtents());
                for (int j = 0; j < block.getInodeList().get(i).getNumberOfExtents(); j++) {
                    disk.writeInt(block.getInodeList().get(i).pointers.get(j).getStartIndex());
                    disk.writeShort(block.getInodeList().get(i).pointers.get(j).getSize());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    public static void write(int blockNumber, byte [] block) {
        try {
            seek(blockNumber);
            disk.write(block);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    // endregion

    // TODO: 24.9.2019. Add read of the iNode - and add iNode
    //    // TODO: 24.9.2019. Add write methods

    public static void main(String[] args) {
        Disk d = new Disk();
    }
}
