package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * A Java class that represents node in the ontology explorer. It calculates
 * dynamically keys to build children. A key is represented by an array of two
 * elements: a resource and a path to the icon to be associated to the
 * relationship between the node and the children. For each key a node is
 * created using the <code>createNode(Object key)</code> method.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ResourceNodeChildren extends Children.Keys {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); // NOI18N;

    /** Resource associated with this children. */
    private Resource resource;

    private ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();

    /** Creates new ResourceChildren. */
    public ResourceNodeChildren(Resource resource) {
        this.resource = resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Children#addNotify()
     */
    protected void addNotify() {
        Set keys = findSubResources(this.resource);
        if (keys != null) {
            setKeys(keys);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Children#removeNotify()
     */
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }

    /**
     * createNodes creates a node representing the key.
     * 
     * @param key
     *            A key is an array of two elements: a resource and a path to
     *            the associated icon
     * @return Array of created nodes (this function creates one node for each
     *         key.
     */
    protected Node[] createNodes(Object key) {
        Object[] tuple = (Object[]) key;
        Resource aResource = (Resource) tuple[0];
        ResourceNode rn = new ResourceNode(aResource);
        ResourceNodeInfo rni = new ResourceNodeInfo();
        rni.setResource(aResource);
        rn.setInfo(rni);
        // associate an icon dependening on the relation
        // between the children's resource and the parent's one
        ResourceNode aResourceNode = (ResourceNode) getNode();

        if (aResourceNode != null) {

            Object iconUrl = tuple[1];
            if (iconUrl != null) {
                rn.setIconBase((String) iconUrl);
            }
        }
        return new Node[] { (Node) rn };
    }

    /**
     * Updates keys. A key is represented by an array of three elements: a
     * resource and a path to the associated icon
     * 
     * @param resource
     *            Resource
     * @return List of keys.
     */
    private Set findSubResources(Resource resource) {

        Set allTargets = new HashSet();
        java.util.Map hierarchyDescription = OntologyProperties.getInstance()
                .getHierarchyDescription(
                        ((ResourceNode) getNode()).getConfiguration());

        Iterator it = hierarchyDescription.values().iterator();

        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();

            Resource prop = (Resource) tuple[0];
            Criterion crit = (Criterion) tuple[1];
            String iconUrl = (String) tuple[2];

            Set targets = null;
            try {
                targets = resource.getTargets(prop, crit);
            } catch (AllontoException ae) {
            }

            if (targets != null) {
                Iterator it2 = targets.iterator();
                while (it2.hasNext()) {
                    allTargets.add(new Object[] { it2.next(), iconUrl });
                }
            }
        }
        if (allTargets.isEmpty()) {
            allTargets = null;
        }

        return allTargets;
    }
}