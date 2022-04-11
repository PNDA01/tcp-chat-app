import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ServerController
{
    static int port;
    static InetAddress ip;
    static ServerSocket serveur;
    static Date serverStartTime;

    /**indicates if Server has started */
    static boolean running = false;
    static Stage primaryStage;

    @FXML
    TextField Port;
    @FXML
    Pane Window;
    @FXML
    TextField Field;
    @FXML
    TextFlow Flow;
    @FXML
    ToggleButton Start;

    /**ArrayList of all connected clients */
    public static ArrayList<ServerClient> clients = new ArrayList<>();

    /**
     * Setter to pass stage from Main to Controller
     * @param stage stage
     */
    public static void setStage(Stage stage){primaryStage = stage;}

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
        primaryStage.setTitle("Server App");
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
        Double x = Port.getLayoutX();
        try {ServerController.port = Integer.parseInt(Port.getText());} 
        catch (Exception ex) 
        {
            Label error = new Label("Insert valid port.");
            Double y = Port.getLayoutY() + Port.getHeight();
            error.relocate(x, y);
            error.setTextFill(Color.RED);
            Window.getChildren().add(error);
        }
        if(port == 0)
        {
            Label error = new Label("Insert valid port.");
            Double y = Port.getLayoutY() + Port.getHeight();
            error.relocate(x, y);
            error.setTextFill(Color.RED);
            Window.getChildren().add(error);
        }
        else
        {
            primaryStage.close();
            ChatInterface(primaryStage);
        }
    }

    /**
     * Function trigerred by Start ToggleButton
     */
    public void Toggle()
    {
        if (Start.isSelected()) {StartServer();}
        else QuitServer();
    }

    public void StartServer()
    {
        try
        {
            serveur = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        } 
        
        try 
        {
            ip = InetAddress.getLocalHost();
            addMessageToChat("Ip of server: " + ip.getHostAddress(), Flow);    
        }
        catch (Exception ex) 
        {
            System.out.println(ex.getMessage());
        }

        running = true;
        serverStartTime = new Date(System.currentTimeMillis());
        addMessageToChat("Server Start Time: " + serverStartTime.toString() + "\nServer Port: " + port, Flow);
        ManageThread manage = new ManageThread(Flow);
        manage.start();
    }

    public void QuitServer() 
    {
        sendToAll("Server Down !", Flow);
        for (int i = 0; i < clients.size(); i++) 
        {
            disconnect(clients.get(i).ID);
        }
        running = false;
        try 
        {
            serveur.close();
            System.exit(0);
        }
        catch (Exception ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Function trigerred by Send Button
     */
    public void Action()
    {    
        String text = Field.getText();
        Field.clear();
        if(!running){addMessageToChat("Start Server!", Flow);}
        if(text.isEmpty() || !running){return;} //if text is empty or if server hasn't started
        if (!text.startsWith("/")) 
        {
            sendToAll("Server : " + text, Flow);
        }
        text = text.substring(1);
        if (text.startsWith("online")) 
        {
            addMessageToChat("_________________________", Flow);
            for (int i = 0; i < clients.size(); i++) 
            {
                ServerClient client = clients.get(i);
                addMessageToChat("ID : " + client.ID + " UserName : " + client.pseudo, Flow);
            }
            addMessageToChat("|| Nb of Clients : " + clients.size(), Flow);
            addMessageToChat("_________________________", Flow);
        } 
        else if (text.startsWith("time")) 
        {
            Date date = new java.util.Date(System.currentTimeMillis());
            addMessageToChat(date.toString(), Flow);
        } 
        else if (text.startsWith("kick")) 
        {
            String clientName = text.split(" ")[1];
            boolean num = true;
            int id = 0;
            try 
            {
                id = Integer.parseInt(clientName);
            } 
            catch (Exception ex) 
            {
                num = false;
            }

            if (num)
            {
                for (int i = 0; i < clients.size(); i++) 
                {
                    ServerClient c = clients.get(i);
                    if (id == c.ID)
                    {
                        sendToAll("Client " + id + " has been kicked by Server", Flow);
                        disconnect(id);
                        return;
                    } 
                }
                addMessageToChat("Client does not exists.", Flow);
            }
            else 
            {
                for (int i = 0; i < clients.size(); i++) 
                {
                    ServerClient c = clients.get(i);
                    if (clientName.toLowerCase().equals(c.pseudo.toLowerCase())) 
                    {
                        sendToAll("Client " + c.pseudo + " has been kicked by Server", Flow);
                        disconnect(c.ID);
                        return;
                    }   
                }
                addMessageToChat("Client does not exists.", Flow);
            }
        }
        else if (text.startsWith("help")) 
        {
            addMessageToChat("__________________________________________________________________", Flow);
            addMessageToChat("/online -> View online connected clients", Flow);
            addMessageToChat("/kick [username or userID] => To kick particular client", Flow);
            addMessageToChat("/help -> help menu", Flow);
            addMessageToChat("__________________________________________________________________", Flow);
        } 
    }  

    /**
     * Send Thread
     * @param mess message to send
     * @param client receiver of message
     */
    private static void send(String mess, ServerClient client) 
    {
        Thread send = new Thread("Server Send Thread") 
        {
            public void run() 
            {
                try 
                {
                    DataOutputStream out = new DataOutputStream(client.socket.getOutputStream());
                    out.writeUTF(mess);
                } 
                catch (IOException e) 
                {
                    System.out.println(e.getMessage());
                }
            }
        };
        send.start();
    }

    public static void sendToAll(String mess, TextFlow textflow)
    {
        addMessageToChat(mess, textflow);
        for(ServerClient cl: clients)
        {
            send(mess, cl);
        }
    }

    /**
     * Disconnect client of ID "id"
     * @param id ID of client
     */
    public static void disconnect(int id) 
    {
        ServerClient c;
        for (int i = 0; i < clients.size(); i++) 
        {
            if (clients.get(i).ID == id) 
            {
                c = clients.get(i);
                clients.remove(i);
                try 
                {
                    c.socket.close();
                }
                catch (IOException e) 
                {
                    System.out.println(e.getMessage());
                }
                break;
            }
        }
    }

    /**
     * add message to textFlow
     * @param message message to add
     * @param textflow textflow where message is added
     */
    public static void addMessageToChat(String message, TextFlow textflow)
    {
        Platform.runLater(()->
        {
            Text text = new Text(message + "\n");
            text.setFont(Font.font("Verdana"));
            textflow.getChildren().add(text);
        });
    }
}