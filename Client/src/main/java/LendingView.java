import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Georg Kunze, m28909, u36363
 * View des MVC-Entwurfsmusters, welches die Benutzeroberfläche der Geräteausleih-Anwendung implementiert.
 * Die Benutzeroberfläche hat die Funktionen der Gerätesuche, Ausleihe/Rückgabe eines Gerätes
 * und die Admin-Funktionen wie Geräte zu bearbeiten und hinzuzufügen.
 */
public class LendingView {
    private final Stage stage;
    private Scene scene;
    private final LoginModel loginModel; //Modell für die Speicherung der Daten vom Benutzer
    private final LendingModel lendingModel; //Modell für die Speicherung der Daten der Geräteausleih-Anwendung
    private final LendingController controller; //Controller für das Verarbeiten der Button-Events
    private final ClientRestEndpoints rest; //Rest Endpunkt des Klienten
    private final double screenWidth; //Breite des Bildschirms
    private final double screenHeight; //Höhe des Bildschirms
    private final String CSSPATH = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm(); // Pfad zur css Datei (setzt die Schriftart auf "Helvetica" und die Schriftgröße auf 16)

    /**
     * Konstruktor der Klasse LendingView.
     * Initialisiert die Variablen und ruft die Initialisierung der Benutzeroberfläche auf
     * @param stage übergebene Stage
     * @param loginModel Modell für die Speicherung der Daten vom Benutzer
     * @param lendingModel Modell für die Speicherung der Daten der Geräteausleih-Anwendung
     * @param rest Rest Endpunkt des Klienten
     */
    public LendingView(Stage stage, LoginModel loginModel, LendingModel lendingModel, ClientRestEndpoints rest) {
        this.stage = stage;
        this.loginModel = loginModel;
        this.lendingModel = lendingModel;
        this.rest = rest;

        controller = new LendingController(loginModel, lendingModel, this, rest);
        //Ermitteln der Bildschirmgröße zur Zentrierung auf dem Bildschirm
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getVisualBounds();
        //Breite des Bildschirms
        screenWidth = screenBounds.getWidth();
        //Höhe des Bildschirms
        screenHeight = screenBounds.getHeight();

        //loginView = new LoginView(stage, loginModel, rest);
        initializeView();
    }

    /**
     * Initialisieren der UI-Komponenten für den Hauptbildschirm der Geräteausleih-Anwendung.
     * Der Hauptbildschirm enthält die Funktionen der Gerätesuche, Ausleihe/Rückgabe eines Gerätes
     * und die Admin-Funktionen wie Geräte zu bearbeiten und hinzuzufügen.
     */
    private void initializeView() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Überschrift
        Label headlineLabel = new Label("Ger\u00E4teausleih-Anwendung");
        headlineLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        GridPane.setConstraints(headlineLabel, 0, 0);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #000000;");
        GridPane.setConstraints(separator, 0, 1, 4, 1);

        grid.getChildren().addAll(headlineLabel, separator);

        // Bereich zur Gerätesuche
        Label searchLabel = new Label("Ger\u00E4te suchen nach:");
        GridPane.setConstraints(searchLabel, 0, 2);

        ChoiceBox<SearchCriteria> searchCriteria = new ChoiceBox<>();
        searchCriteria.getItems().addAll(SearchCriteria.values());
        searchCriteria.setValue(SearchCriteria.BRAND);
        searchCriteria.setMinWidth(220);
        GridPane.setConstraints(searchCriteria, 1, 2);

        Label tooltipLabel = new Label("?");
        tooltipLabel.setStyle("-fx-text-fill: white; -fx-border-radius: 100; -fx-border-color: white; -fx-padding: 0 9; -fx-background-color: #1752be; -fx-background-radius: 100;");
        GridPane.setConstraints(tooltipLabel, 2, 2);
        Tooltip tooltip = new Tooltip("""
                Bitte ein Suchkriterien in der Auswahl ausw\u00E4hlen
                und entsprechend nach diesem Kriterium suchen.\n
                Wenn im Textfeld nichts eingegeben wurde, werden
                alle zum Ausleihen verf\u00FCgbaren Ger\u00E4te angezeigt.
                """);
        Tooltip.install(tooltipLabel, tooltip);

