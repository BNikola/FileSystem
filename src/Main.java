import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {
    public static void main(String[] args) {
//        SuperBlock sb = new SuperBlock();
//        InodeBlock ib = new InodeBlock();
//        Inode in = new Inode();
//        in.numberOfExtents = 1;
//        in.pointers.add(new Extent(12,2));
//        ib.inodeList.add(in);
//        Disk d = new Disk();
//        d.read(0, sb);
//        System.out.println(sb);
        // format disk
//        try (RandomAccessFile raf = new RandomAccessFile("DISK", "rw")) {
//            for (int i = 0; i < 10_000; i++) {
//                raf.writeInt(i + 1);
//                raf.writeBoolean(false);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Disk d = new Disk();
        SuperBlock sb = new SuperBlock();
        d.write(0, sb);
        Block block = new Block();
        d.read(0, block);
        System.out.println(block);
        d.read(12, block);
        System.out.println(block);
        d.read(68, block);
        System.out.println(block);



    }
}
