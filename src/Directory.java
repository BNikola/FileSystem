import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Directory implements Serializable {
    public HashMap<Integer, String> fileNames = new HashMap<>();     // hash map: nameOfFile:inodeNumber
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

    public boolean addFile(Integer iNode, String newFileName) {
        if (fileNames.containsKey(iNode)) {
            return false;
        } else {
            fileNames.put(iNode, newFileName);
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
        return new ArrayList<>(fileNames.values());
    }

    public Integer getKey(String value) {
        return fileNames.entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().get();
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
