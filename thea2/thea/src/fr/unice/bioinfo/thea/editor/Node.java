package fr.unice.bioinfo.thea.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
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
    /**
     * Flag that indicates this node's name corresponds the one from the
     * classification.
     */
    public static String CLASSIFICATION_ID = "Classification ID";

    /**
     * Flag that indicates this node's name corresponds the one from the used DB
     */
    public static String DB_KEY = "DataBase Key";

    /** Flag that indicates this node's name corresponds the one from ?? */
    public static String SYMBOL = "Symbol";

    /** indicates if the userData with a specified key should be saved */
    private static Set serializableUserData = new HashSet();

    /** Contains properties which could be visible from the GUI */
    private Hashtable /* to store all string values */strings;

    /** Annotion consists of associating an {@link Entity}to this node. */
    private Entity entity;

    /** The parent of the node. */
    private Node parent;

    /** Children of this node. */
    private List children;

    /** The name of the node. */
    private String name;
    private String clName;

    /** The length of the branch. */
    private double branchLength;

    /** The number of leaves */
    private int leavesCount = -1;

    /** User data associated with this node */
    private Map userData;

    /**
     * Creates a Node.
     */
    public Node() {
        init();

        //      register itseld as a listener for settings
        CESettings.getInstance().addPropertyChangeListener(this);
    }

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
        leavesCount = -1;
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
     * Setter for branchLength
     * @param length The value for branchLength
     */
    public void setBranchLength(double length) {
        branchLength = length;
    }

    /**
     * Getter for branchLength
     * @return The branch length for the node
     */
    public double getBranchLength() {
        return branchLength;
    }

    /**
     * Setter for children
     * @param l The list of childs
     */
    public void setChildren(List l) {
        children = l;

        Iterator iterator = children.iterator();

        while (iterator.hasNext()) {
            Node n = (Node) iterator.next();
            n.setParent(this);
        }
    }

    /**
     * Adds a new child
     * @param node The child to be added
     */
    public void addChild(Node node) {
        children.add(node);
        node.setParent(this);
    }

    /**
     * Removing a child
     * @param node The child to be removed
     */
    public void removeChild(Node node) {
        children.remove(node);
        node.setParent(null);
    }

    /**
     * Returns the list of direct children of this node.
     * @return The list of childs
     */
    public List getChildren() {
        return children;
    }

    /**
     * Setter for parent
     * @param node The parent node
     */
    private void setParent(Node node) {
        this.parent = node;
    }

    /**
     * Getter for parent
     * @return The node's parent
     */
    public Node getParent() {
        return parent;
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

    public void setUserData(Map m) {
        userData = m;
    }

    /**
     * Getter for userData
     * @return the map of serialisable user data
     */
    public Map getUserData() {
        Map m = new HashMap();
        Iterator it = userData.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();

            if (serializableUserData.contains(key)) {
                m.put(key, userData.get(key));
            }
        }

        return m;
    }

    public Node getRoot() {
        return getRoot(this);
    }

    public Node getRoot(Node node) {
        if (node.getParent() == null) {
            return node;
        }

        return getRoot(node.getParent());
    }

    public static void setSerializableUserData(String key, boolean b) {
        if (b) {
            serializableUserData.add(key);
        } else {
            serializableUserData.remove(key);
        }
    }

    public static void setSerializableUserData(Set s) {
        System.err.println("set called with " + s);
        serializableUserData = new HashSet(s);
    }

    public static Set getSerializableUserData() {
        return serializableUserData;
    }

    /**
     * Indicates if the node is a leaf
     * @return True if this node has no child
     */
    public boolean isLeaf() {
        return (children == null);
    }

    /**
     * Indicates if the node is an ancestor of the parameter node
     * @param node The node that has to be searched in the descendant of the
     *        current node
     * @return A boolean indicating if this node is ancestor of <b>node </b>
     */
    public boolean isAncestor(Node node) {
        if (node == null) {
            return false;
        }

        if (node.getParent() == this) {
            return true;
        }

        return isAncestor(node.getParent());
    }

    /**
     * Count leaves that are descendant of that node
     * @return The number of leaves rooted to this node
     */
    public int countLeaves() {
        if (leavesCount != -1) {
            return leavesCount;
        }

        if (children == null) {
            return 1;
        }

        int leaves = 0;
        Iterator iterator = children.iterator();

        while (iterator.hasNext()) {
            leaves += ((Node) iterator.next()).countLeaves();
        }

        leavesCount = leaves;

        return leaves;
    }

    /**
     * Count descendants of that node
     * @return The number of descendants of this node
     */
    public int countDescendants() {
        if (children == null) {
            return 1;
        }

        int descendants = 0;
        Iterator iterator = children.iterator();

        while (iterator.hasNext()) {
            descendants += ((Node) iterator.next()).countDescendants();
        }

        return descendants + 1;
    }

    /**
     * Return the list of all leaves that are descendant of that node
     * @return The list of leaves
     */
    public List getAllLeaves() {
        Vector leavesList = new Vector();
        collectAllLeaves(leavesList);

        return leavesList;
    }

    private void collectAllLeaves(List leavesList) {
        if (isLeaf()) {
            leavesList.add(this);
        } else {
            Iterator iterator = getChildren().iterator();

            while (iterator.hasNext()) {
                ((Node) iterator.next()).collectAllLeaves(leavesList);
            }
        }
    }

    /**
     * Return the list of all nodes that are descendant of that node
     * @return The list of nodes
     */
    public Collection getAllChildNodes() {
        Collection childs = new HashSet();
        collectAllChildNodes(childs);

        return childs;
    }

    private void collectAllChildNodes(Collection childs) {
        childs.add(this);

        if (isLeaf()) {
            return;
        }

        Iterator iterator = getChildren().iterator();

        while (iterator.hasNext()) {
            ((Node) iterator.next()).collectAllChildNodes(childs);
        }
    }

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

    /**
     * Move the specified node (which must be present in the list of childs at
     * the end of the list
     * @param child the child node to move at the end
     */
    public void moveChildAtEnd(Node child) {
        children.remove(child);
        children.add(child);
    }

    /**
     * Move the specified node (which must be present in the list of childs at
     * the specified position
     * @param child the child node to move
     */
    public void moveChild(Node child, int newPosition) {
        children.remove(child);
        children.add(newPosition, child);
    }

    /**
     * Compute the distance from an ancestor to this node
     * @param ancestor the ancestor (if ancestor is null, root is assumed)
     * @param b useBranchLength a flag specifying if branch lengths has to be
     *        used
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