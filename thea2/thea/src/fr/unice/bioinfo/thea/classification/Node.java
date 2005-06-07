package fr.unice.bioinfo.thea.classification;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import fr.unice.bioinfo.allonto.datamodel.Entity;
import fr.unice.bioinfo.thea.classification.editor.util.Discretization;
import fr.unice.bioinfo.thea.classification.settings.CESettings;

/**
 * A class representing a node. Each node has a link to its parent and hold the
 * list of its childs. Nodes that have no child are leaves.
 * @author Claude Pasquier
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Node {

    /** this node's name property. */
    public static final String NAME = "name";//NOI18N
    /** Annotations of this node */
    public static final String ANNOTATIONS = "annotations";//NOI18N
    /***/
    public static final String MEASURES = "measures";//NOI18N
    /***/
    public static final String NB_MEASURES = "nbMeasures";//NOI18N
    /***/
    public static final String USER_ANNOTATIONS = "userAnnotations";//NOI18N
    /***/
    public static final String ANNOTATION = "annotation";//NOI18N
    /***/
    public static final String MIN_MEASURE = "minMeasure";//NOI18N
    /***/
    public static final String MAX_MEASURE = "maxMeasure";//NOI18N
    /***/
    public static final String UNDER_EXP_DECILES = "underExpDeciles";//NOI18N
    /***/
    public static final String OVER_EXP_DECILES = "overExpDeciles";//NOI18N
    /***/
    public static final String FROZEN = "frozen";//NOI18N
    /***/
    public static final String ID_IN_CLASSIF = "idInClassif";//NOI18N
    /***/
    public static final String DB_KEY = "dbKey";//NOI18N

    public static final String CHROMOSOME = "chromosome";//NOI18N

    public static final String COMPLEMENT_POS = "complementPos";//NOI18N

    public static final String CHROMOSOMAL_POSITION = "chromosomal_position";//NOI18N

    public static final String STRAND_POSITION = "strand_position";//NOI18N

    public static final String START_POS = "startPos";//NOI18N

    public static final String END_POS = "endPos";//NOI18N

    public static final String DB_KEY_MAP = "dbKeyMap";//NOI18N

    public static final String SYMBOL = "symbol";//NOI18N

    public static final String GENE_PRODUCT_ID = "GeneProductID";//NOI18N

    public static final String SPECIES_MAP = "speciesMap";//NOI18N

    public static final String NB_GENE_PRODUCTS_IN_SPECIE = "nbGeneProductsInSpecie";//NOI18N

    public static final String ASSOC_TERMS = "assocTerms";//NOI18N

    public static final String TERMS_MAP = "termsMap";//NOI18N

    public static final String GENE_NA = "geneNA";//NOI18N

    public static final String GENE_UNKNOWN = "geneUnknown";//NOI18N

    /** Node's children. This contains only direct children. */
    private List children = null;

    /** A collection that contains all properties of this node. */
    private Hashtable properties = null;

    /** The parent of this node. */
    private Node parent = null;

    /** Number of leaf nodes that have this node as a root node. */
    private int numberOfLeaves = -1;

    /** The length of the branch. */
    private double branchLength = 0;

    /**
     * Each node has a label that may be used for display. The label could be
     * the name given in a classification or extracted from a database
     * (ontology). Whene a node is created, the label is by default the name of
     * this node in the used classification.
     */
    private String label = null;

    /** The name of the node. */
    private String name = null;

    /** This node's layout support. */
    private NodeLayoutSupport layoutSupport;

    /**
     * The position of this node when it is drown in a Swing component inside
     * the classifiaction editor.
     */
    private Point2D position;

    /** The area ( a rectangle ) this node is drawn into. */
    private Rectangle2D.Double area;

    /** Flag to store this node's collapsed/uncollapsed state. */
    private boolean collapsed = false;

    /** Flag to determine if this node is terminal. */
    private boolean terminal = false;

    /**
     * Flag that indicates if this node is not paint due to a lack of space on
     * the display area.
     */
    private boolean detailed = false;

    /** Flag indicating if the node is in the clipping area */
    private boolean inClipArea = false;

    /** Terminal nodes (non hidden nodes) that are descendant of that node. */
    private int terminals;

    /** Flag that indicates if this node is selected. */
    private boolean selected = false;

    /**
     * Flag ndicating if the node is collapsed, or a descendant of a collapsed
     * node.
     */
    private boolean hidden = false;

    /** Creates a node using a given list of children. */
    public Node(List children) {
        this();
        this.children = children;
    }

    /** Correspending {@link Entity}to this node. */
    private Entity entity;

    /** Creates a node. */
    public Node() {
        // instanciate properties list
        this.properties = new Hashtable();
        // Creates this node's layout support
        this.layoutSupport = new NodeLayoutSupport();
    }

    /** Associate an {@link Entity}to this node. */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /** Returns the {@link Entity}associated to this node. */
    public Entity getEntity() {
        return entity;
    }

    /** Returns <i>True </i> if this node is collapsed. <i>False </i>otherwise. */
    public boolean isCollapsed() {
        return collapsed;
    }

    /** Sets collapsed/uncollapsed state for this node. */
    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    /** Returns <i>True </i> if this node is terminal. <i>False </i>otherwise. */
    public boolean isTerminal() {
        return ((this.isLeaf()) || this.isCollapsed());
    }

    /** Give terminal/not terminal state for this node. */
    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    /** Returns the area occuped by this node. */
    public Rectangle2D.Double getArea() {
        return area;
    }

    /** Sets the area for this node. */
    public void setArea(Rectangle2D.Double area) {
        this.area = area;
    }

    /** Returns the position of this node. */
    public Point2D getPosition() {
        return position;
    }

    /** Sets the position of this node. */
    public void setPosition(Point2D position) {
        this.position = position;
    }

    /** Returns the branch length. */
    public double getBranchLength() {
        return branchLength;
    }

    /** Sets the branch length. */
    public void setBranchLength(double branchLength) {
        this.branchLength = branchLength;
    }

    /** Tells wether this node's state is detailed or not. */
    public boolean isDetailed() {
        return detailed;
    }

    /** Stets detailed/undetaile state of this node. */
    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
        if (this.isLeaf()) {
            return;
        }
        Iterator iterator = this.getChildren().iterator();
        while (iterator.hasNext()) {
            ((Node) iterator.next()).setDetailed(detailed);
        }
    }

    /**
     * Returns <i>True </i>if this node is in the clip area. <i>False
     * </i>otherwise.
     */
    public boolean isInClipArea() {
        return inClipArea;
    }

    /** Tells this node and its children if they are in the clip area. */
    public void setInClipArea(boolean inClipArea) {
        this.inClipArea = inClipArea;
        if (this.isLeaf()) {
            return;
        }
        Iterator iterator = this.getChildren().iterator();
        while (iterator.hasNext()) {
            ((Node) iterator.next()).setInClipArea(inClipArea);
        }
    }

    /**
     * Returns <i>True </i> if this node is collapsed or a descendant of a
     * collapsed one.
     */
    public boolean isHidden() {
        if (this.isCollapsed()) {
            return true;
        }
        if (this.parent == null) {
            return false;
        }
        return parent.isHidden();
    }

    /** Setter for this node's hidden/not hidden state. */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Counts and returns terminals (non hidden nodes) that are descendant of
     * this node.
     * @return int Number if terminal nodes.
     */
    public int getTerminals() {
        if (this.isTerminal()) {
            return 1;
        }
        terminals = 0;
        Iterator iterator = this.getChildren().iterator();
        while (iterator.hasNext()) {
            terminals += ((Node) iterator.next()).getTerminals();
        }
        return terminals;
    }

    /** Sets the number of terminal nodes. */
    public void setTerminals(int terminals) {
        this.terminals = terminals;
    }

    /**
     * Returns <i>True </i>if this node is selected by the user. <i>False
     * </i>otherwise.
     */
    public boolean isSelected() {
        return selected;
    }

    /** Sets selected/unselected state. */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** returns this node's layout support. */
    public NodeLayoutSupport getLayoutSupport() {
        return layoutSupport;
    }

    /** Attach a layout support to this node. */
    public void setLayoutSupport(NodeLayoutSupport nls) {
        this.layoutSupport = nls;
    }

    /**
     * Adds a child to the this node's children list.
     * @param aChild A child to add.
     */
    public void addChild(Node aChild) {
        children.add(aChild);
        aChild.setParent(this);
    }

    /**
     * Remove the given node from this node's children list.
     * @param aChild The child to remove.
     */
    public void removeChild(Node aChild) {
        aChild.setParent(null);
        children.remove(aChild);
    }

    /**
     * Returns this node's children list.
     * @return java.util.List This node's children.
     */
    public List getChildren() {
        return children;
    }

    /**
     * Sets this node's children list.
     * @param children Children.
     */
    public void setChildren(List children) {
        this.children = children;
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            Node aChild = (Node) iterator.next();
            aChild.setParent(this);
        }
    }

    /**
     * Sets the parent of this node.
     * @param parent The parent of this node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Returns the parent of this node.
     * @return fr.unice.bioinfo.thea.cle.model.Node This node's parent.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Returns this node's label.
     * @return java.lang.String The current label of this node.
     */
    public String getLabel() {
        if (label == null) {
            label = this.getName();
        }
        return label;
    }

    /** Sets the name of this node. */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns this nodes's name. */
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    /**
     * Returns list of properties of this node.
     * @return java.util.Hashtable Properties of this node.
     */
    public Hashtable getProperties() {
        return properties;
    }

    /**
     * Adds a property to this node's properties list.
     * @param key The name of tht property.
     * @param value The value of the property.
     */
    public void addProperty(Object key, Object value) {
        properties.put(key, value);
    }

    /**
     * Returns a property from this node's properties list using the given key
     * as a property name.
     * @param key The name of tht property.
     * @return java.lang.Object A property associated the given key.
     */
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    /**
     * Computes the distance from an ancestor to this node
     * @param ancestor the ancestor (if ancestor is null, root is assumed)
     * @param useBranchLength a flag specifying if branch lengths has to be used
     * @return The distance between an ancestor's node and the current node
     */
    public double getDistanceToAncestor(Node ancestor, boolean useBranchLength) {
        double bl = (useBranchLength ? branchLength : 1);
        if (parent == null) {
            return 0;
        }
        if (parent == ancestor) {
            return bl;
        }
        return (parent.getDistanceToAncestor(ancestor, useBranchLength) + bl);
    }

    /**
     * Returns the number of leaf nodes rooted at this node.
     * @return int Number of leaf nodes rooted at this node.
     */
    public int getNumberOfLeaves() {
        if (numberOfLeaves != -1) {
            return numberOfLeaves;
        }
        if (children == null) {
            return 1;
        }
        int counter = 0;
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            counter += ((Node) iterator.next()).getNumberOfLeaves();
        }
        numberOfLeaves = counter;
        return numberOfLeaves;
    }

    /**
     * Returns wether this node is an ancestor of given node.
     * @param node the node looking for an ancestor
     * @return boolean <i>True </i>if this node is the ancestor of <i>node </i>.
     *         <i>False otherwise.
     */
    public boolean isAncestorOf(Node node) {
        if (node == null) {
            return false;
        }
        if (node.getParent() == this) {
            return true;
        }
        return isAncestorOf(node.getParent());
    }

    /**
     * Returns wether this node is leaf or not.
     * @return boolean <i>True </i>if this node is leaf. <i>False </i>otherwise.
     */
    public boolean isLeaf() {
        return (children == null);
    }

    /**
     * Move the specified node (which must be present in the list of children at
     * the end of the list.
     * @param aChild the child node to move at the end.
     */
    public void moveChildAtEnd(Node aChild) {
        children.remove(aChild);
        children.add(aChild);
    }

    /**
     * Move the specified node (which must be present in the list of children at
     * the specified position.
     * @param aChild the child node to move
     * @param newPosition the new position of the given child.
     */
    public void moveChild(Node aChild, int newPosition) {
        children.remove(aChild);
        children.add(newPosition, aChild);
    }

    /** Returns the list of all visible leaves that are descendant of this node. */
    public List getVisibleLeaves() {
        List vnodes = new LinkedList();
        List children = this.getChildren();
        if (this.isTerminal()) {
            vnodes.add(this);
        } else {
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                vnodes.addAll(((Node) iterator.next()).getVisibleLeaves());
            }
        }
        return vnodes;
    }

    /**
     * Returns the list of all leaf nodes that are descendant of this one.
     * @return java.util.List List of leaves.
     */
    public List getLeaves() {
        List lnodes = new LinkedList();
        collectLeaves(lnodes);
        return lnodes;
    }

    /** Collects all leaf nodes that are descendant of this one. */
    private void collectLeaves(List l) {
        if (isLeaf()) {
            l.add(this);
        } else {
            Iterator iterator = getChildren().iterator();
            while (iterator.hasNext()) {
                ((Node) iterator.next()).collectLeaves(l);
            }
        }
    }

    /* under tests */

    public void init() {
        List leaves = this.getLeaves();
        if (leaves == null) {
            return;
        }
        if (leaves.size() == 0) {
            return;
        }
        Node firstLeaf = (Node) leaves.get(0);
        List measures = (List) firstLeaf.getProperty(Node.MEASURES);
        int nbMeasures = (measures == null) ? 0 : measures.size();
        this.addProperty(Node.NB_MEASURES, new Integer(nbMeasures));
        if (nbMeasures == 0) {
            return;
        }
        Iterator lIt = leaves.iterator();
        List measuresTable = new Vector();

        while (lIt.hasNext()) {
            Node lNode = (Node) lIt.next();
            measures = (List) lNode.getProperty(Node.MEASURES);
            int nbMeas = (measures == null) ? 0 : measures.size();
            if (nbMeas != nbMeasures) {
                this.addProperty(Node.NB_MEASURES, new Integer(0));
                return;
            }
            measuresTable.addAll(measures);
        }
        double minMeasure = ((Double) Collections.min(measuresTable))
                .doubleValue();
        Double medianValue = null;
        boolean logValues = false;

        if (minMeasure < 0) { //log values
            medianValue = new Double(0);
            logValues = true;
        } else {
            medianValue = new Double(1);
        }

        // The following bloc is to be moved/deleted
        if (CESettings.getInstance().isXpColorAutomaticSelected()) {
            Discretization d = new Discretization(measuresTable, logValues,
                    CESettings.getInstance().getSlots());
        } else if (CESettings.getInstance().isXpColorCustomizedSelected()) {
            Discretization d = new Discretization(measuresTable, CESettings
                    .getInstance().getPaletteLowerBounds(), CESettings
                    .getInstance().getPaletteColors(), CESettings.getInstance()
                    .getSlots());
        }
        // end of bloc
        measuresTable.add(medianValue);
        Collections.sort(measuresTable);
        int medianPos = measuresTable.indexOf(medianValue);
        int underExpDecileValue = medianPos / 10;
        int overExpDecileValue = (measuresTable.size() - medianPos) / 10;
        List underExpDeciles = new Vector();
        List overExpDeciles = new Vector();
        for (int i = 1; i < 10; i++) {
            underExpDeciles.add(measuresTable.get(medianPos
                    - (underExpDecileValue * i)));
            overExpDeciles.add(measuresTable.get(medianPos
                    + (overExpDecileValue * i)));
        }
        this.addProperty(Node.UNDER_EXP_DECILES, underExpDeciles);
        this.addProperty(Node.OVER_EXP_DECILES, overExpDeciles);
        this.addProperty(Node.MIN_MEASURE, Collections.min(measuresTable));
        this.addProperty(Node.MAX_MEASURE, Collections.max(measuresTable));
        //        List an = new ArrayList();
        //        an.add(new String("annotation 1"));
        //        an.add(new String("annotation 2"));
        //        this.addProperty(Node.USER_ANNOTATIONS,an);
    }
}