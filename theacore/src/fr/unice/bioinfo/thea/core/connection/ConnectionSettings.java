package fr.unice.bioinfo.thea.core.connection;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/**
 * A persistent class used to store settings related to databases connections.
 * It will save entred settings and reload them each time a module asks for a
 * connection to database used in Thea.
 * <P>
 * This class is not to be instanciated directtly. Alwyas use the
 * <i>getInstance() <i>as a look up method to get the singleton instance created
 * according to the netbeans options API specifications.
 * @author SAÏD, EL KASMI.
 */
public class ConnectionSettings extends SystemOption {

    /** generated Serialized Version UID */
    static final long serialVersionUID = 801133540702917911L;

    /*
     * (non-Javadoc)
     * @see org.openide.options.SystemOption#displayName()
     */
    public String displayName() {
        return NbBundle.getMessage(ConnectionSettings.class,
                "LBL_ConnectionSettings_Name");
    }

    protected void initialize() {
        super.initialize();
    }

    /** Returns the unique instance of this class. */
    public static ConnectionSettings getInstance() {
        return ((ConnectionSettings) ConnectionSettings.findObject(
                ConnectionSettings.class, true));
    }

    /** Data Adapter ie: Data Base Shemea (type)*/
    private static String dataAdapter;

    /** Property name for dataAdapter */
    public static final String PROP_DATA_ADAPTER = "dataAdapter";// NOI18N

    /** Driver used to connect to teh DataBase */
    private static String dataBaseDriver = "com.mysql.jdbc.Driver";

    /** Property name for dataBaseDriver */
    public static final String PROP_DATABASE_DRIVER = "dataBaseDriver";// NOI18N

    /** URL to the DataBase */
    private static String dataBaseUrl = "jdbc:mysql://localhost/allonto";

    /** Property name for dataBaseUrl */
    public static final String PROP_DATABASE_URL = "dataBaseUrl";// NOI18N

    /** User name */
    private static String username = "uname";

    /** Property name for username */
    public static final String PROP_USERNAME = "username";// NOI18N

    /** User's password.*/
    private static String password;

    /** Property name for password */
    public static final String PROP_PASSWORD = "password";// NOI18N

    public static String MYSQL_DA = new String("MySQL DataBase Format");

    public static String HSQLDB_DA = new String("HSQLDB DataBase Format");

    /** List of supported adapters */
    private static Object[] availableAdapters = new Object[] { MYSQL_DA,
            HSQLDB_DA };

    /** Last used data adapter */
    private static String lastSelectedDataAdapter = MYSQL_DA;

    /** Property name for lastSelectedDataAdapter */
    public static final String PROP_LAST_SELECTED_DA = "lastSelectedDataAdapter";// NOI18N

    /** Default driver for the MySql Data Adapter.*/
    private static String mysqlFormatDBDriver = "com.mysql.jdbc.Driver";

    /** Property name for mysqlFormatDBDriver */
    public static final String PROP_MYSQL_DRIVER = "mysqlFormatDBDriver";// NOI18N

    /** Default DB's URL for MySql Data Adapter */
    private static String mysqlFormartURL = "jdbc:mysql://localhost/allonto";

    /** Property name for mysqlFormartURL */
    public static final String PROP_MYSQL_URL = "mysqlFormartURL";// NOI18N

    /** Default driver for the HSQLDB Data Adapter.*/
    private static String hsqldbFormatDBDriver = "";

    /** Property name for hsqldbFormatDBDriver */
    public static final String PROP_HSQLDB_DRIVER = "hsqldbFormatDBDriver";// NOI18N

    /** Default DB's URL for HSQLDB Data Adapter */
    private static String hsqldbFormartURL = "";

    /** Property name for hsqldbFormartURL */
    public static final String PROP_HSQLDB_URL = "hsqldbFormartURL";// NOI18N

    /** Property name for password */
    public static final String PROP_AVAILABLE_ADAPTERS = "availableAdapters";// NOI18N

    /** Returns dataAdapter */
    public String getDataAdapter() {
        return dataAdapter;
    }

    /** Sets dataAdapter to new value */
    public void setDataAdapter(String value) {
        value = value == null ? "" : value;
        String old = getDataAdapter();
        if (!value.equals(old)) {
            dataAdapter = value;
            firePropertyChange(PROP_DATA_ADAPTER, old, value);
        }
    }

    /** Returns dataBaseDriver */
    public String getDataBaseDriver() {
        return dataBaseDriver;
    }

