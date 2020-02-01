import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            Inode rootInode = DISC.inodeBlock.inodeList.get(0);
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

    public void ls(String... path) {
        if (path.length == 0 || path[0].equals("/root")) {
            System.out.println("/root");
//            currentDirectory.listFileNames().forEach(System.out::println);
            currentDirectory.list();
        } else {
            String lsPath = path[0];
            if (parsePath(lsPath) == 1) {
                System.out.println(lsPath);
                ArrayList<String> parsedPath = new ArrayList<>(Arrays.asList(path[0].split("/")));
                parsedPath.remove(0);
                if (parsedPath.size() == 2) {
                    Directory dir = null;

                    Integer index = currentDirectory.fileNames.get(parsedPath.get(1));
                    Inode dirInode = DISC.inodeBlock.getInodeList().get(index);

                    if (dirInode.getFlags() == 0) {
                        try {
                            dir = Directory.convertFromBytes(dirInode.readExents());
                        } catch (IOException | ClassNotFoundException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }

                        if (dir != null) {
//                            dir.listFileNames().forEach(System.out::println);
                            dir.list();
                        }
                    }
                }
            }
        }
    }

    public boolean mkdir(String newDirName) {
        if (parsePath(newDirName) != 0) {
            System.out.println("MKDIR FAIL");
            return false;
        } else {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newDirName.split("/")));
            path.remove(0);
            if (path.size() > 2) {
                return false;
            }
            Directory directory = new Directory(path.get(path.size() - 1));
            Inode newDirInode = new Inode();

            try {
                newDirInode.bytesToExtents(directory.convertToBytes(), DISC.superBlock);
                newDirInode.writeExtents(directory.convertToBytes());
                DISC.inodeBlock.addNodeToList(newDirInode);
//                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newDirInode), path.get(path.size() - 1));
                currentDirectory.addFile(DISC.inodeBlock.getKey(newDirInode), path.get(path.size() - 1));
//                currentInode.append(currentDirectory.convertToBytes());
                currentInode = DISC.inodeBlock.getInodeList().get(0);
                DISC.inodeBlock.removeNodeFromList(0);
                DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
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
    }

    public boolean create(String newFilePath) {
        boolean result = false;
        if (parsePath(newFilePath) == 0) {
            ArrayList<String> path = new ArrayList<>(Arrays.asList(newFilePath.split("/")));
            path.remove(0);

            Inode newFileInode = new Inode(1);
//            newFileInode.setFlags(1);       // set file flag
            MyFile newFile = new MyFile();
            try {
                newFileInode.bytesToExtents(newFile.convertToBytes(), DISC.superBlock);
            } catch (IOException e) {
                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            DISC.inodeBlock.addNodeToList(newFileInode);

            if (path.size() == 2) {
//                newFileInode.writeExtents(new byte[5]);
                try {
                    newFileInode.writeExtents(newFile.convertToBytes());
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
//                currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newFileInode), path.get(1));
                currentDirectory.addFile(DISC.inodeBlock.getKey(newFileInode), path.get(path.size() - 1));

//                DISC.inodeBlock.getInodeList().remove(currentInode);
                currentInode = DISC.inodeBlock.getInodeList().get(0);
                DISC.inodeBlock.removeNodeFromList(0);
                try {
//                    DISC.inodeBlock.getInodeList().add(0, currentInode.append(currentDirectory.convertToBytes()));
                    DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }

            } else {
                Directory secondLevelDir = null;
                Integer index = currentDirectory.fileNames.get(path.get(1));
//                String index = currentDirectory.getKey(path.get(1), 0);
                try {
                    secondLevelDir = Directory.convertFromBytes(DISC.inodeBlock.inodeList.get(index).readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
//                newFileInode.writeExtents(new byte[5]);
                try {
                    newFileInode.writeExtents(newFile.convertToBytes());
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
//                secondLevelDir.addFile(DISC.inodeBlock.getInodeList().indexOf(newFileInode), path.get(2));
                secondLevelDir.addFile(DISC.inodeBlock.getKey(newFileInode), path.get(path.size() - 1));

                Inode secondInode = DISC.inodeBlock.getInodeList().get(index);
//                DISC.inodeBlock.getInodeList().remove(secondInode);
                DISC.inodeBlock.removeNodeFromList(index);
                try {
//                    DISC.inodeBlock.getInodeList().add(index, secondInode.append(secondLevelDir.convertToBytes()));
                    DISC.inodeBlock.addNodeToList(index, secondInode.append(secondLevelDir.convertToBytes()));
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
//                        destDir.addFile(DISC.inodeBlock.getInodeList().indexOf(newInode), newPath.get(newPath.size() - 1));
                        destDir.addFile(DISC.inodeBlock.getKey(newInode), newPath.get(newPath.size() - 1));

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
//                        currentDirectory.addFile(DISC.inodeBlock.getInodeList().indexOf(newInode), newPath.get(newPath.size() - 1));
                        currentDirectory.addFile(DISC.inodeBlock.getKey(newInode), newPath.get(newPath.size() - 1));

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

    public boolean mv(String oldLocation, String newLocation) {
        if (oldLocation.equals(newLocation)) {
            return true;
        }
        if (cp(oldLocation, newLocation)) {
            rm(oldLocation);
            return true;
        } else {
            return false;
        }
    }

    public boolean rm(String oldLocation, String... mode) {

        if (parsePath(oldLocation) == 1) {
            ArrayList<String> oldPath = new ArrayList<>(Arrays.asList(oldLocation.split("/")));
            oldPath.remove(0);
            if (oldPath.size() == 2) {
                currentInode = DISC.inodeBlock.getInodeList().get(0);
                int index = currentDirectory.fileNames.get(oldPath.get(1));
                Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
//                Inode oldInode = currentDirectory.fileNames.get(oldPath.get(1));

                if (oldInode.getFlags() == 0 && mode.length == 0) {
                    System.out.println("Trying to delete directory");
                    return false;
                } else if(oldInode.getFlags() == 0 && mode.length > 0) {
                    if ("-r".equals(mode[0])) {
                        Directory dir = null;

                        try {
                            dir = Directory.convertFromBytes(oldInode.readExents());
                        } catch (IOException | ClassNotFoundException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                        for (String fileName : dir.fileNames.keySet()) {
                            rm(oldLocation + "/" + fileName);
                        }

                        DISC.inodeBlock.removeNodeFromList(index);
                        oldInode.resetExtents();
                        currentDirectory.fileNames.remove(oldPath.get(oldPath.size() - 1));
                        DISC.inodeBlock.removeNodeFromList(0);
                        try {
                            DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
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
                    DISC.inodeBlock.removeNodeFromList(index);
//                    DISC.inodeBlock.removeNodeFromList(oldInode);
                    oldInode.resetExtents();
                    currentDirectory.fileNames.remove(oldPath.get(oldPath.size() - 1));
                    DISC.inodeBlock.removeNodeFromList(0);
                    try {
                        DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
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
            } else {
                int dirIndex = currentDirectory.fileNames.get(oldPath.get(1));
                Inode dirInode = DISC.inodeBlock.getInodeList().get(dirIndex);
//                Inode dirInode = currentDirectory.fileNames.get(oldPath.get(1));

                Directory dir = null;

                try {
                    dir = Directory.convertFromBytes(dirInode.readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }

                int index = dir.fileNames.get(oldPath.get(oldPath.size() - 1));
                Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
//                Inode oldInode = dir.fileNames.get(oldPath.get(oldPath.size() - 1));

                DISC.inodeBlock.removeNodeFromList(index);
//                DISC.inodeBlock.removeNodeFromList(oldInode);

                oldInode.resetExtents();
                dir.fileNames.remove(oldPath.get(oldPath.size() - 1));
                DISC.inodeBlock.removeNodeFromList(dirIndex);
//                DISC.inodeBlock.removeNodeFromList(dirInode);
                try {
//                    DISC.inodeBlock.addNodeToList(dirIndex, dirInode.append(dir.convertToBytes()));
                    DISC.inodeBlock.addNodeToList(dirIndex, dirInode.append(dir.convertToBytes()));
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
        } else {
            return false;
        }
    }

    public String cat(String path) {
        String result = "";
        MyFile f = null;
        if (parsePath(path) == 1) {
            ArrayList<String> parsedPath = new ArrayList<>(Arrays.asList(path.split("/")));
            parsedPath.remove(0);
            if (parsedPath.size() == 2) {
                currentInode = DISC.inodeBlock.getInodeList().get(0);
                int index = currentDirectory.fileNames.get(parsedPath.get(1));
                Inode fileInode = DISC.inodeBlock.getInodeList().get(index);
                if (fileInode.getFlags() == 1) {
                    try {
                        f = MyFile.convertFromBytes(fileInode.readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    if (f != null) {
                        System.out.println("File: " + path);
                        System.out.println(new String(f.getData()));
                    }
                }
            } else {
                int dirIndex = currentDirectory.fileNames.get(parsedPath.get(1));
                Inode dirInode = DISC.inodeBlock.getInodeList().get(dirIndex);

                Directory dir = null;

                try {
                    dir = Directory.convertFromBytes(dirInode.readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }

                if (dir != null) {
                    int fileIndex = dir.fileNames.get(parsedPath.get(parsedPath.size() - 1));
                    Inode fileInode = DISC.inodeBlock.getInodeList().get(fileIndex);

                    try {
                        f = MyFile.convertFromBytes(fileInode.readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    if (f != null) {
                        System.out.println("File: " + path);
                        System.out.println(new String(f.getData()));
                    }
                }
            }
            try {
                disc.writeHeader(DISC.superBlock, DISC.inodeBlock);
            } catch (IOException e) {
                DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
        if (f != null) {
            result = new String(f.getData());
        }

        return result;
    }

    public void echo(String path, String data) {
        if (parsePath(path) == 1) {
            ArrayList<String> parsedPath = new ArrayList<>(Arrays.asList(path.split("/")));
            parsedPath.remove(0);
            MyFile f = null;
            if (parsedPath.size() == 2) {
                currentInode = DISC.inodeBlock.getInodeList().get(0);
                int index = currentDirectory.fileNames.get(parsedPath.get(1));
                Inode fileInode = DISC.inodeBlock.getInodeList().get(index);
                if (fileInode.getFlags() == 1) {
                    try {
                        f = MyFile.convertFromBytes(fileInode.readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    if (f != null) {
                        try {
                            f.setData(data.getBytes());
                            DISC.inodeBlock.removeNodeFromList(index);
                            DISC.inodeBlock.addNodeToList(index, fileInode.append(f.convertToBytes()));
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }

                }
            } else {
                int dirIndex = currentDirectory.fileNames.get(parsedPath.get(1));
                Inode dirInode = DISC.inodeBlock.getInodeList().get(dirIndex);

                Directory dir = null;

                try {
                    dir = Directory.convertFromBytes(dirInode.readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }

                if (dir != null) {
                    int fileIndex = dir.fileNames.get(parsedPath.get(parsedPath.size() - 1));
                    Inode fileInode = DISC.inodeBlock.getInodeList().get(fileIndex);

                    try {
                        f = MyFile.convertFromBytes(fileInode.readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    if (f != null) {
                        try {
                            f.setData(data.getBytes());
                            DISC.inodeBlock.removeNodeFromList(fileIndex);
                            DISC.inodeBlock.addNodeToList(fileIndex, fileInode.append(f.convertToBytes()));
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }
                }
            }
        }
    }

    public void put(String sourcePath, String destinationPath) {
        java.io.File f = new java.io.File(sourcePath);
        if (f.exists()) {
            if (f.isFile()) {
                System.out.println("Source file exists and it is a file");
                if (parsePath(destinationPath) == 0) {
                    System.out.println("Put the file to the FS");
                    ArrayList<String> parsedPath = new ArrayList<>(Arrays.asList(destinationPath.split("/")));
                    parsedPath.remove(0);

                    Inode newFileInode = new Inode();
                    newFileInode.setFlags(1);
                    MyFile newFile = new MyFile();
                    try {
                        byte[] content = Files.readAllBytes(Paths.get(f.toURI()));
                        if (content.length > 64_000) {
                            System.out.println("File is too large");
                            return;
                        }
                        newFile.setData(content);
                    } catch (IOException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }

                    DISC.inodeBlock.addNodeToList(newFileInode);

                    if (parsedPath.size() == 2) {
                        try {
                            newFileInode.bytesToExtents(newFile.convertToBytes(), DISC.superBlock);
                            newFileInode.writeExtents(newFile.convertToBytes());
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }

                        currentDirectory.addFile(DISC.inodeBlock.getKey(newFileInode), parsedPath.get(parsedPath.size() - 1));
                        currentInode = DISC.inodeBlock.getInodeList().get(0);
                        DISC.inodeBlock.removeNodeFromList(0);
                        try {
                            DISC.inodeBlock.addNodeToList(0, currentInode.append(currentDirectory.convertToBytes()));
                        } catch (IOException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    } else {
                        Directory dir = null;
                        Integer index = currentDirectory.fileNames.get(parsedPath.get(1));
                        try {
                            dir = Directory.convertFromBytes(DISC.inodeBlock.getInodeList().get(index).readExents());
                            newFileInode.writeExtents(newFile.convertToBytes());
                        } catch (IOException | ClassNotFoundException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }

                        dir.addFile(DISC.inodeBlock.getKey(newFileInode), parsedPath.get(parsedPath.size() - 1));

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
                }
            } else {
                System.out.println("Source file is a dir");
            }
        } else {
            System.out.println("Source file does not exist");
        }

    }

    public void stat(String sourcePath) {
        if (parsePath(sourcePath) == 1) {
            System.out.println("==============================================");
            System.out.println(sourcePath);
            System.out.println("==============================================");
            ArrayList<String> parsedPath = new ArrayList<>(Arrays.asList(sourcePath.split("/")));
            parsedPath.remove(0);
            if (parsedPath.size() == 2) {
                Integer index = currentDirectory.fileNames.get(parsedPath.get(1));
                Inode fileInode = DISC.inodeBlock.getInodeList().get(index);
                System.out.println(fileInode);
            } else {
                int dirIndex = currentDirectory.fileNames.get(parsedPath.get(1));
                Inode dirInode = DISC.inodeBlock.getInodeList().get(dirIndex);

                Directory dir = null;

                try {
                    dir = Directory.convertFromBytes(dirInode.readExents());
                } catch (IOException | ClassNotFoundException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }

                if (dir != null) {
                    int fileIndex = dir.fileNames.get(parsedPath.get(parsedPath.size() - 1));
                    Inode fileInode = DISC.inodeBlock.getInodeList().get(fileIndex);

                    System.out.println(fileInode);
                }
            }

        }
    }

    public void get(String sourcePath, String destinationPath) {
        java.io.File f = new java.io.File(destinationPath);
        if (!f.exists()) {
            if (parsePath(sourcePath) == 1) {
                ArrayList<String> parsedPath = new ArrayList<>(Arrays.asList(sourcePath.split("/")));
                parsedPath.remove(0);
                byte [] data = null;
                MyFile file = null;
                if (parsedPath.size() == 2) {
                    Integer index = currentDirectory.fileNames.get(parsedPath.get(1));
                    Inode oldInode = DISC.inodeBlock.getInodeList().get(index);
                    if (oldInode.getFlags() == 1) {
                        try {
                            file = MyFile.convertFromBytes(oldInode.readExents());
                            data = file.getData();
                        } catch (IOException | ClassNotFoundException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }
                } else {
                    int dirIndex = currentDirectory.fileNames.get(parsedPath.get(1));
                    Inode dirInode = DISC.inodeBlock.getInodeList().get(dirIndex);

                    Directory dir = null;

                    try {
                        dir = Directory.convertFromBytes(dirInode.readExents());
                    } catch (IOException | ClassNotFoundException e) {
                        DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }

                    if (dir != null) {
                        int fileIndex = dir.fileNames.get(parsedPath.get(parsedPath.size() - 1));
                        Inode fileInode = DISC.inodeBlock.getInodeList().get(fileIndex);

                        try {
                            file = MyFile.convertFromBytes(fileInode.readExents());
                        } catch (IOException | ClassNotFoundException e) {
                            DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }

                        if (file != null) {
                            data = file.getData();
                        }
                    }
                }

                try (BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()))) {
                    baos.write(data);
                } catch (IOException e) {
                    DISC.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }
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
                    Inode secondInode = DISC.inodeBlock.inodeList.get(index);
                    if (secondInode.getFlags() != 0) {
                        return -1;
                    } else {
                        Directory secondLevelDir = null;
                        try {
                            secondLevelDir = Directory.convertFromBytes(secondInode.readExents());
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
}
