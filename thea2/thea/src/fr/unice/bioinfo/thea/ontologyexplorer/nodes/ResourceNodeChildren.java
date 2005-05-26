package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * A Java class that represents node in the ontology explorer. It calculates
 * dynamically keys to build children. For each key a node is created using the
 * <code>createNode(Object key)</code> method.
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class ResourceNodeChildren extends Children.Keys {
    /** List of properties to be used to compute keys. */
    private static Set properties = new HashSet();
    /** The partof relationship property name */
    private static String partofPropertyName;
    /** The is a relationship property name */
    private static String isaPropertyName;

    static {
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.hierarchy.uri");//NOI18N
        if (o instanceof Collection) {
            ArrayList al = new ArrayList((Collection) o);
            Object[] names = al.toArray();
            for (int counter = 0; counter < al.size(); counter++) {
                String name = (String) names[counter];
                Resource r = resourceFactory.getProperty(name);
                properties.add(r);
            }
        }
        // get the partof and is a properties names:
        Object partof = con.getProperty("ontologyexplorer.hierarchy.partof");//NOI18N
        partofPropertyName = (String) partof;
        Object isa = con.getProperty("ontologyexplorer.hierarchy.isa");//NOI18N
        isaPropertyName = (String) isa;
    }

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
        Resource r = (Resource) key;
        ResourceNode rn = new ResourceNode(r);
        ResourceNodeInfo rni = new ResourceNodeInfo();
        //rni.setConnection(dbc.getConnection());
        rn.setInfo(rni);
        // associate an icon dependening on the relation
        // between the children's resource and the parent's one
        ResourceNode p = (ResourceNode) getNode();

        if (p != null) {
            try {
                Session sess = HibernateUtil.currentSession();
                if (!sess.isConnected()) {
                    sess.reconnect();
                }
            } catch (HibernateException e) {
                e.printStackTrace();
            }
            Set pchilds = resource.getTargets(resourceFactory
                    .getProperty(partofPropertyName));
            Set ichilds = resource.getTargets(resourceFactory
                    .getProperty(isaPropertyName));

            if (pchilds != null) {
                if (pchilds.contains(r)) {
                    rn
                            .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/partOfIcon");//NOI18N
                }
            }

            if (ichilds != null) {
                if (ichilds.contains(r)) {
                    rn
                            .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/isAIcon");//NOI18N
                }
            }
        }

        //        RequestProcessor.getDefault().post(new Runnable() {
        //            public void run() {
        //            }
        //        }, 0);

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
        Set childs = ((Resource) resource).getTargets(properties);
        return childs;
    }
}