import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Georg Kunze, m28909, u36363
 * Die Klasse Server implementiert einen Grizzly Server und initialisiert die Datenbank
 */
public class Server {
    private static Server instance;  // Statische Instanz für das Singleton-Entwurfsmuster

    /**
     * Privater Konstruktor der Server Klasse um die externe Instanziierung zu verhindern
     */
    private Server() {
    }

    /**
     * Getter für die Instanz der Server Klasse
     * @return Instanz der Server Klasse
     */
    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    /**
     * Stellt die Verbindung zum HTTP-Server her und startet ihn.
     * Initialisiert die Datenbank.
     */
    public void startServer() {
        try {
            Logger.getLogger("org.glassfish").setLevel(Level.SEVERE);

            URI baseURI = new URI("http://localhost:8080/rest"); // Uri für den Server
            ResourceConfig config = new ResourceConfig(ServerRestEndpoints.class); // Konfigurierung für den REST-Server mit der ServerRestEndpoints Klasse
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseURI, config); // Grizzly HTTP-Server mit der URI und Konfiguration

            if (!server.isStarted()) {
                server.start();
            }

            System.out.println(">>> Server gestartet " + baseURI + " <<<");

            //Initialisierung der Datenbank
            DatabaseUtil db = new DatabaseUtil();
            db.initialize();
        } catch (URISyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
