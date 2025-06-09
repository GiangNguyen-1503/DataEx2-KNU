import java.net.*;
import java.io.*;
import java.util.Random;

public class Receiver {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java Receiver <port> <data_packet_drop_probability>");
            return;
        }

        int port = Integer.parseInt(args[0]); // port to listen for incoming packets
        double dropProb = Double.parseDouble(args[1]); // drop probability of DATA packets received
        DatagramSocket socket = new DatagramSocket(port);
        Random rand = new Random();

        String fileName = "received_file";
        FileOutputStream fos = new FileOutputStream("received_file");
        int expectedSeq = 0;
        boolean receiving = true;

        System.out.println(getTime() + " Receiver started, waiting on port " + port);

        while (receiving) {
            byte[] buf = new byte[4096];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            socket.receive(dp);

            // Deserialize packet
            Packet pkt = null;
            try {
                pkt = Utils.deserialize(buf, dp.getLength());
            } catch (Exception e) {
                System.out.println(getTime() + " [ERROR] Failed to deserialize packet.");
                continue;
            }

            // Simulate packet loss
            if (pkt.type == Packet.DATA && rand.nextDouble() < dropProb) {
                System.out.println(getTime() + " [DROP] DATA seq=" + pkt.seqNum);
                continue;
            }

            // Handle DATA packet
            if (pkt.type == Packet.DATA) {
                System.out.println(getTime() + " [RECV] DATA seq=" + pkt.seqNum + ", len=" + pkt.length);

                if (pkt.seqNum == expectedSeq) {
                    fos.write(pkt.data, 0, pkt.length);
                    System.out.println(getTime() + " [WRITE] DATA seq=" + pkt.seqNum + " to file");
                    expectedSeq = 1 - expectedSeq; // Toggle 0 <-> 1
                } else {
                    System.out.println(getTime() + " [DUPLICATE] DATA seq=" + pkt.seqNum);
                }

                Packet ackPkt = new Packet(Packet.ACK, 0, pkt.seqNum);
                byte[] ackBytes = Utils.serialize(ackPkt);
                DatagramPacket ackDp = new DatagramPacket(ackBytes, ackBytes.length, dp.getAddress(), dp.getPort());
                socket.send(ackDp);
                System.out.println(getTime() + " [SEND] ACK ackNum=" + pkt.seqNum);
            }
            // Handle EOT
            else if (pkt.type == Packet.EOT) {
                System.out.println(getTime() + " [RECV] EOT, closing file and socket...");
                receiving = false;

                Packet eotPkt = new Packet(Packet.EOT, pkt.seqNum, pkt.seqNum);
                byte[] eotBytes = Utils.serialize(eotPkt);
                DatagramPacket eotDp = new DatagramPacket(eotBytes, eotBytes.length, dp.getAddress(), dp.getPort());
                socket.send(eotDp);

                if (fos != null) fos.close();
                break;
            }
        }
        socket.close();
        System.out.println(getTime() + " Receiver stopped. File saved as: " + fileName);
    }

    private static String getTime() {
        return "[" + System.currentTimeMillis() + "]";
    }
}