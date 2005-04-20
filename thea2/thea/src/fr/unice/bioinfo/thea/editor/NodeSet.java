package fr.unice.bioinfo.thea.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * A class representing a set of nodes.
 */
public class NodeSet {
    /** The List of nodes */
    private List nodes;

    /** User data associated with this object */
    private Map userData;

    /**
     * Default constructor
     */
    public NodeSet() {
        init();
    }

    public NodeSet(List nodes) {
        this.nodes = nodes;
        userData = new HashMap();
    }

    /**
     * Initialize NodeSets
     */
    private void init() {
        nodes = new Vector();
        userData = new HashMap();
    }

    /**
     * Setter for userData. Allows external applications to associate data with
     * this object
     * @param key The key used to access external data
     * @param o The object associated with this node
     */
    public void setUserData(String key, Object o) {
        userData.put(key, o);
    }

    /**
     * Getter for userData. Allows external applications to retrieve data
     * associated with this object
     * @param key The key used to access external data
     * @return The object associated with this node
     */
    public Object getUserData(String key) {
        return userData.get(key);
    }

    public void setUserData(Map m) {
        userData = m;
    }

    public Map getUserData() {
        return userData;
    }

    /**
     * Getter for nodes
     * @return the List of nodes
     */
    public List getNodes() {
        return nodes;
    }

    /**
     * Setter for nodes
     * @param nodes the set of nodes
     * @return the List of nodes
     */
    public void setNodes(List nodes) {
        this.nodes = new Vector(nodes);
    }
}