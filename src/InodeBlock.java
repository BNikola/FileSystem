import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InodeBlock implements Serializable {
    public static List<Inode> inodeList = new ArrayList<>();
    public static int size; // number of i nodes
    private static final long serialVersionUID = 1L;


    // region Constructors
    public InodeBlock() {
        size = 0;
    }
    // endregion

    // region Getters and Setters

    public List<Inode> getInodeList() {
        return inodeList;
    }

    public void setInodeList(List<Inode> inodeList) {
        this.inodeList = inodeList;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    // endregion

    // region Methods
    // TODO: 19.11.2019. maybe remove static
    public static void addNodeToList(Inode inode) {
        inodeList.add(inode);
        size++;
    }
    // endregion


    public byte[] convertToBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(this);
            return bos.toByteArray();
        }
    }

    public static InodeBlock convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            System.out.println(ois.readObject().toString());
            return (InodeBlock) ois.readObject();
        }
    }

    @Override
    public String toString() {
        return "InodeBlock: " + size + "\n" +
                inodeList.toString();
    }
}
