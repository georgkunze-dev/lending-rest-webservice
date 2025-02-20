import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * @author Georg Kunze, m28909, u36363
 * Die Klasse ClientRestEndpoints implementiert die REST Endpunkte des Klienten für die Kommunikation
 * mit dem Geräteausleih-Server.
 * Die Klasse unterstützt verschiedene Anfragen wie das Hinzufügen eines Benutzernamens, die Suche nach
 * Geräten, das Abrufen aller Geräte, die Bearbeitung des Ausleihstatus eines Gerätes, das Abrufen der
 * vom Benutzer ausgeliehenen Geräte, das Bearbeiten eines Gerätes und das Hinzufügen eines neuen Gerätes.
 */
public class ClientRestEndpoints {
    private final Client client = ClientBuilder.newClient();

    /**
     * Verwaltet die Klienten seitige POST Anfrage, um einen Benutzernamen zu registrieren.
     * @param username Der Benutzername
     * @return true, wenn die Anfrage erfolgreich war, sonst false
     */
    public boolean postUsername(String username) {
        try {
            WebTarget target = getTarget("POST", "/" + username);
            Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(username));
            return status(response) == Response.Status.OK.getStatusCode();
        } catch (Exception e) { //Abfangen, wenn keine Verbindung zum Server besteht
            return false;
        }
    }

    /**
     * Verwaltet die Klienten seitige GET Anfrage, welche eine Liste mit Geräten entsprechend der
     * Suchanfrage zurückliefert
     * @param search SuchString
     * @param criteria Das Suchkriterium
     * @return List mit den passenden Geräten, wenn die Anfrage erfolgreich war, sonst null
     */
    public List<Device> getMatchingDevices(String search, SearchCriteria criteria) {
        WebTarget target = getTarget("GET", "/" + search + "/" + criteria);
        Response response = target.request().accept(MediaType.APPLICATION_JSON).get();
        if (status(response) == Response.Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<>() {});
        } else {
            System.out.println("GET: Fehler bei der Client Server Kommunikation");
            return null;
        }
    }

    /**
     * Verwaltet die Klienten seitige GET Anfrage, welche eine Liste mit allen nicht ausgeliehenen
     * Geräten zurückgibt
     * @return List mit allen nicht ausgeliehenen Geräten, wenn die Anfrage erfolgreich war, sonst null
     */
    public List<Device> getAllDevices() {
        WebTarget target = getTarget("GET", "/getAllDevices");
        Response response = target.request().accept(MediaType.APPLICATION_JSON).get();
        if (status(response) == Response.Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<>() {});
        } else {
            System.out.println("GET: Fehler bei der Client Server Kommunikation");
            return null;
        }
    }

    /**
     * Verwaltet die Klienten seitige PUT Anfrage, um den Ausleihstatus eines Gerätes zu ändern
     * @param username Der Benutzername
     * @param id ID des Gerätes
     * @param action Aktion (Ausleihen oder Zurückgeben)
     * @return true, wenn das Ändern erfolgreich war, sonst false
     */
    public boolean putBorrower(String username, String id , Action action) {
        WebTarget target = getTarget("PUT", "/" + username + "/" + id);
        Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(action));
        return status(response) == Response.Status.OK.getStatusCode();
    }

    /**
     * Verwaltet die Klienten seitige GET Anfrage, um die vom Benutzer ausgeliehenen Geräte abzurufen
     * @param username Der Benutzername
     * @return List mit den vom Benutzer ausgeliehenen Geräte
     */
    public List<Device> getBorrowedDevices(String username) {
        WebTarget target = getTarget("GET", "/" + username);
        Response response = target.request().accept(MediaType.APPLICATION_JSON).get();

        if (this.status(response) == Response.Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<>() {});
        } else {
            System.out.println("GET: Fehler bei der Client Server Kommunikation");
            return null;
        }
    }

    /**
     * Verwaltet die Klienten seitige PUT Anfrage, um ein vorhandenes Gerät zu bearbeiten.
     * @param id ID
     * @param device Gerät mit den neuen Attributen
     * @return true, wenn das Bearbeiten erfolgreich war, sonst false
     */
    public boolean putDevice(String id, Device device) {
        WebTarget target = getTarget("PUT", "/" + id);
        Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(device));
        return status(response) == Response.Status.OK.getStatusCode();
    }

    /**
     * Verwaltet die Klienten seitige POST Anfrage, um ein neues Gerät hinzuzufügen.
     * @param device Neues Gerät
     * @return true, wenn das Hinzufügen erfolgreich war, sonst false
     */
    public boolean postDevice(Device device) {
        WebTarget target = getTarget("POST", "/postDevice");
        Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(device));
        return status(response) == Response.Status.OK.getStatusCode();
    }

    /**
     * Gibt das WebTarget entsprechend der übergebenen Uri zurück
     * @param crud crud
     * @param uri uri
     * @return WebTarget
     */
    private WebTarget getTarget(String crud, String uri) {
        final String BASEURI = "http://localhost:8080/rest/lending";
        System.out.printf("%n>>> %s %s%s <<<%n", crud, BASEURI, uri);
        return client.target(BASEURI + uri);
    }

    /**
     * Gibt den status der Response zurück
     * @param response - Response
     * @return Status der Response
     */
    private int status(Response response) {
        int code = response.getStatus();
        String reason = response.getStatusInfo().getReasonPhrase();
        System.out.printf(">>> Status: %d %s <<<%n", code, reason);
        return code;
    }
}