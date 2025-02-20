import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Georg Kunze, m28909, u36363
 * Die Client Klasse ist die Hauptklasse der Anwendung und extendet die Java FX Application.
 * Die Geräteausleih-Anwendung besteht aus einem Anmeldebildschirm und dem Hauptfenster zur Interaktion mit dem
 * Geräteausleih. Der Anmeldebildschrim wird durch die LoginView Klasse aufgerufen.
 */
public class Client extends Application {

    /**
     * Startet die Application
     */
    public void launchApplication() {
        launch();
    }

    /**
     * Startet die Anwendung und initialisiert den Anmeldebildschirm.
     * @param stage Standard Stage
     */
    @Override
    public void start(Stage stage) {
        ClientRestEndpoints rest = new ClientRestEndpoints();

        LoginModel loginModel = new LoginModel();
        LoginView loginView = new LoginView(stage, loginModel, rest);

        loginView.showLoginScene(); //Anzeigen des Anmeldebildschirms
    }
}