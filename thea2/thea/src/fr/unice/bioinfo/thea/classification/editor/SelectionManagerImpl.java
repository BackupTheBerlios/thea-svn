package fr.unice.bioinfo.thea.classification.editor;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.NodeSet;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class SelectionManagerImpl implements SelectionManager {

    /** The list of selected nodes */
    private Set selectedNodes = new HashSet();

    /** The list of successive selection lists */
    private List selectionsList = new Vector();

    /** The number of selections done */
    private int counter;//initially was declared as nbSel

    /** The name of the selection */
    private String selectionName;

    private DrawableClassification drawable = null;

    /** The colors used to display selections */
    private Color[] colors = new Color[18];
    
    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport;

    public SelectionManagerImpl(DrawableClassification drawable) {
        this.drawable = drawable;
        init();
    }

    private void init() {
        counter = 0;
        colors[0] = new Color(255, 128, 0);
        colors[1] = new Color(0, 128, 0);
        colors[2] = new Color(0, 0, 255);
        colors[3] = new Color(128, 0, 128);
        colors[4] = new Color(0, 0, 160);
        colors[5] = new Color(128, 0, 0);

        colors[6] = new Color(128, 0, 255);
        colors[7] = new Color(255, 0, 128);
        colors[8] = new Color(128, 128, 255);
        colors[9] = new Color(128, 128, 0);
        colors[10] = new Color(0, 255, 0);
        colors[11] = new Color(0, 255, 255);

        colors[12] = new Color(255, 255, 0);
        colors[13] = new Color(0, 255, 128);
        colors[14] = new Color(255, 128, 255);
        colors[15] = new Color(64, 128, 128);
        colors[16] = new Color(192, 192, 192);
        colors[17] = new Color(64, 0, 0);
        
        propertySupport = new PropertyChangeSupport(this);
    }

    /**
     * Set the selected state of a node ant its children.
     * @param n The node to update
     * @param state state Indicates the new selected state of the node.
     *        <ul>
     *        <li>state < 0 : unselect</li>
     *        <li>state = 0 : invert</li>
     *        <li>state > 0 : select</li>
     *        </ul>
     */
    private void setSelectedNodeAndChilds(Node n, int state) {
        if (state < 0) {
            selectedNodes.remove(n);
        } else if (state > 0) {
            selectedNodes.add(n);
        } else { // state == 0
            if (isNodeSelected(n)) {
                selectedNodes.remove(n);
            } else {
                selectedNodes.add(n);
            }
        }
        if (n.isLeaf()) {
            return;
        }
        Iterator iterator = n.getChildren().iterator();
        while (iterator.hasNext()) {
            Node childNode = (Node) iterator.next();
            setSelectedNodeAndChilds(childNode, state);
        }
    }

    /**
     * Update the selected state of the node
     */
    private void updateSelectedState(Node aNode) {
        if (aNode == null) {
            return;
        }
        if (aNode.isLeaf()) {
            return; // to be sure
        }
        boolean nodeIsSelected = isNodeSelected(aNode);
        boolean allChildsInSelectedState = true;
        Iterator iterator = aNode.getChildren().iterator();
        while (iterator.hasNext()) {
            Node aChild = (Node) iterator.next();
            if (!isNodeSelected(aChild)) {
                allChildsInSelectedState = false;
                break;
            }
        }
        if (nodeIsSelected != allChildsInSelectedState) {
            // update needed
            if (allChildsInSelectedState) {
                selectedNodes.add(aNode);
            } else {
                selectedNodes.remove(aNode);
            }
            // repaint the area
            Rectangle2D.Double aNodeArea = (Rectangle2D.Double) aNode.getArea();
            if (aNodeArea != null) {
                drawable.repaintRectangle((int) aNodeArea.x, (int) aNodeArea.y,
                        (int) aNodeArea.width, (int) aNodeArea.height);
            }
            // modify the selected state of the parent if necessary
            updateSelectedState(aNode.getParent());
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#setSelected(java.util.Collection,
     *      int)
     */
    public void setSelected(Collection nodes, int state) {
        Iterator it = nodes.iterator();

        while (it.hasNext()) {
            Node n = (Node) it.next();
            setSelectedNodeAndChilds(n, state);

            // modify the selected state of the parent if necessary
            updateSelectedState(n.getParent());
        }
        //sendSelectionEvent();
        // repaint the whole panel
        //repaint();
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#setSelected(fr.unice.bioinfo.thea.classification.Node,
     *      int)
     */
    public void setSelected(Node aNode, int state) {
        setSelectedNodeAndChilds(aNode, state);
        // modify the selected state of the parent if necessary
        updateSelectedState(aNode.getParent());
        //sendSelectionEvent();
        // repaint the area
        Rectangle2D.Double rectangle = (Rectangle2D.Double) aNode.getArea();
        if (rectangle != null) {
            drawable.repaintRectangle((int) rectangle.x, (int) rectangle.y,
                    (int) rectangle.width, (int) rectangle.height);
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#setSelected(java.util.Collection,
     *      int, java.lang.String)
     */
    public void setSelected(Collection nodes, int state, String name) {
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node n = (Node) it.next();
            setSelectedNodeAndChilds(n, state);
            // modify the selected state of the parent if necessary
            updateSelectedState(n.getParent());
        }
        //sendSelectionEvent();
        // repaint the whole panel
        //repaint();
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#setSelectionName(java.lang.String)
     */
    public void setSelectionName(String name) {
        selectionName = name;
        System.out.println(selectionName);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#getSelectionName()
     */
    public String getSelectionName() {
        return selectionName;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#setSelectionsList(java.util.Collection)
     */
    public void setSelectionsList(Collection collection) {
        if (collection == null) {
            selectionsList = null;
        } else {
            selectionsList = new Vector(collection);
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#getSelectionList()
     */
    public List getSelections() {
        return this.selectionsList;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#getSelectedLeaves(boolean)
     */
    public List getSelectedLeaves(boolean b) {
        Node aNode = (b ? drawable.getClassificationRootNode() : drawable
                .getCurrentRootNode());
        List selectedLeaves = new Vector();
        Iterator iterator = aNode.getLeaves().iterator();
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            if (isNodeSelected(node)) {
                selectedLeaves.add(node);
            }
        }
        return selectedLeaves;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#getSelectedLeaves()
     */
    public List getSelectedLeaves() {
        return getSelectedLeaves(false);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#clearSelected()
     */
    public void removeSelectedNodes() {
        String selId = String.valueOf(counter);
        //        org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent event = new
        // org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent(
        //                this, selId);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionClearedInClassif(event);

        selectedNodes.clear();
        selectionName = null;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#keepSelection()
     */
    public void keepSelection() {
        if (selectedNodes == null) {
            return;
        }
        NodeSet selection = new NodeSet(getSelectedLeaves(true));
        String selectionID = String.valueOf(counter);
        selection.addProperty(NodeSet.COLOR, colors[counter % 18]);
        selection.addProperty(NodeSet.SEL_ID, selectionID);
        selection.addProperty(NodeSet.SEL_NAME, selectionName);
        //System.out.println("ID = "+selectionID+" Name = "+selectionName);

        //        org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent event = new
        // org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent(
        //                this, selID, null,
        //                (Color) selectionToKeep.getUserData("color"), null, null, null);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionDoneInClassif(event);
        selectionsList.add(selection);
        counter++;
        removeSelectedNodes();
        propertySupport.firePropertyChange("selectionsList", null, selectionsList);
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#getSelected()
     */
    public Collection getSelectedNodes() {
        return selectedNodes;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#isNodeSelected(fr.unice.bioinfo.thea.classification.Node)
     */
    public boolean isNodeSelected(Node aNode) {
        return selectedNodes.contains(aNode);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#getNumberOfSelections()
     */
    public int getNumberOfSelections() {
        return this.counter;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#moveSelectionToCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void moveSelectionToCurrent(NodeSet selection) {
        removeSelectedNodes();
        setSelected(selection.getNodes(), 1, (String) selection
                .getProperty(NodeSet.SEL_NAME));
        removeSelection(selection);
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#copySelectionToCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void copySelectionToCurrent(NodeSet selection) {
        removeSelectedNodes();
        setSelected(selection.getNodes(), 1, (String) selection
                .getProperty(NodeSet.SEL_NAME));
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#unionSelectionWithCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void unionSelectionWithCurrent(NodeSet selection) {
        String newSelectionName = (String) selection
                .getProperty(NodeSet.SEL_NAME);
        if (selectionName.startsWith("Manual")) {
            // use selName for the name of the union
        } else {
            if (newSelectionName.startsWith("Manual")) {
                newSelectionName = selectionName;
            } else {
                newSelectionName = selectionName + " OR " + newSelectionName;
            }
        }
        setSelected(selection.getNodes(), 1, newSelectionName);
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#intersectSelectionWithCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void intersectSelectionWithCurrent(NodeSet selection) {
        String newSelectionName = (String) selection
                .getProperty(NodeSet.SEL_NAME);
        if (selectionName.startsWith("Manual")) {
            // use selName for the name of the union
        } else {
            if (newSelectionName.startsWith("Manual")) {
                newSelectionName = selectionName;
            } else {
                newSelectionName = selectionName + " AND " + newSelectionName;
            }
        }
        Collection currentSel = getSelectedLeaves(true);
        currentSel.retainAll(selection.getNodes());
        removeSelectedNodes();
        setSelected(currentSel, 1, newSelectionName);
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#removeSelection(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void removeSelection(NodeSet selection) {
        String selId = (String) selection.getProperty(NodeSet.SEL_ID);
        //        org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent event = new
        // org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent(
        //                this, selId);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionClearedInClassif(event);

        if (selectionsList.contains(selection)) {
            selectionsList.remove(selection);
        }
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#groupSelection(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void groupSelection(NodeSet selection) {
        List l = new Vector(selection.getNodes());
        while (!l.isEmpty()) {
            Node aNode = (Node) l.get(0);
            Boolean frozen = (Boolean) aNode.getProperty(NodeSet.FROZEN);
            if ((frozen == null) || frozen.equals(Boolean.FALSE)) {
                Node parent = aNode.getParent();
                List children = parent.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    frozen = (Boolean) ((Node) children.get(i))
                            .getProperty(NodeSet.FROZEN);
                    if ((frozen == null) || frozen.equals(Boolean.FALSE)) {
                        parent.moveChild(aNode, i);
                        break;
                    }
                }
            }
            l.remove(aNode);
        }
    }

    /* (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    /* (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }

}