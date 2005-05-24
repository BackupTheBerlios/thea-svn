package fr.unice.bioinfo.thea.classification.editor;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
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
    private int nbSel;

    /** The name of the selection */
    private String selectionName;

    private DrawableClassification drawable = null;

    /** The colors used to display selections */
    private Color[] sel_color = new Color[18];

    public SelectionManagerImpl(DrawableClassification drawable) {
        this.drawable = drawable;
        init();
    }

    private void init() {
        nbSel = 0;
        sel_color[0] = new Color(255, 128, 0);
        sel_color[1] = new Color(0, 128, 0);
        sel_color[2] = new Color(0, 0, 255);
        sel_color[3] = new Color(128, 0, 128);
        sel_color[4] = new Color(0, 0, 160);
        sel_color[5] = new Color(128, 0, 0);

        sel_color[6] = new Color(128, 0, 255);
        sel_color[7] = new Color(255, 0, 128);
        sel_color[8] = new Color(128, 128, 255);
        sel_color[9] = new Color(128, 128, 0);
        sel_color[10] = new Color(0, 255, 0);
        sel_color[11] = new Color(0, 255, 255);

        sel_color[12] = new Color(255, 255, 0);
        sel_color[13] = new Color(0, 255, 128);
        sel_color[14] = new Color(255, 128, 255);
        sel_color[15] = new Color(64, 128, 128);
        sel_color[16] = new Color(192, 192, 192);
        sel_color[17] = new Color(64, 0, 0);
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
        String selId = String.valueOf(nbSel);
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
        NodeSet ns = new NodeSet(getSelectedLeaves(true));
        String selID = String.valueOf(nbSel);

        if (ns == null) {
            System.out.println("ns is null");
        }
        ns.addProperty(NodeSet.COLOR, sel_color[nbSel % 18]);
        ns.addProperty(NodeSet.SEL_ID, selID);
        ns.addProperty(NodeSet.SEL_NAME, selectionName);

        //        org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent event = new
        // org.bdgp.apps.dagedit.gui.event.ClassifSelectionEvent(
        //                this, selID, null,
        //                (Color) selectionToKeep.getUserData("color"), null, null, null);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionDoneInClassif(event);
        selectionsList.add(ns);
        nbSel++;
        removeSelectedNodes();
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
        return this.nbSel;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#moveSelectionToCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void moveSelectionToCurrent(NodeSet sel) {
        removeSelectedNodes();
        setSelected(sel.getNodes(), 1, (String) sel
                .getProperty(NodeSet.SEL_NAME));
        removeSelection(sel);
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#copySelectionToCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void copySelectionToCurrent(NodeSet sel) {
        removeSelectedNodes();
        setSelected(sel.getNodes(), 1, (String) sel
                .getProperty(NodeSet.SEL_NAME));
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#unionSelectionWithCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void unionSelectionWithCurrent(NodeSet sel) {
        String newSelectionName = (String) sel.getProperty(NodeSet.SEL_NAME);
        if (selectionName.startsWith("Manual")) {
            // use selName for the name of the union
        } else {
            if (newSelectionName.startsWith("Manual")) {
                newSelectionName = selectionName;
            } else {
                newSelectionName = selectionName + " OR " + newSelectionName;
            }
        }
        setSelected(sel.getNodes(), 1, newSelectionName);
        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.SelectionManager#intersectSelectionWithCurrent(fr.unice.bioinfo.thea.classification.NodeSet)
     */
    public void intersectSelectionWithCurrent(NodeSet sel) {
        String newSelectionName = (String) sel.getProperty(NodeSet.SEL_NAME);

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
        currentSel.retainAll(sel.getNodes());
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

}