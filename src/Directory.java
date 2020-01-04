import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Directory implements Serializable {
    // TODO: 5.1.2020. Change hash map to <Integer, String> - same 
    public HashMap<String, Integer> fileNames = new HashMap<>();     // hash map: nameOfFile:inodeNumber
    public List<Integer> fileNamesLengths = new ArrayList<>();  // when reading, read how many files are in hash map
    public int filesLength;
    public String name;
    public int nameLength;
    private static final long serialVersionUID = 1L;

    public Directory(String name) {
        this.name = name;
        this.nameLength = name.length();
        this.filesLength = fileNames.size();
    }

    public boolean addFile(String newFileName, Integer iNode) {
        if (fileNames.containsKey(newFileName) && fileNames.values().contains(iNode)) {
            return false;
        } else {
            fileNames.put(newFileName, iNode);
            filesLength++;
            fileNamesLengths.add(newFileName.length());
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

    @Override
    public String toString() {
        return "Directory{" +
                "nameLength=" + nameLength +
                ", filesLength=" + filesLength +
                ", fileNamesLengths=" + fileNamesLengths +
                ", name='" + name + '\'' +
                ", files= " + fileNames +
                '}';
    }
}
