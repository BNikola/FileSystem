import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
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

//    public static void read(int blocknum, SuperBlock block) {
//        try {
//            seek(blocknum);
//            block.startOfINode = disk.readInt();
//            block.startOfFree = disk.readInt();
//            block.numberOfInodes = disk.readInt();
//        }
//        catch (EOFException e) {
//            if (blocknum != 0) {
//                System.err.println(e);
//                System.exit(1);
//            }
//            block.startOfINode = block.startOfFree = 0;
//        }
//        catch (IOException e) {
//            System.err.println(e);
//            System.exit(1);
//        }
//        readCount++;
//    }

    public static void read(int blockNumber, InodeBlock block) {
        try {
            seek(blockNumber);
            block.setSize(disk.readInt());
            for (int i = 0; i < block.getInodeList().size(); i++) {
                block.getInodeList().get(i).setNumberOfExtents(disk.readInt());
                for (int j = 0; j < block.getInodeList().get(i).getNumberOfExtents(); j++) {
                    block.getInodeList().get(i).getPointers().set(j, new Extent(disk.readInt(), disk.readShort()));
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

    public Directory readDirectoryFromINode(Inode inode) {
        // TODO: 8.11.2019. implement this
        return new Directory("root");
    }

    // endregion


    // region Write methods

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

    public static void write(int blockNumber, InodeBlock block) {
        try {
            seek(blockNumber);
            disk.writeInt(block.getSize());
            for (int i = 0; i < block.getInodeList().size(); i++) {
                disk.writeInt(block.getInodeList().get(i).getNumberOfExtents());
                for (int j = 0; j < block.getInodeList().get(i).getNumberOfExtents(); j++) {
                    disk.writeInt(block.getInodeList().get(i).getPointers().get(j).getStartIndex());
                    disk.writeShort(block.getInodeList().get(i).getPointers().get(j).getSize());
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

    public void formatDisc() {
        for (int i = 0; i < 4_000_000; i++) {
            try {
                disk.writeInt(i+1);
                disk.writeBoolean(false);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }

        // write the super block
        SuperBlock superBlock = new SuperBlock();
        write(0, superBlock);

        // write the inode for the root dir
        // create root directory
        Inode rootINode = new Inode();
        Directory rootDir = new Directory("root");
        rootDir.addFile("root", 0);



    }

    // TODO: 24.9.2019. Add read of the iNode - and add iNode
    //    // TODO: 24.9.2019. Add write methods

    public static void main(String[] args) {
        Disk d = new Disk();
//        d.formatDisc();
//
//        System.out.println("Citanje sa diska");
//        System.out.println("-------------------------");
//
//        Block b = new Block();
//        for (int i = 0; i < 100; i++) {
//            Disk.read(i, b);
//            System.out.println(b);
//
//        }
        Inode rootINode1 = new Inode();
        Inode rootINode2 = new Inode();
        Directory rootDir = new Directory("root");
        rootDir.addFile("root", 0);
        System.out.println(rootDir);
        rootINode1.addPointer(new Extent(20, (short) 5));
        rootINode1.addPointer(new Extent(27, (short) 5));
        rootINode2.addPointer(new Extent(55, (short) 2));
        InodeBlock inb = new InodeBlock();
        InodeBlock.addNodeToList(rootINode1);
        InodeBlock.addNodeToList(rootINode2);
        System.out.println(inb);

        // TODO: 9.11.2019. change read and write methods using this, add serialization to everything
        // TODO: 9.11.2019. check if the data after stays ok
//        try {
//            byte[] bytes = inb.convertToBytes();
//            FileOutputStream out = new FileOutputStream("inode_block_test.txt");
////            out.write();
//            out.write(bytes);
//            out.write(bytes);
//            out.close();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        rootINode1.addPointer(new Extent(435, (short) 23));

//        try (RandomAccessFile raf = new RandomAccessFile("inod_block_test.txt", "rw")) {
//            raf.seek(0);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try (FileInputStream in = new FileInputStream("inode_block_test.txt")) {
            byte[] bytes = in.readAllBytes();
            System.out.println("prvo citanje");
            System.out.println("----------------------");
            InodeBlock inb2 = new InodeBlock();
            inb2.convertFromBytes(bytes);
            System.out.println(new String(bytes));

            System.out.println(inb2);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        try {

            FileOutputStream out = new FileOutputStream("inode_block_test.txt", true);
//            out.write();
            out.write("\novo je test\n".getBytes());
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream in = new FileInputStream("inode_block_test.txt")) {
            byte[] bytes = in.readAllBytes();
            System.out.println(new String(bytes));
            System.out.println("Drugo citanje");
            System.out.println("----------------------");
            InodeBlock inb2 = new InodeBlock();
            inb2.convertFromBytes(bytes);

            System.out.println(inb2);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }
}
