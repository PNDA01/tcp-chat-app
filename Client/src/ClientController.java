
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ClientController
{
    @FXML
    TextField Username;
    @FXML
    TextField Port;
    @FXML
    Pane Window;
    @FXML
    TextField Field;
    @FXML
    Button Send;
    @FXML
    TextFlow Flow;
    @FXML
    ToggleButton Connect;

    static Stage stage;
    static int port;
    static String pseudo;
    static Socket client;

    /**indicates if Client has been connected */
    static boolean connected = false;
    
    /**
     * Setter to pass stage from Main to Controller
     * @param stage stage
     */
    public static void setStage(Stage stage)
    {
        ClientController.stage = stage;
    }

     /**
     * show Interface window
     * @param primaryStage stage
     * @throws IOException
     */
    void ChatInterface(Stage primaryStage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("interface.fxml"));
        Pane window = loader.load();
        Scene scene = new Scene(window);
        primaryStage.setTitle("Chat App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Function triggered by login button
     * @throws UnknownHostException
     * @throws IOException
     */
    public void Login() throws UnknownHostException, IOException
    {
        Double x = Username.getLayoutX();
        pseudo = Username.getText();
        if(pseudo.isEmpty())
        {
            Label error = new Label("Insert valid username.");
            Double y = Username.getLayoutY() + Username.getHeight();
            error.relocate(x, y);
            error.setTextFill(Color.RED);
            Window.getChildren().add(error);
        }
        try 
        {
            port = Integer.parseInt(Port.getText());
            stage.close();
            ChatInterface(stage);
        } 
        catch (Exception e1) 
        {
            Label error = new Label("Insert valid port.");
            Double y = Port.getLayoutY() + Port.getHeight();
            error.relocate(x, y);
            error.setTextFill(Color.RED);
            Window.getChildren().add(error);
        }
    }

    /**
     * Function trigerred by Connect ToggleButton
     */
    public void Toggle(ActionEvent e) throws IOException
    {
        if (Connect.isSelected()) {client = connectClient();}
        else 
        {
            DataOutputStream outs = new DataOutputStream(client.getOutputStream());
            addMessageToChat("Disconnected from Server");
            outs.writeUTF("/d");
        }
    }

    public Socket connectClient() throws UnknownHostException, IOException
    {
        client = new Socket(InetAddress.getLocalHost(), port);
        addMessageToChat("Connection established...");
        DataOutputStream outs = new DataOutputStream(client.getOutputStream());
        outs.writeUTF(pseudo);
        connected = true;
        ReceiveThread receive = new ReceiveThread(Flow);
        receive.start();
        return client;
    }

    /**
     * Function trigerred by Send Button
     */
    public void Send() throws IOException
    {
        if(!connected)
        {
            addMessageToChat("Connect to Server!");
            return;
        }
        DataOutputStream outs = new DataOutputStream(client.getOutputStream());
        String mess = Field.getText();
        if(!mess.isEmpty())
        {
            Field.clear();
            outs.writeUTF(mess);
        }
    }

     /**
     * add message to textFlow
     * @param message message to add
     */
    public void addMessageToChat(String message)
    {
        Platform.runLater(new Runnable() 
        {
            public void run()
            {
                Text text = new Text(message + "\n");
                text.setFont(Font.font("Verdana"));
                Flow.getChildren().add(text);
            }
        });
    }
}
