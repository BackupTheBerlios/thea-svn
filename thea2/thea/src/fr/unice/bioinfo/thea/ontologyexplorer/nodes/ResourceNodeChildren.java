package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;

/**
 * @author Saïd El Kasmi
 */
public class ResourceNodeChildren extends Children.Keys {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); //NOI18N;

    /** Children */
    private TreeSet children = new TreeSet();

    private transient PropertyChangeSupport propertySupport = new PropertyChangeSupport(
            this);

    // PropertyChangeListener
    private PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("finished")) { //NOI18N
                remove(getNodes()); //remove wait node
                nodes = getCh(); // change children ...
                refresh(); // ... and refresh them
                removeListener(); // Remove the listener at the end
            }
        }
    };

    /** Resource associated with this children. */
    private Resource resource;

    /** Creates new ResourceChildren. */
    public ResourceNodeChildren(Resource resource) {
        this.resource = resource;
    }

    private TreeSet getCh() {
        return children;
    }

    private void setCh(TreeSet children) {
        this.children = children;
    }

    /** Removes the listener after nodes' creation */
    private void removeListener() {
        propertySupport.removePropertyChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Children#addNotify()
     */
    protected void addNotify() {
        Set keys = findSubResources(this.resource);
        if(keys != null){
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
            Set pchilds = resource.getTargets(Consts.partofProperty);
            if (pchilds != null) {
                if (pchilds.contains(r)) {
                    rn
                            .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/partOfIcon");
                }
            }
            Set ichilds = resource.getTargets(Consts.subsumeProperty);
            if (ichilds != null) {
                if (ichilds.contains(r)) {
                    rn
                            .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/isAIcon");
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
        Set childs = ((Resource) resource).getTargets(Consts
                .getListOfProperties());
        return childs;
    }
}