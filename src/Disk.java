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
    public static void read(int blocknum, SuperBlock block) {
        try {
            seek(blocknum);
            block.setStartOfFree(disk.readInt());
            block.setStartOfINode(disk.readInt());
            block.setStartOfRoot(disk.readInt());
            block.setNumberOfInodes(disk.readInt());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }
        readCount++;
    }

    /**
     * Read the InodeBlock
     * @param blockNumber   - start of read
     * @param block         - InodeBlock to read the data into
     */
    public static void read(int blockNumber, InodeBlock block) {
        // TODO: 22.11.2019. this does not work 
        try {
//            seek(blockNumber);
//            block.setSize(disk.readInt());
//            for (int i = 0; i < block.getInodeList().size(); i++) {
//                block.getInodeList().get(i).setNumberOfExtents(disk.readInt());
//                for (int j = 0; j < block.getInodeList().get(i).getNumberOfExtents(); j++) {
//                    block.getInodeList().get(i).getPointers().set(j, new Extent(disk.readInt(), disk.readShort()));
//                }
//            }
            System.out.println("Inode block: " + blockNumber);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                Block b = new Block();
                int sum = 0;
                for (int i = 50; i < 400_000; i++) {
                    read(i, b);
                    if (b.getNext() == i + 1) {
                        break;
                    }
                    sum+=5;
                    System.out.println(sum);
                }
                byte[] bytes = new byte[sum];
                disk.seek(50*5);
                System.out.println("FP: " + disk.getFilePointer());
                disk.read(bytes);
                InodeBlock inb = InodeBlock.convertFromBytes(bytes);
                System.out.println(bytes.length);
                System.out.println("TEST: " + inb);
            }

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
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
        for (int i = 0; i < 4_000_000; i++) {
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
        write(0, superBlock);

        // write the inode for the root dir
        // create root directory
        Inode rootINode = new Inode();
        Directory rootDir = new Directory("root");
        rootDir.addFile("root", 0);
        InodeBlock inodeBlock = new InodeBlock();
        InodeBlock.addNodeToList(rootINode);
        // write root directory and inode block to disc
        try {
            rootINode.bytesToExtents(rootDir.convertToBytes(), superBlock.getStartOfFree());
            rootINode.writeExtents(rootDir.convertToBytes());
            write(superBlock.getStartOfINode(), inodeBlock.convertToBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: 22.11.2019. consider boolean
    public static void boot(SuperBlock superBlock, InodeBlock inodeBlock) {
//        superBlock = new SuperBlock();
        read(0, superBlock);
//        inodeBlock = new InodeBlock();
        read(superBlock.getStartOfINode(), inodeBlock);
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
        SuperBlock superBlock = new SuperBlock();
        InodeBlock inodeBlock = new InodeBlock();
        Disk.boot(superBlock, inodeBlock);
        System.out.println(superBlock);
        System.out.println(inodeBlock);
        Block b = new Block();
        for (int i = 0; i < 350; i++) {
            Disk.read(i, b);
            System.out.println(i + " -> " + b);
        }
    }
}
