import java.net.*;
import java.io.*;

public class Receiver {
    public static void main(String[] args) throws Exception {
        int port = 1503;
        DatagramSocket socket = new DatagramSocket(port);
        byte[] buffer = new byte[4096];

        //Greeting
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String greeting = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received: " + greeting);

        //File
        socket.receive(packet);
        String fileName = new String(packet.getData(), 0, packet.getLength());
        System.out.println("File name: " + fileName);

        //Ok
        InetAddress senderAddress = packet.getAddress();
        int senderPort = packet.getPort();
        byte[] okMsg = "OK".getBytes();
        DatagramPacket okPacket = new DatagramPacket(okMsg, okMsg.length, senderAddress, senderPort);
        socket.send(okPacket);
        
        //RECEIVING
        FileOutputStream fos = new FileOutputStream("received_" + fileName);
        while (true) {
            socket.receive(packet);
            String msg = new String(packet.getData(), 0, packet.getLength());
            if (msg.equals("Finish")) {
                //Weldone
                byte[] doneMsg = "Welldone".getBytes();
                DatagramPacket donePacket = new DatagramPacket(doneMsg, doneMsg.length, senderAddress, senderPort);
                socket.send(donePacket);
                break;
            }
            fos.write(packet.getData(), 0, packet.getLength());
        }
        fos.close();
        socket.close();
    }    
}
