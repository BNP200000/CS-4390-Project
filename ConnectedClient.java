import java.net.*;
import java.io.*;

/**
 * ConnectedClient.java
 * 
 * Represents a client connected to the server. It basically
 * functions as the middleware between the client and server.
 * 
 * Responsible for handiling client interaction, such as:
 * - Receiving mathematical expressions,
 * - Calculating the result,
 * - Sending the result back to the client
 */
public class ConnectedClient {
    Socket clientSocket; // Socket to manage connection with the client
    DataInputStream in; // Input stream to read data from the client
    DataOutputStream out; // Output stream to send response to the client
    int id; // Unique identifier for the client
    Infix expr; // Infix object for evaluating mathematical expression

    /**
     * ConnectedClient(Socket socket, int id)
     * 
     * Initialize the connection with the client and set
     * up the input and output streams
     * 
     * @param socket The socket coonection for the client
     * @param id The unique client identifier
     */
    public ConnectedClient(Socket socket, int id) {
        this.clientSocket = socket;
        this.id = id;
        expr = new Infix(); 

        try {
            // Log the connection of the client
            System.out.printf("Client %d has connected\n", id);

            // Set up the I/O streams
            in = new DataInputStream(
                new BufferedInputStream(clientSocket.getInputStream())
            );
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch(IOException i) {
            // Print an error if there is a problem with the connection setup
            System.err.println(i);
            return;
        }
    }

    /**
     * read()
     * 
     * Reads a mathematical expression from the client, evaluates it,
     * and sends the result back to the client.
     * 
     * @return The mathematical expression from the client
     */
    public String read() {
        try {
            return in.readUTF();
        } catch(IOException i) {
            System.err.println(i);
            return "";
        }
    }

    /**
     * sendResponse()
     * 
     * Sends the result of the evaluation bacck to the client
     * 
     * @param result The result to send back to the client
     */
    public void sendRespone(double result) {
        try {
            out.writeUTF(String.valueOf(result));
        } catch(IOException i) {
            System.err.println(i);
            return;
        }
    }

    /**
     * close()
     * 
     * Closes the client's socket and input stream.
     * 
     * Called when the client disconnects or the
     * server is done interacting with the client.
     */
    public void close() {
        try {
            clientSocket.close();
            in.close();
        } catch(IOException i) {
            System.err.println(i);
            return;
        }
    }

    /**
     * getId()
     * 
     * @return The client's id connected to the server
     */
    public int getId() {
        return id;
    }
}
