import java.net.*;
import java.io.*;
import java.util.Random;

public class Sender {
    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            System.out.println("Usage: java Sender <sender_port> <receiver_ip> <receiver_port> <timeout> <filename> <ack_drop_probability>");
            return;
        }

        int senderPort = Integer.parseInt(args[0]);
        String receiverIp = args[1];
        int receiverPort = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);
        String filePath = args[4];
        double dropProb = Double.parseDouble(args[5]);  // Probability of dropping ACK packets

        DatagramSocket socket = new DatagramSocket(senderPort);
        InetAddress receiverAddr = InetAddress.getByName(receiverIp);
        Random rand = new Random();

        FileInputStream fis = new FileInputStream(filePath);
        int seqNum = 0;
        boolean lastPacket = false;

        System.out.println(getTime() + " Sender start sending file: " + filePath);

        byte[] buf = new byte[1000];
        while (!lastPacket) {
            int bytesRead = fis.read(buf);
            if (bytesRead == -1) {
                lastPacket = true;
                break;
            }
            
            String dataStr = new String(buf, 0, bytesRead, "ISO_8859_1");
            Packet pkt = new Packet(Packet.DATA, seqNum, dataStr);
            byte[] dataBytes = Utils.serialize(pkt);

            boolean acked = false;
            while (!acked) {
                // Send DATA packet
                DatagramPacket dp = new DatagramPacket(dataBytes, dataBytes.length, receiverAddr, receiverPort);
                socket.send(dp);
                System.out.println(getTime() + " [SEND] DATA seq=" + seqNum + ", len=" + bytesRead);

                // Wait ACK
                try {
                    socket.setSoTimeout(timeout * 1000);
                    byte[] ackBuf = new byte[4096];
                    DatagramPacket ackDp = new DatagramPacket(ackBuf, ackBuf.length);
                    socket.receive(ackDp);

                    // Deserialize ACK
                    Packet ackPkt = Utils.deserialize(ackBuf, ackDp.getLength());

                    // Simulate drop ACK
                    if (ackPkt.type == Packet.ACK && rand.nextDouble() < dropProb) {
                        System.out.println(getTime() + " [DROP] ACK ackNum=" + ackPkt.ackNum);
                        continue;
                    }

                    if (ackPkt.type == Packet.ACK && ackPkt.ackNum == seqNum) {
                        System.out.println(getTime() + " [RECV] ACK ackNum=" + ackPkt.ackNum);
                        acked = true;
                        seqNum = 1 - seqNum; // Toggle seqNum
                    }
                } catch (SocketTimeoutException ste) {
                    // Timeout, gửi lại
                    System.out.println(getTime() + " [TIMEOUT] DATA seq=" + seqNum + ", retransmit...");
                }
            }
        }
        fis.close();

        // Send EOT
        Packet eotPkt = new Packet(Packet.EOT, seqNum, seqNum);
        byte[] eotBytes = Utils.serialize(eotPkt);
        DatagramPacket eotDp = new DatagramPacket(eotBytes, eotBytes.length, receiverAddr, receiverPort);
        socket.send(eotDp);
        System.out.println(getTime() + " [SEND] EOT seq=" + seqNum);

        // wait EOT (optional)
        try {
            socket.setSoTimeout(timeout * 1000);
            byte[] eotAckBuf = new byte[4096];
            DatagramPacket eotAckDp = new DatagramPacket(eotAckBuf, eotAckBuf.length);
            socket.receive(eotAckDp);
            Packet eotAckPkt = Utils.deserialize(eotAckBuf, eotAckDp.getLength());
            if (eotAckPkt.type == Packet.EOT) {
                System.out.println(getTime() + " [RECV] EOT ACK, transmission finished.");
            }
        } catch (SocketTimeoutException ste) {
            System.out.println(getTime() + " [WARN] Did not receive EOT ACK, but transmission likely finished.");
        }

        socket.close();
        System.out.println(getTime() + " Sender finished.");
    }

    private static String getTime() {
        return "[" + System.currentTimeMillis() + "]";
    }
}