package fr.unice.bioinfo.thea.ontologyexplorer.db;

import fr.unice.bioinfo.allonto.persistence.PersistentResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.sql.DataSource;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;

import fr.unice.bioinfo.thea.ontologyexplorer.db.driver.JDBCDriver;
import fr.unice.bioinfo.thea.ontologyexplorer.db.driver.JDBCDriverManager;

/**
 * Connection information This class encapsulates all information needed for
 * connection to database (database and driver url, login name, password and
 * schema name). It can create JDBC connection and feels to be a bean (has
 * propertychange support and customizer). Instances of this class uses explorer
 * option to store information about open connection.
 */
public class DatabaseConnection implements DBConnection {
    static final ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.db.Bundle"); // NOI18N
    
    static final long serialVersionUID = 4554639187416958735L;
    
    private static final String SUPPORT = "_schema_support"; // NOI18N
    
    public static final String PROP_DRIVER = "driver"; // NOI18N
    
    public static final String PROP_DATABASE = "database"; // NOI18N
    
    public static final String PROP_USER = "user"; // NOI18N
    
    public static final String PROP_PASSWORD = "password"; // NOI18N
    
    public static final String PROP_SCHEMA = "schema"; // NOI18N
    
    public static final String PROP_DRIVERNAME = "drivername"; // NOI18N
    
    public static final String PROP_NAME = "name"; // NOI18N
    
    private Set exceptionListeners = Collections.synchronizedSet(new HashSet());
    
    private Connection con;
    
    /** Driver URL and name */
    private String drv;
    
    /** Driver URL and name */
    private String drvname;
    
    /** Database URL */
    private String db;
    
    /** User login name */
    private String usr;
    
    /** Schema name */
    private String schema;
    
    /** User password */
    private String pwd = ""; // NOI18N
    
    /** Remembers password */
    private Boolean rpwd = Boolean.FALSE;
    
    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport;
    
    /** Connection name */
    private String name;
    
    OpenConnectionInterface openConnection;
    
