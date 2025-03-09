import java.net.*;
import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

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

    List<ConnectedClient> clients; // List of connected clients

    /**
     * Server(int port)
     * 
     * Constructor for the centralized Server class.
     * Initializes the server to listen for incoming client connections.
     * 
     * @param port The port number the server will listen on
     */
    public Server(int port) {
        clients = new ArrayList<ConnectedClient>();
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
            System.err.println("Failed to start server: " + i);
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

        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        String clientName = dis.readUTF();
        if(clientName.trim().isEmpty()) {
            System.err.println("Client name cannot be emptty");
            clientSocket.close();
            return;
        }
        
        // Create a new thread to handle client communication
        new Thread(() -> {
            // Create an instance of ConnectedClient for this specific client
            ConnectedClient client = new ConnectedClient(clientSocket, ++index, clientName);
            clients.add(client);

            // Process client's input and send the result
            handleClientRequest(client);

            // Close the client connection
            client.close();

            synchronized(Server.class) {
                try {
                    logClient();
                } catch (IOException e) {
                    System.out.println("Failed to log client: " + e);
                }
            }
            
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
            System.out.printf("Client [%s]-%d is asking for: %s\n", client.getName(), client.getId(), eq);
            res = evaluateExpression(eq);
            client.sendResponse(res);
            client.logRequest(eq, res);
        }

        client.setDisconnectTime(LocalDateTime.now());
        
    }

    /**
     * logClient()
     * 
     * Write a log of connected clients and their requests
     * to a log file.
     * 
     * @throws IOException Server failed to log the client
     */
    void logClient() throws IOException {
        // Create a /Log directory if it does not exists
        String dirName = "Log";
        String currDir = System.getProperty("user.dir");
        String dirPath = currDir + File.separator + dirName;
        File dir = new File(dirPath);

        if(!dir.exists()) 
            dir.mkdir();


        // Create the log file
        String fName = String.format(
            "log_%s.txt", 
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            )
        );
        File f = new File(dirPath + File.separator + fName);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-d HH:mm:ss");
        
        // Log every connected client to the file
        try(FileWriter fw = new FileWriter(f)) {
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for(ConnectedClient client : clients) {
                Duration session = Duration.between(client.getStartTime(), LocalDateTime.now());
                long minutes = session.toMinutes();
                long seconds = session.getSeconds() % 60;

                String startTime = client.getStartTime().format(df);

                String disconnectTime = client.getDisconnectTime() != null ?
                    client.getDisconnectTime().format(df) :
                    "Still Connected";

                pw.println("==========================================================");
                pw.printf("Client: [%s]-%d\n", client.getName(), client.getId());
                pw.printf("Connected At: %s\n", startTime);
                pw.printf("Session Duration: %d min %d sec\n", minutes, seconds);
                pw.println("Requests:");
                for(String log : client.getRequestLog()) {
                    pw.printf("\t%s\n", log);
                }
                pw.printf("Disconnected at: %s\n", disconnectTime);
                pw.println("==========================================================");
            }

            pw.flush();
        } catch(IOException i) {
            System.err.println("Failed to write file: " + i);
            return;
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