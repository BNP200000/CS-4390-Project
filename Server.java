import java.net.*;
import java.io.*;

/**
 * Server.java
 * 
 * A centralized server that any client can connect to, using a TCP
 * connection.
 * 
 * It's primary function is to calculate simple math equations given by
 * the client and deliver the result back to them.
 */
public class Server {
    Socket s; // Socket for handling communication with a client
    ServerSocket serverSocket; // ServerSocket to listen for incoming client connections
    DataInputStream in; // Input stream for receiving data from the client
    DataOutputStream out; // Output stream for sending data to the client
    int index; // Keeps track of the client connection index

    public static final int PORT = 5000; // Port number to connect to
    public static final String STOP = "#"; // Stopping string

    /**
     * Server(int port)
     * 
     * Constructor for the centralized Server class.
     * Initializes the server to listen for incoming client connections.
     * 
     * @param port The port number the server will listen on
     */
    public Server(int port) {
        index = 0; // Initialize the current client connection index

        // Run the server and wait for a client to connect
        try {
            // Set up the server on the specified port
            serverSocket = new ServerSocket(port);
            System.out.printf("Server started on port %d\n", port);
            
            // Indicate that the server is waiting for a client connection
            System.out.println("Waiting for a client...");

            // Handle incoming client connections
            while(true) 
                initConnection();
        } catch(IOException i) {
            System.err.println(i);
            return;
        }
    }

    /**
     * initConnection()
     * 
     * Initiate client connection and handles server-side logic.  
     * Listens for incoming client connections and spawns a new
     * thread to handle specific client communication
     * 
     * @throws IOException Something went wrong on the client side
     */
    void initConnection() throws IOException {
        // Accept a new client connection
        Socket clientSocket = serverSocket.accept();
        
        // End function if client could not be connected successfully
        if(!clientSocket.isConnected()) return;
        
        // Create a new thread to handle client communication
        new Thread(() -> {
            // Create an instance of ConnectedClient for this specific client
            ConnectedClient client = new ConnectedClient(clientSocket, ++index);
            
            // Process client's input and send the result
            handleClientRequest(client);

            // Close the client connection
            client.close();
        }).start(); // Start the new thread for the client
    }

    /**
     * handleClientRequest(ConnectedClient client)    
     * 
     * Handles mathematical equation received from the client.
     * Calls the evaluate method to compute the result and
     * sends it back to the client.
     * 
     * @param client The ConnectedClient instance representing the connected client
     */
    void handleClientRequest(ConnectedClient client) {
        String eq;
        double res;

        while(!(eq = client.read()).equals(STOP)) {
            System.out.printf("Client %d is asking for: %s\n", client.getId(), eq);
            res = evaluateExpression(eq);
            client.sendRespone(res);
        }
    }

    /**
     * evaluateExpression(String expr)
     * 
     * Evaluates a given mathematical expression
     * 
     * @param expr The mathematical expression to be evaluated
     * @return The result of the evaluation
     */
    double evaluateExpression(String expr) {
        Infix in = new Infix();
        return in.evaluate(expr);
    }

    public static void main(String[] args) {
        new Server(PORT);
    }
}