import java.util.ArrayList;
import java.util.List;

public class InodeBlock {
    private List<Inode> inodeList;
    private int size; // number of i nodes


    // region Constructors
    public InodeBlock() {
        inodeList = new ArrayList<>();
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
    public void addNodeToList(Inode inode) {
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
