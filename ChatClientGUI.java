import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ChatClientGUI extends Application {
    private TextArea chatArea;
    private TextField inputField;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    @Override
    public void start(Stage primaryStage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);

        inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setOnAction(e -> sendMessage());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(10, inputField, sendButton);
        BorderPane root = new BorderPane(chatArea, null, null, inputBox, null);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Chat Client");
        primaryStage.show();

        connectToServer("localhost", 1234); // Adjust as needed
    }

    private void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> chatArea.appendText(finalLine + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

        } catch (IOException e) {
            chatArea.appendText("Could not connect to server.\n");
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            inputField.clear();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (socket != null) socket.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}