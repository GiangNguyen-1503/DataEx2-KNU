import java.io.*;

public class Utils {
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        out.flush();
        return bos.toByteArray();
    }

    public static Packet deserialize(byte[] data, int length) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data, 0, length);
        ObjectInputStream in = new ObjectInputStream(bis);
        Packet pkt = (Packet) in.readObject();
        in.close();
        return pkt;
    }
}