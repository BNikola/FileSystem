import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {
    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();
//        InodeBlock ib = new InodeBlock();
//        Inode in = new Inode();
//        in.numberOfExtents = 1;
//        in.pointers.add(new Extent(12,2));
//        ib.inodeList.add(in);
//        Disk d = new Disk();
//        d.read(0, sb);
//        System.out.println(sb);
        // format disk
        try (RandomAccessFile raf = new RandomAccessFile("DISK", "rw")) {
            raf.seek(SuperBlock.startOfFree);
            for (int i = SuperBlock.startOfFree; i < SuperBlock.startOfFree + 10; i++) {
                raf.writeInt(i + 1);
                raf.writeBoolean(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Disk d = new Disk();
        System.out.println("Start of free from sb" + SuperBlock.startOfFree);
        d.write(0, sb);
        Block block = new Block();
        d.read(0, block);
        System.out.println(block);
        d.read(12, block);
        System.out.println(block);
        d.read(SuperBlock.startOfFree + 1, block);
        System.out.println(SuperBlock.startOfFree);
        System.out.println(block);

        FileSystem fs = new FileSystem();
//        fs.put("test.txt");
        byte[] array = new byte[20];
        d.read(400_000, array);
        System.out.println(new String(array));


    }
}
