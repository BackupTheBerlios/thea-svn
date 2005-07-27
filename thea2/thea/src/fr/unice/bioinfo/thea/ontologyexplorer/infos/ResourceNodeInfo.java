package fr.unice.bioinfo.thea.ontologyexplorer.infos;

import java.sql.Connection;
import java.util.Hashtable;

import org.openide.nodes.Node;

import fr.unice.bioinfo.allonto.datamodel.Resource;

/**
 * @author Saïd El Kasmi
 */
public class ResourceNodeInfo extends Hashtable implements Node.Cookie {
    // Serial Version UID for serialization
    static final long serialVersionUID = 1176243907461868244L;
    public static final String NAME = "name"; //NOI18N
    public static final String CONNECTION = "conection"; //NOI18N

    private Resource resource = null;

    /*
     * (non-Javadoc)
     * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
     */
    public synchronized Object put(Object key, Object value) {
        //return super.put(key, value);
        Object old = get(key);

        if (key == null)
            throw new NullPointerException();

        if (value != null)
            super.put(key, value);
        else
            remove(key);

        return old;
    }

    /** Called by property editor */
    public Object getProperty(String key) {
        return get(key);
    }

    /** Called by property editor */
    public void setProperty(String key, Object obj) {
        put(key, obj);
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

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}