import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;

public class FileSystem {

    public Directory currentDirectory;
    public Inode currentInode;
    public static DISC disc = new DISC();

    public FileSystem() {
        DISC.boot();
        try {
            currentDirectory = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(0).readExents());
        } catch (IOException | ClassNotFoundException e) {
            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        // add root inode read here
        // currentInode = read first inode
    }

    // region Methods

    public String pwd() {
        String result = "";
        if (currentDirectory.name.equals("root")) {
            result = "/root";
        } else {
            result = "/root/" + currentDirectory.name;
        }

        return result;
    }

    public void ls() {
        System.out.println(currentDirectory.name);
        System.out.println(currentDirectory);
        currentDirectory.listFileNames().forEach(System.out::println);
    }

    public boolean mkdir(String newDirName) {
        // TODO: 3.1.2020. write inode block and super block and add file name to current dir
        boolean result = false;
        if (currentDirectory.fileNames.containsKey(newDirName)) {
            Inode in = DISC.inodeBlock.getInodeList().get(currentDirectory.fileNames.get(newDirName));
            if (in.getFlags() == 0) {
                System.out.println("Ne moze to tako");
                result = false;
            } else {
                System.out.println("It exists!!\n");
                Directory directory = new Directory(newDirName);
                Inode newDirInode = new Inode();
                System.out.println(directory);
                try {
                    newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
                    newDirInode.writeExtents(directory.convertToBytes());
                    DISC.inodeBlock.addNodeToList(newDirInode);
                    currentDirectory.addFile(newDirName, DISC.inodeBlock.getInodeList().indexOf(newDirInode));
                    DISC.inodeBlock.getInodeList().remove(0);
//                    DISC.inodeBlock.getInodeList().add(0, currentDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(newDirInode);
            }
        } else {
            Directory directory = new Directory(newDirName);
            Inode newDirInode = new Inode();
            System.out.println(directory);
            try {
                newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
                newDirInode.writeExtents(directory.convertToBytes());
                DISC.inodeBlock.addNodeToList(newDirInode);
                currentDirectory.addFile(newDirName, DISC.inodeBlock.getInodeList().indexOf(newDirInode));

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("----------\nMKDIR\n----------");
            System.out.println(newDirInode);
            System.out.println(currentDirectory);
            try {
                disc.writeHeader(DISC.superBlock, DISC.inodeBlock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        return result;
    }

//    public int put(String fileName) {
//        // put file from file system to this file system
//        try {
//            byte [] fileBytes = Files.readAllBytes(Paths.get(fileName));
//            if (fileBytes.length > 64_000) {
//                System.out.println("Error! File too big");
//                return -1;
//            }
//            Inode inode = new Inode();
//            inode.setFileSize(fileBytes.length);
//
//            System.out.println(new String(fileBytes));
//            System.out.println(fileBytes.length);
//
//            int startOfFree = SuperBlock.startOfFree;
//            int nextFree = startOfFree;
//            Block firstBlock = new Block();
//            Disk.read(nextFree, firstBlock);
//            // check file length
//            // if file is less than block size
//            if (fileBytes.length < 5) {
//                SuperBlock.startOfFree = firstBlock.getNext();
//                Disk.write(nextFree, fileBytes);
//            } else {
//                int writtenDataCount = 0;
//
//                while (writtenDataCount < fileBytes.length) {
//                    Extent extent = new Extent(nextFree, (short) 1);
//                    while (true) {
//                        Block nextBlock = new Block();
//                        Disk.read(firstBlock.getNext(), nextBlock);
//                        if (firstBlock.getNext() - nextFree == 1) {
//                            extent.setSize((short) (extent.getSize() + 1));
//                            nextFree = firstBlock.getNext();
//                            firstBlock = nextBlock;
//                        } else {
//                            // break this loop and write the extent
//                            inode.addPointer(extent);
//
//                            // TODO: 28.9.2019. test this
//                            Disk.write(extent.getStartIndex(), Arrays.copyOfRange(fileBytes, writtenDataCount, extent.getSize() * 5));
//                            writtenDataCount += 5 * extent.getSize();
//                            break;
//                        }
//                        if (fileBytes.length < extent.getSize() * 5) {
//                            System.out.println("Moze se upisati");
//                            System.out.println(nextFree);
//                            inode.addPointer(extent);
//                            Disk.write(extent.getStartIndex(), Arrays.copyOfRange(fileBytes, writtenDataCount, extent.getSize() * 5));
//                            writtenDataCount = 5 * extent.getSize();
//                            break;
//                        }
//                    }
//                    System.out.println(extent);
//                }
//                System.out.println(nextFree);
//            }
//            // check frees
//            InodeBlock.addNodeToList(inode);
//            SuperBlock.startOfFree = inode.getPointers().getLast().getStartIndex() + inode.getPointers().getLast().getSize();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 1;   // return file descriptor
//    }

    public int rm (String fileName) {
        return 1;
    }

    public int cat(String path) {
        return 1; // return file descriptor
    }

    // endregion
}
