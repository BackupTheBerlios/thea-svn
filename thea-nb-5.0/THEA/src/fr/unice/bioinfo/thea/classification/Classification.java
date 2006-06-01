package fr.unice.bioinfo.thea.classification;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
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

import org.apache.commons.configuration.Configuration;
import org.openide.windows.WindowManager;

import cern.jet.random.HyperGeometric;
import cern.jet.stat.Probability;
import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.api.annotation.Annotatable;
import fr.unice.bioinfo.thea.classification.settings.CESettings;
import fr.unice.bioinfo.thea.util.BlockingSwingWorker;
import fr.unice.bioinfo.thea.util.OWLProperties;
import fr.unice.bioinfo.util.OwlQuery;

/**
 * <p>
 * A java bean that represents a classification. A classification can be created
 * going from a node that is the root node of the tree.
 * </p>
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Classification implements Annotatable {
    
    private ResourceFactory resourceFactory = null;

    private int nb_annotatedClusters = 0;

    private int nb_overAnnotation = 0;

    private int nb_underAnnotation = 0;

    private int nb_colocalized_genes = 0;

    private int nb_colocalized_groups = 0;

    /** The root node of the classification. */
    private Node classificationRootNode;

    /** The connection to an ontology. */
//    private Connection cnx = null;

    /**
     * The resource correspending the root nod of the ontology's branch used to
     * annotate the classification.
     */
    private Resource branchRootResource = null;

    /** The name of the node that represents the branch used for comparing. */
    private String branchRootName = null;

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

    /** Code evidences used to perform the annotation. */
    private String[] codes;

    private Map utilMap = new HashMap();

    // TODO : THIS ATTRIBUTES IS USED ONLY FOR A SPECIFIC TEST
    // TODO : REMOVE IT LATER
    private int nbAssoc = 0;

    private Classification() {
        propertySupport = new PropertyChangeSupport(this);
    }

    /**
     * Crates an object that represents a classification which the root node is
     * the given argument.
     * 
     * @param classificationRootNode
     *            The root node of the classification.
     */
    public Classification(Node classificationRootNode) {
        this();
        this.classificationRootNode = classificationRootNode;
    }

    public void createGeneProducts(final DatabaseConnection dbCon) {
//        if (cnx == null)
//            return;
//
//        this.cnx = dbCon.getConnection();
        this.resourceFactory = AllontoFactory.getResourceFactory(dbCon.getName());

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
                    String acc = "urn:lsid:uniprot.org:uniprot:"+name;
                    keys.add(acc);
                    map.put(acc, aNode);
                }
                // Create a Session using a Connection
                try {
                    Collection list = resourceFactory.getResources(keys);

                    // Resources correspending to keys are now:
                    long t = System.currentTimeMillis();
                    System.out.println("retrieved " + list.size()
                            + " objects in " + (System.currentTimeMillis() - t)
                            + "ms");
                    t = System.currentTimeMillis();

                    // Iterate over the list of found resources and
                    // associate to each Node its corespending Resource
                    Iterator listIt = list.iterator();
                    while (listIt.hasNext()) {
                        Resource res = (Resource) listIt.next();
                        String acc = res.getAcc();
                            ((Node) map.get(acc)).setResource(res);
                    }
                    System.out.println("all genes products retrieved in "
                            + (System.currentTimeMillis() - t) + "ms");
                } catch (AllontoException e) {
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
                // Rember codes evidences used for this annotation:
                codes = evidences;
                // all leave nodes
                List ln = classificationRootNode.getLeaves();
                // Create a Session using a Connection
                try {

                    OwlQuery oq = new OwlQuery(resourceFactory);
                    oq.addQuery("?x",// NOI18N
                            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",// NOI18N
                            "http://www.unice.fr/bioinfo/owl/biowl#Translation");// NOI18N
                    List l = new ArrayList();
                    l.add("x");// NOI18N
                    oq.setResultVars(l);
                    oq.prepareQuery();
                    classificationRootNode.addProperty(
                            Node.NB_GENE_PRODUCTS_IN_SPECIE, new Integer(oq
                                    .getResultCount()));
                    LinkedList ll;
                    Criterion criterion = null;
                    if (evidences != null) {
                        ll = new LinkedList();
                        for (int cnt = 0; cnt < evidences.length; cnt++) {
                            Resource evidenceResource = resourceFactory.getResource(evidences[cnt]);
                            if (evidenceResource != null) {
                                ll.add(evidenceResource);
                            }
                        }
                        criterion = Expression.in(resourceFactory
                                .getResource(OWLProperties.getInstance()
                                        .getHasEvidenceProperty()), ll);
                    }
                    Resource annotatedByProperty = resourceFactory
                            .getResource(OWLProperties.getInstance()
                                    .getAnnotatedByPropertyName());
//                    resourceFactory.setMemoryCached(false);
                    // Iterate over the list of all genes.
                    long t = System.currentTimeMillis();
                    Iterator lnIt = ln.iterator();
                    while (lnIt.hasNext()) {
                        Node aNode = (Node) lnIt.next();
                        Resource resource = aNode.getResource();
                        aNode.addProperty(Node.ASSOC_TERMS, new HashSet());
                        // Some genes don't have any resource. Check for
                        // nullity:
                        if (resource != null) {
                            // we don't need to retrieve these properties in the
                            // frame of
                            // the annotation process
                            // createProperties(aNode, resource, resourceFactory);
                            Collection targets;

                            if (criterion != null) {
                                targets = resource.getTargets(annotatedByProperty, criterion);
                            } else {
                                targets = resource
                                        .getTargets(annotatedByProperty);
                            }
                                // add a the list of terms to each leaf node
                                aNode.addProperty(Node.ASSOC_TERMS, targets);
                        }
                    }
                    System.out.println("retrieved annotation in "
                            + (System.currentTimeMillis() - t) + "ms");
                    long t0 = System.currentTimeMillis();
                    initTermsMap();
                    System.out.println("retrieved ancestors in "
                            + (System.currentTimeMillis() - t0) + "ms");
                } catch (AllontoException e) {
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

    /**
     * Returns <i>True </i> if the classification is associated to an ontology,
     * <i>False </i> elsewhere.
     */
    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }

    /**
     * Returns <i>True </i> if the classification is annotated using an
     * ontology, <i>False </i> elsewhere.
     */
    public boolean isAnnotated() {
        return annotated;
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
    }

    // write javadoc later:
    // extract some properties from the given resources, and
    // attch them the given node.
    private void createProperties(Node aNode, Resource resource) {
        Resource res = null;

        try {
            res = resource.getTarget(OWLProperties.getInstance()
                            .getChromosomePropertyName());
            if (res != null) {
                aNode.addProperty(Node.CHROMOSOME, res.getAcc());
            }
        } catch (AllontoException ae) {
        }

        try {
            res = resource.getTarget(OWLProperties.getInstance()
                            .getEndPosPropertyName());
            if (res != null) {
                aNode.addProperty(Node.END_POS, res.getAcc());
            }
        } catch (AllontoException ae) {
        }

        try {
            res = resource.getTarget(OWLProperties.getInstance()
                            .getStartPosPropertyName());
            if (res != null) {
                aNode.addProperty(Node.START_POS, res.getAcc());
            }
        } catch (AllontoException ae) {
        }
        try {
            res = resource.getTarget(OWLProperties.getInstance()
                            .getSymbolPropertyName());
            if (res != null) {
                aNode.addProperty(Node.SYMBOL, res.getAcc());
            }
        } catch (AllontoException ae) {
        }
        try {
            res = resource.getTarget(OWLProperties.getInstance()
                            .getStrandPropertyName());
            if (res != null) {
                String s = res.getAcc();
                aNode.addProperty(Node.STRAND_POSITION, s);
                boolean comp = ((Integer.parseInt(s) == 1) ? false : true);
                aNode.addProperty(Node.COMPLEMENT_POS, new Boolean(comp));
            }
        } catch (AllontoException ae) {
        }
    }

    // to rename with something more significant !!
    private void initTermsMap() {
        // Collect the set of properties that are used to retrieve terms'
        // ancestors
        Set hierarchyProperties = new HashSet();
        java.util.Map hierarchyDescription = fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties.getInstance()
                .getHierarchyDescription(resourceFactory,TheaConfiguration.getDefault().getConfiguration());
            Iterator it = hierarchyDescription.values().iterator();
            while (it.hasNext()) {
               Object[] tuple = (Object[]) it.next();
                Resource prop = (Resource) tuple[0];
                Criterion crit = (Criterion) tuple[1];
               hierarchyProperties.add(prop);
            }
            

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
                    try {
                        all.addAll(aResource.getTargets(hierarchyProperties, Resource.TRANSITIVE_SEARCH));
                    } catch (AllontoException ex) {
                        ex.printStackTrace();
                    }
            }
            // Keys are associated Terms, Values are number of occurences
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

    // TODO : RENAME THIS METHOD LATER
    // TODO : WRITE ALGORITHM FIRST
    // TODO : OPTIMIZE IT WHEN THE BEST ONE IS FOUND
    private Set compute(Resource nodeResource)
            throws AllontoException {
        Set allAssociatedGenes = null;
            Resource annotateProperty = resourceFactory.getResource(OWLProperties
                    .getInstance().getAnnotatePropertyName());
            // Get direct children of the the resource:
            Set children = new HashSet();
        java.util.Map hierarchyDescription = fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties.getInstance()
                .getHierarchyDescription(resourceFactory,TheaConfiguration.getDefault().getConfiguration());
            
            Iterator it = hierarchyDescription.values().iterator();
            
            while (it.hasNext()) {
                Object[] tuple = (Object[]) it.next();
                
                Resource prop = (Resource) tuple[0];
                Criterion crit = (Criterion) tuple[1];
                
                Collection targets = nodeResource.getTargets(prop, crit);
                
                if (targets != null) {
                    children.addAll(targets);
                }
            }
            
            if (children.isEmpty()) {
                children = null;
            }
            
            // retrieves the set of genes associated with nodeResource or one of its
            // descendants
            allAssociatedGenes = (Set) utilMap.get(nodeResource);
            // if utilMap already contains an entry for the term,
            // then the set of associated genes is already calculated : returns it
            // else, creates a new empty set
            if (allAssociatedGenes != null) {
                return allAssociatedGenes;
            } else {
                allAssociatedGenes = new HashSet();
            }
            // Retrieves all terms used in the classification
            Map terms = (Map) classificationRootNode.getProperty(Node.TERMS_MAP);
            if (children != null) {
                Iterator childrenIt = children.iterator();
                while (childrenIt.hasNext()) {
                    Resource aChild = (Resource) childrenIt.next();
                    // Performs the computation only if the term is used to annotate
                    // the
                    // classification
                    if (terms.containsKey(aChild)) {
                        // recurses to compute utilMap for all childs
                        allAssociatedGenes.addAll(compute(aChild));
                    }
                    
                }
            }
            // Retrieve the set of genes annotated with the term
            Collection targets = nodeResource.getTargets(annotateProperty);
            // If the term is used to annotate genes,
            // add the set of annotated genes to the set of
            // genes annotated with its childs
            if (targets != null) {
                allAssociatedGenes.addAll(targets);
            }
            // updates utilMap
            utilMap.put(nodeResource, allAssociatedGenes);
        
        return allAssociatedGenes;
    }

    /**
     * Extract annotations correspending to the specified branch.
     * 
     * @param nodeResource
     *            The root node of the branch.
     * @param branch
     *            Branch's name.
     */
    public void createAnnotations(final Resource nodeResource,
            final String branch) {
        // Remember It:
//        branchRootResource = nodeResource;
        branchRootName = branch;
        BlockingSwingWorker worker = new BlockingSwingWorker(
                (Frame) WindowManager.getDefault().getMainWindow(),
                "Comparing ...", "Operation in progress, please wait ...", true) {
            protected void doNonUILogic() throws RuntimeException {
                // Create a Session using a Connection

                    boolean ignoreUnknown = CESettings.getInstance()
                            .isIgnoreUnknownSelected();
                    boolean ignoreNotAnnotated = CESettings.getInstance()
                            .isIgnoreNotAnnotatedSelected();

                    // allBranchTerms contains list of terms under a root node
                    // from the ontology
                    Collection allBranchTerms = createWholeBranchTermsList(nodeResource);

                    // Count Genes for the Top root node
                    int genesCount = countGenes(allBranchTerms, ignoreNotAnnotated, ignoreUnknown);
                    System.out.println("genesCount="+genesCount);
                    // Get terms that annotate the root node:
                    Map map = null; // before was called: rootTermsMap

                    if (CESettings.getInstance().isClassifBaseSelected()) {
                        map = (Map) classificationRootNode
                                .getProperty(Node.TERMS_MAP);
                    } else if (CESettings.getInstance()
                            .isOntologyBaseSelected()) {
                        try {
                            // utilMap.clear();
                            compute(nodeResource);
                        } catch (AllontoException ex) {
                            ex.printStackTrace();
                        }
                        int anInt = ((Set) utilMap.get(nodeResource)).size();
                        System.out.println("anInt="+anInt);
                        classificationRootNode.addProperty(branch
                                + Node.NB_ASSOC, new Integer(anInt));
                        classificationRootNode.addProperty(Node.TERMS_GMAP,
                                utilMap);
                        System.out.println("nbAssoc = " + anInt);
                        System.out.println("utilMap.size = " + utilMap.size());
                        System.out.println("utilMap = " + utilMap);
                        // utilMap.clear();

                        map = (Map) classificationRootNode
                                .getProperty(Node.TERMS_GMAP);
                        if (ignoreNotAnnotated) {
                            genesCount = ((Integer) classificationRootNode
                                    .getProperty(branch + Node.NB_ASSOC))
                                    .intValue();
                        } else {
                            genesCount = ((Integer) classificationRootNode
                                    .getProperty(Node.NB_GENE_PRODUCTS_IN_SPECIE))
                                    .intValue();
                        }
                    } else if (CESettings.getInstance()
                            .isUserSpecifiedBaseSelected()) {
                        map = (Map) classificationRootNode
                                .getProperty(Node.TERMS_LIST_MAP);
                        if (map == null) {
                            // throw(new NullPointerException());
                            // JOptionPane
                            // .showMessageDialog(
                            // this,
                            // "Using a gene products' list for the labelling
                            // cannot be done without a prior annotation.");
                            return;
                        }
                        if (ignoreNotAnnotated) {
                            genesCount = ((Integer) classificationRootNode
                                    .getProperty(branch + Node.NB_ASSOC_IN_LIST))
                                    .intValue();
                        } else {
                            genesCount = ((Integer) classificationRootNode
                                    .getProperty(Node.NB_GENE_PRODUCTS_IN_LIST))
                                    .intValue();
                        }
                    }

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

                    createLabels(classificationRootNode,
                            branchTermsMap, genesCount, ignoreUnknown,
                            ignoreNotAnnotated, new HashMap());

                    // IF Show physically adjacent genes IS SELECTED
                    if (CESettings.getInstance().isShowPhysicallyAdjacent()) {
                        compareWithMap();
                    } else {
                        propertySupport.firePropertyChange(
                                "showPhysicallyAdjacents", null, null);// NOI18N
                    }

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
                
                // Fire an event telling the annotation is done:
                propertySupport.firePropertyChange("annotationChanged", null,
                        null);// NOI18N
            }
        };
        worker.start();
    }

    // TODO : write clean javadoc
    // used to update the annotation whene parameters change.
    public void updateAnnotation() {
        this.createAnnotations(this.branchRootResource, this.branchRootName);
    }

    // give a significant name. Any idea Claude ?
    private void compareWithMap() {
        Set colocatedGeneSets = new HashSet();
        compareWithMap(classificationRootNode, colocatedGeneSets);
        nb_colocalized_groups = colocatedGeneSets.size();
        nb_colocalized_genes = 0;

        Set annotatedGenes = new HashSet();
        Iterator it = colocatedGeneSets.iterator();
        int ctr = 0;

        while (it.hasNext()) {
            Set geneSet = (Set) it.next();
            nb_colocalized_genes += geneSet.size();
            annotatedGenes.addAll(geneSet);
            Iterator iterator = geneSet.iterator();

            while (iterator.hasNext()) {
                Node aNode = (Node) iterator.next();
                String annotation = "[Ch_" + aNode.getProperty(Node.CHROMOSOME);// NOI18N
                Boolean cpos = (Boolean) aNode.getProperty(Node.COMPLEMENT_POS);

                if (cpos.equals(Boolean.TRUE)) {
                    annotation += "r";// NOI18N
                }

                annotation += (" s:" + aNode.getProperty(Node.STRAND_POSITION)// NOI18N
                        + " c:" + aNode.getProperty(Node.CHROMOSOMAL_POSITION) + "]");// NOI18N
                aNode.addProperty(Node.ANNOTATION, annotation);
            }
        }
        propertySupport.firePropertyChange("colocalized", null, annotatedGenes);
    }

    // give a significant name. Any idea Claude ?
    private void compareWithMap(Node aNode, Set colocatedGeneSet) {
        if (aNode == null) {
            return;
        }
        if (aNode.isLeaf()) {
            return;
        }
        Iterator childrenIt = aNode.getChildren().iterator();
        while (childrenIt.hasNext()) {
            Node aChild = (Node) childrenIt.next();
            compareWithMap(aChild, colocatedGeneSet);
        }
        aNode.removeProperty(Node.ANNOTATION);
        List ln = aNode.getLeaves();

        if (ln.size() > CESettings.getInstance().getNbrMaxClusterSize()) {
            return;
        }
        boolean bln = CESettings.getInstance().isNbrSeparateStrandSelected();
        String testedPosition = (bln ? "strand_position"
                : "chromosomal_position");
        int maxDistance = CESettings.getInstance().getNbrMaxDistance();
        while (!ln.isEmpty()) {
            Set closeGenes = new HashSet();
            Node leaf1 = (Node) ln.get(0);
            Integer pos1 = ((Integer) leaf1.getProperty(testedPosition));
            String chr1 = (String) leaf1.getProperty(Node.CHROMOSOME);
            Boolean cpos1 = (Boolean) leaf1.getProperty(Node.COMPLEMENT_POS);
            int groupStartPos = 0;
            int groupEndPos = 0;
            if ((pos1 != null) && (chr1 != null) && (cpos1 != null)) {
                groupStartPos = pos1.intValue() - maxDistance;
                groupEndPos = pos1.intValue() + maxDistance;
                Object[] groupArray = ln.toArray();
                for (int i = 1; i < groupArray.length; i++) {
                    Node leaf2 = (Node) groupArray[i];
                    String chr2 = (String) leaf2.getProperty(Node.CHROMOSOME);
                    if (chr2 == null) {
                        continue;
                    }
                    if (!chr1.equals(chr2)) {
                        continue;
                    }
                    if (bln) {
                        Boolean cpos2 = (Boolean) leaf2
                                .getProperty(Node.COMPLEMENT_POS);
                        if (cpos2 == null) {
                            continue;
                        }
                        if (!cpos1.equals(cpos2)) {
                            continue;
                        }
                    }
                    Integer pos2 = ((Integer) leaf2.getProperty(testedPosition));
                    if (pos2 == null) {
                        continue;
                    }
                    if (pos2.intValue() < groupStartPos) {
                        continue;
                    }
                    if (pos2.intValue() > groupEndPos) {
                        continue;
                    }
                    closeGenes.add(leaf2);
                }
            }
            closeGenes.add(leaf1);
            ln.remove(leaf1);
            if (closeGenes.size() <= 3) {
                continue;
            }
            double nbGenes = ((Integer) classificationRootNode
                    .getProperty(Node.NB_GENE_PRODUCTS_IN_SPECIE))
                    .doubleValue();
            double groupSize = (double) maxDistance * 2;
            double globalOccurence = groupSize / nbGenes;
            // Calculation of p-value
            double kValue = (double) closeGenes.size() - 1;
            double n = (double) aNode.getLeaves().size() - 1;
            // n = groupSize;
            double p = globalOccurence;
            double np = n * p;
            double pValue = 0;
            double moreThanKProb = Probability.binomialComplemented(
                    (int) kValue, (int) n, p);
            moreThanKProb = moreThanKProb * n; // bonferonni correction
            if (moreThanKProb <= CESettings.getInstance().getNbrPValue()) {
                // a set of close genes has been found
                // trying to add it in an existing group
                // or create a new group
                boolean found = false;
                Iterator it = colocatedGeneSet.iterator();
                while (it.hasNext()) {
                    Set geneGroup = (Set) it.next();
                    Set genes = new HashSet(geneGroup);
                    if (genes.removeAll(closeGenes)) {
                        geneGroup.addAll(closeGenes);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    colocatedGeneSet.add(closeGenes);
                }
            }
        }
    }

    /**
     * Retreives and returns resource under the branch which the parent node
     * correspends to the given resource, <i>aResource </i>.
     * 
     * @param resourceFactory
     *            A ResourceFactory.
     * @param aResource
     *            The resource represented by the root node of the selected
     *            branch.
     * @return Children of the term correspending to the resource <i>aResource
     *         </i>.
     */
    private Collection createWholeBranchTermsList(Resource aResource) {
        Collection descendants = new HashSet();
        // Get direct children of the the resource:
//        Set children = new HashSet();
        java.util.Map hierarchyDescription = fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties.getInstance()
                .getHierarchyDescription(resourceFactory,TheaConfiguration.getDefault().getConfiguration());
            Set hierarchyProperties = new HashSet();
            Iterator it = hierarchyDescription.values().iterator();
            while (it.hasNext()) {
                Object[] tuple = (Object[]) it.next();

                Resource prop = (Resource) tuple[0];
                Criterion crit = (Criterion) tuple[1];
            try {
                prop = prop.getInverse();
            } catch (AllontoException ex) {
                ex.printStackTrace();
            }
                if (prop == null) continue;
                hierarchyProperties.add(prop);
            }
        try {
            descendants = aResource.getTargets(hierarchyProperties, Resource.TRANSITIVE_SEARCH);
        } catch (AllontoException ex) {
            ex.printStackTrace();
        }
//            try {
//                prop = prop.getInverse();
//                sess.update(prop);
//                if (prop != null) {
//                    Set targets = aResource.getTargets(prop, crit);
//
//                    if (targets != null) {
//                        children.addAll(targets);
//                    }
//                }
//            } catch (AllontoException ae) {
//            }
//        }

//        if (children.isEmpty()) {
//            children = null;
//        }
//
//        if (children != null) {
//            Iterator targetsIt = children.iterator();
//            while (targetsIt.hasNext()) {
//                Resource target = (Resource) targetsIt.next();
//                descendants.addAll(createWholeBranchTermsList(resourceFactory,
//                        target));
//            }
//            descendants.addAll(children);
//        }
        return descendants;
    }

    /**
     * Create a labels for classification' nodes using terms that annotate them.
     */
    private int createLabels(Node aNode,
            Map rootTermsMap, int nbRootAssociatedGenes, boolean ignoreUnknown,
            boolean ignoreNotAnnotated, Map termsMap) {
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
        // aNode.setName("");
        aNode.setLabel("");// NOI18N
        aNode.removeProperty(Node.TERM_AND_SCORE);
        int count = 0;/* number of associated Genes */
        // Iterate over the list of "aNode"'s children
        Iterator childrenIt = aNode.getChildren().iterator();
        while (childrenIt.hasNext()) {
            Node aChild = (Node) childrenIt.next();
            Map m = new HashMap();
            // For each child:
            count += createLabels(aChild, rootTermsMap,
                    nbRootAssociatedGenes, ignoreUnknown, ignoreNotAnnotated, m);
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
        // Set rootTerms = new HashSet(getController().getRootTerms().values());
        // Set unknownTerms = new HashSet(getController().getUnknownTerms()
        // .values());
        int nbTestedTerms = 0; // used for multiple testing correction
        while (it.hasNext()) {
            Resource aResource = (Resource) it.next();
            String aResourceName = "";// NOI18N
            try {
                
                Resource res = aResource
                        .getTarget(OWLProperties
                                .getInstance().getNodeNameProperty());
                if (res != null) {
                    aResourceName = res.getAcc();
                }
            } catch (AllontoException ae) {
            }
            // continue calculation only if the term belong to the right branch
            if (rootTermsMap.get(aResource) == null) {
                continue;
            }
            // do not process ontologie roots
            // if (rootTerms.contains(t)) {
            // continue;
            // }

            // do not process unknown terms if ignoreUnknown flag is true
            if (ignoreUnknown && aResourceName.endsWith("unknown")) {// NOI18N
                continue;
            }
            // if (t == getController().getRoot()) {
            // continue;
            // }
            nbTestedTerms += 1;
            int nbGenesAssociatedWithTermInNode = (termsMap.get(aResource) == null) ? 0
                    : ((Integer) termsMap.get(aResource)).intValue();
            Object o = rootTermsMap.get(aResource);
            double nbGenesAssociatedWithTermInRoot = 0;
            if (o instanceof Set) {
                nbGenesAssociatedWithTermInRoot = ((Set) o).size();
            } else if (o instanceof Integer) {
                nbGenesAssociatedWithTermInRoot = ((Integer) o).intValue();
            }

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
            // Score Calculation
            // Ontology Terms Count:
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
            Score score = (Score) mapE.getValue();
            if (settings.isStatisticalCalculationSelected()) {
                double pValue = score.getScore();

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
                score.putScore(pValue);
                if (score.isOverexpressed()) {
                    nb_overAnnotation += 1;
                } else {
                    nb_underAnnotation += 1;
                }
            }
            commonTerms.add(score);
        }
        if (commonTerms.isEmpty()) {
            return count;
        }
        nb_annotatedClusters += 1;
        Collections.sort(commonTerms, new Comp());
        if (settings.isStatisticalCalculationSelected()) {
            Collections.reverse(commonTerms);
        }
        System.out.println("annotations for node");
        Iterator tit = commonTerms.iterator();
        while (tit.hasNext()) {
            Score sc = (Score) tit.next();
            System.out.println(sc.getTerm().getName() + ": "
                    + sc.getNbAssociatedGenesInCluster() + "/"
                    + sc.getClusterSize() + " ("
                    + sc.getNbAssociatedGenesInPopulation() + "/"
                    + sc.getpopulationSize() + ") = " + sc.getScore());
        }
        Resource bestTerm = ((Score) commonTerms.get(0)).getTerm();
        String label = "";// NOI18N
        if (settings.isShowTermID()) {
            label += bestTerm.getResource_id();
        }
        if (settings.isShowTermName()) {
            if (!"".equals(label)) {
                label += ":";
            }
            try {
                Resource res = bestTerm
                        .getTarget(OWLProperties
                                .getInstance().getNodeNameProperty());
                if (res != null) {
                    label += (res.getAcc() + " ");
                }
            } catch (AllontoException ae) {
            }
        }
        if (commonTerms.size() > 1) {
            label += "(+)";// NOI18N
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
                    aChild.setLabel("");// NOI18N
                }
            }
        }
        return count;
    }

    /**
     * Count the number of gene products.
     * 
     * @param resourceFactory
     *            Factory for hibernate support.
     * @param allBranchTerms
     *            Terms in a given branch.
     * @param ignoreNotAnnotated
     *            Flags to indicate wether to ignore not annotated genes or not.
     * @param ignoreUnknown
     *            Flags to indicate wether to ignore genes annotated with
     *            unknown genes or not.
     * @return number of gene products.
     */
    private int countGenes(Collection allBranchTerms,
            boolean ignoreNotAnnotated, boolean ignoreUnknown) {
            System.out.println("allBranchTerms "+allBranchTerms);
        // allBranchTerms contains all terms from the onotology branch
        List ln = classificationRootNode.getLeaves();
        // If we dont ignore:
        // - Genes annotated with unknown terms
        if (!ignoreNotAnnotated && !ignoreUnknown) {
            return ln.size();
        }
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
            System.out.println("annotations for "+aNode+"="+at);
            boolean bln = false;
            // Tells if the node aNode is annotated with an unkown Term
            boolean annotatedWithUnknown = true;
            boolean isEndsWithUnknown = false;
            // Iterate over the list of terms associated to "aNode" if not null
            if ((at != null) && !at.isEmpty()) {
                Set annots = new HashSet(at);
                if (annots.removeAll(allBranchTerms)) {
                    bln = true;
                }
//                Iterator atIt = at.iterator();
//                while (atIt.hasNext()) {
//                    Resource aResource = (Resource) atIt.next();
//                    // If the list of terms of the brach contains
//                    // one of terms associated the the Node "leaf"
//                    // brean the while loop
//                    if (allBranchTerms.contains(aResource)) {
//                        // OK, the current node is annotated with a Term that
//                        // belong to the this branch
//                        bln = true;
//                        break;
//                    }
//                }
//                // Now try to find out if the gene is annotated using
//                // an unknown term:
//                Iterator atIt = at.iterator();
//                String name = "";// NOI18N
//                StringValue sv;
//                while (atIt.hasNext()) {
//                    Resource aResource = (Resource) atIt.next();
//                    try {
//                        try {
//                            sess.update(aResource);
//                        } catch (HibernateException ex) {
//                            ex.printStackTrace();
//                        }
//                        sv = (StringValue) aResource.getTarget(resourceFactory
//                                .getResource(OWLProperties.getInstance()
//                                        .getNodeNameProperty()));
//                        if (sv != null) {
//                            name = sv.getValue();
//                        }
//                    } catch (AllontoException ae) {
//                    }
//                    // If the list of terms of the brach contains
//                    // one of terms associated the the Node "leaf"
//                    // brean the while loop
//                    if (name.endsWith("unknown")) {// NOI18N
//                        isEndsWithUnknown = true;
//                        break;
//                    }
//                }
            }
//            if (bln && isEndsWithUnknown) {
//                // Yes, the Gene is annotated with
//                // an unknown
//                // Term
//                annotatedWithUnknown = false;
//            }
                annotatedWithUnknown = false;
            aNode.addProperty(Node.GENE_NA, new Boolean(!bln));
            aNode.addProperty(Node.GENE_UNKNOWN, new Boolean(
                    !annotatedWithUnknown));
            
            boolean annotationHasToBeCounted = false;
            if (bln || !ignoreNotAnnotated) {
                count += 1;
            }
//            // If we ignore not annotated Genes and
//            // Genes annotated with unknown Terms
//            if (!ignoreNotAnnotated && ignoreUnknown) {
//                // IF the Gene is not annotated with a Term that belongs
//                // to the treated branch
//                // OR
//                // the Gene is annotated with an unknown Term
//                if (!bln || annotatedWithUnknown) {
//                    count += 1;
//                }
//                // If we ignore not annotated Genes
//                // AND
//                // Genes annotated with an unknown Term
//            } else if (ignoreNotAnnotated && ignoreUnknown) {
//                if (bln && annotatedWithUnknown) {
//                    count += 1;
//                }
//                // If we ignore not annotated Genes
//                // AND
//                // we DONT ignore Genes annotated with unkonw Terms
//            } else if (ignoreNotAnnotated && !ignoreUnknown) {
//                if (bln) {
//                    count += 1;
//                }
//            }
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