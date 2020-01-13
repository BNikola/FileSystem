import java.io.IOException;
import java.util.ArrayList;
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
            currentInode = DISC.inodeBlock.inodeList.get(0);
        } catch (IOException | ClassNotFoundException e) {
            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
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
    // TODO: 6.1.2020. add remove inode method to InodeBlock and replace it here
    public boolean mkdir(String newDirName) {
        if (parsePath(newDirName) != 0) {
            System.out.println("MKDIR FAIL");
            return false;
        } else {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newDirName.split("/")));
            path.remove(0);
            Directory directory = new Directory(path.get(path.size() - 1));
            Inode newDirInode = new Inode();
            System.out.println(directory);

            try {
                newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
                newDirInode.writeExtents(directory.convertToBytes());
                DISC.inodeBlock.addNodeToList(newDirInode);
                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newDirInode), path.get(path.size() - 1));
                currentInode.append(currentDirectory.convertToBytes());
                System.out.println(DISC.inodeBlock);
                System.out.println("MKD REM");
                DISC.inodeBlock.removeNodeFromList(0);
                System.out.println(DISC.inodeBlock);
                DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                System.out.println(DISC.inodeBlock);
            } catch (IOException e) {
                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            try {
                disc.writeHeader(DISC.superBlock, DISC.inodeBlock);
            } catch (IOException e) {
                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
            }

            return true;
        }
//        if (!currentDirectory.name.equals("root")) {
//            return false;
//        }
//        if (currentDirectory.fileNames.containsKey(newDirName)) {
//            System.out.println("File exists");
//            return false;
//        } else {
//
//        }
//            Directory directory = new Directory(newDirName);
//            Inode newDirInode = new Inode();
//            System.out.println(directory);
//            try {
//                System.out.println(Arrays.toString(directory.convertToBytes()));
//                newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
//                newDirInode.writeExtents(directory.convertToBytes());
//                System.out.println("After writing the dir");
//                DISC.inodeBlock.addNodeToList(newDirInode);
//                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newDirInode),newDirName);
//                currentInode.append(currentDirectory.convertToBytes());
//
////                DISC.inodeBlock.getInodeList().remove(currentInode);
////                DISC.inodeBlock.getInodeList().add(0, currentInode.append(currentDirectory.convertToBytes()));
////                DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
//                DISC.inodeBlock.removeNodeFromList(0);
//                DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("----------\nMKDIR\n----------");
//            System.out.println(newDirInode);
//            System.out.println(currentDirectory);
//            try {
//                disc.writeHeader(DISC.superBlock, DISC.inodeBlock);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//
//        return result;
    }

    // TODO: 5.1.2020. finish after Directory class update
    public boolean create(String newFilePath) {
        boolean result = false;
        if (parsePath(newFilePath) == 0) {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newFilePath.split("/")));
            path.remove(0);

            Inode newFileInode = new Inode();
            newFileInode.bytesToExtents(new byte[5], DISC.superBlock);
            newFileInode.setFlags(1);       // set file flag
            DISC.inodeBlock.addNodeToList(newFileInode);

            if (path.size() == 2) {
                try {
                    System.out.println("LEVEL 2: ");
                    System.out.println(currentDirectory);
                    currentDirectory = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(0).readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                System.out.println(currentDirectory);
                newFileInode.writeExtents(new byte[5]);
                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newFileInode), path.get(1));
                System.out.println(currentDirectory);

