import java.io.*;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DISC {
    // the size in bytes of each disk block
    public static final int BLOCK_SIZE = 5;
    // size of the disc in bytes
    public static final int SIZE = 20_000_000;
    // the number of disk blocks in the system
    public static final int NUMBER_OF_BLOCKS = 4_000_000;

    // number of reads and writes to the file system
    private static int readCount = 0;
    private static int writeCount = 0;

    // super block
    public static SuperBlock superBlock = null;
    public static InodeBlock inodeBlock = null;


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

    public DISC() {
        try {
            fileName = new File("DISK");
            disk = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    public static RandomAccessFile getDisk() {
        return disk;
    }

    /**
     * Seek to a blocknumber
     * @param blockNumber   - block number to seek
     * @throws IOException  - if the block number exceeds disc capacity
     */
    private static void seek(int blockNumber) throws IOException {
        if (blockNumber < 0 || blockNumber > NUMBER_OF_BLOCKS) {
            throw new RuntimeException("Block number: " + blockNumber + " is out of range");
        }
        disk.seek((long) (blockNumber * BLOCK_SIZE));
    }

    // region Read methods

    /**
     * Read from blockNumber to outputBuffer. The number of read is the size of the buffer
     * @param blockNumber   - start of read
     * @param outputBuffer  - buffer to put the read data
     */
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

    /**
     * Read the SuperBlock
     */
    public static SuperBlock read() {
        SuperBlock block = new SuperBlock();
        try {
            seek(0);
            block.setStartOfFree(disk.readInt());
            block.setStartOfINode(disk.readInt());
            block.setStartOfRoot(disk.readInt());
            block.setNumberOfInodes(disk.readInt());
            block.setEndOfInodeBlock(disk.readInt());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }
        readCount++;
        return block;
    }

    /**
     * Read the InodeBlock
     * @param superBlock         - SuperBlock to read the data into
     */
    public static InodeBlock read(SuperBlock superBlock) {
        byte[] inodeBytes = new byte[superBlock.getEndOfInodeBlock()];
        InodeBlock block = null;
        try {
            disk.seek(5 * superBlock.getStartOfINode());
            disk.read(inodeBytes);
            block = (InodeBlock) InodeBlock.convertFromBytes(inodeBytes);
            System.out.println("TEST:: " + block);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return block;
    }

    /**
     * Read singe 5B block (next:int, free:boolean)
     * @param blockNumber   - start of read
     * @param block         - Block to read into
     */
    public static void read(int blockNumber, Block block) {
        try {
            seek(blockNumber);
            block.setNext(disk.readInt());
            block.setFree(disk.readBoolean());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Reading directory from a given inode
     * @param inode - the inode of a directory
     * @return - null if there was an error, else the wanted directroy
     */
    public Directory readDirectoryFromINode(Inode inode) {
        Directory readDir = null;
        try {
            byte [] directoryBytes = inode.readExents();
            readDir = Directory.convertFromBytes(directoryBytes);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return readDir;
    }

    // endregion


    // region Write methods

    /**
     * Write Superblock to disk
     * @param blocknum  - start of writing the superblock
     * @param block - the block we are writing
     */
    // maybe change this to use the same functionality as InodeBlock (with convertToBytes)
    public static void write(int blocknum, SuperBlock block) {
        try {
            seek(blocknum);
            disk.writeInt(block.getStartOfFree());
            disk.writeInt(block.getStartOfINode());
            disk.writeInt(block.getStartOfRoot());
            disk.writeInt(block.getNumberOfInodes());
            disk.writeInt(block.getEndOfInodeBlock());
        }
        catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        writeCount++;
    }

    /**
     * Write inode block
     * @param blockNumber   - start of writing the InodeBlock
     * @param block - block to write
     */
    public static void write(int blockNumber, InodeBlock block) {
        try {
            write(blockNumber, block.convertToBytes());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Write any bytes to disc
     * @param blockNumber   - start of writing
     * @param block - byte array to write
     */
    public static void write(int blockNumber, byte [] block) {
        try {
            seek(blockNumber);
            disk.write(block);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Write single block (5 bytes) to disc (next:int, free:boolean)
     * @param blockNumber   - start of writing
     * @param block - block to write
     */
    public static void write(int blockNumber, Block block) {
        try {
            seek(blockNumber);
            disk.writeInt(block.getNext());
            disk.writeBoolean(false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    // endregion


    /**
     * Formatting of the disc
     * - set all blocks with proper int:boolean values (next:free)
     * - write SuperBlock
     * - create rootDir and Inode
     */
    public void formatDisc() {
        // TODO: 23.11.2019. change 401_00 to 4_000_000
        for (int i = 0; i <= 406_500; i++) {
            try {
                disk.writeInt(i+1);
                disk.writeBoolean(false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
        // write the super block
        SuperBlock superBlock = new SuperBlock();


        // write the inode for the root dir
        // create root directory
        Inode rootINode = new Inode();
        Directory rootDir = new Directory("root");
        rootDir.addFile(0, "root");
        InodeBlock inodeBlock = new InodeBlock();
        // write root directory and inode block to disc
        try {
            rootINode.bytesToExtents(rootDir.convertToBytes(), superBlock);
            rootINode.writeExtents(rootDir.convertToBytes());
            inodeBlock.addNodeToList(rootINode);
            writeHeader(superBlock, inodeBlock);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    public void writeHeader(SuperBlock superBlock, InodeBlock inodeBlock) throws IOException {
        byte [] inodeBlockBytes = inodeBlock.convertToBytes();
        superBlock.setEndOfInodeBlock(inodeBlockBytes.length);
        write(0, superBlock);
        write(superBlock.getStartOfINode(), inodeBlock);
    }

    public static void boot() {
        superBlock = read();
        inodeBlock = read(superBlock);
        inodeBlock.index = inodeBlock.number;
    }
}
