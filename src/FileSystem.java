import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileSystem {

    public boolean mkdir(String startDirName, String newDirName) {
        try {
        } catch (Exception e) {

        }
        return false;
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
}
