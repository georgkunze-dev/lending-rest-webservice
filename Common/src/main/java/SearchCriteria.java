/**
 * @author Georg Kunze, m28909, u36363
 * Das Enum SearchCriteria repräsentiert die verschiedenen Kriterien, nachdem ein Gerät gesucht werden kann.
 * Jedes Suchkriterium hat einen Anzeigetext und kann in eine SQL-Spaltenbezeichnung
 * übersetzt werden.
 */
public enum SearchCriteria {
    BRAND("Marke"),
    MODEL("Modell"),
    CATEGORY("Kategorie"),
    PURCHASEYEAR("Kaufjahr"),
    ID("ID");

    private final String displayText;

    /**
     * Konstruktor in dem jedes Suchkriterium mit einem Anzeigetext initialisiert wird.
     * @param displayText Der angezeigte Text für die ChoiceBox
     */
    SearchCriteria(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Gibt den SQL-Spaltennamen zurück, der dem Suchkriterium entspricht.
     * @return Der SQL-Spaltenname für das Suchkriterium.
     */
    public String getSqlColumnName() {
        return switch (this) {
            case BRAND -> "brand";
            case MODEL -> "model";
            case CATEGORY -> "category";
            case PURCHASEYEAR -> "purchaseyear";
            case ID -> "id";
        };
    }

    /**
     * toString Methode die den Anzeigetext des Suchkriteriums zurückgibt.
     * @return Anzeigetext
     */
    @Override
    public String toString() {
        return displayText;
    }
}
