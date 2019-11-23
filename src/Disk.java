import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: 19.11.2019. Rename class to DISC
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
     * @param blocknum  - start of read
     * @param block     - SuperBlock to put the read data
     */
    public static SuperBlock read(int blocknum) {
        SuperBlock block = new SuperBlock();
        try {
            seek(blocknum);
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
     * @param blockNumber   - start of read
     * @param block         - InodeBlock to read the data into
     */
    public static InodeBlock read(SuperBlock superBlock) {
        byte[] inodeBytes = new byte[superBlock.getEndOfInodeBlock()];
        InodeBlock block = null;
        try {
            disk.seek(5 * superBlock.getStartOfINode());
            disk.read(inodeBytes);
//            try {
//                // TODO: 23.11.2019. do a deep copy
//                block = (InodeBlock)((InodeBlock) InodeBlock.convertFromBytes(inodeBytes)).clone();
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
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
            // TODO: 22.11.2019. remove old code
//            seek(blockNumber);
//            disk.writeInt(block.getSize());
//            for (int i = 0; i < block.getInodeList().size(); i++) {
//                disk.writeInt(block.getInodeList().get(i).getNumberOfExtents());
//                for (int j = 0; j < block.getInodeList().get(i).getNumberOfExtents(); j++) {
//                    disk.writeInt(block.getInodeList().get(i).getPointers().get(j).getStartIndex());
//                    disk.writeShort(block.getInodeList().get(i).getPointers().get(j).getSize());
//                }
//            }
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
        for (int i = 0; i < 401_000; i++) {
            try {
                disk.writeInt(i+1);
                disk.writeBoolean(false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }

        // TODO: 19.11.2019. Maybe use serializeHeader method
        // write the super block
        SuperBlock superBlock = new SuperBlock();


        // write the inode for the root dir
        // create root directory
        Inode rootINode = new Inode();
        Directory rootDir = new Directory("root");
        rootDir.addFile("root", 0);
        InodeBlock inodeBlock = new InodeBlock();
        // write root directory and inode block to disc
        try {
            rootINode.bytesToExtents(rootDir.convertToBytes(), superBlock.getStartOfFree());
            rootINode.writeExtents(rootDir.convertToBytes());
            inodeBlock.addNodeToList(rootINode);
            byte [] inodeBlockBytes = inodeBlock.convertToBytes();
            superBlock.setEndOfInodeBlock(inodeBlockBytes.length);
            write(0, superBlock);
            write(superBlock.getStartOfINode(), inodeBlock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: 22.11.2019. consider boolean
    // TODO: 23.11.2019. Inode block not being read
    // TODO: 23.11.2019. check static members (they don't get serialized)
    public static void boot(SuperBlock superBlock, InodeBlock inodeBlock) {
//        superBlock = new SuperBlock();
//        read(0, superBlock);
//        inodeBlock = new InodeBlock();
//        read(superBlock, inodeBlock);
    }

    // TODO: 24.9.2019. Add read of the iNode - and add iNode
    //    // TODO: 24.9.2019. Add write methods

    public static void main(String[] args) {
        // region Old main
//        Disk d = new Disk();
////        d.formatDisc();
////
////        System.out.println("Citanje sa diska");
////        System.out.println("-------------------------");
////
////        Block b = new Block();
////        for (int i = 0; i < 100; i++) {
////            Disk.read(i, b);
////            System.out.println(b);
////
////        }
//
//
//        // TODO: 9.11.2019. change read and write methods using this, add serialization to everything
//        // TODO: 9.11.2019. check if the data after stays ok
//
        // endregion

        Disk d = new Disk();
//        d.formatDisc();
        SuperBlock superBlock = null;
        InodeBlock inodeBlock = null;
//        System.out.println("Nakon boot\n--------------");
//        Disk.boot(superBlock, inodeBlock);
        System.out.println(superBlock);
        System.out.println(inodeBlock);
        Block b = new Block();
//        for (int i = 0; i < 350; i++) {
//            Disk.read(i, b);
//            System.out.println(i + " -> " + b);
//        }
        superBlock = Disk.read(0);

        inodeBlock = Disk.read(superBlock);
//        byte[] inodeBytes = new byte[superBlock.getEndOfInodeBlock()];
//        try {
//            disk.seek(5 * superBlock.getStartOfINode());
//            System.out.println(disk.getFilePointer());
//            disk.read(inodeBytes);
//            System.out.println(disk.getFilePointer());
//            System.out.println(inodeBytes);
//            System.out.println(inodeBytes.length);
//            InodeBlock inodeBlock1 = InodeBlock.convertFromBytes(inodeBytes);
//            System.out.println(inodeBlock1 + " proslo je!");
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        System.out.println(superBlock);
        System.out.println(inodeBlock);
// TODO: 23.11.2019. remove clone and clean up code. Read of header done 
    }
}
