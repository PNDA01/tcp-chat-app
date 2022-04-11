import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ServerMain extends Application
{
    /**
     * Show Login window
     * @param primaryStage stage
     * @throws IOException
     */
    void LoginWindow(Stage primaryStage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Pane page = loader.load();
        Scene scene = new Scene(page);
        primaryStage.setTitle("Login Window");
        primaryStage.setScene(scene);
        primaryStage.show();
        ServerController.setStage(primaryStage);
    }

    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        LoginWindow(primaryStage);   
    }
    
    /**lancement de la fenetre*/
    public static void main(String[] args) 
    {
        launch(args);
    }
}

