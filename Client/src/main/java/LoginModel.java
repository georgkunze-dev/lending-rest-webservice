/**
 * @author Georg Kunze, m28909, u36363
 * Modell für die Speicherung des Benutzernamens und des Benutzertypens
 */
public class LoginModel {
    private String username; //Benutzername
    private String usertype; //Benutzertyp

    /**
     * Getter für den Benutzernamen
     * @return Benutzername
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter für den Benutzernamen
     * @param username Benutzername
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter für den Benutzertyp
     * @return Benutzertyp
     */
    public String getUsertype() {
        return usertype;
    }

    /**
     * Setter des Benutzertypen
     * @param usertype Benutzertyp
     */
    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}
