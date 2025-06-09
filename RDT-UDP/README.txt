Reliable Data Transfer (RDT) - UDP Stop-and-Wait
================================================

1. Description
--------------
This project implements a reliable file transfer protocol over UDP using the Stop-and-Wait RDT protocol.
The system consists of a Sender and a Receiver.
It handles packet loss, duplicate packets, timeouts, retransmissions, and logs all major events.

2. Requirements
---------------
Java 8 or higher.
Compile all files before running:
    bash
javac *.java    

3. Running the Programs
-----------------------
1: Receiver
Start the receiver first:
    bash
java Receiver <receiver_port> <data_packet_drop_probability>
    <receiver_port> : UDP port to listen on
    <data_packet_drop_probability> : Probability to drop DATA packets
    Example: 'java Receiver 8888 0.2'

2: Sender
Start the sender:
    bash
java Sender <sender_port> <receiver_ip> <receiver_port> <timeout> <filename> <ack_drop_probability>
    <sender_port> : UDP port for the sender
    <receiver_ip> : IP address of the receiver
    <receiver_port> : UDP port of the receiver
    <timeout> : Timeout interval in seconds
    <filename> : Path to the file to be sent
    <ack_drop_probability> : Probability to drop received ACK packets
    Example: 'java Sender 9999 127.0.0.1 8888 2 input.txt 0.1'

4. Features
-----------
- Splits the file into packets (max 1000 bytes per packet)
- Sends each packet and waits for ACK (stop-and-wait)
- Simulates packet loss for for DATA and ACK packets based on user-defined Probability
- Handles duplicate packets
- Sends EOT (End Of Transmission) when the file is finished
- Logs all events: send/receive, drop, timeout, retransmit, duplicate, and finish

5. Event Logging
----------------
Each event is logged with a timestamp and relevant packet information, for example:
[1717520000000] [SEND] DATA seq=0, len=1000
[1717520000100] [RECV] ACK ackNum=0
[1717520000200] [DROP] ACK ackNum=0
[1717520000300] [TIMEOUT] DATA seq=0, retransmit...
[1717520000400] [SEND] EOT seq=1

6. Notes
--------
- The program works for both text and binary files (uses ISO_8859_1 encoding for safe byte-to-string conversion).
- Always start the Receiver before the Sender.
- Received files are saved as 'received_file'.

7. Author
---------
- Name: Nguyen Thi Huong Giang - 응웬 티 흐엉 양
- Student ID: 202311987