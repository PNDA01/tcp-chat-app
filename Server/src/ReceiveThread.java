import java.io.DataInputStream;
import java.io.IOException;

import javafx.scene.text.TextFlow;

public class ReceiveThread extends Thread
{
    boolean running;
    ServerClient client;
    TextFlow textflow;

    ReceiveThread(ServerClient client, TextFlow textflow)
    {
        this.running = ServerController.running;
        this.client = client;
        this.textflow = textflow;
    }

    @Override
    public void run() 
    {    
        System.out.println("Receive Thread is running");
        try 
        {
            DataInputStream in = new DataInputStream(client.socket.getInputStream());
            while (running) 
            {
                String mess = in.readUTF();
                if (mess.startsWith("/d"))
                {
                    ServerController.disconnect(client.ID);
                }
                else 
                {
                    ServerController.sendToAll(client.pseudo + " : " + mess, textflow);
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
