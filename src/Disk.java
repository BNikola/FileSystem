import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Disk {
    // the size in bytes of each disk block
    public static final int BLOCK_SIZE = 5;
    // the number of disk blocks in the system
    public static final int NUMBER_OF_BLOCKS = 4_000_000;

    // number of reads and writes to the file system
    private int readCount = 0;
    private int writeCount = 0;


    // file that represents the file system
    private File fileName;
    private RandomAccessFile disk;

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

    private void seek(int blockNumber) throws IOException {
        if (blockNumber < 0 || blockNumber > NUMBER_OF_BLOCKS) {
            throw new RuntimeException("Block number: " + blockNumber + " is out of range");
        }
        disk.seek((long) (blockNumber * BLOCK_SIZE));
    }

    private void read(int blockNumber, byte [] outputBuffer) {
        if (outputBuffer.length != BLOCK_SIZE) {
            throw new RuntimeException("Read: bad buffer length: " + outputBuffer.length);
        }
        try {
            seek(blockNumber);
            disk.read(outputBuffer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        readCount++;
    }

    private void read(int blockNumber, SuperBlock block) {
        try {
            seek(blockNumber);
            block.size = disk.readInt();
            block.iSize = disk.readInt();
            block.freeList = disk.readInt();
        } catch (EOFException e) {
            if (blockNumber != 0) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            block.size = block.iSize = block.freeList = 0;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        readCount++;
    }

    // TODO: 24.9.2019. Add read of the iNode - and add iNode
    // TODO: 24.9.2019. Add write methods

    public static void main(String[] args) {
        Disk d = new Disk();
    }
}
