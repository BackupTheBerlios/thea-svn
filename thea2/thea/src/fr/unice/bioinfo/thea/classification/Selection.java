package fr.unice.bioinfo.thea.classification;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Selection {

    /***/
    public static final String COLOR = "color";//NOI18N
    /***/
    public static final String BG_COLOR = "bgcolor";//NOI18N
    /***/
    public static final String SEL_ID = "selId";//NOI18N
    /***/
    public static final String SEL_NAME = "selName";//NOI18N
    /***/
    public static final String FROZEN = "frozen";//NOI18N
    
    /** The List of nodes */
    private List nodes;

    /** User data associated with this object */
    private Hashtable properties = new Hashtable();

    public Selection() {
        nodes = new ArrayList();
    }

    public Selection(List nodes) {
        this.nodes = nodes;
    }

    /**
     * Setter for properties. Allows external applications to associate data
     * with this object
     * @param key The key used to access external data
     * @param o The object associated with this node
     */
    public void addProperty(String key, Object o) {
        properties.put(key, o);
    }

    /**
     * Getter for properties. Allows external applications to retrieve data
     * associated with this object
     * @param key The key used to access external data
     * @return The object associated with this node
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    /** @return The List of nodes. */
    public List getNodes() {
        return nodes;
    }
}