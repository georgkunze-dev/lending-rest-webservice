import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;
import java.util.Objects;

/**
 * @author Georg Kunze, m28909, u36363
 * Die Klasse ServerRestEndpoints stellt REST-Endpunkte für Geräte-Ausleihfunktionen bereit
 */
@Path("lending")
public class ServerRestEndpoints {
    DatabaseUtil database = new DatabaseUtil();

    /**
     * Aufgerufen durch post(String username) von ClientRestEndpoints.
     * Verwaltet die serverseitige POST-Anfrage für das Registrieren eines Benutzers mit dem
     * Benutzernamen beim Server
     * @param username Der Benutzername des zu registrierenden Benutzers.
     * @return HTTP-Antwort, die den Erfolg der Benutzerregistrierung angibt.
     */
    @POST
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(@PathParam("username") String username) {
        boolean isRegistered = database.isRegistered(username); // Prüfung ob Benutzer bereits in der Datenbank ist
        if (!isRegistered) {
            boolean addSuccessful = database.addUser(username); // Einfügen des Benutzernamens in der Datenbank
            if (addSuccessful) {
                System.out.println(">>> Benutzer registriert: " + username + " <<<");
                return Response.noContent().status(Response.Status.OK).build();
            } else return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } else {
            System.out.println(">>> Benutzer angemeldet: " + username + " <<<");
            return Response.noContent().status(Response.Status.OK).build();
        }

    }

    /**
     * Aufgerufen durch getMatchingDevices(String search, SearchCriteria criteria) von ClientRestEndpoints.
     * Verwaltet die serverseitige GET Anfrage für die Suche nach einem Gerät mit einem String nach einem
     * bestimmten Kriterium
     * @param search Der Suchbegriff.
     * @param criteria Das Suchkriterium (Marke, Modell, Kategorie, Kaufdatum, ID)
     * @return HTTP-Antwort, die die gefundenen Geräte im JSON-Format enthält.
     */
    @GET
    @Path("{search}/{criteria}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchDevices(@PathParam("search") String search, @PathParam("criteria") String criteria) {
        SearchCriteria searchCriteria = convertBackToSearchCriteria(criteria); //String des Suchkriteriums zurückkonvertieren
        List<Device> matchingDevices = database.getMatchingDevices(search, searchCriteria); //Passende Geräte nach dem Suchbegriff und Kriterium aus der Datenbank holen

        if (!matchingDevices.isEmpty()) {
            return Response.ok().entity(matchingDevices).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Konvertiert einen String in ein Enum-Objekt vom Typ SearchCriteria.
     * @param criteria Suchkriterium als String
     * @return Das entsprechende SearchCriteria-Objekt.
     */
    private SearchCriteria convertBackToSearchCriteria(String criteria) {
        return switch (criteria) {
            case "Marke" -> SearchCriteria.BRAND;
            case "Modell" -> SearchCriteria.MODEL;
            case "Kategorie" -> SearchCriteria.CATEGORY;
            case "Kaufjahr" -> SearchCriteria.PURCHASEYEAR;
            case "ID" -> SearchCriteria.ID;
            default -> null;
        };
    }

    /**
     * Aufgerufen durch getAllDevices() von ClientRestEndpoints.
     * Verwaltet die serverseitige GET Anfrage für das Zurückliefern aller Geräte
     * @return HTTP-Antwort, die alle Geräte im JSON-Format enthält.
     */
    @GET
    @Path("getAllDevices")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllDevices() {
        List<Device> matchingDevices = database.getAllDevices(); //Holen von allen Geräten aus der Datenbank

        if (!matchingDevices.isEmpty()) {
            return Response.ok().entity(matchingDevices).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    /**
     * Aufgerufen durch putBorrower(Action action, String id) von ClientRestEndpoints.
     * Verwaltet die serverseitige PUT Anfrage für das Hinzufügen oder Entfernen eines Ausleihers zu dem
     * jeweiligen Gerät
     * @param username Der Benutzername des Ausleihers.
     * @param id Die ID des Gerätes.
     * @param action Die Aktion (BORROW oder RETURN)
     * @return HTTP-Antwort, die den Erfolg der Aktion angibt.
     */
    @PUT
    @Path("{username}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeBorrower(@PathParam("username") String username, @PathParam("id") String id, Action action) {
        Device device = database.getMatchingDevices(id, SearchCriteria.ID).getFirst(); //Gerät dessen Status geändert werden soll aus der Datenbank holen
        try {
            boolean isBorrowed = device.borrower() != null;
            switch (action) {
                case BORROW -> { //Aktion: Ausleihen eines Gerätes
                    if (!isBorrowed) {
                        boolean setSuccessful = database.setBorrower(id, username); //Username und Rückgabedatum in dem Datenbankeintrag des Gerätes hinzufügen
                        if (setSuccessful) {
                            return Response.noContent().status(Response.Status.OK).build();
                        } else return Response.noContent().status(Response.Status.NOT_FOUND).build();
                    } else return Response.status(Response.Status.NOT_FOUND).build();
                }
                case RETURN -> { //Aktion: Zurückgeben eines Gerätes
                    if (isBorrowed && (Objects.equals(username, device.borrower()))) {
                        boolean deleteSuccessful = database.deleteBorrower(id); //Username und Rückgabedatum in dem Datenbankeintrag des Gerätes auf null setzten
                        if (deleteSuccessful) {
                            return Response.noContent().status(Response.Status.OK).build();
                        } else return Response.status(Response.Status.NOT_FOUND).build();
                    } else return Response.status(Response.Status.NOT_FOUND).build();
                }
                default -> {
                    return Response.noContent().status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (Exception e) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Aufgerufen von getReservedDevices(String username) von ClientRestEndpoints.
     * Verwaltet die serverseitige GET Anfrage für das Zurückliefern aller vom Benutzer ausgeliehenen Geräte
     * @param username Der Benutzername.
     * @return HTTP-Antwort, die die ausgeliehenen Geräte im JSON-Format enthält.
     */
    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReservedDevices(@PathParam("username") String username) {
        List<Device> borrowedDevicesByUser = database.getBorrowedDevicesByUser(username); //Geräte die vom User ausgeliehen sind aus der Datenbank holen
        return Response.ok().entity(borrowedDevicesByUser).build();
    }

    /**
     * Aufgerufen von putDevice(Device device) von ClientRestEndpoints.
     * Verwaltet die serverseitige PUT Anfrage für das Bearbeiten der Informationen eines Gerätes anhand seiner ID.
     * @param id Die ID des zu bearbeitenden Gerätes.
     * @param device Das Gerät-Objekt mit den aktualisierten Informationen.
     * @return HTTP-Antwort, die den Erfolg der Bearbeitung angibt.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editDevice(@PathParam("id") String id, Device device) {
        boolean editSuccessful = database.editDevice(id, device); //Gerät mit den neuen Geräteinformationen in der Datenbank bearbeiten

        if (editSuccessful) {
            return Response.noContent().status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Aufgerufen durch postDevice(Device device) von ClientRestEndpoints.
     * Verwaltet die serverseitige POST Anfrage für das Hinzufügen eines neuen Gerätes in die Datenbank
     * @param device Gerät, das hinzugefügt werden soll
     * @return HTTP-Antwort, die den Erfolg des Hinzufügens angibt.
     */
    @POST
    @Path("/postDevice")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDevice(Device device) {
        boolean addSuccessful = database.addDevice(device); //Hinzufügen des Gerätes in die Datenbank

        if (addSuccessful) {
            return Response.noContent().status(Response.Status.OK).build();
        } else return Response.noContent().status(Response.Status.NOT_FOUND).build();
    }

}
