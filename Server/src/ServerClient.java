
import java.net.Socket;

/**class with caracteristics of one client */
public class ServerClient
{
    String pseudo;
    int ID;
    int port;
    Socket socket;
    
    public ServerClient(String pseudo, final int ID, int port, Socket socket)
    {
        this.pseudo = pseudo;
        this.ID = ID;
        this.port = port;
        this.socket = socket;
    }
}
