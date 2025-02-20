import javafx.stage.Stage;

/**
 * @author Georg Kunze, m28909, u36363
 * Kontroller für die OnAction-Events der Button in dem Anmeldebildschirm
 */
public class LoginController {
    Stage stage;
    private final LoginModel loginModel; //Modell für die Speicherung der Daten vom Benutzer
    private final ClientRestEndpoints rest; //Rest Endpunkt des Klienten
    private Message message; //Klasse zum Anzeigen einer Pop-up-Nachricht

    /**
     * Konstruktor der Klasse LoginController.
     * Initialisiert die Variablen und ruft die Initialisierung des Kontrollers auf.
     * @param stage JavaFX Stage
     * @param loginModel Modell für den Benutzer
     * @param rest Rest Endpunkt des Klienten
     */
    public LoginController(Stage stage, LoginModel loginModel, ClientRestEndpoints rest) {
        this.stage = stage;
        this.loginModel = loginModel;
        this.rest = rest;
        initializeController();
    }

    /**
     * Initialisiert weitere Variablen für den Kontroller
     */
    private void initializeController() {
        message = new Message();
    }

    /**
     * Event-Handler für den Login-Button zum Anmelden beim Server und Anzeigen der Benutzeroberfläche
     * der Geräteausleih-Anwendung
     */
    public void handleLoginButton() {
        String username = loginModel.getUsername();

        if (!username.isEmpty()) {
            boolean loginSuccessful = rest.postUsername(username); //Benutzer mit Benutzernamen am Server und in der Datenbank registrieren
            if (loginSuccessful) {
                LendingModel lendingModel = new LendingModel();
                LendingView lendingView = new LendingView(stage, loginModel, lendingModel, rest);
                lendingView.showLibraryScene(); //Anzeigen des Hauptfensters der Geräteausleih-Anwendung
            } else message.show("Login Fehler", "Anmeldung fehlgeschlagen. Keine Verbindung zum Server! Bitte Server starten!");
        } else {
            message.show("Fehler", "Bitte einen Benutzernamen eingeben!");
        }
    }
}
