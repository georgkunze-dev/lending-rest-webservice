import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Georg Kunze, m28909, u36363
 * Kontroller für die OnAction-Events der Button in dem Hauptbildschirm der Geräteausleih-Anwendung
 */
public class LendingController {
    private final LoginModel loginModel; //Modell für die Speicherung der Daten vom Benutzer
    private final LendingModel lendingModel; //Modell für die Speicherung der Daten der Geräteausleih-Anwendung
    private final LendingView lendingView; //View für das Anzeigen der Benutzeroberfläche der Geräteausleih-Anwendung
    private final ClientRestEndpoints rest; //Rest Endpunkt des Klienten
    private final Message message; //Klasse zum Anzeigen einer Pop-up-Nachricht

    /**
     * Konstruktor der LendingController Klasse.
     * Initialisiert die Variablen und ruft die Initialisierung des Kontrollers auf.
     * @param loginModel Modell für die Speicherung der Daten vom Benutzer
     * @param lendingModel Modell für die Speicherung der Daten der Geräteausleih-Anwendung
     * @param lendingView View für das Anzeigen der Benutzeroberfläche der Geräteausleih-Anwendung
     * @param rest Rest Endpunkt des Klienten
     */
    public LendingController(LoginModel loginModel, LendingModel lendingModel, LendingView lendingView, ClientRestEndpoints rest) {
        this.loginModel = loginModel;
        this.lendingModel = lendingModel;
        this.lendingView = lendingView;
        this.rest = rest;
        message = new Message();
    }

    /**
     * Event-Handler für den Such-Button zur Such nach einem oder allen nicht ausgeliehenen Geräten
     */
    public void handleSearchButton() {
        SearchCriteria criteria = lendingModel.getSearchCriteria();
        String searchString = lendingModel.getSearch();
        List<Device> matchingDevices;

        if (searchString.isEmpty()) {
            matchingDevices = rest.getAllDevices(); // Alle Geräte vom Server und der Datenbank holen
        } else {
            matchingDevices = rest.getMatchingDevices(searchString, criteria); // Geräte anhand der Suche und des Kriteriums vom Server und der Datenbank holen
        }

        //Ausgeliehene Geräte rausfiltern
        List <Device> notBorrowedDevices = new ArrayList<>();
        for (Device device : matchingDevices) {
            if (device.borrower() == null) {
                notBorrowedDevices.add(device);
            }
        }
        ObservableList<String> matchingDevicesOL = convertToObservableList(notBorrowedDevices); // Kopieren der Liste in eine ObservableList

        //Wenn kein Gerät gefunden wurde, entsprechende Nachricht für die Anzeige hinzufügen
        if (matchingDevicesOL.isEmpty()) {
            matchingDevicesOL.add("Kein passendes Ger\u00E4t zu der Eingabe gefunden oder das Ger\u00E4t ist nicht mehr ausleihbar");
        }

        lendingModel.setObservableList(matchingDevicesOL);
    }

    /**
     * Event-Handler für den Ausleih-Button zum Ausleihen eines Gerätes
     * @return true, wenn die Aktion erfolgreich war, sonst false
     */
    public boolean handleBorrowButton() {
        String id = lendingModel.getId();

        if (!id.isEmpty() && isValidId(id)) { //Wenn die Eingabe nicht leer ist und die ID gültig ist
            boolean successful = rest.putBorrower(loginModel.getUsername(), id, Action.BORROW); //Ausleihen des Gerätes
            if (successful) {
                message.show("Ausleihen", "Das Ger\u00E4t wurde erfolgreich ausgeliehen!");
                return true;
            } else {
                message.show("Fehler", "Das Ger\u00E4t wurde schon ausgeliehen oder die ID wurde falsch eingegeben!");
                return false;
            }
        } else {
            message.show("Fehler", "Bitte eine g\u00FCltige ID eingeben!");
            return false;
        }
    }

    /**
     * Event-Handler für den Rückgabe-Button zum Zurückgeben eines Gerätes
     * @return true, wenn die Aktion erfolgreich war, sonst false
     */
    public boolean handleReturnButton() {
        String id = lendingModel.getId();

        if (!id.isEmpty() && isValidId(id)) { //Wenn die Eingabe nicht leer ist und die ID gültig ist
            boolean successful = rest.putBorrower(loginModel.getUsername(), id, Action.RETURN); //Zurückgeben des Gerätes
            if (successful) {
                message.show("Zur\u00FCckgeben", "Das Ger\u00E4t wurde erfolgreich zur\u00FCckgegeben");
                return true;
            } else {
                message.show("Fehler", "Das Ger\u00E4t kann nicht zur\u00FCckgegeben werden oder die ID wurde falsch eingegeben");
                return false;
            }
        } else {
            message.show("Fehler", "Bitte eine g\u00FCltige ID eingeben!");
            return false;
        }
    }

    /**
     * Event-Handler für den Bearbeiten-Button zum Bearbeiten eines Gerätes.
     * Der Button ruft ein Fenster auf, um die ID des zu bearbeitenden Gerätes einzugeben
     */
    public void handleEditButton() {
        String id = lendingModel.getId();

        if (!id.isEmpty() && (isValidId(id) && isExistingId(id))) { //Wenn die Eingabe nicht leer ist und die ID gültig und vorhanden ist
            lendingView.showEditStage(id);
        } else {
            message.show("Fehler", "Bitte eine g\u00FCltige ID eingeben!");
        }
    }

