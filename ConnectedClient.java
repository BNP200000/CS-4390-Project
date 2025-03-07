import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
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
    LocalDateTime startTime; // Start time of client connection
    LocalDateTime endTime; // End time of client connection
    List<String> requestLogs; // Logs from the client to the server
    String name; // Client name

    /**
     * ConnectedClient(Socket socket, int id)
     * 
     * Initialize the connection with the client and set
     * up the input and output streams
     * 
     * @param socket The socket coonection for the client
     * @param id The unique client identifier
     */
    public ConnectedClient(Socket socket, int id, String name) {
        this.clientSocket = socket;
        this.id = id;
        this.name = name;
        requestLogs = new ArrayList<>();
        startTime = LocalDateTime.now();

        try {
            // Log the connection of the client
            System.out.printf("Client [%s]-%d has connected\n", name, id);

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
            System.err.println("ERROR: " + i);
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
    public void sendResponse(double result) {
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
            System.out.printf("Client [%s]-%d has disconnected\n", name, id);
            clientSocket.close();
            in.close();
        } catch(IOException i) {
            System.err.println(i);
            return;
        }
    }

    /**
     * getName()
     * 
     * @return The client's name
     */
    public String getName() {
        return name;
    }

    /**
     * getId()
     * 
     * @return The client's id connected to the server
     */
    public int getId() {
        return id;
    }

    /**
     * getStartTime()
     * 
     * @return The start time when the client connects to the server
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * getDisconnectTime()
     * 
     * @return The end time when the client disconnects to the server
     */
    public LocalDateTime getDisconnectTime() {
        return endTime;
    }

    /**
     * setDisconnectTime(LocalDateTime disconnectTime)
     * 
     * Set the date and time when the client disconnects from
     * the server.
     * 
     * @param disconnectTime The date-time when the client disconnects
     */
    public void setDisconnectTime(LocalDateTime disconnectTime) {
        endTime = disconnectTime;
    }

    /**
     * logRequest(String eq, double result)
     * 
     * Logs every requests sent from the client
     * to the server
     * 
     * @param eq String mathematical input
     * @param result The evaluated result of the equation
     */
    public void logRequest(String eq, double result) {
        String res = String.format("Equation: %s | Result: %.3f", eq, result);
        requestLogs.add(res);
    }

    /**
     * getRequestLog()
     * 
     * @return A list of requests that the client sent to the server
     */
    public List<String> getRequestLog() {
        return requestLogs;
    }
}
