package fr.unice.bioinfo.thea.ontologyexplorer.infos;

import java.util.Hashtable;

import org.openide.nodes.Node;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author Saïd El Kasmi
 */
public class ResourceNodeInfo extends Hashtable implements Node.Cookie {
    // Serial Version UID for serialization
    static final long serialVersionUID = 1176243907461868244L;

    public static final String NAME = "name"; // NOI18N
    // public static final String ONTOLOGY_NODE = "ontology_node"; //NOI18N

    private Resource resource = null;

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

    // /** Returns the ontology node of this resource */
    // public OntologyNode getOntologyNode() {
    // return (OntologyNode) get(ONTOLOGY_NODE);
    // }
    //
    // /** Sets the ontology node for this resource */
    // public void setOntologyNode(OntologyNode on) {
    // put(ONTOLOGY_NODE, on);
    // }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}