        TextField searchInput = new TextField();
        GridPane.setColumnSpan(searchInput, 3);
        GridPane.setConstraints(searchInput, 0, 3, 3, 1);

        ListView<String> searchResults = new ListView<>();
        searchResults.setMinHeight(100);
        GridPane.setConstraints(searchResults, 0, 4, 4, 1);

        Button searchButton = new Button("Suchen");
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setOnAction(e -> {
            lendingModel.setSearchCriteria(searchCriteria.getValue());
            lendingModel.setSearch(searchInput.getText());
            controller.handleSearchButton();
            searchResults.setItems(lendingModel.getObservableList()); // Anzeigen der ObservableList
        });
        GridPane.setConstraints(searchButton, 3, 3);

        grid.getChildren().addAll(searchLabel, searchCriteria, tooltipLabel, searchInput, searchButton, searchResults);

        // Ausleih/Rückgabe-Bereich
        Label transactionHeadlineLabel = new Label("Ger\u00E4t ausleihen oder zur\u00FCckgeben:");
        GridPane.setConstraints(transactionHeadlineLabel, 0, 5);

        Label tooltipLabel2 = new Label("?");
        tooltipLabel2.setStyle("-fx-text-fill: white; -fx-border-radius: 100; -fx-border-color: white; -fx-padding: 0 9; -fx-background-color: #1752be; -fx-background-radius: 100; ");
        GridPane.setConstraints(tooltipLabel2, 1, 5);
        Tooltip tooltip2 = new Tooltip("Bitte die ID des Ger\u00E4tes eingeben");
        Tooltip.install(tooltipLabel2, tooltip2);

        GridPane.setMargin(transactionHeadlineLabel, new Insets(50, 0, 0, 0));
        GridPane.setMargin(tooltipLabel2, new Insets(50, 0, 0, 0));

        Label transactionLabel = new Label("Ger\u00E4t-ID:");
        GridPane.setConstraints(transactionLabel, 0, 6);

        TextField transactionInput = new TextField();
        GridPane.setConstraints(transactionInput, 0, 6, 2, 1);

        Button borrowButton = new Button("Ausleihen");
        borrowButton.setMaxWidth(Double.MAX_VALUE);
        borrowButton.setOnAction(e -> {
            lendingModel.setId(transactionInput.getText());
            boolean successful = controller.handleBorrowButton();
            if (successful) transactionInput.clear();
        });
        GridPane.setConstraints(borrowButton, 2, 6);

        Button returnButton = new Button("Zur\u00FCckgeben");
        returnButton.setMaxWidth(Double.MAX_VALUE);
        returnButton.setOnAction(e -> {
            lendingModel.setId(transactionInput.getText());
            boolean successful = controller.handleReturnButton();
            if (successful) transactionInput.clear();
        });
        GridPane.setConstraints(returnButton, 3, 6);

        grid.getChildren().addAll(transactionHeadlineLabel, tooltipLabel2, transactionLabel, transactionInput, borrowButton, returnButton);

        // Anzeige der ausgeliehenen Geräte
        Button showBorrowedDevicesButton = new Button("Ausgeliehene Ger\u00E4te anzeigen");
        showBorrowedDevicesButton.setMaxWidth(Double.MAX_VALUE);
        showBorrowedDevicesButton.setOnAction(e -> showNewWindow(Action.SHOWBORROWEDDEVICES));
        GridPane.setConstraints(showBorrowedDevicesButton, 0, 7, 2, 1);

