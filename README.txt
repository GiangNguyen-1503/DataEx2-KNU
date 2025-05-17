UDP File Transder Project - Java Implement
==========================================

1. Requirements
---------------
- Two source files: Receiver.java and Sender.java in the same directory

2. Compilation
--------------
Open a terminal in the project directory and run:
    javac Receiver.java
    javac Sender.java

3. Running the Programs
-----------------------
Step 1: Start the Receiver first
    java Receiver

    The receiver will listen on port 1503 (set in the code).

Step 2: Start the Sender in another terminal
    java Sender <receiver_host> <receiver_port> <file_path>

    - <receiver_host>: IP address of the Receiver (use 127.0.0.1 if on the same machine)
    - <receiver_port>: Port number of the Receiver (default is 1503)
    - <file_path>: Path to the file you want to send

    Example:
        java Sender 127.0.0.1 1503 Web-LEGO.txt

4. Result
---------
- After successful transfer, a new file named received_<original_filename> will appear in the directory.
- The content of the received file should match the original file.

5. Notes
--------
- Always start the Receiver before the Sender.
- If you encounter errors, check the IP address, port, and file name.
- If running on different machines, ensure that UDP port 1503 is not blocked by a firewall.

6. Author
---------
- Name: Nguyen Thi Huong Giang - 응웬 티 흐엉 양
- Student ID: 202311987