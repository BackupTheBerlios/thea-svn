package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;

/**
 * This class represents a child in the Ontology Explorer
 * tree. As parents, childs are built using instances
 * of type Resource as keys.
 * @author SAÏD, EL KASMI.
 */
public class ResourceChildren extends Children.Keys {
    
    /** Resource associated with this children.*/
    private Resource resource;
    /** Creates new ResourceChildren.*/
    public ResourceChildren(Resource resource) {
        this.resource = resource;
    }
    /* (non-Javadoc)
     * @see org.openide.nodes.Children.Keys#createNodes(java.lang.Object)
     */
    protected Node[] createNodes(Object key) {
        Resource r = (Resource) key;
        ResourceNode rn = new ResourceNode(r, updateKeys(r));
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
                    rn.setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/part_of");
                }
            }
            Set ichilds = resource.getTargets(Consts.subsumeProperty);
            if (ichilds != null) {
                if (ichilds.contains(r)) {
                    rn.setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/is_a");
                }
            }
        }
        return new Node[] { (Node) rn };
    }
    
    /* (non-Javadoc)
     * @see org.openide.nodes.Children#addNotify()
     */
    protected void addNotify() {
        //super.addNotify();
        //setKeys(keys);
        setKeys(updateKeys(resource));
    }
    /* (non-Javadoc)
     * @see org.openide.nodes.Children#removeNotify()
     */
    protected void removeNotify() {
        super.removeNotify();
    }
    /**
     * Updates keys. A key is simply a Resource.
     * @param resource Resource
     * @return List of keys.
     */
    private Set updateKeys(Resource resource) {
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
    /** Returns the resource. */
    public Resource getResource() {
        return resource;
    }
    public void update(Resource r){
        Set all = updateKeys(r);
        if(all == null) return;
        all.remove(r);
        setKeys(all);
    }
}