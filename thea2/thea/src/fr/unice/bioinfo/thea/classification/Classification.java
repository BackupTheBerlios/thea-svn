package fr.unice.bioinfo.thea.classification;

import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    /** The partof relationship property name */
    private static String partofPropertyName;
    /** The is a relationship property name */
    private static String isaPropertyName;

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

        o = con.getProperty("ontologyexplorer.hierarchy.partof");//NOI18N
        partofPropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.hierarchy.isa");//NOI18N
        isaPropertyName = (String) o;
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

    /**
     * Flag indicating if the classification represented by this class have been
     * annotated using an ontology or not.
     */
    private boolean annotated = false;

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
                setLinked(true);
            }
        };
        worker.start();
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
                        aNode.addProperty(Node.ASSOC_TERMS, new HashSet());
                        // Some genes don't have any resource. Check for
                        // nullity:
                        if (entity != null) {
                            createProperties(aNode, entity, resourceFactory);
                            Set targets = entity
                                    .getTargets(annotatedByProperty);
                            if (targets != null) {
                                // add a the list of terms to each leaf node
                                aNode.addProperty(Node.ASSOC_TERMS, targets);
                            }
                        }
                    }
                    long t0 = System.currentTimeMillis();
                    initTermsMap(resourceFactory);
                    System.out.println("time = "
                            + (System.currentTimeMillis() - t0));

                } catch (HibernateException he) {
                    setAnnotated(false);
                    he.printStackTrace(System.out);
                } catch (Exception e) {
                    setAnnotated(false);
                    e.printStackTrace(System.out);
                }
                setAnnotated(true);
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

    public boolean isAnnotated() {
        return annotated;
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
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

    // write javadoc later:
    // extract some properties from the given resources, and
    // attch them the given node.
    private void createProperties(Node aNode, Resource resource,
            ResourceFactory resourceFactory) {
        StringValue sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(chromosomePropertyName));
        if (sv != null) {
            aNode.addProperty(Node.CHROMOSOME, sv.getValue());
        }

        sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(endPosPropertyName));
        if (sv != null) {
            aNode.addProperty(Node.END_POS, sv.getValue());
        }

        sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(startPosPropertyName));
        if (sv != null) {
            aNode.addProperty(Node.START_POS, sv.getValue());
        }

        sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(symbolPropertyName));
        if (sv != null) {
            aNode.addProperty(Node.SYMBOL, sv.getValue());
        }

        //        sv = (StringValue) resource.getTarget(resourceFactory
        //                .getProperty(strandPropertyName));
        //        if (sv != null) {
        //            aNode.addProperty(Node.STRAND_POSITION, sv.getValue());
        //        }
    }

    private void initTermsMap(ResourceFactory resourceFactory) {
        Map map = new HashMap();
        // Iterate over leave nodes
        List ln = classificationRootNode.getLeaves();
        Iterator lnIt = ln.iterator();
        while (lnIt.hasNext()) {
            Node aNode = (Node) lnIt.next();
            // Get associated terms for the each leaf node
            Set aNodeTerms = (Set) aNode.getProperty(Node.ASSOC_TERMS);
            // Add also resources that annotates inderictly this leaf node.
            Set all = new HashSet(aNodeTerms);
            Iterator iterator = aNodeTerms.iterator();
            // Add also ancestors of each Term to the list of terms
            while (iterator.hasNext()) {
                Resource aResource = (Resource) iterator.next();
                all.addAll(createAncestorsList(aResource, resourceFactory));
            }
            /*
             * Keys are associated Terms, Values are number of occurences
             */
            Map lMap = new HashMap();
            iterator = all.iterator();
            // Iterate over the list of all terms associated to "leaf"
            // this list is formed by Terms directly associated to "leaf"
            // plus their ancestors
            while (iterator.hasNext()) {
                Resource aResource = (Resource) iterator.next();
                lMap.put(aResource, new Integer(1));
                if (map.containsKey(aResource)) {
                    map.put(aResource, new Integer(((Integer) map
                            .get(aResource)).intValue() + 1));
                } else {
                    map.put(aResource, new Integer(1));
                }
            }
            aNode.addProperty(Node.TERMS_MAP, lMap);
        }
        classificationRootNode.addProperty(Node.TERMS_MAP, map);
    }

    private Set createAncestorsList(Resource aResource,
            ResourceFactory resourceFactory) {
        Set ancestors = new HashSet();
        Set properties = new HashSet();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.hierarchy.uri");//NOI18N
        if (o instanceof Collection) {
            ArrayList al = new ArrayList((Collection) o);
            Object[] names = al.toArray();
            for (int counter = 0; counter < al.size(); counter++) {
                String name = (String) names[counter];
                Resource r = resourceFactory.getProperty(name).getInverse();
                properties.add(r);
            }
        }
        Set targets = ((Resource) aResource).getTargets(properties);
        if (targets != null) {
            Iterator iterator = targets.iterator();
            while (iterator.hasNext()) {
                Resource target = (Resource) iterator.next();
                ancestors.addAll(createAncestorsList(target, resourceFactory));
            }
            ancestors.addAll(targets);
        }
        return ancestors;
    }
}