import java.io.DataInputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ReceiveThread extends Thread
{
    TextFlow textflow;

    ReceiveThread(TextFlow textflow)
    {
        this.textflow = textflow;
    }

    @Override
    public void run() 
    {   
        System.out.println("Receive thread is running");
        try
        {
            DataInputStream in = new DataInputStream(ClientController.client.getInputStream());
            while (true) 
            {
                String mess = in.readUTF();
                addMessageToChat(mess);
            }
        } 
        catch (IOException e) 
        {
            System.out.println(e.getMessage());
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
                textflow.getChildren().add(text);
            }
        });
    }
}
