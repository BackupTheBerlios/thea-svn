package fr.unice.bioinfo.thea.classification;

import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.openide.windows.WindowManager;

import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.util.BlockingSwingWorker;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Classification {

    /** The root node of the classification. */
    private Node classificationRootNode;

    /** The connection to an ontology. */
    private Connection cnx = null;

    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport;

    private Classification() {
        propertySupport = new PropertyChangeSupport(this);
    }

    /**
     * Crates an object that represents a classification which the root node is
     * the given argument.
     * @param classificationRootNode The root node of the classification.
     */
    public Classification(Node classificationRootNode) {
        this();
        this.classificationRootNode = classificationRootNode;
    }

    //    public void createGeneProducts(Connection cnx) {
    //        if (cnx == null)
    //            return;
    //
    //        propertySupport.firePropertyChange("linking", null, null);
    //        System.out.println("linking");
    //
    //        List /* all leave nodes */ln = classificationRootNode.getLeaves();
    //        // keys = List formed by leaves' nodes names.
    //        List keys /* list of names from leave nodes */= new ArrayList();
    //        // This map is created to make a leaf node accessible via its
    //        // name
    //        Map map = new HashMap();
    //        Iterator iterator = ln.iterator();
    //        while (iterator.hasNext()) {
    //            Node aNode = (Node) iterator.next();
    //            String name = aNode.getName();
    //            keys.add(name);
    //            map.put(name, aNode);
    //        }
    //        // Create a Session using a Connection
    //        try {
    //            HibernateUtil.createSession(cnx);
    //            Session sess = HibernateUtil.currentSession();
    //            ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
    //                    .getResourceFactory();
    //            Query q = sess
    //                    .createQuery("select res, dbkey.value from Resource res, Resource dbxref,
    // StringValue dbkey where dbxref.arcs[:propidentifies] = res and
    // dbxref.arcs[:propdbkey] = dbkey and dbkey.value in (:dbkeyval)");
    //
    //            Property p = resourceFactory
    //                    .getProperty("http://www.unice.fr/bioinfo/owl/biowl#xref");
    //            q.setEntity("propidentifies", p.getInverse());
    //
    //            q.setEntity("propdbkey", resourceFactory
    //                    .getProperty("http://www.unice.fr/bioinfo/owl/biowl#acc"));
    //
    //            q.setParameterList("dbkeyval", keys);
    //
    //            // Resources correspending to keys are now:
    //            List list = q.list();
    //            // Iterate over the list of found resources and
    //            // associate to each Node its corespending Resource
    //            Iterator it = list.iterator();
    //            while (it.hasNext()) {
    //                Object[] tuple = (Object[]) it.next();
    //                Resource geneProduct = (Resource) tuple[0];
    //                String key = (String) tuple[1];
    //                // associate for each Node its correspending Resource(Entity)
    //                ((Node) map.get(key)).setEntity(geneProduct);
    //            }
    //        } catch (HibernateException he) {
    //            he.printStackTrace();
    //        }
    //
    //        propertySupport.firePropertyChange("linked", null, null);
    //        System.out.println("linked");
    //    }

    public void createGeneProducts(final Connection cnx) {
        if (cnx == null)
            return;

        this.cnx = cnx;

        BlockingSwingWorker worker = new BlockingSwingWorker(
                (Frame) WindowManager.getDefault().getMainWindow(),
                "Annotating ...",
                "Status: annotation in progress, please wait ...", true) {
            protected void doNonUILogic() throws RuntimeException {

                List /* all leave nodes */ln = classificationRootNode
                        .getLeaves();
                // keys = List formed by leaves' nodes names.
                List keys /* list of names from leave nodes */= new ArrayList();
                // This map is created to make a leaf node accessible via its
                // name
                Map map = new HashMap();
                Iterator iterator = ln.iterator();
                while (iterator.hasNext()) {
                    Node aNode = (Node) iterator.next();
                    String name = aNode.getName();
                    keys.add(name);
                    map.put(name, aNode);
                }
                // Create a Session using a Connection
                try {
                    HibernateUtil.createSession(cnx);
                    Session sess = HibernateUtil.currentSession();
                    ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                            .getResourceFactory();
                    Query q = sess
                            .createQuery("select res, dbkey.value from Resource res, Resource dbxref, StringValue dbkey where dbxref.arcs[:propidentifies] = res and dbxref.arcs[:propdbkey] = dbkey and dbkey.value in (:dbkeyval)");

                    Property p = resourceFactory
                            .getProperty("http://www.unice.fr/bioinfo/owl/biowl#xref");

                    q.setEntity("propidentifies", p.getInverse());
                    System.out.println(p.getInverse().getAcc());

                    q
                            .setEntity(
                                    "propdbkey",
                                    resourceFactory
                                            .getProperty("http://www.unice.fr/bioinfo/owl/biowl#acc"));

                    q.setParameterList("dbkeyval", keys);

                    // Resources correspending to keys are now:
                    List list = q.list();
                    // Iterate over the list of found resources and
                    // associate to each Node its corespending Resource
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        Object[] tuple = (Object[]) it.next();
                        Resource geneProduct = (Resource) tuple[0];
                        String key = (String) tuple[1];
                        // associate for each Node its correspending
                        // Resource(Entity)
                        ((Node) map.get(key)).setEntity(geneProduct);
                    }
                } catch (HibernateException he) {
                    he.printStackTrace();
                }

            }
        };
        worker.start();
    }

    /**
     * Add property change listener Registers a listener for the PropertyChange
     * event. The classification will fire its status to other objects.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    /**
     * Remove property change listener Remove a listener for the PropertyChange
     * event.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }

    /** Returns the classificationRootNode. */
    public Node getClassificationRootNode() {
        return classificationRootNode;
    }

    /** Sets he classification's root node. */
    public void setClassificationRootNode(Node classificationRootNode) {
        this.classificationRootNode = classificationRootNode;
    }
}