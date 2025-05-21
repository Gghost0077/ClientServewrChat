import java.io.BufferedReader;   
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;



public class ClientHandler implements Runnable{

// Responisble for tracking clinets messages and sending messages to multiple clients
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket; // socekt passed thorough our server class, resopnsible for stablishign connection between the serverand client 
    private BufferedReader bufferedReader; // reads the input stream from the client
    private BufferedWriter bufferedWriter; // writes the output stream to the client
    private String clientUsername; // stores the username of the client to represent them 

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //stream to send things
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // stream to read things
            this.clientUsername = bufferedReader.readLine(); // read the username from the client
            clientHandlers.add(this); // add this client to the list of the array of clientHandlers 
            broadcastMessage("SERVER: " + clientUsername + " has joined the chat"); // broadcast the message to all clients to notify someone has joined the chat 
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }


    }

    // listens for messages from the client on a seperate thread
    @Override 
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); // program will halt until a message is recieved from client
                broadcastMessage(messageFromClient); // broadcast the message to all clients
            } catch (IOException e) { // input output exceptions
                closeEverything(socket, bufferedReader, bufferedWriter);
                break; // once client disconnects the loop will break
            }
           
        }
    }   


    public void broadcastMessage(String messageToSend) { // loops thorugh array lsit of clients and sends message to each client
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) { // makes sure the message is not sent to the client who sent it
                    clientHandler.bufferedWriter.write(messageToSend); // 
                    clientHandler.bufferedWriter.newLine(); // pressing enter key
                    clientHandler.bufferedWriter.flush(); // flushes the buffer
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() { // signal to show that a client has left the chat
        clientHandlers.remove(this); // rmeoves current client handler from the array list
        broadcastMessage( "SERVER: " + clientUsername + " has left the chat"); // broadcast the message to all clients to sginal someone has left the chat
    }

    public void closeEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) { // closes the socket, buffered reader and buffered writer
        removeClientHandler(); // calls the removeClientHandler method as well as closing the socket, buffered reader and buffered writer using try catch block
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
