import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.scene.text.TextFlow;

public class ManageThread extends Thread
{
    boolean running;
    ServerSocket serveur;
    ArrayList<ServerClient> clients;
    TextFlow textflow;

    ManageThread(TextFlow textflow)
    {
        this.running = ServerController.running;
        this.serveur = ServerController.serveur;
        this.clients = ServerController.clients;
        this.textflow = textflow;
    }

    @Override
    public void run() 
    {
        System.out.println("Manage Thread is running");
        int ID = 1;
        while(running)
        {
            try 
            {
                Socket cl = serveur.accept();
                System.out.println("New Connection established...");
                DataInputStream in = new DataInputStream(cl.getInputStream());
                String pseudo = in.readUTF();
                ServerClient client = new ServerClient(pseudo, ID, ServerController.port, cl);
                ID++;
                clients.add(client);
                ServerController.sendToAll(pseudo + " has entered chat", textflow);
                ReceiveThread receive = new ReceiveThread(client, textflow);
                receive.start();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
}
