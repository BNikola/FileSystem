import java.util.ArrayList;
import java.util.List;

public class InodeBlock {
    public static List<Inode> inodeList = new ArrayList<>();
    public static int size; // number of i nodes


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
    public static void addNodeToList(Inode inode) {
        inodeList.add(inode);
        size++;
    }
    // endregion




    @Override
    public String toString() {
        return "InodeBlock: " + size + "\n" +
                inodeList.toString();
    }
}
