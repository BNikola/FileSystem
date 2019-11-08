import java.io.*;

public class SuperBlock implements Serializable {

    private static int startOfINode;
    private static int startOfFree;
    private static int startOfRoot;
    private static int numberOfInodes;

    // this is singleton
    private SuperBlock() {
        startOfINode = 4;
        startOfFree = 400_000;
        startOfRoot = 400_000;  // just the first time the disk is opened (disc is empty)
        numberOfInodes = 1;

    }

    public static int getStartOfINode() {
        return startOfINode;
    }

    public static void setStartOfINode(int startOfINode) {
        SuperBlock.startOfINode = startOfINode;
    }

    public static int getStartOfFree() {
        return startOfFree;
    }

    public static void setStartOfFree(int startOfFree) {
        SuperBlock.startOfFree = startOfFree;
    }

    public static int getStartOfRoot() {
        return startOfRoot;
    }

    public static int getNumberOfInodes() {
        return numberOfInodes;
    }

    public static void setNumberOfInodes(int numberOfInodes) {
        SuperBlock.numberOfInodes = numberOfInodes;
    }

    @Override
    public String toString() {
        return "Superblock: " + startOfINode + " " + startOfFree + " " + numberOfInodes;
    }
}
