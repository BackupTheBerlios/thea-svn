package fr.unice.bioinfo.thea.editor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.unice.bioinfo.allonto.datamodel.Entity;
import fr.unice.bioinfo.thea.editor.settings.CESettings;

/**
 * A class representing a node. Each node has a link to its parent and hold the
 * list of its childs. Nodes that have no child are leaves.
 * @author Claude Pasquier
 * @author Saïd El kasmi.
 */
public class Node implements PropertyChangeListener {

    //BEGIN FACTORING
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
    private String label;
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
    /** Flag to determine if this node's state is detailed. */
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

    /** Creates a node. */
    public Node() {
        // instanciate properties list
        this.properties = new Hashtable();
        // Creates this node's layout support
        this.layoutSupport = new NodeLayoutSupport();
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
        if (this.isLeaf())
            return;
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
        if (this.isLeaf())
            return;
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
            hidden = true;
        }
        if (this.parent == null) {
            hidden = false;
        }
        hidden = parent.isHidden();
        return hidden;
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
            terminals = 1;
            return terminals;
        }
        Iterator iterator = this.getChildren().iterator();
        while (iterator.hasNext()) {
            terminals += ((Node) iterator.next()).getTerminals();
        }
        return terminals;
    }

    /***/
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

    /** Sets selected/unioselected state. */
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
        return label;
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

    //END FACTORING

    /** indicates if the userData with a specified key should be saved */
    private static Set serializableUserData = new HashSet();

    /** Contains properties which could be visible from the GUI */
    private Hashtable /* to store all string values */strings;

    /** Annotion consists of associating an {@link Entity}to this node. */
    private Entity entity;

    /** The name of the node. */
    private String name = null;
    private String clName;

    /** User data associated with this node */
    private Map userData = new HashMap();

    //    /**
    //     * Creates a Node.
    //     */
    //    public Node() {
    //        init();
    //
    //        // register itseld as a listener for settings
    //        CESettings.getInstance().addPropertyChangeListener(this);
    //    }

    /**
     * Associate an {@link Entity}to this node.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
        //this.extractStringValues();
    }

    /**
     * Returns the {@link Entity}associated to this node.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Initialize this node.
     */
    private void init() {
        parent = null;
        children = null;
        name = null;
        branchLength = 0;
        numberOfLeaves = -1;
        userData = new HashMap();
    }

    /**
     * Give a nome to this node.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns this node's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for userData. Allows external application to associate data with
     * nodes
     * @param key The key used to access external data
     * @param o The object associated with this node
     */
    public void setUserData(String key, Object o) {
        userData.put(key, o);
    }

    /**
     * Getter for userData. Allows external application to retrieve data
     * associated with this nodes
     * @param key The key used to access external data
     * @return The object associated with this node
     */
    public Object getUserData(String key) {
        return userData.get(key);
    }

    //    public void setUserData(Map m) {
    //        userData = m;
    //    }

    //    /**
    //     * Getter for userData
    //     * @return the map of serialisable user data
    //     */
    //    public Map getUserData() {
    //        Map m = new HashMap();
    //        Iterator it = userData.keySet().iterator();
    //
    //        while (it.hasNext()) {
    //            String key = (String) it.next();
    //
    //            if (serializableUserData.contains(key)) {
    //                m.put(key, userData.get(key));
    //            }
    //        }
    //
    //        return m;
    //    }

    //    public Node getRoot() {
    //        return getRoot(this);
    //    }

    //    public Node getRoot(Node node) {
    //        if (node.getParent() == null) {
    //            return node;
    //        }
    //
    //        return getRoot(node.getParent());
    //    }

    public static void setSerializableUserData(String key, boolean b) {
        if (b) {
            serializableUserData.add(key);
        } else {
            serializableUserData.remove(key);
        }
    }

    //    public static void setSerializableUserData(Set s) {
    //        System.err.println("set called with " + s);
    //        serializableUserData = new HashSet(s);
    //    }

    //    public static Set getSerializableUserData() {
    //        return serializableUserData;
    //    }

    //    /**
    //     * Indicates if the node is a leaf
    //     * @return True if this node has no child
    //     */
    //    public boolean isLeaf() {
    //        return (children == null);
    //    }

    //    /**
    //     * Indicates if the node is an ancestor of the parameter node
    //     * @param node The node that has to be searched in the descendant of the
    //     * current node
    //     * @return A boolean indicating if this node is ancestor of <b>node </b>
    //     */
    //    public boolean isAncestorOf(Node node) {
    //        if (node == null) {
    //            return false;
    //        }
    //
    //        if (node.getParent() == this) {
    //            return true;
    //        }
    //
    //        return isAncestorOf(node.getParent());
    //    }

    //    /**
    //     * Count leaves that are descendant of that node
    //     * @return The number of leaves rooted to this node
    //     */
    //    public int getNumberOfLeaves() {
    //        if (numberOfLeaves != -1) {
    //            return numberOfLeaves;
    //        }
    //
    //        if (children == null) {
    //            return 1;
    //        }
    //
    //        int leaves = 0;
    //        Iterator iterator = children.iterator();
    //
    //        while (iterator.hasNext()) {
    //            leaves += ((Node) iterator.next()).getNumberOfLeaves();
    //        }
    //
    //        numberOfLeaves = leaves;
    //
    //        return leaves;
    //    }

    //    /**
    //     * Count descendants of that node
    //     * @return The number of descendants of this node
    //     */
    //    public int countDescendants() {
    //        if (children == null) {
    //            return 1;
    //        }
    //
    //        int descendants = 0;
    //        Iterator iterator = children.iterator();
    //
    //        while (iterator.hasNext()) {
    //            descendants += ((Node) iterator.next()).countDescendants();
    //        }
    //
    //        return descendants + 1;
    //    }

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

    //    /**
    //     * Return the list of all nodes that are descendant of that node
    //     * @return The list of nodes
    //     */
    //    public Collection getAllChildNodes() {
    //        Collection childs = new HashSet();
    //        collectAllChildNodes(childs);
    //
    //        return childs;
    //    }
    //
    //    private void collectAllChildNodes(Collection childs) {
    //        childs.add(this);
    //
    //        if (isLeaf()) {
    //            return;
    //        }
    //
    //        Iterator iterator = getChildren().iterator();
    //
    //        while (iterator.hasNext()) {
    //            ((Node) iterator.next()).collectAllChildNodes(childs);
    //        }
    //    }

    /**
     * Get the depth of this node
     * @return The number of getParent() to perform in order to reach the root
     */
    public int getDepth() {
        if (parent == null) {
            return 0;
        }

        return (parent.getDepth() + 1);
    }

    //    /**
    //     * Compute the distance from an ancestor to this node
    //     * @param ancestor the ancestor (if ancestor is null, root is assumed)
    //     * @param b useBranchLength a flag specifying if branch lengths has to be
    //     * used
    //     * @return The distance between an ancestor's node and the current node
    //     */
    //    public double getDistanceToAncestor(Node ancestor, boolean
    // useBranchLength) {
    //        double bl = (useBranchLength ? branchLength : 1);
    //        if (parent == null) {
    //            return 0;
    //        }
    //        if (parent == ancestor) {
    //            return bl;
    //        }
    //        return (parent.getDistanceToAncestor(ancestor, useBranchLength) + bl);
    //    }

    /**
     * Find the nodes that match the string
     * @param s The string to be matched
     * @param ignoreCase true to perform a case insensitive search
     * @return a List holding all matched nodes
     */
    public List findMatchingNodes(String s, boolean ignoreCase) {
        List matches = new Vector();

        if (ignoreCase && s.equalsIgnoreCase(name)) {
            matches.add(this);
        } else if (!ignoreCase && s.equals(name)) {
            matches.add(this);
        }

        if (children == null) {
            return matches;
        }

        // lookup child nodes
        Iterator iterator = children.iterator();

        while (iterator.hasNext()) {
            List l = ((Node) iterator.next()).findMatchingNodes(s, ignoreCase);
            matches.addAll(l);
        }

        return matches;
    }

    /**
     * Find the nodes that match a pattern
     * @param p The pattern to match
     * @return A List holding all matched nodes
     */
    public List findMatchingNodes(Pattern p) {
        List matches = new Vector();

        if (name != null) {
            Matcher m = p.matcher(name);

            if (m.matches()) {
                matches.add(this);
            }
        }

        if (children == null) {
            return matches;
        }

        // lookup child nodes
        Iterator iterator = children.iterator();

        while (iterator.hasNext()) {
            List l = ((Node) iterator.next()).findMatchingNodes(p);
            matches.addAll(l);
        }

        return matches;
    }

    /**
     * Extracts string values. String values are simply properties of the entity
     * associated to a leaf node
     */
    private void extractStringValues() {
        //        if (this.entity == null) {
        //            return;
        //        }
        //
        //        ResourceFactory rf = null;
        //
        //        try {
        //            rf = (ResourceFactory) AllontoFactory.getResourceFactory();
        //            HibernateUtil.createSession(ConnectionManager.getConnection());
        //        } catch (StackOverflowError s) {
        //            s.printStackTrace(System.err);
        //        } catch (HibernateException he) {
        //            he.printStackTrace(System.err);
        //        }
        //
        //        Iterator mapIt = ((Resource) entity).getArcs().entrySet().iterator();
        //        String accessor;
        //        Resource resource;
        //        Entity e;
        //        Map.Entry entry;
        //
        //        // Initialize list of straing values
        //        strings = new Hashtable();
        //
        //        while (mapIt.hasNext()) {
        //            entry = (Map.Entry) mapIt.next();
        //            e = (Entity) entry.getValue();
        //
        //            // Since values in the Map are not all instances of StringValue,
        //            // do tests using the instanceof operator
        //            if (e instanceof StringValue) {
        //                resource = (Resource) entry.getKey();
        //                accessor = resource.getAcc();
        //                strings.put(accessor, ((StringValue) e).getValue());
        //            }
        //        }
        //
        //        // store the list of string values in the common settings object
        //        CESettings.getInstance().setAccTable(strings);
        //
        //        // Finally, save initial name
        //        clName = name;
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equalsIgnoreCase(CESettings.PROP_ACCESSOR)) {
            // applay this only for leaves nodes
            if (isLeaf()) {
                if (entity != null) {
                    String key = e.getNewValue().toString();

                    if (!key.equalsIgnoreCase("NAME")) {
                        // update name
                        setName(clName + " [" + strings.get(key) + "]");
                    } else {
                        setName(clName);
                    }
                }
            }
        }
    }
}