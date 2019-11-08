import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjIntConsumer;

public class SuperBlock implements Serializable {

    private static int startOfINode;
    private static int startOfFree;
    private static int numberOfInodes;

    public Directory root;
    public List<Inode> inodes = new ArrayList<>();

    // this is singleton
    private SuperBlock() {
        startOfINode = 3;
        startOfFree = 400_000;
        numberOfInodes = 1;
        root = new Directory("/");

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
