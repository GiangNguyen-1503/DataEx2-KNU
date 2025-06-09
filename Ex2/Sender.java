import java.net.*;
import java.io.*;

public class Sender {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java Sender <receiver_host> <receiver_port> <file_path>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String filePath = args[2];
        File file = new File(filePath);
        String fileName = file.getName();

        DatagramSocket socket = new DatagramSocket();
        InetAddress receiverAddress = InetAddress.getByName(host);

        //SENDING GREETING
        byte[] greetingMsg = "Greeting".getBytes();
        DatagramPacket greetingPacket = new DatagramPacket(greetingMsg, greetingMsg.length, receiverAddress, port);
        socket.send(greetingPacket);
        System.out.println("Sent: " + new String(greetingMsg));  // Print the sent message

        //SENDING FILE NAME
        byte[] fileNameMsg = fileName.getBytes();
        DatagramPacket fileNamePacket = new DatagramPacket(fileNameMsg, fileNameMsg.length, receiverAddress, port);
        socket.send(fileNamePacket);
        System.out.println("Sending: " + new String(fileNameMsg) + " File");  // Print the sent message

        //RECEIVING OK
        byte[] buffer = new byte[4096];
        DatagramPacket okPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(okPacket);
        String okMsg = new String(okPacket.getData(), 0, okPacket.getLength());
        if (!okMsg.equals("OK")) {
            System.out.println("Haven't received OK yet");
            socket.close();
            return;
        } else {
            System.out.println("Received: " + okMsg);  // Print the received message
        }

        //SENDING FILE
        FileInputStream fis = new FileInputStream(file);
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            DatagramPacket dataPacket = new DatagramPacket(buffer, bytesRead, receiverAddress, port);
            socket.send(dataPacket);
        }
        fis.close();

        //SENDING FINISH
        byte[] finishMsg = "Finish".getBytes();
        DatagramPacket finishPacket = new DatagramPacket(finishMsg, finishMsg.length, receiverAddress, port);
        socket.send(finishPacket);
        System.out.println("Sent: " + new String(finishMsg));  // Print the sent message

        //RECEIVING WELLDONE
        DatagramPacket donePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(donePacket);
        String doneMsg = new String(donePacket.getData(), 0, donePacket.getLength());
        if (!doneMsg.equals("Welldone")) {
            System.out.println("Haven't received Welldone yet");
        } else {
            System.out.println("Received: " + doneMsg);  // Print the received message
        }
        socket.close();
    }    
}