    /**
     * Event-Handler für den Bearbeiten-Button zum Bearbeiten eines Gerätes in der Datenbank
     * @return true, wenn die Aktion erfolgreich war, sonst false
     */
    public boolean handleEditDeviceButton() {
        Device currentDevice = lendingModel.getCurrentDevice();
        TextField[] inputFields = lendingModel.getInputFields();
        List<Label> currentDeviceLabels = lendingModel.getCurrentDeviceLabels();
        String[] newAttributes = new String[inputFields.length];
        Date parsedDate = currentDevice.returnDate();

        //Schleife über die einzelnen TextFields
        for (int i = 0; i < inputFields.length; i++) {
            String attr = inputFields[i].getText();

            if (attr.isEmpty()) { // Wenn im Textfeld keine Eingabe ist, wird das "alte" Attribut übernommen
                newAttributes[i] = currentDeviceLabels.get(i).getText();
            } else {
                newAttributes[i] = attr;
                try {
                    if (i == 3) { // Nur für das Attribut purchaseyear
                        Integer.parseInt(attr); // Überprüfung, ob String in Integer umgewandelt werden kann
                    } else if (i == 4 && (!isValidId(attr) || isExistingId(attr)) && Integer.parseInt(attr)!=currentDevice.id()) { //Überprüfen, ob die ID valide ist und existiert und nicht der aktuellen ID entspricht
                        message.show("Fehler", "Bitte eine g\u00FCltige ID eingeben!");
                        return false;
                    } else if (i == 5 && attr.equalsIgnoreCase("null")) { //Abfangen, dass der String null in die Datenbank eingefügt wird
                        newAttributes[i] = null;
                    }else if (i == 6) { // Nur für das Attribut returnDate
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        parsedDate = dateFormat.parse(attr); // Überprüfung, ob String in Datum umgewandelt werden kann
                        message.show("Fehler", "Bitte ein g\u00FCltiges Datum eingeben!");
                        return false;
                    }
                } catch (NumberFormatException | ParseException err) {
                    if (i == 3) {
                        message.show("Fehler", "Bitte ein g\u00FCltiges Kaufjahr eingeben");
                    } else message.show("Fehler", "Bitte ein g\u00FCltiges Datum eingeben");
                    return false; // Abbruch des setOnAction-Events
                }
            }
        }
        Device newDevice = new Device(newAttributes[0], newAttributes[1], newAttributes[2], Integer.parseInt(newAttributes[3]), Integer.parseInt(newAttributes[4]), newAttributes[5], parsedDate);

        boolean editDeviceSuccessful = rest.putDevice(String.valueOf(currentDevice.id()), newDevice); // Gerät bearbeiten
        if (editDeviceSuccessful) {
            message.show("Ger\u00E4tebearbeitung", "Das Ger\u00E4t wurde erfolgreich bearbeitet");
            return true;
        } else {
            message.show("Fehler", "Fehler: Fehler beim Bearbeiten des Ger\u00E4tes");
            return false;
        }
    }

    /**
     * Event-Handler für den Hinzufügen-Button zum Hinzufügen eines Gerätes in der Datenbank
     * @return true, wenn die Aktion erfolgreich war, sonst false
     */
    public boolean handleAddButton() {
        TextField[] inputFields = lendingModel.getInputFields();
        String[] attributes = new String[inputFields.length];
        boolean errMessageShowed = false;

        //Schleife über die einzelnen TextFields
        for (int i = 0; i < inputFields.length; i++) {
            String attr = inputFields[i].getText();
            if (attr.isEmpty() && !errMessageShowed) {
                message.show("Fehler", "Fehler: Bitte in allen Feldern eine Eingabe t\u00E4tigen");
                errMessageShowed = true;
            } else {
                attributes[i] = attr;
                try {
                    if (i == 3) { // Überprüfung, ob der String des Kaufjahres in einen Integer geparst werden kann
                        Integer.parseInt(attr);
                    } else if (i == 4 && (!isValidId(attr) || isExistingId(attr))) { // Überprüfung, ob der String eine gültige und bisher nicht vorhandene id ist
                        message.show("Fehler", "Bitte eine g\u00FCltige oder nicht vorhandene ID eingeben!");
                        return false;
                    }
                } catch (NumberFormatException err) {
                    message.show("Fehler", "Bitte ein g\u00FCltiges Kaufjahr eingeben");
                    return false;
                }
            }
        }
        Device device = new Device(attributes[0], attributes[1], attributes[2], Integer.parseInt(attributes[3]), Integer.parseInt(attributes[4]), null, null);

        boolean addDeviceSuccessful = rest.postDevice(device); // Übergeben des neuen Gerätes an den Server
        if (addDeviceSuccessful) {
            message.show("Ger\u00E4tebearbeitung", "Das Ger\u00E4t wurde erfolgreich hinzugef\u00FCgt");
            return true;
        } else {
            message.show("Fehler", "Fehler: Fehler beim Bearbeiten des Ger\u00E4tes");
            return false;
        }
    }

    /**
     * Prüft, ob eine gegebene ID eine Zahl ist, die größer als 0 ist.
     * @param id ID
     * @return true, wenn die ID übereinstimmt, sonst false
     */
    public static boolean isValidId(String id) {
        try {
            int intId = Integer.parseInt(id);
            return intId >= 1;
        } catch(NumberFormatException err) {
            return false;
        }
    }

    public boolean isExistingId(String id) {
        List<Device> matchingDevices = rest.getMatchingDevices(id, SearchCriteria.ID);
        return matchingDevices != null;
    }

    /**
     * Konvertiert eine Liste mit Geräten in eine ObservableList, um sie in einer ListView anzeigen
     * lassen zu können
     * @param devices Liste mit Geräten
     * @return ObservableList aus Strings mit den Geräten
     */
    private ObservableList<String> convertToObservableList(List<Device> devices) {
        ObservableList<String> ol = FXCollections.observableArrayList();
        if (devices != null) {
            for (Device device : devices) {
                ol.add(device.toString());
            }
        }
        return ol;
    }
}