//                DISC.inodeBlock.getInodeList().remove(currentInode);
                System.out.println("CREATE REMOVE 1");
                System.out.println(DISC.inodeBlock.inodeList);
                DISC.inodeBlock.removeNodeFromList(0);
                System.out.println(DISC.inodeBlock.inodeList);
                try {
//                    DISC.inodeBlock.getInodeList().add(0, currentInode.append(currentDirectory.convertToBytes()));
                    DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                System.out.println(DISC.inodeBlock.inodeList);

            } else {
                Directory secondLevelDir = null;
                Integer index = currentDirectory.fileNames.get(path.get(1));
//                String index = currentDirectory.getKey(path.get(1), 0);
                try {
                    secondLevelDir = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(index).readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                newFileInode.writeExtents(new byte[5]);
                secondLevelDir.addFile(DISC.inodeBlock.getInodeList().indexOf(newFileInode), path.get(2));
                System.out.println(secondLevelDir);

                Inode secondInode = DISC.inodeBlock.getInodeList().get(index);
//                DISC.inodeBlock.getInodeList().remove(secondInode);
                System.out.println("CREATE REMOVE");
                System.out.println(DISC.inodeBlock.inodeList);
                DISC.inodeBlock.removeNodeFromList(index);
                System.out.println(DISC.inodeBlock.inodeList);
                try {
//                    DISC.inodeBlock.getInodeList().add(index, secondInode.append(secondLevelDir.convertToBytes()));
                    DISC.inodeBlock.addNodeToList(index, secondInode.append(secondLevelDir.convertToBytes()));
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
            System.out.println("NE VALJAAAA");
            return false;
        }
    }


    public boolean rename(String oldName, String newName) {
        if (oldName.split("/").length == 2 || oldName.split("/").length > 4) {
            return false;
        }
        if (oldName.equals(newName)) {
            return true;
        }
        if (oldName.split("/").length != newName.split("/").length) {
            return false;
        }
        if (parsePath(oldName) == 1) {
            if (parsePath(newName) == 0) {
                ArrayList<String> oldPath = new ArrayList<>(Arrays.asList(oldName.split("/")));
                ArrayList<String> newPath = new ArrayList<>(Arrays.asList(newName.split("/")));
                oldPath.remove(0);
                newPath.remove(0);

                if (oldPath.size() == 2) {
//                    currentDirectory.rename(currentDirectory.getKey(oldPath.get(1), flag), oldPath.get(1), newPath.get(1));
                    currentDirectory.rename(oldPath.get(1), newPath.get(1));

                    DISC.inodeBlock.removeNodeFromList(0);
                    System.out.println(DISC.inodeBlock.inodeList);
                    try {
                        DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                    } catch (IOException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                } else {
                    Directory dir = null;
//                    Integer index = currentDirectory.getKey(oldPath.get(1), 0);
                    Integer index = currentDirectory.fileNames.get(oldPath.get(1));
                    try {
                        dir = Directory.convertFromBytes(DISC.inodeBlock.getInodeList().get(index).readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
//                    dir.rename(dir.getKey(oldPath.get(2), flag), oldPath.get(2), newPath.get(2));
                    dir.rename(oldPath.get(2), newPath.get(2));

                    Inode secondInode = DISC.inodeBlock.getInodeList().get(index);
                    DISC.inodeBlock.removeNodeFromList(index);
                    try {
                        DISC.inodeBlock.addNodeToList(index, secondInode.append(dir.convertToBytes()));
                    } catch (IOException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
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
        } else {
            return false;
        }
    }

    public boolean cp(String oldLocation, String newLocation) {
        if (oldLocation.equals(newLocation)) {
            return true;
        }
        if (parsePath(oldLocation) == 1) {
            if (parsePath(newLocation) == 0) {
                ArrayList<String> oldPath = new ArrayList<>(Arrays.asList(oldLocation.split("/")));
                ArrayList<String> newPath = new ArrayList<>(Arrays.asList(newLocation.split("/")));
                oldPath.remove(0);
                newPath.remove(0);

                if (!oldPath.get(oldPath.size() - 1).equals(newPath.get(newPath.size() - 1))) {
                    return false;
                } else {
                    if (newPath.size() == 3) {
                        Inode newInode = null;
                        if (oldPath.size() == 2) {
                            Integer index = currentDirectory.fileNames.get(oldPath.get(1));
                            Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
                            newInode = new Inode(oldInode);

                            newInode.bytesToExtents(oldInode.readExents(), DISC.superBlock);
                            newInode.writeExtents(oldInode.readExents());

                            DISC.inodeBlock.addNodeToList(newInode);
                        } else {
                            Directory dir = null;
                            Integer dirIndex = currentDirectory.fileNames.get(oldPath.get(1));
                            try {
                                dir = Directory.convertFromBytes(DISC.inodeBlock.getInodeList().get(dirIndex).readExents());
                            } catch (IOException | ClassNotFoundException e) {
                                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }

                            Integer index = dir.fileNames.get(oldPath.get(2));
                            Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
                            newInode = new Inode(oldInode);

                            newInode.bytesToExtents(oldInode.readExents(), DISC.superBlock);
                            newInode.writeExtents(oldInode.readExents());

                            DISC.inodeBlock.addNodeToList(newInode);

                        }
                        Directory destDir = null;
                        Integer dirIndex = currentDirectory.fileNames.get(newPath.get(1));
                        try {
                            destDir = Directory.convertFromBytes(DISC.inodeBlock.getInodeList().get(dirIndex).readExents());
                        } catch (IOException | ClassNotFoundException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                        destDir.addFile(DISC.inodeBlock.getInodeList().indexOf(newInode), newPath.get(newPath.size() - 1));

                        Inode dirInode = DISC.inodeBlock.getInodeList().get(dirIndex);
                        DISC.inodeBlock.removeNodeFromList(dirIndex);
                        try {
                            DISC.inodeBlock.addNodeToList(dirIndex, dirInode.append(destDir.convertToBytes()));
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    } else {
                        Inode newInode = null;
                        if (oldPath.size() == 2) {
                            Integer index = currentDirectory.fileNames.get(oldPath.get(1));
                            Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
                            newInode = new Inode(oldInode);

                            newInode.bytesToExtents(oldInode.readExents(), DISC.superBlock);
                            newInode.writeExtents(oldInode.readExents());

                            DISC.inodeBlock.addNodeToList(newInode);

                        } else {
                            Directory dir = null;
                            Integer dirIndex = currentDirectory.fileNames.get(oldPath.get(1));
                            try {
                                dir = Directory.convertFromBytes(DISC.inodeBlock.getInodeList().get(dirIndex).readExents());
                            } catch (IOException | ClassNotFoundException e) {
                                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }

                            Integer index = dir.fileNames.get(oldPath.get(2));
                            Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
                            newInode = new Inode(oldInode);

                            newInode.bytesToExtents(oldInode.readExents(), DISC.superBlock);
                            newInode.writeExtents(oldInode.readExents());

                            DISC.inodeBlock.addNodeToList(newInode);
                        }
                        currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newInode), newPath.get(newPath.size() - 1));

                        DISC.inodeBlock.removeNodeFromList(0);
                        try {
                            DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }
                    try {
                        disc.writeHeader(DISC.superBlock, DISC.inodeBlock);
                    } catch (IOException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * @param newFilePath
     * @return
     * -1   wrong path
     * 0    good
     * 1    file exists
     */
    public int parsePath(String newFilePath) {
        if (newFilePath.endsWith("/")) {
            System.out.println("Error: wrong file name" );
            return -1;
        }else if (!newFilePath.startsWith("/root")) {
            System.out.println("Ne pocinje sa root");
            return -1;
        } else {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newFilePath.split("/")));
            path.remove(0);
            System.out.println(path);
            if (path.size() > 3 || path.size() < 2) {
                System.out.println("ERR: " + newFilePath);
                return -1;
            } else if (path.size() == 3) {
                if (currentDirectory.fileNames.containsKey(path.get(1))) {
                    Integer index = currentDirectory.fileNames.get(path.get(1));
                    Directory secondLevelDir = null;
                    try {
                        secondLevelDir = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(index).readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    System.out.println(secondLevelDir);
                    if (secondLevelDir.fileNames.containsKey(path.get(2))) {
                        System.out.println("ERR: File exists");
                        return 1;
                    } else {
                        System.out.println("GOOD " + newFilePath);
                        return 0;
                    }
                } else {
                    System.out.println("ERR: Directory " + path.get(1) + " does not exist");
                    return -1;
                }
            } else {
                if (currentDirectory.fileNames.containsKey(path.get(1))) {
//                    String index = currentDirectory.getKey(path.get(1), flag);
//                    if (index == -1) {
//                        return 0;
//                    } else {
//                        return 1;
//                    }
                    System.out.println("File exists" + path);
                    return 1;
                } else {
                    System.out.println("GOOD " + newFilePath);
                    return 0;
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
