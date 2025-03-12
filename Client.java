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
    String user; // The user trying to connect to the server

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
    public Client(String address, String name) throws IOException {
        this(address, 5000, name);
    }

    public Client(String address, int port, String name) throws IOException {
        // Attempt to establish a connection
        try {
            // Establish a socket connection to the server
            s = new Socket(address, port); 
            System.out.printf("Connected to %d\n", port);

            // Set up the input and output streams
            in = new BufferedReader(new InputStreamReader(System.in)); // Client-side input
            out = new DataOutputStream(s.getOutputStream()); // Sending data to the server
            serverIn = new DataInputStream(s.getInputStream()); // Reading server responses

        } catch(UnknownHostException u) {
            // Print error if host is unknown
            System.err.println("Failed to connect to host: " + u); 
            return;
        } catch(IOException i) {
            // Print error if an IO issue occurs
            System.err.println("Failed to connect I/O streams: " + i);
            return;
        }

        // Send the client name to the server
        out.writeUTF(name);

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

        while(!line.equals("#")) {
            // Send equation to the server
            System.out.print("Enter equation (# to close): ");
            line = in.readLine();

            if(line.equals("#")) {
                out.writeUTF(line);
                break;
            }
            
            // Write the message to the server
            out.writeUTF(line);

            // Wait for the server's response and print it
            String response = serverIn.readUTF();
            
            // If server receives a maligned input from client, simply
            // output an error message
            if(response.equals(String.valueOf(Double.NaN)))
                response = "Error parsing expression";
                
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
        System.out.println("CLOSE");
        in.close(); // Close the input stream for client-side
        out.close(); // Close the output strem for sending data to the server
        serverIn.close(); // Close thee input stream for receiving data from the server
        s.close(); // Close the socket connection to the server
    }

    /**
     * getIPAddress
     * 
     * @return Get the local IP address of the client
     */
    static String getIPAddress() {
        try {
            InetAddress inet = InetAddress.getLocalHost();
            return inet.getHostAddress();
        } catch(UnknownHostException u) {
            System.err.println(u);
            return "127.0.0.1";
        }
    }

    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            System.err.println("java Client <IP Address> <Name>");
            System.exit(-1);
        }

        String ip = args[0];
        String name = args[1];
        new Client(ip, name);       
    }
}
