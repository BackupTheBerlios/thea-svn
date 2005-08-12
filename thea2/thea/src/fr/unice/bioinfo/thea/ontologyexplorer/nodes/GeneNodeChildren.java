package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneNodeChildren extends Children.Array {
    private Resource resource;

    public GeneNodeChildren(Resource resource) {
        this.resource = resource;
    }

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); //NOI18N;

    private TreeSet children;
    private transient PropertyChangeSupport propertySupport = new PropertyChangeSupport(
            this);
    private static Object sync = new Object(); // synchronizing object

    private PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("finished")) { //NOI18N
                //                Mutex.EVENT.writeAccess(new Runnable() {
                //                    public void run() {
                remove(getNodes()); //remove wait node
                nodes = getCh(); // change children ...
                refresh(); // ... and refresh them
                removeListener();
                //                    }
                //                });
            }
        }
    };

    protected Collection initCollection() {
        propertySupport.addPropertyChangeListener(listener);

        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                //                DatabaseNodeInfo nodeinfo =
                // ((DatabaseNode)getNode()).getInfo();
                //                java.util.Map nodeord =
                // (java.util.Map)nodeinfo.get(DatabaseNodeInfo.CHILDREN_ORDERING);
                //                boolean sort = (nodeinfo.getName().equals("Drivers") ||
                // (nodeinfo instanceof TableNodeInfo) || (nodeinfo instanceof
                // ViewNodeInfo)) ? false : true; //NOI18N
                //                TreeSet children = new TreeSet(new NodeComparator(nodeord,
                // sort));
                //
                //                try {
                //                    Vector chlist;
                //                    synchronized (sync) {
                //                        chlist = nodeinfo.getChildren();
                //                    }
                //
                //                    for (int i=0;i<chlist.size();i++) {
                //                        Node snode = null;
                //                        Object sinfo = chlist.elementAt(i);
                //
                //                        if (sinfo instanceof DatabaseNodeInfo) {
                //                            DatabaseNodeInfo dni = (DatabaseNodeInfo) sinfo;
                //
                //                            // aware! in this method is clone of instance dni created
                //                            snode = createNode(dni);
                //
                //                        }
                //                        else
                //                            if (sinfo instanceof Node)
                //                                snode = (Node)sinfo;
                //                        if (snode != null)
                //                            children.add(snode);
                //                    }
                //                } catch (Exception e) {
                //                        e.printStackTrace();
                //                        children.clear();
                //                }
                String propertyName;
                ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                        .getResourceFactory();
                Configuration con = TheaConfiguration.getDefault()
                        .getConfiguration();
                propertyName = (String) con
                        .getProperty("ontologyexplorer.nodes.annotates");//NOI18N

                TreeSet children = new TreeSet();

                Set genes = null;
                try {
                    genes = resource.getTargets(resourceFactory
                            .getResource(propertyName));
                } catch (AllontoException ae) {
                }
                if (genes != null) {
                    Iterator iterator = genes.iterator();
                    while (iterator.hasNext()) {
                        children.add(createNode((Resource) iterator.next()));
                    }

                }

                setCh(children);

                propertySupport.firePropertyChange("finished", null, null); //NOI18N
            }
        }, 0);

        TreeSet ts = new TreeSet();
        ts.add(createWaitNode());
        return ts;
    }

    /*
     * Creates and returns the instance of the node representing the status
     * 'WAIT' of the node. It is used when it spent more time to create elements
     * hierarchy. @return the wait node.
     */
    private Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(bundle.getString("WaitNode")); //NOI18N
        n.setIconBase("org/netbeans/modules/db/resources/waitIcon"); //NOI18N
        return n;
    }

    private GeneNode createNode(Resource r) {
        GeneNode gn = new GeneNode(r);
        return gn;
    }

    private TreeSet getCh() {
        return children;
    }

    private void setCh(TreeSet children) {
        this.children = children;
    }

    private void removeListener() {
        propertySupport.removePropertyChangeListener(listener);
    }
}