    /**
     * Default constructor
     */
    public DatabaseConnection() {
        propertySupport = new PropertyChangeSupport(this);
        
        // For Java Studio Enterprise. Create instanceof OpenConnection
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            openConnection = (OpenConnectionInterface) Class.forName(
                    bundle.getString("CLASS_open_connection"), true, cl)
                    .newInstance();
        } catch (Exception ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
        }
    }
    
    /**
     * Advanced constructor Allows to specify all needed information.
     *
     * @param driver
     *            Driver URL
     * @param database
     *            Database URL
     * @param user
     *            User login name
     * @param password
     *            User password
     */
    public DatabaseConnection(String driver, String database, String user,
            String password) {
        this();
        drv = driver;
        db = database;
        usr = user;
        pwd = password;
        name = null;
        name = getName();
    }
    
    /**
     * Returns driver URL
     */
    public String getDriver() {
        return drv;
    }
    
    /**
     * Sets driver URL Fires propertychange event.
     *
     * @param driver
     *            DNew driver URL
     */
    public void setDriver(String driver) {
        if ((driver == null) || driver.equals(drv)) {
            return;
        }
        
        String olddrv = drv;
        drv = driver;
        propertySupport.firePropertyChange(PROP_DRIVER, olddrv, drv);
    }
    
    public String getDriverName() {
        return drvname;
    }
    
    public void setDriverName(String name) {
        if ((name == null) || name.equals(drvname)) {
            return;
        }
        
        String olddrv = drvname;
        drvname = name;
        
        if (propertySupport != null) {
            propertySupport
                    .firePropertyChange(PROP_DRIVERNAME, olddrv, drvname);
        }
    }
    
    /**
     * Returns database URL
     */
    public String getDatabase() {
        if (db == null) {
            db = "";
        }
        
        return db;
    }
    
    /**
     * Sets database URL Fires propertychange event.
     *
     * @param database
     *            New database URL
     */
    public void setDatabase(String database) {
        if ((database == null) || database.equals(db)) {
            return;
        }
        
        String olddb = db;
        db = database;
        name = null;
        name = getName();
        
        if (propertySupport != null) {
            propertySupport.firePropertyChange(PROP_DATABASE, olddb, db);
        }
    }
    
    /**
     * Returns user login name
     */
    public String getUser() {
        if (usr == null) {
            usr = "";
        }
        
        return usr;
    }
    
    /**
     * Sets user login name Fires propertychange event.
     *
     * @param user
     *            New login name
     */
    public void setUser(String user) {
        if ((user == null) || user.equals(usr)) {
            return;
        }
        
        String oldusr = usr;
        usr = user;
        name = null;
        name = getName();
        
        if (propertySupport != null) {
            propertySupport.firePropertyChange(PROP_USER, oldusr, usr);
        }
    }
    
    /**
     * Returns name of the connection
     */
    public String getName() {
        if (name == null) {
            if ((getSchema() == null) || (getSchema().length() == 0)) {
                name = MessageFormat.format(bundle
                        .getString("ConnectionNodeUniqueName"), new String[] {
                    getDatabase(), getUser(),
                    bundle.getString("SchemaIsNotSet") }); // NOI18N
            } else {
                name = MessageFormat.format(bundle
                        .getString("ConnectionNodeUniqueName"), new String[] {
                    getDatabase(), getUser(), getSchema() }); // NOI18N
            }
        }
        
        return name;
    }
    
    public String getDisplyName() {
        return null;
    }
    
    /**
     * Sets user name of the connection Fires propertychange event.
     *
     * @param value
     *            New connection name
     */
    public void setName(String value) {
        if ((name == null) || name.equals(value)) {
            return;
        }
        
        String old = name;
        name = value;
        
        if (propertySupport != null) {
            propertySupport.firePropertyChange(PROP_NAME, old, name);
        }
    }
    
    /**
     * Returns user schema name
     */
    public String getSchema() {
        if (schema == null) {
            schema = "";
        }
        
        return schema;
    }
    
    /**
     * Sets user schema name Fires propertychange event.
     *
     * @param schema_name
     *            New login name
     */
    public void setSchema(String schema_name) {
        if ((schema_name == null) || schema_name.equals(schema)) {
            return;
        }
        
        String oldschema = schema;
        schema = schema_name;
        name = null;
        name = getName();
        
        if (propertySupport != null) {
            propertySupport.firePropertyChange(PROP_SCHEMA, oldschema, schema);
        }
    }
    
    /**
     * Returns if password should be remembered
     */
    public boolean rememberPassword() {
        return rpwd.equals(Boolean.TRUE);
    }
    
    /**
     * Sets password should be remembered
     *
     * @param flag
     *            New flag
     */
    public void setRememberPassword(boolean flag) {
        rpwd = (flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Returns password
     */
    public String getPassword() {
        return pwd;
    }
    
    /**
     * Sets password Fires propertychange event.
     *
     * @param password
     *            New password
     */
    public void setPassword(String password) {
        if ((password == null) || password.equals(pwd)) {
            return;
        }
        
        String oldpwd = pwd;
        pwd = password;
        
        if (propertySupport != null) {
            propertySupport.firePropertyChange(PROP_PASSWORD, oldpwd, pwd);
        }
    }
    
    /**
     * Creates JDBC connection Uses DriverManager to create connection to
     * specified database. Throws DDLException if none of
     * driver/database/user/password is set or if driver or database does not
     * exist or is inaccessible.
     */
    public Connection createJDBCConnection() throws DDLException {
        System.out.println("entering createJDBCConnection method");
        if ((drv == null) || (db == null) || (usr == null) || (pwd == null)) {
            throw new DDLException(bundle.getString("EXC_InsufficientConnInfo"));
        }
        
        Properties dbprops = new Properties();
        dbprops.put("user", usr); // NOI18N
        dbprops.put("password", pwd); // NOI18N
        
        try {
            propertySupport.firePropertyChange("connecting", null, null);
            
            Connection connection;
            JDBCDriver[] drvs = JDBCDriverManager.getDefault().getDriver(drv);
            
            // For Java Studio Enterprise.
            openConnection.enable();
            checkRuntime();
            
            if (drvs.length == 0) {
                Class.forName(drv);
                connection = DriverManager.getConnection(db, dbprops);
            } else {
                int drvIndex = 0;
                
                for (int i = 0; i < drvs.length; i++)
                    if (drvs[i].getName().equals(getDriverName())) {
                    drvIndex = i;
                    
                    break;
                    }
                
                DbURLClassLoader l = new DbURLClassLoader(drvs[drvIndex]
                        .getURLs());
                Class c = Class.forName(drv, true, l);
                Driver d = (Driver) c.newInstance();
                connection = d.connect(db, dbprops);
            }
            
            propertySupport.firePropertyChange("connected", null, null);
            
            // For Java Studio Enterprise.
            openConnection.disable();
            
            return connection;
        } catch (SQLException e) {
            String message = MessageFormat.format(bundle
                    .getString("EXC_CannotEstablishConnection"), new String[] {
                db, drv, e.getMessage() }); // NOI18N
            
            // commented out for 3.6 release, need to solve for next Studio
            // release
            // hack for Pointbase Network Server
            // if(drv.equals(PointbasePlus.DRIVER))
            // if(e.getErrorCode()==PointbasePlus.ERR_SERVER_REJECTED)
            // message =
            // MessageFormat.format(bundle.getString("EXC_PointbaseServerRejected"),
            // new String[] {message, db}); // NOI18N
            propertySupport.firePropertyChange("failed", null, null);
            
            // For Java Studio Enterprise.
            openConnection.disable();
            
            throw new DDLException(message);
        } catch (Exception exc) {
            String message = MessageFormat.format(bundle
                    .getString("EXC_CannotEstablishConnection"), new String[] {
                db, drv, exc.getMessage() }); // NOI18N
            
            propertySupport.firePropertyChange("failed", null, null);
            
            // For Java Studio Enterprise.
            openConnection.disable();
            
            throw new DDLException(message);
        }
    }
    
    public void connect() {
        createConnectTask();
    }
    
    public void disconnect() {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    getConnection().close();
                    propertySupport.firePropertyChange("disconnected", null,
                            null);
                } catch (SQLException e) {
                    ErrorManager.getDefault().notify(e);
                    
                    // e.printStackTrace();
                }
            }
        }, 0);
    }
    
    public Task createConnectTask() {
        return RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                if ((drv == null) || (db == null) || (usr == null)
                || (pwd == null)) {
                    sendException(new DDLException(bundle
                            .getString("EXC_InsufficientConnInfo")));
                }
                
                Properties dbprops = new Properties();
                dbprops.put("user", usr); // NOI18N
                dbprops.put("password", pwd); // NOI18N
                
                try {
                    propertySupport
                            .firePropertyChange("connecting", null, null);
                    
                    // For Java Studio Enterprise.
                    openConnection.enable();
                    
                    Connection connection;
                    JDBCDriver[] drvs = JDBCDriverManager.getDefault()
                    .getDriver(drv);
                    
                    // For Java Studio Enterprise.
                    openConnection.enable();
                    checkRuntime();
                    if (drvs.length == 0) {
                        Class.forName(drv);
                        connection = DriverManager.getConnection(db, dbprops);
                    } else {
                        int drvIndex = 0;
                        
                        for (int i = 0; i < drvs.length; i++)
                            if (drvs[i].getName().equals(getDriverName())) {
                            drvIndex = i;
                            
                            break;
                            }
                        
                        DbURLClassLoader l = new DbURLClassLoader(
                                drvs[drvIndex].getURLs());
                        Class c = Class.forName(drv, true, l);
                        Driver d = (Driver) c.newInstance();
                        connection = d.connect(db, dbprops);
                    }
                    
                    setConnection(connection);
                    DataSource ds = AllontoFactory.createDataSource(getName(),getDriver(), getDatabase(), getUser(), getPassword());
                    AllontoFactory.registerResourceFactory(getName(), new PersistentResourceFactory(ds));
                    propertySupport.firePropertyChange("connected", null, null);
                    
                    // For Java Studio Enterprise.
                    openConnection.disable();
                } catch (SQLException e) {
                    System.out.println("connection12. SQL exception");
                    String message = MessageFormat.format(bundle
                            .getString("EXC_CannotEstablishConnection"),
                            new String[] { db, drv, e.getMessage() }); // NOI18N
                    // commented out for 3.6 release, need to solve for next
                    // Studio release
                    // hack for Pointbase Network Server
                    // if (drv.equals(PointbasePlus.DRIVER))
                    // if (e.getErrorCode() ==
                    // PointbasePlus.ERR_SERVER_REJECTED)
                    // message =
                    // MessageFormat.format(bundle.getString("EXC_PointbaseServerRejected"),
                    // new String[] {message, db}); // NOI18N
                    propertySupport.firePropertyChange("failed", null, null);
                    
                    // For Java Studio Enterprise.
                    openConnection.disable();
                    
                    sendException(new DDLException(message));
                } catch (Exception exc) {
                    propertySupport.firePropertyChange("failed", null, null);
                    
                    // For Java Studio Enterprise.
                    openConnection.disable();
                    
                    sendException(exc);
                }
            }
        }, 0);
    }
    
    private void checkRuntime() {
        // DatabaseRuntime runtime = DatabaseRuntimeManager.getDefault()
        // .getRuntime(drv);
        //
        // // If DatabaseRuntimeManager does not have an entry for the runtime,
        // do
        // // not check whether
        // // it is running or attempt to start it.
        // if (runtime == null)
        // return;
        //
        // if (runtime.isRunning())
        // return;
        // if (runtime.canStart() && runtime.acceptsConnectionUrl(db))
        // runtime.start();
    }
    
    public void addExceptionListener(ExceptionListener l) {
        if (l != null) {
            exceptionListeners.add(l);
        }
    }
    
    public void removeExceptionListener(ExceptionListener l) {
        exceptionListeners.remove(l);
    }
    
    private void sendException(Exception exc) {
        synchronized (exceptionListeners) {
            Iterator it = exceptionListeners.iterator();
            
            while (it.hasNext()) {
                ExceptionListener l = (ExceptionListener) it.next();
                l.exceptionOccurred(exc);
            }
        }
    }
    
    private void setConnection(Connection c) {
        con = c;
    }
    
    public Connection getConnection() {
        return con;
    }
    
    /**
     * Add property change listener Registers a listener for the PropertyChange
     * event. The connection object should fire a PropertyChange event whenever
     * somebody changes driver, database, login name or password.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }
    
    /**
     * Remove property change listener Remove a listener for the PropertyChange
     * event.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }
    
    public int hashCode() {
        return drv.hashCode() + db.hashCode() + usr.hashCode();
    }
    
    /**
     * Compares two connections. Returns true if driver, database and login name
     * equals.
     */
    public boolean equals(Object obj) {
        if (obj instanceof DBConnection) {
            DBConnection con = (DBConnection) obj;
            
            return toString().equals(con.toString());
        }
        
        return false;
    }
    
    /**
     * Reads object from stream
     */
    private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, ClassNotFoundException {
        drv = (String) in.readObject();
        db = (String) in.readObject();
        usr = (String) in.readObject();
        schema = (String) in.readObject();
        rpwd = Boolean.FALSE;
        name = (String) in.readObject();
        
        try {
            drvname = (String) in.readObject();
        } catch (Exception exc) {
            // IGNORE - not stored in 3.6 and earlier
        }
        
        // boston setting/pilsen setting?
        if ((name != null) && (name.equals(DatabaseConnection.SUPPORT))) {
            // pilsen
        } else {
            // boston
            schema = null;
        }
        
        name = null;
        name = getName();
    }
    
    /**
     * Writes object to stream
     */
    private void writeObject(java.io.ObjectOutputStream out)
    throws java.io.IOException {
        out.writeObject(drv);
        out.writeObject(db);
        out.writeObject(usr);
        out.writeObject(schema);
        out.writeObject(DatabaseConnection.SUPPORT);
        out.writeObject(drvname);
    }
    
    public String toString() {
        return "Driver:" + drv + "Database:" + db.toLowerCase() + "User:"
                + usr.toLowerCase() + "Schema:" + schema.toLowerCase();
    }
}