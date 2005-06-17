package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * A Java class that represents node in the ontology explorer. It calculates
 * dynamically keys to build children. For each key a node is created using the
 * <code>createNode(Object key)</code> method.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ResourceNodeChildren extends Children.Keys {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); //NOI18N;

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
     * @see org.openide.nodes.Children#removeNotify()
     */
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Children.Keys#createNodes(java.lang.Object)
     */
    protected Node[] createNodes(Object key) {
        Resource aResource = (Resource) key;
        ResourceNode rn = new ResourceNode(aResource);
        ResourceNodeInfo rni = new ResourceNodeInfo();
        rni.setResource(aResource);
        //rni.setConnection(dbc.getConnection());
        rn.setInfo(rni);
        // associate an icon dependening on the relation
        // between the children's resource and the parent's one
        ResourceNode aResourceNode = (ResourceNode) getNode();

        if (aResourceNode != null) {
            try {
                Session sess = HibernateUtil.currentSession();
                if (!sess.isConnected()) {
                    sess.reconnect();
                }
            } catch (HibernateException e) {
                e.printStackTrace();
            }
            Set pchilds = resource.getTargets(resourceFactory
                    .getProperty(OWLProperties.getInstance()
                            .getPartofPropertyName()));
            Set ichilds = resource.getTargets(resourceFactory
                    .getProperty(OWLProperties.getInstance()
                            .getIsaPropertyName()));

            if (pchilds != null) {
                if (pchilds.contains(aResource)) {
                    rn
                            .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/partOfIcon");//NOI18N
                }
            }

            if (ichilds != null) {
                if (ichilds.contains(aResource)) {
                    rn
                            .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/isAIcon");//NOI18N
                }
            }
        }
        return new Node[] { (Node) rn };
    }

    /**
     * Updates keys. A key is simply a Resource.
     * @param resource Resource
     * @return List of keys.
     */
    private Set findSubResources(Resource resource) {
        try {
            Session sess = HibernateUtil.currentSession();
            if (!sess.isConnected()) {
                sess.reconnect();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        Set childs = ((Resource) resource).getTargets(OWLProperties
                .getInstance().getHierarchyProperties());
        return childs;
    }
}