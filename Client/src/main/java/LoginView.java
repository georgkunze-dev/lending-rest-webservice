import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author Georg Kunze, m28909, u36363
 * View des MVC-Entwurfsmusters, welches die Benutzeroberfläche des Anmeldebildschirms zur
 * Geräteausleih-Anwendung implementiert
 */
public class LoginView {
    private final Stage stage;
    private Scene scene;
    private final LoginModel loginModel; //Modell für die Speicherung der Daten vom Benutzer
    private final LoginController controller; //Controller für das Verarbeiten der Button-Events

    /**
     * Konstruktor der LoginView Klasse.
     * Initialisiert die Variablen und ruft die Initialisierung der Benutzeroberfläche auf
     * @param stage JavaFX Stage
     * @param loginModel Modell für den Benutzer
     * @param rest Rest Endpunkt des Klienten
     */
    public LoginView(Stage stage, LoginModel loginModel, ClientRestEndpoints rest) {
        this.stage = stage;
        this.loginModel = loginModel;

        controller = new LoginController(stage, loginModel, rest);

        initializeView();
    }

    /**
     * Initialisieren der UI-Komponenten für den Anmeldebildschirm.
     * Der Anmeldebildschirm enthält die Auswahl des Benutzertyps und die Eingabemöglichkeit des
     * Benutzernamens an und ermöglicht die Anmeldung bei der Geräteausleih-Anwendung.
     */
    private void initializeView() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Überschrift
        Label headlineLabel = new Label("Anmeldung");
        headlineLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        GridPane.setConstraints(headlineLabel, 0, 0);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: black;");
        GridPane.setConstraints(separator, 0, 1, 2, 1);

        // Bereich zur Auswahl des Benutzertypen (Normal oder Admin)
        Label userTypeLabel = new Label("Benutzertyp:");
        GridPane.setConstraints(userTypeLabel, 0, 2);

        ChoiceBox<String> userTypeChoice = new ChoiceBox<>();
        userTypeChoice.getItems().addAll("Normaler Nutzer", "Admin");
        userTypeChoice.setValue("Normaler Nutzer");
        GridPane.setConstraints(userTypeChoice, 1, 2);

        // Bereich zum Eingeben des Benutzernamens
        Label usernameLabel = new Label("Benutzername:");
        GridPane.setConstraints(usernameLabel, 0, 3);

        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 3);

        Button loginButton = new Button("Anmelden");
        loginButton.setOnAction(e -> {
            loginModel.setUsername(usernameInput.getText());
            loginModel.setUsertype(userTypeChoice.getValue());
            controller.handleLoginButton();
        });
        GridPane.setConstraints(loginButton, 1, 4);

        grid.getChildren().addAll(headlineLabel, separator, userTypeLabel, userTypeChoice, usernameLabel, usernameInput, loginButton);

        scene = new Scene(grid, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        showLoginScene();
    }

    /**
     * Anzeigen der Benutzeroberfläche des Anmeldebildschirms
     */
    public void showLoginScene() {
        String csspath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm(); // Pfad zur css Datei (setzt die Schriftart auf "Helvetica" und die Schriftgröße auf 16)
        scene.getStylesheets().add(csspath);
        stage.setScene(scene);
        stage.show();

        //Ermitteln der Bildschirmgröße zur Zentrierung auf dem Bildschirm
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getVisualBounds();
        double screenWidth = screenBounds.getWidth(); // Breite des Bildschirms
        double screenHeight = screenBounds.getHeight(); // Höhe des Bildschirms

        // Szene in die Mitte des Bildschirms setzen
        stage.setX((screenWidth - stage.getWidth()) / 2);
        stage.setY((screenHeight - stage.getHeight()) / 2);

        stage.setTitle("Anmeldung");
    }
}
