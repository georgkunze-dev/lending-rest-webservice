import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author Georg Kunze, m28909, u36363
 * Zeigt ein Pop-up-Fenster mit übergebenem Titel und Nachricht an
 */
public class Message {
    // Pfad zur css Datei (setzt die Schriftart auf "Helvetica" und die Schriftgröße auf 16)
    private final String CSSPATH = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();

    /**
     * Standard Konstruktor der Klasse Message
     */
    public Message() {
    }

    /**
     * Zeigt ein Pop-up-Fenster an.
     * Bei einer Fehlernachricht werden zusätzlich zwei rote Separator angezeigt
     * @param title Titel des Fensters
     * @param message Nachricht im Fenster
     */
    public void show(String title, String message) {
        Stage stage = new Stage();
        stage.setTitle(title);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Zusätzliches anzeigen von 2 roten Separator, wenn eine Fehlermeldung vorliegt
        if (title.toLowerCase().contains("fehler")) {
            Separator separator1 = new Separator();
            separator1.setStyle("-fx-background-color: red;");

            Label messageLabel = new Label(message);

            Separator separator2 = new Separator();
            separator2.setStyle("-fx-background-color: red;");

            vbox.getChildren().addAll(separator1, messageLabel, separator2);
        } else {
            Label messageLabel = new Label(message);
            vbox.getChildren().add(messageLabel);
        }

        Button closeButton = new Button("Schlie\u00DFen");
        closeButton.setOnAction(e -> stage.close());

        vbox.getChildren().add(closeButton);

        Scene scene = new Scene(vbox, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        scene.getStylesheets().add(CSSPATH);
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
    }
}
