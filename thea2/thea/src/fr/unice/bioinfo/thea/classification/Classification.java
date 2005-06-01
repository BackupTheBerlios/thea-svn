package fr.unice.bioinfo.thea.classification;

import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.configuration.Configuration;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.util.BlockingSwingWorker;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Classification {

    /** The partof relationship property name */
    private static String propdbkeyPropertyName;
    /** The is a relationship property name */
    private static String xrefPropertyName;
    /** annotatedbyname property name */
    private static String annotatedByPropertyName;
    private static String hasEvidenceProperty;

    static {
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        // get the partof and is a properties names:
        Object o = con.getProperty("ontologyexplorer.nodes.xrefname");//NOI18N
        xrefPropertyName = (String) o;
        o = con.getProperty("annotation.propdbkeyname");//NOI18N
        propdbkeyPropertyName = (String) o;
        o = con.getProperty("annotation.annotatedbyname");//NOI18N
        annotatedByPropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.nodes.hasevidence");//NOI18N
        hasEvidenceProperty = (String) o;
    }

    /** The root node of the classification. */
    private Node classificationRootNode;

    /** The connection to an ontology. */
    private Connection cnx = null;

    /**
     * Flag indicating if the classification represented by this class have been
     * linked to an ontology or not.
     */
    private boolean linked = false;

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
                "Classification - Ontology association ...",
                "Looking for resources, please wait ...", true) {
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

                    Property p = resourceFactory.getProperty(xrefPropertyName);

                    q.setEntity("propidentifies", p.getInverse());
                    q.setEntity("propdbkey", resourceFactory
                            .getProperty(propdbkeyPropertyName));

                    q.setParameterList("dbkeyval", keys);

                    // Resources correspending to keys are now:
                    List list = q.list();
                    // Iterate over the list of found resources and
                    // associate to each Node its corespending Resource
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        Object[] tuple = (Object[]) it.next();
                        Resource r = (Resource) tuple[0];
                        String key = (String) tuple[1];
                        // associate for each Node its correspending
                        // Resource(Entity)
                        ((Node) map.get(key)).setEntity(r);
                    }
                } catch (HibernateException he) {
                    setLinked(false);
                    he.printStackTrace();
                }

            }
        };
        worker.start();
        setLinked(true);
    }

    public void annotate(final String[] evidences) {
        BlockingSwingWorker worker = new BlockingSwingWorker(
                (Frame) WindowManager.getDefault().getMainWindow(),
                "Annotating ...", "Annotation in progress, please wait ...",
                true) {
            protected void doNonUILogic() throws RuntimeException {
                List /* all leave nodes */ln = classificationRootNode
                        .getLeaves();
                Iterator iterator = ln.iterator();

                ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                        .getResourceFactory();
                resourceFactory.setMemoryCached(true);
                LinkedList ll = new LinkedList();
                for (int cnt = 0; cnt < evidences.length; cnt++) {
                    ll.add(resourceFactory.getResource(evidences[cnt]));
                }
                Criterion criterion = Expression.in(resourceFactory
                        .getResource(hasEvidenceProperty), ll);

                Resource annotateProperty = resourceFactory
                        .getProperty(annotatedByPropertyName);

                resourceFactory.setMemoryCached(false);

                while (iterator.hasNext()) {
                    Node aNode = (Node) iterator.next();
                    Resource resource = (Resource) aNode.getEntity();
                    if (resource != null) {
                        System.out.println(resource.getAcc() + " "
                                + resource.getArcs());
                        Set genes = resource.getTargets(annotateProperty,
                                criterion);
                        if (genes != null) {
                            System.out.println(aNode.getName() + "->"
                                    + genes.size());
                        }
                    }
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

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }
}