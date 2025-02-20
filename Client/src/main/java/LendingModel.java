import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * @author Georg Kunze, m28909, u36363
 * Modell für die Speicherung des Suchbegriffs, des Suchkriteriums, der ObservableList, der ID,
 * des aktuellen Gerätes, der Eingabefelder zur Eingabe der Attribute eines Gerätes und der Labels mit den
 * Attributen des aktuellen Gerätes
 */
public class LendingModel {
    private String search; //Suchbegriff
    private SearchCriteria searchCriteria; //Suchkriterium
    private ObservableList<String> observableList; //ObservableList zur Anzeige in einer ListView
    private String id; //ID
    private Device currentDevice; //aktuelles Gerät
    private TextField[] inputFields; //Eingabefelder zur Eingabe der Attribute eines Gerätes
    private List<Label> currentDeviceLabels; //Label mit den Attributen des aktuellen Gerätes

    /**
     * Getter des Suchbegriffs
     * @return Suchbegriff
     */
    public String getSearch() {
        return search;
    }

    /**
     * Setter des Suchbegriffs
     * @param search Suchbegriff
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Getter des Suchkriteriums
     * @return Suchkriterium
     */
    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * Setter des Suchkriteriums
     * @param searchCriteria Suchkriterium
     */
    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * Getter der ObservableList
     * @return ObservableList
     */
    public ObservableList<String> getObservableList() {
        return observableList;
    }

    /**
     * Setter der ObservableList
     * @param observableList ObservableList
     */
    public void setObservableList(ObservableList<String> observableList) {
        this.observableList = observableList;
    }

    /**
     * Getter der ID
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter der ID
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter des aktuellen Gerätes
     * @return aktuelles Gerät
     */
    public Device getCurrentDevice() {
        return currentDevice;
    }

    /**
     * Setter des aktuellen Gerätes
     * @param currentDevice aktuelles Gerät
     */
    public void setCurrentDevice(Device currentDevice) {
        this.currentDevice = currentDevice;
    }

    /**
     * Getter der Eingabefelder
     * @return Eingabefelder
     */
    public TextField[] getInputFields() {
        return inputFields;
    }

    /**
     * Setter der Eingabefelder
     * @param inputFields Eingabefelder
     */
    public void setInputFields(TextField[] inputFields) {
        this.inputFields = inputFields;
    }

    /**
     * Getter der Label
     * @return Label
     */
    public List<Label> getCurrentDeviceLabels() {
        return currentDeviceLabels;
    }

    /**
     * Setter der Label
     * @param currentDeviceLabels Label
     */
    public void setCurrentDeviceLabels(List<Label> currentDeviceLabels) {
        this.currentDeviceLabels = currentDeviceLabels;
    }
}
