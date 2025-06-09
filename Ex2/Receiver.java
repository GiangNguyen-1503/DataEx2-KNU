import java.net.*;
import java.io.*;

public class Receiver {
    public static void main(String[] args) throws Exception {
        int port = 1503;    // Port number for the receiver
        DatagramSocket socket = new DatagramSocket(port);   // Create a DatagramSocket to listen on the specified port
        byte[] buffer = new byte[4096]; // Buffer for receiving data

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);  // Create a packet to receive data

        socket.receive(packet); //RECEIVE GREETING
        String greetingMsg = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received: " + greetingMsg);

        socket.receive(packet); //RECEIVING FILE NAME
        String fileNameMsg = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Receiving: " + fileNameMsg + " File");

        //SENDING OK
        InetAddress senderAddress = packet.getAddress();
        int senderPort = packet.getPort();
        byte[] okMsg = "OK".getBytes();
        DatagramPacket okPacket = new DatagramPacket(okMsg, okMsg.length, senderAddress, senderPort);
        socket.send(okPacket);
        System.out.println("Sent: " + new String(okMsg));  // Print the sent message

        FileOutputStream fos = new FileOutputStream("received_" + fileNameMsg);
        while (true) {
            socket.receive(packet); //RECEIVING FILE
            String msg = new String(packet.getData(), 0, packet.getLength());
            
            //socket.receive(packet); //RECEIVING FINISH
            //String finish = new String(packet.getData(), 0, packet.getLength());
            //System.out.println("2. Received: " + msg);
            if (msg.equals("Finish")) {
                //Welldone
                byte[] doneMsg = "Welldone".getBytes();
                DatagramPacket donePacket = new DatagramPacket(doneMsg, doneMsg.length, senderAddress, senderPort);
                socket.send(donePacket);
                System.out.println("Sent: " + new String(doneMsg));  // Print the sent message
                break;
            }
            fos.write(packet.getData(), 0, packet.getLength());
        }
        fos.close();
        socket.close();
    }    
}
