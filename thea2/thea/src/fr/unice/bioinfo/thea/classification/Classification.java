package fr.unice.bioinfo.thea.classification;

import java.awt.Color;
import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.configuration.Configuration;
import org.openide.windows.WindowManager;

import cern.jet.random.HyperGeometric;
import cern.jet.stat.Probability;
import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.classification.settings.CESettings;
import fr.unice.bioinfo.thea.util.BlockingSwingWorker;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Classification {

    private int nb_annotatedClusters = 0;

    private int nb_overAnnotation = 0;

    private int nb_underAnnotation = 0;

    private int nb_colocalized_genes = 0;

    private int nb_colocalized_groups = 0;

    /** The root node of the classification. */
    private Node classificationRootNode;

    /** The connection to an ontology. */
    private Connection cnx = null;

    /**
     * The resource correspending the root nod of the ontology's branch used to
     * annotate the classification.
     */
    private Resource branchRootResource = null;

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

                    resourceFactory.setMemoryCached(true);
                    //                    Property p =
                    // resourceFactory.getProperty(xrefPropertyName);
                    Property p = resourceFactory.getProperty(OWLProperties
                            .getInstance().getXrefPropertyName());
                    if (p != null)
                        p = p.getInverse();

                    //q.setEntity("propidentifies", p.getInverse());
                    //                    q.setEntity("propdbkey", resourceFactory
                    //                            .getProperty(propdbkeyPropertyName));
                    q.setEntity("propdbkey", resourceFactory
                            .getProperty(OWLProperties.getInstance()
                                    .getPropdbkeyPropertyName()));
                    resourceFactory.setMemoryCached(false);

                    q.setParameterList("dbkeyval", keys);

                    // Resources correspending to keys are now:
                    long t = System.currentTimeMillis();
                    List list = q.list();
                    System.out.println("retrieved " + list.size()
                            + " objects in " + (System.currentTimeMillis() - t)
                            + "ms");
                    t = System.currentTimeMillis();

                    // Iterate over the list of found resources and
                    // associate to each Node its corespending Resource
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        Object[] tuple = (Object[]) it.next();
                        Resource dbxref = (Resource) tuple[0];
                        String key = (String) tuple[1];
                        Set s = dbxref.getTargets(p);
                        // associate for each Node its correspending
                        // Resource(Entity)
                        Iterator itr = s.iterator();
                        while (itr.hasNext()) {
                            Resource entity = (Resource) itr.next();
                            ((Node) map.get(key)).setEntity(entity);
                        }
                    }
                    System.out.println("all genes retrieved in "
                            + (System.currentTimeMillis() - t) + "ms");
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
                // all leave nodes
                List ln = classificationRootNode.getLeaves();

                // Create a Session using a Connection
                try {
                    HibernateUtil.createSession(cnx);
                    Session sess = HibernateUtil.currentSession();
                    ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                            .getResourceFactory();
                    resourceFactory.setMemoryCached(true);
                    LinkedList ll;
                    Criterion criterion = null;
                    if (evidences != null) {
                        ll = new LinkedList();
                        for (int cnt = 0; cnt < evidences.length; cnt++) {
                            ll.add(resourceFactory.getResource(evidences[cnt]));
                        }
                        criterion = Expression.in(resourceFactory
                                .getResource(OWLProperties.getInstance()
                                        .getHasEvidenceProperty()), ll);
                    }
                    Resource annotatedByProperty = resourceFactory
                            .getProperty(OWLProperties.getInstance()
                                    .getAnnotatedByPropertyName());
                    resourceFactory.setMemoryCached(false);
                    // Iterate over the list of all genes.
                    long t = System.currentTimeMillis();
                    Iterator lnIt = ln.iterator();
                    while (lnIt.hasNext()) {
                        Node aNode = (Node) lnIt.next();
                        Resource entity = (Resource) aNode.getEntity();
                        aNode.addProperty(Node.ASSOC_TERMS, new HashSet());
                        // Some genes don't have any resource. Check for
                        // nullity:
                        if (entity != null) {
                            // we don't need to retrieve these properties in the
                            // frame of
                            // the annotation process
                            //createProperties(aNode, entity, resourceFactory);
                            Set targets;
                            if (criterion != null) {
                                targets = entity.getTargets(
                                        annotatedByProperty, criterion);
                            } else {
                                targets = entity
                                        .getTargets(annotatedByProperty);
                            }
                            if (targets != null) {
                                // add a the list of terms to each leaf node
                                aNode.addProperty(Node.ASSOC_TERMS, targets);
                            }
                        }
                    }
                    System.out.println("retrieved annotation in "
                            + (System.currentTimeMillis() - t) + "ms");
                    long t0 = System.currentTimeMillis();
                    initTermsMap(resourceFactory);
                    System.out.println("retrieved ancestors in "
                            + (System.currentTimeMillis() - t0) + "ms");

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
                .getProperty(OWLProperties.getInstance()
                        .getChromosomePropertyName()));
        if (sv != null) {
            aNode.addProperty(Node.CHROMOSOME, sv.getValue());
        }

        sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(OWLProperties.getInstance()
                        .getEndPosPropertyName()));
        if (sv != null) {
            aNode.addProperty(Node.END_POS, sv.getValue());
        }

        sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(OWLProperties.getInstance()
                        .getStartPosPropertyName()));
        if (sv != null) {
            aNode.addProperty(Node.START_POS, sv.getValue());
        }

        sv = (StringValue) resource.getTarget(resourceFactory
                .getProperty(OWLProperties.getInstance()
                        .getSymbolPropertyName()));
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
        // Collect the set of properties that are used to retrieve terms'
        // ancestors
        Set inverseHierarchyProperties = new HashSet();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.hierarchy.uri");//NOI18N
        resourceFactory.setMemoryCached(true);
        if (o instanceof Collection) {
            ArrayList al = new ArrayList((Collection) o);
            Object[] names = al.toArray();
            for (int counter = 0; counter < al.size(); counter++) {
                String name = (String) names[counter];
                Resource aProperty = resourceFactory.getProperty(name)
                        .getInverse();
                inverseHierarchyProperties.add(aProperty);
            }
        } else if (o instanceof String) {
            inverseHierarchyProperties.add(resourceFactory.getProperty(
                    (String) o).getInverse());
        } else {
            throw new ClassCastException();
        }
        resourceFactory.setMemoryCached(false);

        Map map = new HashMap();
        // Iterate over leave nodes
        List ln = classificationRootNode.getLeaves();
        Iterator lnIt = ln.iterator();
        while (lnIt.hasNext()) {
            Node aNode = (Node) lnIt.next();
            // Get associated terms for the each leaf node
            Set aNodeTerms = (Set) aNode.getProperty(Node.ASSOC_TERMS);
            // Add also resources that annotates indirerctly this leaf node.
            Set all = new HashSet(aNodeTerms);
            Iterator iterator = aNodeTerms.iterator();
            // Add also ancestors of each Term to the list of terms
            while (iterator.hasNext()) {
                Resource aResource = (Resource) iterator.next();
                all.addAll(createAncestorsList(aResource,
                        inverseHierarchyProperties));
            }
            //Keys are associated Terms, Values are number of occurences
            Map lMap = new HashMap();
            iterator = all.iterator();
            // Iterate over the list of all terms associated to "aNode"
            // this list is formed by Terms directly associated to "aNode"
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

    /**
     * Finds and return ancestors of the given resource.
     * @param aResource The resource to find out ancestors.
     * @param properties Properties that fix the <i>ancestor </i> relationship.
     * @return List of ancestors.
     */
    private Set createAncestorsList(Resource aResource, Set properties) {
        Set ancestors = new HashSet();
        Set targets = ((Resource) aResource).getTargets(properties);
        if (targets != null) {
            Iterator targetsIt = targets.iterator();
            while (targetsIt.hasNext()) {
                Resource target = (Resource) targetsIt.next();
                ancestors.addAll(createAncestorsList(target, properties));
            }
            ancestors.addAll(targets);
        }
        return ancestors;
    }

    public void compareWithClassification(final Resource nodeResource) {
        // Remember It:
        branchRootResource = nodeResource;
        BlockingSwingWorker worker = new BlockingSwingWorker(
                (Frame) WindowManager.getDefault().getMainWindow(),
                "Annotating ...", "Annotation in progress, please wait ...",
                true) {
            protected void doNonUILogic() throws RuntimeException {
                // Create a Session using a Connection
                try {
                    HibernateUtil.createSession(cnx);
                    Session sess = HibernateUtil.currentSession();
                    ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                            .getResourceFactory();
                    //resourceFactory.setMemoryCached(true);

                    boolean ignoreUnknown = CESettings.getInstance()
                            .isIgnoreUnknownSelected();
                    boolean ignoreNotAnnotated = CESettings.getInstance()
                            .isIgnoreNotAnnotatedSelected();

                    // allBranchTerms contains list of terms under a root node
                    // from the ontology
                    Set allBranchTerms = createResources(resourceFactory,
                            nodeResource);

                    // Count Genes for the Top root node
                    int genesCount = countGenes(resourceFactory,
                            allBranchTerms, ignoreNotAnnotated, ignoreUnknown);
                    // Get terms that annotate the root node:
                    Map map = null; // before was called: rootTermsMap
                    map = (Map) classificationRootNode
                            .getProperty(Node.TERMS_MAP);

                    Map branchTermsMap = new HashMap();
                    // Iterate over the list of terms in the treated branch
                    Iterator it = allBranchTerms.iterator();
                    while (it.hasNext()) {
                        Resource aResource = (Resource) it.next();
                        branchTermsMap.put(aResource, map.get(aResource));
                    }

                    nb_annotatedClusters = 0;
                    nb_overAnnotation = 0;
                    nb_underAnnotation = 0;

                    labelNodesWithCommonTerms(resourceFactory,
                            classificationRootNode, branchTermsMap, genesCount,
                            ignoreUnknown, ignoreNotAnnotated, new HashMap());

                    List ln = classificationRootNode.getLeaves();
                    // Iterate over leave nodes list
                    Iterator lnIt = ln.iterator();
                    while (lnIt.hasNext()) {
                        Node aNode = (Node) lnIt.next();
                        aNode.setLayoutSupport(null);
                        if ((ignoreNotAnnotated && Boolean.TRUE
                                .equals((Boolean) aNode
                                        .getProperty(Node.GENE_NA)))
                                || (ignoreUnknown && Boolean.TRUE
                                        .equals((Boolean) aNode
                                                .getProperty(Node.GENE_UNKNOWN)))) {
                            aNode.setLayoutSupport(new NodeLayoutSupport(null,
                                    Color.gray, null,
                                    NodeLayoutSupport.NO_FRAME, null));
                            Boolean frozen = (Boolean) aNode
                                    .getProperty(Node.FROZEN);
                            if ((frozen == null)
                                    || frozen.equals(Boolean.FALSE)) {
                                aNode.getParent().moveChildAtEnd(aNode);
                            }
                            continue;
                        }
                    }
                } catch (HibernateException he) {
                    he.printStackTrace(System.out);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
                // Fire an event telling the annotation is done:
                propertySupport.firePropertyChange("annotationChanged", null,
                        null);//NOI18N
            }
        };
        worker.start();
    }

    private Set createResources(ResourceFactory resourceFactory,
            Resource aResource) {
        Set descendants = new HashSet();
        Set targets = aResource.getTargets(OWLProperties.getInstance()
                .getHierarchyProperties());
        if (targets != null) {
            Iterator targetsIt = targets.iterator();
            while (targetsIt.hasNext()) {
                Resource target = (Resource) targetsIt.next();
                descendants.addAll(createResources(resourceFactory, target));
            }
            descendants.addAll(targets);
        }
        return descendants;
    }

    private int labelNodesWithCommonTerms(ResourceFactory resourceFactory,
            Node aNode, Map rootTermsMap, int nbRootAssociatedGenes,
            boolean ignoreUnknown, boolean ignoreNotAnnotated, Map termsMap) {
        if (aNode == null) {
            return 0;
        }
        // aNode is leaf
        if (aNode.isLeaf()) {
            // If the Gene is not annotated
            // AND
            // We ignore not annotated Genes
            if (Boolean.TRUE.equals((Boolean) aNode.getProperty(Node.GENE_NA))
                    && ignoreNotAnnotated) {
                return 0;
            }
            // If the Gene is annotated with an unknown Gene
            // AND
            // We ignore Genes annotated with an unknown Term
            if (Boolean.TRUE.equals(aNode.getProperty(Node.GENE_UNKNOWN))
                    && ignoreUnknown) {
                return 0;
            }

            termsMap.putAll((Map) aNode.getProperty(Node.TERMS_MAP));
            return 1;
        }
        // Starting from this line, the node aNode is not leaf.
        //aNode.setName("");
        aNode.setLabel("");//NOI18N
        //aNode.addProperty(Node.TERM_AND_SCORE, null);
        int count = 0;/* number of associated Genes */
        // Iterate over the list of "aNode"'s children
        Iterator childrenIt = aNode.getChildren().iterator();
        while (childrenIt.hasNext()) {
            Node aChild = (Node) childrenIt.next();
            Map m = new HashMap();
            // For each child:
            count += labelNodesWithCommonTerms(resourceFactory, aChild,
                    rootTermsMap, nbRootAssociatedGenes, ignoreUnknown,
                    ignoreNotAnnotated, m);
            Set keySet = m.keySet();
            Iterator ksIt = keySet.iterator();
            while (ksIt.hasNext()) {
                Resource aResource = (Resource) ksIt.next();
                if (termsMap.containsKey(aResource)) {
                    termsMap.put(aResource, new Integer(((Integer) m
                            .get(aResource)).intValue()
                            + ((Integer) termsMap.get(aResource)).intValue()));
                } else {
                    termsMap.put(aResource, m.get(aResource));
                }
            }
        }

        CESettings settings = CESettings.getInstance();

        // remove terms for which their occurence in this cluster
        // is greater than a given cutoff
        Set keySet = termsMap.keySet();
        Map overAndUnderExpTerms = new HashMap(); // overexpressed terms
        Iterator it = keySet.iterator();
        //Set rootTerms = new HashSet(getController().getRootTerms().values());
        //        Set unknownTerms = new HashSet(getController().getUnknownTerms()
        //                .values());
        int nbTestedTerms = 0; // used for multiple testing correction

        while (it.hasNext()) {
            Resource aResource = (Resource) it.next();
            String aResourceName = "";//NOI18N
            StringValue sv = (StringValue) aResource.getTarget(resourceFactory
                    .getProperty(OWLProperties.getInstance()
                            .getNodeNameProperty()));
            if (sv != null) {
                aResourceName = sv.getValue();
            }
            // continue calculation only if the term belong to the right branch
            if (rootTermsMap.get(aResource) == null) {
                continue;
            }

            // do not process ontologie roots
            //            if (rootTerms.contains(t)) {
            //                continue;
            //            }

            // do not process unknown terms if ignoreUnknown flag is true
            if (ignoreUnknown && aResourceName.endsWith("unknown")) {//NOI18N
                continue;
            }

            //            if (t == getController().getRoot()) {
            //                continue;
            //            }

            nbTestedTerms += 1;

            int nbGenesAssociatedWithTermInNode = (termsMap.get(aResource) == null) ? 0
                    : ((Integer) termsMap.get(aResource)).intValue();
            double nbGenesAssociatedWithTermInRoot = ((Integer) rootTermsMap
                    .get(aResource)).intValue();
            double occurence = (double) nbGenesAssociatedWithTermInNode
                    / (double) count;
            double globalOccurence = nbGenesAssociatedWithTermInRoot
                    / (double) nbRootAssociatedGenes;

            if (count < settings.getMinClusterSize()) {
                continue;
            }
            // Get input: "Minimum Associated Genes"
            if (nbGenesAssociatedWithTermInNode < settings
                    .getMinAssociatedGenes()) {
                continue;
            }

            double scoreCutoff = 0;
            //Score Calculation
            //Ontology Terms Count:
            if (settings.isTermsCountSelected()) {
                scoreCutoff = settings.getTermsCount();

                if (nbGenesAssociatedWithTermInNode >= scoreCutoff) {
                    Double previousScore = (Double) overAndUnderExpTerms
                            .get(aResource);

                    if ((previousScore == null)
                            || (previousScore.doubleValue() < nbGenesAssociatedWithTermInNode)) {
                        overAndUnderExpTerms.put(aResource, new Score(
                                aResource, nbGenesAssociatedWithTermInNode,
                                true, nbGenesAssociatedWithTermInNode, count,
                                (int) nbGenesAssociatedWithTermInRoot,
                                nbRootAssociatedGenes));
                    }
                }
            } else if (settings.isTermsDensityInClusterSelected()) {
                scoreCutoff = settings.getDensityInCluster();
                if (occurence >= scoreCutoff) {
                    Double previousScore = (Double) overAndUnderExpTerms
                            .get(aResource);

                    if ((previousScore == null)
                            || (previousScore.doubleValue() < occurence)) {
                        overAndUnderExpTerms.put(aResource, new Score(
                                aResource, occurence, true,
                                nbGenesAssociatedWithTermInNode, count,
                                (int) nbGenesAssociatedWithTermInRoot,
                                nbRootAssociatedGenes));
                    }
                }
            } else if (settings.isTermsDensityInPopulationSelected()) {
                scoreCutoff = settings.getDensityInPopulation();

                double score = (double) nbGenesAssociatedWithTermInNode
                        / (double) nbGenesAssociatedWithTermInRoot;

                if (score >= scoreCutoff) {
                    Double previousScore = (Double) overAndUnderExpTerms
                            .get(aResource);

                    if ((previousScore == null)
                            || (previousScore.doubleValue() < score)) {
                        overAndUnderExpTerms.put(aResource, new Score(
                                aResource, score, true,
                                nbGenesAssociatedWithTermInNode, count,
                                (int) nbGenesAssociatedWithTermInRoot,
                                nbRootAssociatedGenes));
                    }
                }
            } else if (settings.isTermsRelativeDensitySelected()) {
                scoreCutoff = settings.getRelativeDensity();

                if ((occurence / globalOccurence) >= scoreCutoff) {
                    Double previousScore = (Double) overAndUnderExpTerms
                            .get(aResource);

                    if ((previousScore == null)
                            || (previousScore.doubleValue() < (occurence / globalOccurence))) {
                        overAndUnderExpTerms.put(aResource, new Score(
                                aResource, occurence / globalOccurence, true,
                                nbGenesAssociatedWithTermInNode, count,
                                (int) nbGenesAssociatedWithTermInRoot,
                                nbRootAssociatedGenes));
                    }
                }
            } else if (settings.isStatisticalCalculationSelected()) {
                scoreCutoff = settings.getStat();

                // Initialization of parameters
                double kValue = nbGenesAssociatedWithTermInNode;
                double n = count;
                double p = globalOccurence;
                double np = n * p;

                if (settings.isMethod2Selected()) {
                    kValue = nbGenesAssociatedWithTermInNode;
                    n = nbGenesAssociatedWithTermInRoot;
                    p = count / (double) nbRootAssociatedGenes;
                    np = n * p;
                }

                double pValue = 0;
                double lessThanKProb = 0;
                double moreThanKProb = 0;

                if (settings.isBinomialLawSelected()) {
                    if (kValue == 0) {
                        moreThanKProb = 1;
                    } else {
                        moreThanKProb = Probability.binomialComplemented(
                                (int) kValue - 1, (int) n, p);
                    }

                    lessThanKProb = Probability.binomial((int) kValue, (int) n,
                            p);
                } else if (settings.isHypergeometricLawSelected()) {
                    HyperGeometric hyperG = new HyperGeometric(
                            nbRootAssociatedGenes,
                            (int) nbGenesAssociatedWithTermInRoot, (int) n,
                            cern.jet.random.AbstractDistribution
                                    .makeDefaultGenerator());

                    if (kValue == 0) {
                        moreThanKProb = 1;
                    } else {
                        for (int i = (int) kValue; i <= n; i++) {
                            moreThanKProb += hyperG.pdf(i);
                        }
                    }

                    for (int i = 0; i < (int) kValue; i++) {
                        lessThanKProb += hyperG.pdf(i);
                    }
                }
                if (moreThanKProb < 0) {
                    moreThanKProb = 0;
                }

                if (lessThanKProb < 0) {
                    lessThanKProb = 0;
                }

                if ((moreThanKProb <= scoreCutoff)
                        && settings.isShowOverRepresented()) {
                    Score tas = (Score) overAndUnderExpTerms.get(aResource);

                    if (tas == null) {
                        overAndUnderExpTerms.put(aResource, new Score(
                                aResource, moreThanKProb, true,
                                nbGenesAssociatedWithTermInNode, count,
                                (int) nbGenesAssociatedWithTermInRoot,
                                nbRootAssociatedGenes));
                    } else {
                        double previousScore = tas.getScore();

                        if (previousScore > moreThanKProb) {
                            overAndUnderExpTerms.put(aResource, new Score(
                                    aResource, moreThanKProb, true,
                                    nbGenesAssociatedWithTermInNode, count,
                                    (int) nbGenesAssociatedWithTermInRoot,
                                    nbRootAssociatedGenes));
                        }
                    }
                }

                if ((lessThanKProb <= scoreCutoff)
                        && settings.isShowUnderRepresented()) {
                    Score tas = (Score) overAndUnderExpTerms.get(aResource);

                    if (tas == null) {
                        overAndUnderExpTerms.put(aResource, new Score(
                                aResource, lessThanKProb, false,
                                nbGenesAssociatedWithTermInNode, count,
                                (int) nbGenesAssociatedWithTermInRoot,
                                nbRootAssociatedGenes));
                    } else {
                        double previousScore = tas.getScore();

                        if (previousScore > lessThanKProb) {
                            overAndUnderExpTerms.put(aResource, new Score(
                                    aResource, moreThanKProb, false,
                                    nbGenesAssociatedWithTermInNode, count,
                                    (int) nbGenesAssociatedWithTermInRoot,
                                    nbRootAssociatedGenes));
                        }
                    }
                }
            }
        }

        if (overAndUnderExpTerms.isEmpty()) {
            return count;
        }

        List commonTerms = new Vector();

        // collecting overexpressed terms
        Set entries = overAndUnderExpTerms.entrySet();
        it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry mapE = (Map.Entry) it.next();
            Score tas = (Score) mapE.getValue();

            if (settings.isStatisticalCalculationSelected()) {
                double pValue = tas.getScore();

                // correction of cutoff
                double scoreCutoff = settings.getStat();

                if (settings.isBonferonniCorrectionSelected()) {
                    pValue = pValue * nbTestedTerms;

                    if (pValue > scoreCutoff) {
                        continue;
                    }
                } else if (settings.isSidakCorrectionSelected()) {
                    pValue = 1 - Math.pow(1 - pValue, nbTestedTerms);

                    if (pValue > scoreCutoff) {
                        continue;
                    }
                }

                tas.putScore(pValue);
                if (tas.isOverexpressed()) {
                    nb_overAnnotation += 1;
                } else {
                    nb_underAnnotation += 1;
                }
            }

            commonTerms.add(tas);
        }

        if (commonTerms.isEmpty()) {
            return count;
        }

        nb_annotatedClusters += 1;

        Collections.sort(commonTerms, new Comp());

        if (settings.isStatisticalCalculationSelected()) {
            Collections.reverse(commonTerms);
        }

        Resource bestTerm = ((Score) commonTerms.get(0)).getTerm();
        String label = "";//NOI18N

        if (settings.isShowTermID()) {
            label += bestTerm.getId();
        }

        if (settings.isShowTermName()) {
            if (!"".equals(label)) {
                label += ":";
            }
            StringValue sv = (StringValue) bestTerm.getTarget(resourceFactory
                    .getProperty(OWLProperties.getInstance()
                            .getNodeNameProperty()));
            if (sv != null) {
                label += (sv.getValue() + " ");
            }
        }

        if (commonTerms.size() > 1) {
            label += "(+)";//NOI18N
        }
        aNode.setLabel(label);
        if (((Score) commonTerms.get(0)).isOverexpressed()) {
            aNode.setLayoutSupport(new NodeLayoutSupport(null, null, new Color(
                    255, 200, 200), NodeLayoutSupport.RECTANGLE, null));
        } else {
            aNode.setLayoutSupport(new NodeLayoutSupport(null, null, new Color(
                    200, 255, 200), NodeLayoutSupport.RECTANGLE, null));
        }

        double bestScore = ((Score) commonTerms.get(0)).getScore();
        aNode.addProperty(Node.TERM_AND_SCORE, commonTerms);
        aNode.addProperty(Node.ASSOC_TERM, bestTerm);
        aNode.addProperty(Node.BEST_SCORE, new Double(bestScore));

        if (settings.isHideSimilarAnnotation()) {
            // remove labels from child nodes if they are the same
            childrenIt = aNode.getChildren().iterator();
            while (childrenIt.hasNext()) {
                Node aChild = (Node) childrenIt.next();
                if (aNode.getLabel().equals(aChild.getLabel())) {
                    aChild.setLabel("");//NOI18N
                }
            }
        }
        return count;
    }

    /**
     * Count the number of gene product that are descendant of the parameter
     * node
     * @param n The node to check
     * @param ontologyBranch The name of the ontology branch to use
     * @param ignoreUnknown True if genes associated to unknown term have to be
     *        ignored
     * @param ignoreNotAnnotated True if genes not associated with a term have
     *        to be ignored
     * @return The number of gene product that are descendants of the parameter
     *         node
     */
    private int countGenes(ResourceFactory resourceFactory, Set allBranchTerms,
            boolean ignoreNotAnnotated, boolean ignoreUnknown) {

        // allBranchTerms contains all terms from the onotology branch

        List ln = classificationRootNode.getLeaves();
        // If we dont ignore:
        // - Genes annotated with unknown terms
        if (!ignoreNotAnnotated && !ignoreUnknown) {
            return ln.size();
        }

        // Unknown Terms are Terms that ends with "unknown"
        //        Collection unknownTerms = getController().getUnknownTerms().values();
        //        Resource anUnknownResource = null; // Unknown Resource
        //        // Iterate over the list of unknown terms
        //        Iterator uIt = unknownTerms.iterator();
        //        while (uIt.hasNext()) {
        //            Resource uResource = (Resource) uIt.next();
        //            // If the list of terms of the branch contains
        //            // a terms which is "unknown", keep the first one and break
        //            // the while loop
        //            if (allBranchTerms.contains(uResource)) {
        //                anUnknownResource = uResource;
        //                break;
        //            }
        //        }
        int count = 0;/* Number of associated genes */
        // Iterate over the list of leaves node
        Iterator lnIt = ln.iterator();
        while (lnIt.hasNext()) {
            Node aNode = (Node) lnIt.next();
            // Get the list of terms associated:
            // at as associated terms
            Set at = (Set) aNode.getProperty(Node.ASSOC_TERMS);
            // the bln variable TELLS IF THE NODE "aNode" IS ANNOTATED WITH A
            // TERM THAT BELONGS TO THE TREATED BRANCH
            boolean bln = false;
            // Tells if the node aNode is annotated with an unkown Term
            boolean annotatedWithUnknown = true;
            boolean isEndsWithUnknown = false;
            // Iterate over the list of terms associated to "aNode" if not null
            if ((at != null) && !at.isEmpty()) {
                Iterator atIt = at.iterator();
                while (atIt.hasNext()) {
                    Resource aResource = (Resource) atIt.next();
                    // If the list of terms of the brach contains
                    // one of terms associated the the Node "leaf"
                    // brean the while loop
                    if (allBranchTerms.contains(aResource)) {
                        // OK, the current node is annotated with a Term that
                        // belong to the this branch
                        bln = true;
                        break;
                    }
                }
                // Now try to find out if the gene is annotated using
                // an unknown term:
                atIt = at.iterator();
                String name = "";
                StringValue sv;
                while (atIt.hasNext()) {
                    Resource aResource = (Resource) atIt.next();
                    sv = (StringValue) aResource.getTarget(resourceFactory
                            .getProperty(OWLProperties.getInstance()
                                    .getNodeNameProperty()));
                    if (sv != null) {
                        name = sv.getValue();
                    }
                    // If the list of terms of the brach contains
                    // one of terms associated the the Node "leaf"
                    // brean the while loop
                    if (name.endsWith("unknown")) {//NOI18N
                        isEndsWithUnknown = true;
                        break;
                    }
                }
            }

            // Test if the node "aNode" is annotated with an
            // unknown Term
            //            if (bln && (anUnknownResource != null)
            //                    && at.contains(anUnknownResource)) {
            //                annotatedWithUnknown = false;//Yes, the Gene is annotated with
            //                // an unknown
            //                // Term
            //            }
            if (bln && isEndsWithUnknown) {
                // Yes, the Gene is annotated with
                // an unknown
                // Term
                annotatedWithUnknown = false;
            }
            aNode.addProperty(Node.GENE_NA, new Boolean(!bln));
            aNode.addProperty(Node.GENE_UNKNOWN, new Boolean(
                    !annotatedWithUnknown));
            // If we ignore not annotated Genes and
            // Genes annotated with unknown Terms
            if (!ignoreNotAnnotated && ignoreUnknown) {
                // IF the Gene is not annotated with a Term that belongs
                // to the treated branch
                // OR
                // the Gene is annotated with an unknown Term
                if (!bln || annotatedWithUnknown) {
                    count += 1;
                }
                // If we ignore not annotated Genes
                // AND
                // Genes annotated with an unknown Term
            } else if (ignoreNotAnnotated && ignoreUnknown) {
                if (bln && annotatedWithUnknown) {
                    count += 1;
                }
                // If we ignore not annotated Genes
                // AND
                // we DONT ignore Genes annotated with unkonw Terms
            } else if (ignoreNotAnnotated && !ignoreUnknown) {
                if (bln) {
                    count += 1;
                }
            }
        }
        return count;
    }

    private class Comp implements Comparator {
        public int compare(Object o1, Object o2) {
            double score1 = ((Score) o1).getScore();
            double score2 = ((Score) o2).getScore();

            if (score1 > score2) {
                return -1;
            } else if (score1 == score2) {
                return 0;
            } else {
                return 1;
            }
        }
    }

}