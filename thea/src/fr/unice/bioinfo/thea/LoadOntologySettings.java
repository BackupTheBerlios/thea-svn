package fr.unice.bioinfo.thea;

import org.openide.options.SystemOption;

/**
 * @author SAÏD, EL KASMI.
 */
public class LoadOntologySettings extends SystemOption {
    
    private String dataAdapter;
    private String dataBaseDriver;
    private String dataBaseUrl;
    private String username;
    private String password;
    private Object[] availableAdapters;
    
    public LoadOntologySettings() {
        availableAdapters = new Object[] { 
                new String("MySQL DataBase Format"),
                new String("HSQLDB DataBase Format")};
        dataBaseDriver = new String("com.mysql.jdbc.Driver");
        dataBaseUrl = new String("jdbc:mysql://localhost/allonto2");
        username = new String("ontology");
    }   
    
    /**
     * @return Returns the dataAdapter.
     */
    public String getDataAdapter() {
        return dataAdapter;
    }
    /**
     * @param dataAdapter The dataAdapter to set.
     */
    public void setDataAdapter(String dataAdapter) {
        this.dataAdapter = dataAdapter;
    }
    /**
     * @return Returns the dataBaseDriver.
     */
    public String getDataBaseDriver() {
        return dataBaseDriver;
    }
    /**
     * @param dataBaseDriver The dataBaseDriver to set.
     */
    public void setDataBaseDriver(String dataBaseDriver) {
        this.dataBaseDriver = dataBaseDriver;
    }
    /**
     * @return Returns the dataBaseUrl.
     */
    public String getDataBaseUrl() {
        return dataBaseUrl;
    }
    /**
     * @param dataBaseUrl The dataBaseUrl to set.
     */
    public void setDataBaseUrl(String dataBaseUrl) {
        this.dataBaseUrl = dataBaseUrl;
    }
    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(char[] password) {
        this.password = new String(password);
    }
    
    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }
    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Returns the availableAdapters.
     */
    public Object[] getAvailableAdapters() {
        return availableAdapters;
    }
    /**
     * @param availableAdapters The availableAdapters to set.
     */
    public void setAvailableAdapters(Object[] availableAdapters) {
        this.availableAdapters = availableAdapters;
    }

    /* (non-Javadoc)
     * @see org.openide.options.SystemOption#displayName()
     */
    public String displayName() {
        // TODO Auto-generated method stub
        return null;
    }
}
