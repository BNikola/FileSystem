import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class FileSystem {

    public Directory currentDirectory;
    public Inode currentInode;
    public static DISC disc = new DISC();

    public FileSystem() {
        DISC.boot();
        try {
            currentDirectory = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(0).readExents());
            currentInode = DISC.inodeBlock.inodeList.get(0);
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

    // TODO: 5.1.2020. change using parsePath
    public boolean mkdir(String newDirName) {
        boolean result = false;
        if (!currentDirectory.name.equals("root")) {
            return false;
        }
        if (currentDirectory.fileNames.containsValue(newDirName)) {
            Inode in = DISC.inodeBlock.getInodeList().get(currentDirectory.getKey(newDirName));
            if (in.getFlags() == 0) {
                System.out.println("The directory already exists");
                result = false;
            } else {
                System.out.println("It exists!!\n");
                Directory directory = new Directory(newDirName);
                Inode newDirInode = new Inode();
                System.out.println(directory);
                try {
                    System.out.println(Arrays.toString(directory.convertToBytes()));
                    newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
                    newDirInode.writeExtents(directory.convertToBytes());
                    DISC.inodeBlock.addNodeToList(newDirInode);
                    currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newDirInode),newDirName);
                    currentInode.append(currentDirectory.convertToBytes());

                    DISC.inodeBlock.getInodeList().remove(currentInode);
                    DISC.inodeBlock.getInodeList().add(0, currentInode);
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
                System.out.println(Arrays.toString(directory.convertToBytes()));
                newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
                newDirInode.writeExtents(directory.convertToBytes());
                System.out.println("After writing the dir");
                currentInode.showMeTheMoney();
                DISC.inodeBlock.addNodeToList(newDirInode);
                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newDirInode),newDirName);
                currentInode.append(currentDirectory.convertToBytes());

                DISC.inodeBlock.getInodeList().remove(currentInode);
                DISC.inodeBlock.getInodeList().add(0, currentInode.append(currentDirectory.convertToBytes()));

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

    // TODO: 5.1.2020. finish after Directory class update
    public boolean create(String newFilePath) {
        boolean result = false;
        if (parsePath(newFilePath, 1)) {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newFilePath.split("/")));
            path.remove(0);
            Inode newFileInode = new Inode();
            newFileInode.bytesToExtents(new byte[5], DISC.superBlock);
            newFileInode.setFlags(1);       // set file flag
            DISC.inodeBlock.addNodeToList(newFileInode);

            if (path.size() == 2) {
                System.out.println(currentDirectory);
                newFileInode.writeExtents(new byte[5]);
                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newFileInode), path.get(1));
                System.out.println(currentDirectory);

                DISC.inodeBlock.getInodeList().remove(currentInode);
                try {
                    DISC.inodeBlock.getInodeList().add(0, currentInode.append(currentDirectory.convertToBytes()));
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            } else {
                Directory secondLevelDir = null;
                try {
                    secondLevelDir = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(currentDirectory.getKey(path.get(1))).readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                newFileInode.writeExtents(new byte[5]);
                secondLevelDir.addFile(DISC.inodeBlock.getInodeList().indexOf(newFileInode), path.get(2));
                System.out.println(secondLevelDir);

                Inode secondInode = DISC.inodeBlock.getInodeList().get(currentDirectory.getKey(path.get(1)));
                DISC.inodeBlock.getInodeList().remove(secondInode);
                try {
                    int index = currentDirectory.getKey(path.get(1));
                    DISC.inodeBlock.getInodeList().add(index, secondInode.append(secondLevelDir.convertToBytes()));
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                System.out.println(secondInode);
            }

            try {
                disc.writeHeader(DISC.superBlock, DISC.inodeBlock);
            } catch (IOException e) {
                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            return true;
        } else {
            return false;
        }
    }

    // TODO: 5.1.2020. test after finishing create
    private boolean parsePath(String newFilePath, int flag) {
        if (newFilePath.endsWith("/")) {
            System.out.println("Error: wrong file name" );
            return false;
        }else if (!newFilePath.startsWith("/root")) {
            System.out.println("Ne pocinje sa root");
            return false;
        } else {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newFilePath.split("/")));
            path.remove(0);
            System.out.println(path);
            if (path.size() > 3 || path.size() < 2) {
                System.out.println("ERR: " + newFilePath);
                return false;
            } else if (path.size() == 3 && flag == 0) {
                System.out.println("ERR: only two level dir " + newFilePath);
                return false;
            } else if (path.size() == 3) {
                if (currentDirectory.fileNames.containsValue(path.get(1))) {
                    Directory secondLevelDir = null;
                    try {
                        secondLevelDir = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(1).readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    System.out.println(secondLevelDir);
                    if (secondLevelDir.fileNames.containsValue(path.get(2))) {
                        Inode in = DISC.inodeBlock.inodeList.get(secondLevelDir.getKey(path.get(2)));
                        System.out.println(in);
                        // TODO: 5.1.2020. check this
                        if (in.getFlags() == flag) {
                            System.out.println("ERR: File exists " + newFilePath);
                            return false;
                        } else {
                            System.out.println("Dir exists, but file does not " + newFilePath);
                            return true;
                        }
                    } else {
                        System.out.println("GOOD " + newFilePath);
                        return true;
                    }
                } else {
                    System.out.println("ERR: Directory " + path.get(1) + " does not exist");
                    return false;
                }
            } else {
                if (currentDirectory.fileNames.containsValue(path.get(1))) {
                    Inode in = DISC.inodeBlock.inodeList.get(currentDirectory.getKey(path.get(1)));
                    if (in.getFlags() == flag) {
                        System.out.println("ERR: File exists " + newFilePath);
                        return false;
                    } else {
                        System.out.println("Dir exists, but file does not " + newFilePath);
                        return true;
                    }
                } else {
                    System.out.println("GOOD " + newFilePath);
                    return true;
                }
            }
        }
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
