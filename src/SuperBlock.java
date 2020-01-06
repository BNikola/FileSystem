import java.io.*;

public class SuperBlock implements Serializable {

    private int startOfFree;
    private int startOfINode;
    private int startOfRoot;
    private int numberOfInodes;
    private int endOfInodeBlock;
    private static final long serialVersionUID = 1L;

    public SuperBlock() {
        startOfINode = 50;
        startOfFree = 400_000;
        startOfRoot = 400_000;  // just the first time the disk is opened (disc is empty)
        numberOfInodes = 1;

    }

    public int getStartOfFree() {
        return startOfFree;
    }

    public void setStartOfFree(int startOfFree) {
        this.startOfFree = startOfFree;
        System.out.println("$$$" + this);
    }

    public int getStartOfINode() {
        return startOfINode;
    }

    public void setStartOfINode(int startOfINode) {
        this.startOfINode = startOfINode;
    }

    public int getStartOfRoot() {
        return startOfRoot;
    }

    public void setStartOfRoot(int startOfRoot) {
        this.startOfRoot = startOfRoot;
    }

    public int getNumberOfInodes() {
        return numberOfInodes;
    }

    public void setNumberOfInodes(int numberOfInodes) {
        this.numberOfInodes = numberOfInodes;
    }

    public int getEndOfInodeBlock() {
        return endOfInodeBlock;
    }

    public void setEndOfInodeBlock(int endOfInodeBlock) {
        this.endOfInodeBlock = endOfInodeBlock;
    }

    @Override
    public String toString() {
        return "SuperBlock{" +
                "startOfFree=" + startOfFree +
                ", startOfINode=" + startOfINode +
                ", startOfRoot=" + startOfRoot +
                ", numberOfInodes=" + numberOfInodes +
                ", endOfInodeBlock=" + endOfInodeBlock +
                '}';
    }
}
