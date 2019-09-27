public class Block {
    private int next;
    private boolean free;

    // region Constructors

    public Block() {
    }

    public Block(int next, boolean free) {
        this.next = next;
        this.free = free;
    }

    // endregion

    // region Getters and Setters

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    // endregion

    @Override
    public String toString() {
        return "Block{" +
                "next=" + next +
                ", free=" + free +
                '}';
    }
}
