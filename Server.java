import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private ServerSocket serverSocket; // object resposible for listening for incoming connnections or clients

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // starts server and resposible for running it
    public void startServer() {

        try { //while loop keeps it runnning until server is closed

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept(); // accept methhod is a blocking method where the process is halted unitl a client conenects
                System.out.println("A new Client connected"); // prints out a message when a client connects
                ClientHandler clientHandler = new ClientHandler(socket); // each object of this class is responisble for communicating with a client and implemtents the runnable interface
                                                                         // the runnable is implemented on a class, whos instances will be executed by a thread which allows us to hadndle multiple clients 

                Thread thread = new Thread(clientHandler); // creates a new thread object and passes the clientHandler object to it creatiung an 
                thread.start();
            }
        
        } catch (IOException e) {

        }

    }

    public void closeServerSocket() { // method to close server socket if an error comes up
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException { // main method to start the server
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

    }
}