    /** Sets dataBaseDriver to new value */
    public void setDataBaseDriver(String value) {
        value = value == null ? "" : value;
        String old = getDataBaseDriver();
        if (!value.equals(old)) {
            dataBaseDriver = value;
            firePropertyChange(PROP_DATABASE_DRIVER, old, value);
        }
    }

    /** Returns dataBaseUrl */
    public String getDataBaseUrl() {
        return dataBaseUrl;
    }

    /** Sets dataBaseUrl to a new value */
    public void setDataBaseUrl(String value) {
        value = value == null ? "" : value;
        String old = getDataBaseUrl();
        if (!value.equals(old)) {
            dataBaseUrl = value;
            firePropertyChange(PROP_DATABASE_URL, old, value);
        }
    }

    /** Returns the password */
    public String getPassword() {
        return password;
    }

    /** Sets the password */
    public void setPassword(String value) {
        value = value == null ? "" : value;
        String old = getPassword();
        if (!value.equals(old)) {
            password = value;
            firePropertyChange(PROP_PASSWORD, old, value);
        }
    }

    /** Returns the username */
    public String getUsername() {
        return username;
    }

    /** Sets username to value */
    public void setUsername(String value) {
        value = value == null ? "" : value;
        String old = getUsername();
        if (!value.equals(old)) {
            username = value;
            firePropertyChange(PROP_USERNAME, old, value);
        }
    }

    /** returns the list of available data adapters */
    public Object[] getAvailableAdapters() {
        return availableAdapters;
    }

    public void setAvailableAdapters(Object[] availableAdapters) {
        ConnectionSettings.availableAdapters = availableAdapters;
    }

    /**
     * @return Returns the hsqldbFormartURL.
     */
    public String getHsqldbFormartURL() {
        return hsqldbFormartURL;
    }

    /**
     * @param hsqldbFormartURL The hsqldbFormartURL to set.
     */
    public void setHsqldbFormartURL(String value) {
        value = value == null ? "" : value;
        String old = getHsqldbFormartURL();
        if (!value.equals(old)) {
            hsqldbFormartURL = value;
            firePropertyChange(PROP_HSQLDB_URL, old, value);
        }
    }

    /**
     * @return Returns the hsqldbFormatDBDriver.
     */
    public String getHsqldbFormatDBDriver() {
        return hsqldbFormatDBDriver;
    }

    /**
     * @param hsqldbFormatDBDriver The hsqldbFormatDBDriver to set.
     */
    public void setHsqldbFormatDBDriver(String value) {
        value = value == null ? "" : value;
        String old = getHsqldbFormatDBDriver();
        if (!value.equals(old)) {
            hsqldbFormatDBDriver = value;
            firePropertyChange(PROP_HSQLDB_DRIVER, old, value);
        }
    }

    /**
     * @return Returns the lastSelectedDataAdapter.
     */
    public String getLastSelectedDataAdapter() {
        return lastSelectedDataAdapter;
    }

    /**
     * @param lastSelectedDataAdapter The lastSelectedDataAdapter to set.
     */
    public void setLastSelectedDataAdapter(String value) {
        value = value == null ? "" : value;
        String old = getLastSelectedDataAdapter();
        if (!value.equals(old)) {
            lastSelectedDataAdapter = value;
            firePropertyChange(PROP_LAST_SELECTED_DA, old, value);
        }
    }

    /**
     * @return Returns the mysqlFormartURL.
     */
    public String getMysqlFormartURL() {
        return mysqlFormartURL;
    }

    /**
     * @param mysqlFormartURL The mysqlFormartURL to set.
     */
    public void setMysqlFormartURL(String value) {
        value = value == null ? "" : value;
        String old = getMysqlFormartURL();
        if (!value.equals(old)) {
            mysqlFormartURL = value;
            firePropertyChange(PROP_MYSQL_URL, old, value);
        }
    }

    /**
     * @return Returns the mysqlFormatDBDriver.
     */
    public String getMysqlFormatDBDriver() {
        return mysqlFormatDBDriver;
    }

    /**
     * @param mysqlFormatDBDriver The mysqlFormatDBDriver to set.
     */
    public void setMysqlFormatDBDriver(String value) {
        value = value == null ? "" : value;
        String old = getMysqlFormatDBDriver();
        if (!value.equals(old)) {
            mysqlFormatDBDriver = value;
            firePropertyChange(PROP_MYSQL_DRIVER, old, value);
        }
    }
}