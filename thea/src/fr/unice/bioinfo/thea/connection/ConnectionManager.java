package fr.unice.bioinfo.thea.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.unice.bioinfo.thea.LoadOntologySettings;

/**
 * @author SAÏD, EL KASMI.
 */
public class ConnectionManager {

    private static Connection connection = null;
    
    /**
     * Create a JDBC Connection and return it.
     * @param loadOntologySettings
     * @return connection.
     */
    public static Connection initConnection(
            LoadOntologySettings loadOntologySettings) {
        // Get settings:
        String dataAdapter = loadOntologySettings.getDataAdapter();
        String dataBaseDriver = loadOntologySettings.getDataBaseDriver();
        String dataBaseUrl = loadOntologySettings.getDataBaseUrl();
        String username = loadOntologySettings.getUsername();
        String password = loadOntologySettings.getPassword();
        // Driver existence !
        try {
            Class.forName(dataBaseDriver);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Driver not loaded: "+cnfe.toString());
            cnfe.printStackTrace();
        }
        // Get the connection
        try {
            connection = DriverManager.getConnection(dataBaseUrl,username,password);
        } catch (SQLException sqle) {
            System.out.println("Connection not created: "+sqle.toString());
            sqle.printStackTrace();
        }
        return connection;
    }
    /**
     * @return Returns the connection.
     */
    public static Connection getConnection() {
        return connection;
    }
}