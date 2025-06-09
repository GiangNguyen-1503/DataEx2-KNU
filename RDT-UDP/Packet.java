import java.io.*;

public class Packet implements Serializable {
    public static final int DATA = 0;
    public static final int ACK = 1;
    public static final int EOT = 2;

    public int type; // Type of packet (DATA, ACK, EOT)
    public int seqNum; // Sequence number of the packet starting from 0.
    public int ackNum; // Acknowledgment number for the packet
    public int length; // Indicating the number of bytes in the data field.
                        // It ranges from 0 to 1000.
                        // For ACK or EOT packets, this value is 0.
    public byte[] data; // Data contained in the packet

    // Following the professor's instructions, the Packet class has the following constructors:
    // Data packet constructor
    public Packet(int type, int seqNum, String data) {
        this.type = type;
        this.seqNum = seqNum;
        this.ackNum = 0; // ACK number is not used for DATA packets
        this.data = data.getBytes(); // Convert String to byte array
        this.length = this.data.length; // Set length based on data size
    }

    public Packet(int type, int seqNum, int ackNum) {
        this.type = type;
        this.seqNum = seqNum;
        this.ackNum = ackNum;
        this.data = null; // No data for ACK or EOT packets
        this.length = 0; // Length is 0 for ACK or EOT packets
    }

    // -----------------------
    // Additional constructor:
    // Full
    public Packet(int type, int seqNum, int ackNum, int length, byte[] data) {
        this.type = type;
        this.seqNum = seqNum;
        this.ackNum = ackNum;
        this.data = data;
        this.length = length;
    }
    // -----------------------

    public int getType() { return type; }
    public int getSeqNum() { return seqNum; }
    public int getAckNum() { return ackNum; }
    public int getLength() { return length; }
    public byte[] getData() { return data; }
}
