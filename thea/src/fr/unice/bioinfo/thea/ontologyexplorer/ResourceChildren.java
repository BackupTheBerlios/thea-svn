package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;

/**
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
        ResourceNode rn = new ResourceNode(r,updateKeys(r));
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
    private Set updateKeys(Resource resource){
        try {
            Session sess = HibernateUtil.currentSession();
            if(!sess.isConnected()){
                sess.reconnect();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        Set childs = ((Resource) resource).getTargets(Consts.getListOfProperties());
        return childs;
    }
}