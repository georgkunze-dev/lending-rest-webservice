import java.util.Date;

/**
 * @author Georg Kunze, m28909, u36363
 * Die Klasse Device implementiert ein Gerät
 * @param brand Marke
 * @param model Modell
 * @param category Kategorie
 * @param purchaseyear Kaufdatum
 * @param id ID
 * @param borrower Benutzername des Ausleihers
 * @param returnDate Rückgabedatum des Gerätes
 */
public record Device(String brand, String model, String category, int purchaseyear, int id, String borrower, Date returnDate) {

    /**
     * toString Methode welche die Attribute für die Ausgabe des Gerätes zurückgibt
     * @return String mit ID, Marke, Modell, Kategorie und Kaufdatum
     */
    @Override
    public String toString() {

        return "ID: " + id + ", Marke: '" + replaceChar(brand) + "', Modell:'" + replaceChar(model) + "', Kategorie: " + replaceChar(category) + ", Kaufdatum: " + purchaseyear;
    }

    /**
     * Ersetzt alle Umlaute im String durch ihre entsprechenden Unicode Zeichen, damit sie in
     * der JavaFX-Oberfläche korrekt angezeigt werden
     * @param input Eingabestring
     * @return String in dem die Umlaute durch die entsprechenden Unicode Zeichen ersetzt werden
     */
    private String replaceChar(String input) {
        if (input == null) {
            return null; // Falls der Input null ist, einfach null zurückgeben
        } else {
            return input.replace("ä", "\u00E4")
                    .replace("ö", "\u00F6")
                    .replace("ü", "\u00FC")
                    .replace("ß", "\u00DF")
                    .replace("Ä", "\u00C4")
                    .replace("Ö", "\u00D6")
                    .replace("Ü", "\u00DC");
        }
    }
}