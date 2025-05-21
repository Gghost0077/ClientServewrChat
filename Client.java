import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Scanner;


public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() { // used to send messages to client handler
         
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in); //allows us to get input from the console 
            while (socket.isConnected()) { // gets what the user types in and sends it over
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        
        }
        catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


        public void listenForMessage() { // lsitens to messages that are broadcasted to the group chat
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String msgFromGroupChat;

                    while (socket.isConnected()) {
                        try {
                            msgFromGroupChat = bufferedReader.readLine();
                            System.out.println(msgFromGroupChat);
                        } catch (IOException e) {
                            closeEverything(socket, bufferedReader, bufferedWriter);                        
                        }
                    }
                }
            }).start();

        }

        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) { // close everthign class that will bbe used in most of the classes to allow us to close everything 
            try {
                if (socket != null) {
                    socket.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public static void main(String[] args) throws IOException { // main method that runs everything 

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234); // local host due to using our own computer
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.sendMessage();    
        }
            

}
