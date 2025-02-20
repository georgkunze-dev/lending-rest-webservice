import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Georg Kunze, m28909, u36363
 * Die Klasse DatabaseUtil stellt Methoden für die Interaktion mit der Datenbank
 * der Geräteausleih-Anwendung bereit.
 */
public class DatabaseUtil {
    Connection connection; //Connection für die Datenbank

    /**
     * Konstruktor der Klasse DatabaseUtil, der eine Verbindung zur Datenbank herstellt.
     */
    public DatabaseUtil() {
        //Laden der Datenbankverbindungsparameter aus der Konfigurationsdatei
        ResourceBundle bundle = ResourceBundle.getBundle("Select");
        String driver = bundle.getString("Driver");
        String url = bundle.getString("URL");
        String user = bundle.getString("User");
        String password = bundle.getString("Password");

        try {
            //JDBC Treiber laden
            Class.forName(driver);
            //Verbindung zur Datenbank herstellen
            connection = DriverManager.getConnection(url , user , password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("\n>>> Fehler: Keine Verbindung zur Datenbank! Bitte Verbindung zur Datenbank herstellen! <<<\n>>> " + e.getMessage() + " <<<\n>>> Programm wird beendet! <<<");
            System.exit(0);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initialisierung der Datenbank, falls sie leer ist.
     */
    public void initialize() {
        if (isDatabaseEmpty()) {
            System.out.println(">>> Datenbank ist leer. Initialisierung... <<<");
            try {
                //Laden der SQL Befehle aus initial_data.sql
                String fileName = "initial_data.sql";
                ArrayList<String> sqlCommands = getSqlCommands(fileName);

                Statement statement = connection.createStatement();
                for (String sql: sqlCommands) {
                    statement.execute(sql);
                    connection.commit();
                }

                System.out.println(">>> Datenbank erfolgreich initialisiert. <<<");
            } catch (SQLException e) {
                printSqlErr(e);
            }
        }
    }

    /**
     * Überprüfung, ob die Datenbank leer ist durch das Zählen der Einträge in der devices Tabelle.
     * Wenn die Tabelle nicht existiert und eine SQLException geworfen wird, werden die Tabellen erstellt
     * @return true, wenn die Datenbank leer ist, sonst false.
     */
    private boolean isDatabaseEmpty() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM devices");
            resultSet.next();
            int deviceCount = resultSet.getInt(1);

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
            resultSet.next();
            int userCount = resultSet.getInt(1);

            return deviceCount == 0 && userCount == 0;
        } catch (SQLException e) {
            createTables(); // Erstellen der Tabellen
            return true;
        }
    }

    /**
     * Erstellung der benötigten Tabellen in der Datenbank.
     */
    private void createTables() {
        try {
            //Laden der SQL Befehle aus create_tables.sql
            String fileName = "create_tables.sql";
            ArrayList<String> sqlCommands = getSqlCommands(fileName);

            Statement statement = connection.createStatement();
            for (String sql: sqlCommands) {
                statement.execute(sql);
                connection.commit();
            }
        } catch (SQLException e) {
            printSqlErr(e);
        }
    }

    /**
     * Liest SQL-Befehle aus einer Datei und gibt sie als Liste zurück.
     * @param fileName Dateiname der Sql-Datei
     * @return ArrayList mit den Sql Befehlen
     */
    private ArrayList<String> getSqlCommands(String fileName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                ArrayList<String> sqlCommands = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    sqlCommands.add(line);
                }

                return sqlCommands;
            } else {
                System.err.println(">>> SQL-Datei nicht gefunden. <<<");
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Prüft, ob der Benutzer bereits in der Datenbank ist, indem alle Benutzer aus der Datenbank
     * geholt und mit dem übergebenen Benutzernamen verglichen werden.
     * @param username Der Benutzername
     * @return true, wenn der Benutzer in der Datenbank ist, sonst false.
     */
    public boolean isRegistered(String username) {
        try {
            PreparedStatement prep = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = prep.executeQuery();

            boolean isregistred = false;
            while (resultSet.next()) {
                String userString = resultSet.getString("username");
                if (username.equals(userString)) {
                    isregistred = true;
                }
            }
            return isregistred;
        } catch (SQLException e) {
            printSqlErr(e);
            return false;
        }
    }

    /**
     * Fügt den übergebenen Benutzernamen in die Datenbank ein
     * @param username Der Benutzername
     * @return true, wenn der Benutzer in die Datenbank hinzugefügt wurde, sonst false.
     */
    public boolean addUser(String username) {
        try {
            PreparedStatement prep = connection.prepareStatement("INSERT INTO users (username) VALUES (?)");
            prep.setString(1, username);

            int affectedRows = prep.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            printSqlErr(e);
            return false;
        }
    }

    /**
     * Holt alle Geräte aus der Datenbank die mit Suchbegriff und Kriterium übereinstimmen
     * @param search Der Suchbegriff
     * @param criteria Das Suchkriterium
     * @return List mit den passenden Geräten
     */
    public List<Device> getMatchingDevices(String search, SearchCriteria criteria) {
        try {
            PreparedStatement prep = connection.prepareStatement("SELECT * FROM devices WHERE " + criteria.getSqlColumnName() + " LIKE ?");
            if (criteria == SearchCriteria.ID) {
                prep.setString(1, search);
            } else {
                prep.setString(1, "%" + search + "%");
            }
            ResultSet resultSet = prep.executeQuery();
            return resultSetToList(resultSet);
        } catch (SQLException e) {
            printSqlErr(e);
            return null;
        }
    }

    /**
     * Gibt alle Geräte zurück, die in der Datenbank und nicht ausgeliehen sind
     * @return List mit allen nicht ausgeliehenen Geräten
     */
    public List<Device> getAllDevices() {
        try {
            PreparedStatement prep = connection.prepareStatement("SELECT * FROM devices WHERE borrower IS NULL");
            ResultSet resultSet = prep.executeQuery();
            return resultSetToList(resultSet);
        } catch (SQLException e) {
            printSqlErr(e);
            return null;
        }
    }

    /**
     * Ausleihung eines Gerätes, indem der borrower auf den Benutzernamen gesetzt und
     * das Rückgabedatum eingefügt wird
     * @param id ID des Gerätes
     * @param username Der Benutzername des Ausleihers
     * @return true, wenn der Benutzer und das Rückgabedatum in die Datenbank eingefügt wurden, sonst false.
     */
    public boolean setBorrower(String id, String username) {
        try {
            PreparedStatement prep = connection.prepareStatement("UPDATE devices SET borrower = ?, returndate = ? WHERE id like ?");
            prep.setString(1, username);
            LocalDate returnDate = LocalDate.now().plusWeeks(2); // Datum 2 Wochen in der Zukunft
            prep.setDate(2, java.sql.Date.valueOf(returnDate));
            prep.setString(3, id);

            int affectedRows = prep.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            printSqlErr(e);
            return false;
        }
    }

    /**
     * Zurückgeben eines Gerätes, indem der borrower und das Rückgabedatum auf null gesetzt wird
     * @param id ID des Gerätes
     * @return true, wenn der Benutzer und das Rückgabedatum auf null gesetzt wurden, sonst false.
     */
    public boolean deleteBorrower(String id) {
        try {
            PreparedStatement prep = connection.prepareStatement("UPDATE devices SET borrower = null, returndate = null WHERE id like ?");
            prep.setString(1, id);

            int affectedRows = prep.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            printSqlErr(e);
            return false;
        }
    }

    /**
     * Holt sich alle Geräte aus der Datenbank, die vom Nutzer ausgeliehen sind
     * @param username Der Benutzername
     * @return List mit den vom Nutzer ausgeliehenen Geräte
     */
    public List<Device> getBorrowedDevicesByUser(String username) {
        try {
            PreparedStatement prep = connection.prepareStatement("SELECT * FROM devices WHERE borrower LIKE ?");
            prep.setString(1, username);
            ResultSet resultSet = prep.executeQuery();
            return resultSetToList(resultSet);
        } catch (SQLException e) {
            printSqlErr(e);
            return null;
        }
    }

    /**
     * Ändern des Datenbankeintrags des Gerätes mit den neuen Attributen
     * @param id ID des zu bearbeitenden Gerätes
     * @param device Gerät mit neuen Attributen
     * @return true, wenn das Gerät erfolgreich bearbeitet wurde, sonst false.
     */
    public boolean editDevice(String id, Device device) {
        try {
            PreparedStatement prep = connection.prepareStatement("""
             UPDATE devices SET
             brand = ?,
             model = ?,
             category = ?,
             purchaseyear = ?,
             id = ?,
             borrower = ?,
             returndate = ?\s
             WHERE id like ?
            """);
            prep.setString(1, device.brand());
            prep.setString(2, device.model());
            prep.setString(3, device.category());
            prep.setInt(4, device.purchaseyear());
            prep.setInt(5, device.id());
            // Setzen des Ausleihers, falls vorhanden, sonst setzen auf NULL.
            if (device.borrower() != null) {
                prep.setString(6, device.borrower());
            } else {
                prep.setNull(6, Types.NULL);
            }
            // Setzen des Rückgabedatums, falls vorhanden, sonst setzen auf NULL.
            if (device.returnDate() != null) {
                prep.setDate(7, new java.sql.Date(device.returnDate().getTime()));
            } else {
                prep.setNull(7, Types.DATE);
            }
            prep.setString(8, id);

            int affectedRows = prep.executeUpdate();
            connection.commit();
            return (affectedRows > 0);
        } catch (SQLException e) {
            printSqlErr(e);
            return false;
        }
    }

    /**
     * Hinzufügen des Gerätes mit den Attributen
     * @param device Gerät mit den Attributen
     * @return true, wenn das Gerät erfolgreich hinzugefügt wurde, sonst false.
     */
    public boolean addDevice(Device device) {
        try {
            PreparedStatement prep = connection.prepareStatement("INSERT INTO devices (brand, model, category, purchaseyear, id, borrower, returndate) VALUES (?, ?, ?, ?, ?, ?, ?);");
            prep.setString(1, device.brand());
            prep.setString(2, device.model());
            prep.setString(3, device.category());
            prep.setInt(4, device.purchaseyear());
            prep.setInt(5, device.id());
            prep.setString(6, device.borrower());
            prep.setDate(7, (Date) device.returnDate());

            int affectedRows = prep.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            printSqlErr(e);
            return false;
        }
    }

    /**
     * Ausgabe der SQLException in übersichtlicherer Weise auf der Konsole
     * @param e Die SQLException
     */
    private void printSqlErr(SQLException e) {
        System.err.printf("%n--- Eine SQLException ist aufgetreten ---%n%n");
        while(e != null) {
            System.err.printf("""
                    Message: %s
                    SQLState: %s
                    ErrorCode: %d
                """.stripIndent(), e.getMessage(), e.getSQLState(), e.getErrorCode());
            e = e.getNextException ();
        }
    }

    /**
     * Konvertierung des ResultSets in eine Liste von Gerät-Objekten.
     * @param resultSet Das ResultSet-Objekt aus der SQL-Abfrage
     * @return List mit Geräten
     */
    private List<Device> resultSetToList(ResultSet resultSet) {
        try {
            List<Device> devices = new ArrayList<>();
            while (resultSet.next()) {
                String brand = resultSet.getString("brand");
                String model = resultSet.getString("model");
                String category = resultSet.getString("category");
                int purchaseyear = resultSet.getInt("purchaseyear");
                int id = resultSet.getInt("id");
                String borrower = resultSet.getString("borrower");
                Date returnDate = resultSet.getDate("returndate");
                Device device = new Device(brand, model, category, purchaseyear, id, borrower, returnDate);
                devices.add(device);
            }
            return devices;
        } catch (SQLException e) {
            printSqlErr(e);
            return null;
        }
    }
}
