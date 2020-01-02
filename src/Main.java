import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();

        System.out.println(fs.currentDirectory);
        System.out.println(fs.pwd());
        fs.ls();
    }
//    public static void main(String[] args) {
//        SuperBlock sb = new SuperBlock();
////        InodeBlock ib = new InodeBlock();
////        Inode in = new Inode();
////        in.numberOfExtents = 1;
////        in.pointers.add(new Extent(12,2));
////        ib.inodeList.add(in);
////        Disk d = new Disk();
////        d.read(0, sb);
////        System.out.println(sb);
//        // format disk
////        try (RandomAccessFile raf = new RandomAccessFile("DISK", "rw")) {
////            for (int i = 0; i < 4_000_000; i++) {
////                raf.writeInt(i + 1);
////                raf.writeBoolean(false);
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        Disk d = new Disk();
//        System.out.println("Start of free from sb" + SuperBlock.startOfFree);
//        d.write(0, sb);
//        Block block = new Block();
//        d.read(0, block);
//        System.out.println(block);
//        d.read(12, block);
//        System.out.println(block);
//        d.read(SuperBlock.startOfFree + 1, block);
//        System.out.println(SuperBlock.startOfFree);
//        System.out.println(block);
//
//        FileSystem fs = new FileSystem();
////        fs.put("test.txt");
//        byte[] array = new byte[20];
//        d.read(400_000, array);
//        System.out.println(new String(array));
//        Directory dir = new Directory("root");
//        boolean first = dir.addFile("root", 1);
//        boolean second = dir.addFile("root", 1);
//        boolean third = dir.addFile("test", 2);
//        System.out.println(first);
//        System.out.println(second);
//        System.out.println(third);
//        System.out.println(dir);
//
//    }
}
