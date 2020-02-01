import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class InodeBlock implements Serializable {
    //    public List<Inode> inodeList = new ArrayList<>();
    public HashMap<Integer, Inode> inodeList = new HashMap<>();
    public static Integer index = 0;
    public int number = 0;
    public int size; // number of i nodes
    private static final long serialVersionUID = 1L;


    // region Constructors
    public InodeBlock() {
        size = 0;
    }
    public InodeBlock(InodeBlock inodeBlock) {
        this.inodeList = inodeBlock.inodeList;
        this.size = inodeBlock.size;
    }
    // endregion

    // region Getters and Setters

    public HashMap<Integer, Inode> getInodeList() {
        return inodeList;
    }

    public void setInodeList(HashMap<Integer, Inode> inodeList) {
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
    public void addNodeToList(Inode inode) {
        System.out.println("ADDING INODE: " + index);
        inodeList.put(index, inode);
        index++;
        number = index;
        size++;
    }

    public void addNodeToList(Integer index, Inode inode) {
        if (!inodeList.containsKey(index)) {
            System.out.println("ADDING INODE: " + index);
            inodeList.put(index, inode);
            size++;
        } else {
            System.out.println("ERR ADDING: " + index + "\n" + inode);
        }
    }

    public void removeNodeFromList(Integer index) {
        System.out.println("REMOVING NODE: " + index);
        inodeList.remove(index);
        size--;
    }

    public Integer getKey(Inode value) {
        for (Map.Entry<Integer, Inode> e : inodeList.entrySet())
            if (value.equals(e.getValue())) {
                return e.getKey();
            }
        return -1;
    }
    // endregion


    public byte[] convertToBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(this);
            return bos.toByteArray();
        }
    }

    public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

    @Override
    public String toString() {
        return "InodeBlock: " + size + inodeList.size() + "\n" +
                inodeList.toString();
    }
}