        // Button um zur Anmeldung zurückzukommen
        Button backToLoginButton = new Button("zur\u00FCck zum Login");
        backToLoginButton.setMaxWidth(Double.MAX_VALUE);
        backToLoginButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, loginModel, rest);
            loginView.showLoginScene();
        });
        GridPane.setConstraints(backToLoginButton, 2, 7, 3, 1);

        GridPane.setMargin(showBorrowedDevicesButton, new Insets(50, 0, 0, 0));
        GridPane.setMargin(backToLoginButton, new Insets(50, 0, 0, 0));

        grid.getChildren().addAll(showBorrowedDevicesButton, backToLoginButton);

        // Admin-Bereich, wenn der ausgewählte userType "Admin" ist
        if (Objects.equals(loginModel.getUsertype(), "Admin")) {
            // Überschrift
            Separator adminSeparator = new Separator();
            adminSeparator.setStyle("-fx-background-color: #000000;");
            GridPane.setConstraints(adminSeparator, 0, 8, 4, 1);

            Label adminHeadlineLabel = new Label("Admin-Funktionen:");
            adminHeadlineLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
            GridPane.setConstraints(adminHeadlineLabel, 0, 9);

            // Button um ein bestimmtes Gerät zu editieren
            Button editDeviceButton = new Button("Ger\u00E4t bearbeiten");
            editDeviceButton.setMaxWidth(Double.MAX_VALUE);
            editDeviceButton.setOnAction(e -> showNewWindow(Action.EDIT));
            GridPane.setConstraints(editDeviceButton, 2, 9);

            // Button um ein Gerät hinzuzufügen
            Button addDeviceButton = new Button("Ger\u00E4t hinzuf\u00FCgen");
            addDeviceButton.setMaxWidth(Double.MAX_VALUE);
            addDeviceButton.setOnAction(e -> showNewWindow(Action.ADD));
            GridPane.setConstraints(addDeviceButton, 3, 9);

            grid.getChildren().addAll(adminSeparator, adminHeadlineLabel, editDeviceButton, addDeviceButton);
        }

        scene = new Scene(grid, Region.USE_COMPUTED_SIZE, 610);
    }

    /**
     * Anzeigen der Benutzeroberfläche des Hauptbildschirms
     */
    public void showLibraryScene() {
        scene.getStylesheets().add(CSSPATH);
        stage.setScene(scene);

        // Szene in die Mitte des Bildschirms setzen
        stage.setX((screenWidth - stage.getWidth()) / 2);
        stage.setY((screenHeight - stage.getHeight()) / 2);

        stage.setTitle("Ger\u00E4teausleih-Anwendung");
        stage.show();
    }

    /**
     * Zeigt ein neues Fenster auf Grundlage der action (SHOWBORROWEDDEVICES, EDIT, ADD).
     * SHOWBORROWEDDEVICES: Zeigt ein neues Fenster mit allen vom Nutzer ausgeliehenen Geräte an.
     * EDIT: Zeigt ein Fenster an um die ID des zu bearbeitenden Gerätes einzugeben.
     * ADD: Zeigt ein Fenster um ein neues Gerät hinzuzufügen.
     * @param action Aktion nach der das Fenster angezeigt werden soll
     */
    private void showNewWindow(Action action) {
        Stage newStage = new Stage();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        switch (action) {
            case SHOWBORROWEDDEVICES -> { //Wenn der Button "showBorrowedDevicesButton" angeklickt wurde
                newStage.setTitle("Ausgeliehene Ger\u00E4te");

                // Überschrift
                Label headlineLabel = new Label("Ausgeliehene Ger\u00E4te");
                headlineLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
                GridPane.setConstraints(headlineLabel, 0, 0);

                Separator separator = new Separator();
                separator.setStyle("-fx-background-color: black;");
                GridPane.setConstraints(separator, 0, 1, 4, 1);

                // Anzeige der ausgeliehenen Geräte
                ListView<String> borrowedDevicesListView = new ListView<>();
                borrowedDevicesListView.setMinWidth(1200);
                GridPane.setConstraints(borrowedDevicesListView, 0, 2, 4, 1);

                List<Device> borrowedDevicesFromUser = rest.getBorrowedDevices(loginModel.getUsername()); //Geräte vom Server aus der Datenbank holen
                ObservableList<String> borrowedDevicesOL = convertToBorrowedObservableList(borrowedDevicesFromUser);
                borrowedDevicesListView.setItems(borrowedDevicesOL);

                Button closeButton = new Button("Schlie\u00DFen");
                closeButton.setMaxWidth(Double.MAX_VALUE);
                closeButton.setOnAction(e -> newStage.close());
                GridPane.setConstraints(closeButton, 0, 3);

                grid.getChildren().addAll(headlineLabel, separator, borrowedDevicesListView, closeButton);
            }
            case EDIT -> { //Wenn der Button "editDeviceButton" angeklickt wurde
                newStage.setTitle("Ger\u00E4t bearbeiten");

                Label inputLabel = new Label("ID des zu bearbeitenden Ger\u00E4tes: ");
                GridPane.setConstraints(inputLabel, 0, 0);

                TextField inputField = new TextField();
                GridPane.setConstraints(inputField, 1, 0, 2, 1);

                Button editButton = new Button("bearbeiten");
                editButton.setMaxWidth(Double.MAX_VALUE);
                editButton.setOnAction(e -> {
                    lendingModel.setId(inputField.getText());
                    controller.handleEditButton();
                    newStage.close();
                });
                GridPane.setConstraints(editButton, 3, 0);

                grid.getChildren().addAll(inputLabel, inputField, editButton);
            }
            case ADD -> { //Wenn der Button "addDeviceButton" angeklickt wurde
                // Überschrift
                Label headlineLabel = new Label("Ger\u00E4t hinzuf\u00FCgen");
                headlineLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
                GridPane.setConstraints(headlineLabel, 0, 0);

                Separator separator = new Separator();
                separator.setStyle("-fx-background-color: black;");
                GridPane.setConstraints(separator, 0, 1, 2, 1);

                grid.getChildren().addAll(headlineLabel, separator);

                // Array mit den Labels zu den Geräteattributen
                Label[] labels = new Label[]{
                        new Label("Marke:"),
                        new Label("Modell:"),
                        new Label("Kategorie:"),
                        new Label("Kaufjahr:"),
                        new Label("ID: ")
                };

                TextField[] inputFields = new TextField[labels.length];

                // Hinzufügen der Labels und TextFields zum Grid
                for (int i = 0; i < labels.length; i++) {
                    Label label = labels[i];
                    TextField inputField = new TextField();

                    GridPane.setConstraints(label, 0, i + 3);
                    GridPane.setConstraints(inputField, 1, i + 3);

                    grid.getChildren().addAll(label, inputField);
                    inputFields[i] = inputField;
                }

                Button closeButton = new Button("Schlie\u00DFen");
                closeButton.setOnAction(e -> newStage.close());
                GridPane.setConstraints(closeButton, 0, labels.length + 3);

                Button editButton = new Button("hinzuf\u00FCgen");
                editButton.setMaxWidth(Double.MAX_VALUE);
                editButton.setOnAction(e -> {
                    lendingModel.setInputFields(inputFields);
                    boolean successful = controller.handleAddButton();
                    if (successful) newStage.close();
                });
                GridPane.setConstraints(editButton, 1, labels.length + 3);

                grid.getChildren().addAll(closeButton, editButton);

            }
        }
        Scene scene = new Scene(grid, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        scene.getStylesheets().add(CSSPATH);
        newStage.setScene(scene);

        newStage.show();
    }

    /**
     * Zeigt das Fenster an in dem das über die ID ausgewählte Gerät bearbeitet werden kann.
     * @param id ID
     */
    public void showEditStage(String id) {
        Device currentDevice = rest.getMatchingDevices(id, SearchCriteria.ID).getFirst(); //Gerät mit der id vom Server von der Datenbank holen

        Stage newStage = new Stage();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // überschrift
        Label headlineLabel = new Label("Ger\u00E4t bearbeiten");
        headlineLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
        GridPane.setConstraints(headlineLabel, 0, 0);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: black;");
        GridPane.setConstraints(separator, 0, 1, 3, 1);

        Label currentDeviceHeadline = new Label("aktuelles Ger\u00E4t:");
        GridPane.setConstraints(currentDeviceHeadline, 1, 2);

        Label newDeviceHeadline = new Label("neues Ger\u00E4t:");
        GridPane.setConstraints(newDeviceHeadline, 2, 2);

        grid.getChildren().addAll(headlineLabel, separator, currentDeviceHeadline, newDeviceHeadline);

        // Array mit den Labels zu den Geräteattributen
        Label[] labels = new Label[]{
                new Label("Marke:"),
                new Label("Modell:"),
                new Label("Kategorie:"),
                new Label("Kaufjahr:"),
                new Label("ID:"),
                new Label("Ausleiher:"),
                new Label("R\u00FCckgabedatum:")
        };

        // Array mit den Labels mit den Geräteattributen zum aktuellen Gerät
        List<Label> currentDeviceLabels = new ArrayList<>(List.of(new Label[]{
                new Label(currentDevice.brand()),
                new Label(currentDevice.model()),
                new Label(currentDevice.category()),
                new Label(String.valueOf(currentDevice.purchaseyear())),
                new Label(String.valueOf(currentDevice.id()))
        }));
        if (currentDevice.borrower() != null) {
            currentDeviceLabels.add(new Label(currentDevice.borrower()));
            Date returnDate = currentDevice.returnDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (currentDevice.returnDate() != null) {
                currentDeviceLabels.add(new Label(dateFormat.format(returnDate)));
            } else {
                currentDeviceLabels.add(new Label("null"));
            }
        } else {
            currentDeviceLabels.add(new Label("null"));
            currentDeviceLabels.add(new Label("null"));
        }

        TextField[] inputFields = new TextField[labels.length];

        // Hinzufügen der Labels und TextFields zum Grid
        for (int i = 0; i < labels.length; i++) {
            Label label = labels[i];
            Label currentDeviceLabel = currentDeviceLabels.get(i);
            TextField inputField = new TextField();

            GridPane.setConstraints(label, 0, i + 3);
            GridPane.setConstraints(currentDeviceLabel, 1, i + 3);
            GridPane.setConstraints(inputField, 2, i + 3);

            grid.getChildren().addAll(label, currentDeviceLabel, inputField);
            inputFields[i] = inputField;
        }

        Label tooltipLabel = new Label("?");
        tooltipLabel.setStyle("-fx-text-fill: white; -fx-border-radius: 100; -fx-border-color: white; -fx-padding: 0 9; -fx-background-color: #1752be; -fx-background-radius: 100;");
        GridPane.setConstraints(tooltipLabel, 3, 9);
        Tooltip tooltip = new Tooltip("Bitte Datum wie folgt eingeben: YYYY-MM-DD");
        Tooltip.install(tooltipLabel, tooltip);

        Button closeButton = new Button("Schlie\u00DFen");
        closeButton.setOnAction(e -> newStage.close());
        GridPane.setConstraints(closeButton, 0, labels.length + 3);

        Button editButton = new Button("bearbeiten");
        editButton.setMaxWidth(Double.MAX_VALUE);
        editButton.setOnAction(e -> {
            lendingModel.setCurrentDevice(currentDevice);
            lendingModel.setInputFields(inputFields);
            lendingModel.setCurrentDeviceLabels(currentDeviceLabels);
            boolean successful = controller.handleEditDeviceButton();
            if (successful) newStage.close();
        });
        GridPane.setConstraints(editButton, 2, labels.length + 3);

        grid.getChildren().addAll(tooltipLabel, closeButton, editButton);

        Scene scene = new Scene(grid, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        scene.getStylesheets().add(CSSPATH);

        newStage.setTitle("Ger\u00E4t bearbeiten");
        newStage.setScene(scene);

        // Szene in die Mitte des Bildschirms setzen
        stage.setX((screenWidth - stage.getWidth()) / 2);
        stage.setY((screenHeight - stage.getHeight()) / 2);

        newStage.show();
    }

    /**
     * Konvertiert eine Liste mit Geräten plus ihrem Rückgabedatum in eine ObservableList,
     * um sie in einer ListView anzeigen lassen zu können
     * @param devices Liste mit Geräten
     * @return ObservableList aus Strings mit den Geräten plus ihrem Rückgabedatum
     */
    private ObservableList<String> convertToBorrowedObservableList(List<Device> devices) {
        ObservableList<String> ol = FXCollections.observableArrayList();
        if (devices != null) {
            for (Device device : devices) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String deviceString = device.toString() + ", R\u00FCckgabedatum: " + dateFormat.format(device.returnDate());
                ol.add(deviceString);
            }
        }
        return ol;
    }
}
