import java.net.*;
import java.io.*;

/**
 * Client.java
 * 
 * Connects to a centralized Server using TCP connection. 
 * 
 * Sends mathematical equations to the Server for it to 
 * solve and respond with the answer.
 */
public class Client {
    Socket s; // Socket object for client connection 
    BufferedReader in; // Input reader for terminal input
    DataOutputStream out; // Output stream to send to the server
    DataInputStream serverIn; // Input stream to receive from the server

    /**
     * Client(String address, int port)
     * 
     * Constructor for the Client class.
     * Initializes the client to send messages to the central server.
     * 
     * @param address The IP address
     * @param port The port number
     * @throws IOException Client failed to connect to the server
     */
    public Client(String address, int port) throws IOException {
        // Attempt to establish a connection
        try {
            // Establish a socket connection to the server
            s = new Socket(address, port); 
            System.out.println("Connected");

            // Set up the input and output streams
            in = new BufferedReader(new InputStreamReader(System.in)); // Client-side input
            out = new DataOutputStream(s.getOutputStream()); // Sending data to the server
            serverIn = new DataInputStream(s.getInputStream()); // Reading server responses
        } catch(UnknownHostException u) {
            // Print error if host is unknown
            System.err.println(u); 
            return;
        } catch(IOException i) {
            // Print error if an IO issue occurs
            System.err.println(i);
            return;
        }

        // Send the equation to the server
        writeToServer(); 

        // Disconnect the client from the server
        close();
    }

    /**
     * writeToServer()
     * 
     * Write a message to the server and receive a 
     * response back.
     * 
     * @throws IOException Message failed to be sent to the server
     */
    void writeToServer() throws IOException {
        String line = "";

        while(!line.equals(Server.STOP)) {
            // Send equation to the server
            System.out.print("Enter equation: ");
            line = in.readLine();
            out.writeUTF(line);

            // Wait for the server's response and print it
            String response = serverIn.readUTF();
            System.out.printf("Server response: %s\n", response);
        }  
    }

    /**
     * close()
     * 
     * Disconnect the client from the central server
     * and close it
     * 
     * @throws IOException Client failed to close
     */
    void close() throws IOException {
        in.close(); // Close the input stream for client-side
        out.close(); // Close the output strem for sending data to the server
        serverIn.close(); // Close thee input stream for receiving data from the server
        s.close(); // Close the socket connection to the server
    }

    public static void main(String[] args) throws IOException {
        new Client("127.0.0.1", Server.PORT);
    }
}
