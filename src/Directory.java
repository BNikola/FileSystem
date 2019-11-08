import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Directory {
    public static HashMap<String, Integer> fileNames = new HashMap<>();
    public List<Integer> fileNamesLengths = new ArrayList<>();
    public int filesLength;
    public String name;
    public int nameLength;

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
