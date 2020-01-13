import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FileSystem.disc.formatDisc();
        FileSystem fs = new FileSystem();

        System.out.println(DISC.inodeBlock);
        System.out.println(DISC.superBlock);
        fs.currentInode.showMeTheMoney();
        System.out.println(fs.currentDirectory);
//        fs.mkdir("root3");
        fs.mkdir("/root/root3");
        fs.mkdir("/root/testic123");
        System.out.println(fs.currentDirectory);
//        fs.currentInode.showMeTheMoney();
        System.out.println(DISC.inodeBlock);
        System.out.println(DISC.superBlock);

//        System.out.println("-----\n");
        fs.create("/root/test");
//        System.out.println("-----\n");
        fs.create("/root/root3");
        fs.create("/root/root2");
        fs.create("/root/testic123/ttt");
//        fs.create("/root/root3/test2");
//        System.out.println("-----TET@");
        fs.create("/root/root3/test");
//        fs.create("/root/root3/test123");
//

        fs.rename("/root/root3", "/root/root32");
        fs.rename("/root/root3", "/root/root32");
        fs.cp("/root/root2", "/root/root32/root2");

        fs.cp("/root/testic123/ttt", "/root/root32/ttt");
        fs.cp("/root/root32/ttt", "/root/ttt");


        System.out.println(DISC.inodeBlock);
        Inode inode = DISC.inodeBlock.getInodeList().get(1);
        Inode rootDir = DISC.inodeBlock.getInodeList().get(0);
        try {
            Directory dir = Directory.convertFromBytes(inode.readExents());
            Directory root = Directory.convertFromBytes(rootDir.readExents());
            System.out.println(dir);
            System.out.println(root);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
