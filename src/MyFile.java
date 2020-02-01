import java.io.*;

public class MyFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte [] data;

    public MyFile() {
//        this.data = new byte[5];
        this.data = "hello".getBytes();
    }

    public MyFile(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] convertToBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(this);
            return bos.toByteArray();
        }
    }

    public static MyFile convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (MyFile) ois.readObject();
        }
    }
}
