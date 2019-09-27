public class SuperBlock {

    public int startOfINode;
    public int startOfFree;
    public int numberOfInodes;

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
