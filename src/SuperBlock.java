public class SuperBlock {

    public static int startOfINode;
    public static int startOfFree;
    public static int numberOfInodes;

    public SuperBlock() {
        startOfINode = 3;
        startOfFree = 400_000;
        numberOfInodes = 1;
    }

    @Override
    public String toString() {
        return "Superblock: " + startOfINode + " " + startOfFree + " " + numberOfInodes;
    }
}
