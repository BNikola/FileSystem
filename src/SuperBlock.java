import java.io.*;

public class SuperBlock implements Serializable {

    private int startOfINode;
    private int startOfFree;
    private int startOfRoot;
    private int numberOfInodes;

    public SuperBlock() {
        startOfINode = 4;
        startOfFree = 400_000;
        startOfRoot = 400_000;  // just the first time the disk is opened (disc is empty)
        numberOfInodes = 1;

    }

    public int getStartOfINode() {
        return startOfINode;
    }

    public void setStartOfINode(int startOfINode) {
        this.startOfINode = startOfINode;
    }

    public int getStartOfFree() {
        return startOfFree;
    }

    public void setStartOfFree(int startOfFree) {
        this.startOfFree = startOfFree;
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

    @Override
    public String toString() {
        return "Superblock: " + startOfINode + " " + startOfFree + " " + numberOfInodes;
    }
}
