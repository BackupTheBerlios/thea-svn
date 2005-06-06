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
import fr.unice.bioinfo.allonto.datamodel.StringValue;
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
    private static String chromosomePropertyName;
    private static String endPosPropertyName;
    private static String strandPropertyName;
    private static String transcribedFromPropertyName;
    private static String startPosPropertyName;
    private static String symbolPropertyName;

    static {
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.nodes.xrefname");//NOI18N
        xrefPropertyName = (String) o;
        o = con.getProperty("annotation.propdbkeyname");//NOI18N
        propdbkeyPropertyName = (String) o;
        o = con.getProperty("annotation.annotatedbyname");//NOI18N
        annotatedByPropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.nodes.hasevidence");//NOI18N
        hasEvidenceProperty = (String) o;
        o = con.getProperty("annotation.chromosomename");//NOI18N
        chromosomePropertyName = (String) o;
        o = con.getProperty("annotation.endPosname");//NOI18N
        endPosPropertyName = (String) o;
        o = con.getProperty("annotation.strandname");//NOI18N
        strandPropertyName = (String) o;
        o = con.getProperty("annotation.transcribedFromname");//NOI18N
        transcribedFromPropertyName = (String) o;
        o = con.getProperty("annotation.startPosname");//NOI18N
        startPosPropertyName = (String) o;
        o = con.getProperty("annotation.symbolname");//NOI18N
        symbolPropertyName = (String) o;
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
                            .createQuery("select dbxref, dbkey.value from Resource dbxref, StringValue dbkey where dbxref.arcs[:propdbkey] = dbkey and dbkey.value in (:dbkeyval)");

                    Property p = resourceFactory.getProperty(xrefPropertyName);

                    //q.setEntity("propidentifies", p.getInverse());
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
                        Resource dbxref = (Resource) tuple[0];
                        String key = (String) tuple[1];
                        Set s = dbxref.getTargets(p.getInverse());
                        // associate for each Node its correspending
                        // Resource(Entity)
                        Iterator itr = s.iterator();
                        while (itr.hasNext()) {
                            Resource entity = (Resource) itr.next();
                            ((Node) map.get(key)).setEntity(entity);
                        }
                    }
                } catch (HibernateException he) {
                    setLinked(false);
                    he.printStackTrace(System.out);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
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

                // Create a Session using a Connection
                try {
                    HibernateUtil.createSession(cnx);
                    Session sess = HibernateUtil.currentSession();

                    ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                            .getResourceFactory();

                    LinkedList ll = new LinkedList();
                    for (int cnt = 0; cnt < evidences.length; cnt++) {
                        ll.add(resourceFactory.getResource(evidences[cnt]));
                    }
                    Criterion criterion = Expression.in(resourceFactory
                            .getResource(hasEvidenceProperty), ll);

                    Resource annotatedByProperty = resourceFactory
                            .getProperty(annotatedByPropertyName);
                    // Iterate over the list of all genes.
                    Iterator iterator = ln.iterator();
                    while (iterator.hasNext()) {
                        Node aNode = (Node) iterator.next();
                        Resource entity = (Resource) aNode.getEntity();
                        // Some genes don't have any resource. Check for
                        // nullity:
                        if (entity != null) {
                            Resource accessedProperty = resourceFactory
                                    .getResource(chromosomePropertyName);
                            StringValue sv = (StringValue) entity
                                    .getTarget(accessedProperty);

                            Set targets = entity
                                    .getTargets(annotatedByProperty);
                            if (targets != null) {
                                // add a the list of terms to each leaf node
                                aNode.addProperty(Node.ASSOC_TERMS, targets);
                            }
                        }
                    }

                } catch (HibernateException he) {
                    he.printStackTrace(System.out);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
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

    private Map createSpeciesMap() {
        Map map = new HashMap();

        String specieID = null;
        Integer obj = (Integer) map.get(specieID);
        int count = ((obj == null) ? 0 : obj.intValue());
        map.put(specieID, new Integer(count + 1));

        return map;
    }

    private String computeMainSpecie(Map map) {
        String ms /* Main Specie */= null;
        Set entrySet = map.entrySet();
        Iterator iterator = entrySet.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (((Integer) entry.getValue()).intValue() > counter) {
                counter = ((Integer) entry.getValue()).intValue();
                ms = (String) entry.getKey();
            }
        }
        return ms;
    }

    private void createProperties(Node aNode, Resource resource,
            ResourceFactory resourceFactory) {
        StringValue sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(chromosomePropertyName));
        System.out.println("node " + aNode.getName() + " -> " + sv.toString());
    }

    //    private void initTermsMap() {
    //        Map map = new HashMap();
    //
    //        // Iterate over leave nodes
    //        List lNodes = classificationRootNode.getLeaves();
    //        Iterator lIt = lNodes.iterator();
    //        while (lIt.hasNext()) {
    //            Node lNode = (Node) lIt.next();
    //            // Get associated terms for "leaf"
    //            Set lTerms = (Set) lNode.getProperty(Node.ASSOC_TERMS);
    //            Set allTerms = new HashSet(lTerms);
    //            Iterator iterator = lTerms.iterator();
    //            // Add also ancestors of each Term to the list of terms
    //            while (iterator.hasNext()) {
    //                Term term = (Term) iterator.next();
    //                allTerms.addAll(term.getAllAncestorsHash().values());
    //            }
    //            /*
    //             * Keys are associated Terms,
    //             * Values are number of occurences
    //             */
    //            Map lMap = new HashMap();
    //            iterator = allTerms.iterator();
    //            // Iterate over the list of all terms associated to "leaf"
    //            // this list is formed by Terms directly associated to "leaf"
    //            // plus their ancestors
    //            while (iterator.hasNext()) {
    //                Term term = (Term) iterator.next();
    //                lMap.put(term, new Integer(1));
    //                if (map.containsKey(term)) {
    //                    map.put(term, new Integer(((Integer) map
    //                            .get(term)).intValue() + 1));
    //                } else {
    //                    map.put(term, new Integer(1));
    //                }
    //            }
    //            lNode.addProperty(Node.TERMS_MAP, lMap);
    //        }
    //        classificationRootNode.addProperty(Node.TERMS_MAP, map);
    //    }
}