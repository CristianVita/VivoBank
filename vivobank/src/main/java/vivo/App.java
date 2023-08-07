package vivo;

import javafx.scene.image.Image;
import vivo.entities.User;
import vivo.entities.Wallet;
import vivo.firebase.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * JavaFX App
 * O sa fie fun, haideti cu hype-ul ala rau de tort!
 */
public class App extends Application {

    private static Scene scene;
    public static User curr_user;
    public static List<Wallet> wallets;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login_auth"));
        stage.getIcons().add(new Image("vivo/icon/icon.png"));
        stage.setTitle("VIVO");
        stage.setScene(scene);
        stage.show();
    }

    /** Metoda folosita la schimbarea dintre scene **/
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static FXMLLoader getFXMLLoader(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    }

    public static void main(String[] args) {
        launch();
    }

}