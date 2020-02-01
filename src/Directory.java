import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Directory implements Serializable {
    public HashMap<String, Integer> fileNames = new HashMap<>();     // hash map: nameOfFile:inodeNumber
    public String name;
    private static final long serialVersionUID = 1L;

    public Directory(String name) {
        this.name = name;
    }

    public boolean addFile(Integer iNode, String newFileName) {
        if (fileNames.containsKey(newFileName)) {
            return false;
        } else {
            fileNames.put(newFileName, iNode);
            return true;
        }
    }

    public byte[] convertToBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(this);
            return bos.toByteArray();
        }
    }

    public static Directory convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Directory) ois.readObject();
        }
    }

    public ArrayList<String> listFileNames() {
        return new ArrayList<>(fileNames.keySet());
    }


    public void rename(String oldName, String newName) {
//        fileNames.replace(index, oldName, newName);
        Integer inode = fileNames.remove(oldName);
        fileNames.put(newName, inode);

    }

    @Override
    public String toString() {
        return "Directory{" +
                ", name='" + name + '\'' +
                ", files= " + fileNames +
                '}';
    }
}
