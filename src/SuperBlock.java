public class SuperBlock {

    // number of blocks in file system
    public int size;
    // number of iNodes in file system
    public int iSize;
    // first block of the free list
    public int freeList;


    @Override
    public String toString() {
        return "SuperBlock{" +
                "size=" + size +
                ", iSize=" + iSize +
                ", freeList=" + freeList +
                '}';
    }
}
