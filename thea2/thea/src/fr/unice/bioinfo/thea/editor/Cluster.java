package fr.unice.bioinfo.thea.editor;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines a cluster in a classification. A cluster is a collection
 * of nodes.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Cluster {
    /** A cluster is a collection of nodes. */
    private List nodes;
    /** Properties list for a cluster. */
    private Hashtable properties;

    /**
     * Create a cluster using a given list of nodes.
     * @param nodes Collection of nodes that form a cluster.
     */
    public Cluster(List nodes) {
        this();
        this.nodes = nodes;
    }

    /** Creates an empty cluster. */
    public Cluster() {
        nodes = new LinkedList();
        properties = new Hashtable();
    }

    /**
     * Adds a child to the this cluster's nodes list.
     * @param aNode A child to add.
     */
    public void addNode(Node aNode) {
        nodes.add(aNode);
    }

    /**
     * Remove the given node from this cluster's nodes list.
     * @param aNode The child to remove.
     */
    public void removeNode(Node aNode) {
        nodes.remove(aNode);
    }

    /**
     * Returns list of properties of this cluster.
     * @return java.util.Hashtable Properties of this node.
     */
    public Hashtable getProperties() {
        return properties;
    }

    /**
     * Adds a property to this cluster's properties list.
     * @param key The name of tht property.
     * @param value The value of the property.
     */
    public void addProperty(Object key, Object value) {
        properties.put(key, value);
    }

    /**
     * Returns a property from this cluster's properties list using the given
     * key as a property name.
     * @param key The name of tht property.
     * @return java.lang.Object A property associated the given key.
     */
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    /**
     * Set a list of nodes that form this cluster.
     * @param nodes Collection of nodes that form a cluster.
     */
    public void setNodes(List nodes) {
        this.nodes = nodes;
    }
}