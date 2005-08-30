package fr.unice.bioinfo.thea.ontologyexplorer.infos;

import java.sql.Connection;
import java.util.Hashtable;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.Node;

import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * @author Saïd El Kasmi
 */
public class OntologyNodeInfo extends Hashtable implements Node.Cookie {
    // Serial Version UID for serialization
    static final long serialVersionUID = 117624370716568244L;

    public static final String NAME = "name"; // NOI18N

    public static final String CONNECTION = "connection"; // NOI18N

    // public static final String CONFIGURATION = "configuration"; // NOI18N

    /*
     * (non-Javadoc)
     * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
     */
    public synchronized Object put(Object key, Object value) {
        // return super.put(key, value);
        Object old = get(key);

        if (key == null)
            throw new NullPointerException();

        if (value != null)
            super.put(key, value);
        else
            remove(key);

        return old;
    }

    /** Returns the name */
    public String getName() {
        return (String) get(NAME);
    }

    /** Sets a name */
    public void setName(String name) {
        put(NAME, name);
    }

    /** Returns the JDBC Connection */
    public Connection getConnection() {
        return (Connection) get(CONNECTION);
    }

    /** Sets a JDBC Connection */
    public void setConnection(Connection con) {
        put(CONNECTION, con);
    }

    // /** Returns the configuration associated with a node */
    // public Configuration getConfiguration() {
    // return (Configuration) get(CONFIGURATION);
    // }
    //
    // /** Sets a configuration */
    // public void setConfiguration(Configuration config) {
    // put(CONFIGURATION, config);
    // }

    /** Called by property editor */
    public Object getProperty(String key) {
        return get(key);
    }

    /** Called by property editor */
    public void setProperty(String key, Object obj) {
        put(key, obj);
    }
}