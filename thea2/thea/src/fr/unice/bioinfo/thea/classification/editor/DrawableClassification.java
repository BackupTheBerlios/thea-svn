package fr.unice.bioinfo.thea.classification.editor;

import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import fr.unice.bioinfo.thea.classification.Node;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface DrawableClassification extends Drawable {

    /**
     * Shows the whole tree (the one drawn at the first time) that represents
     * the whole classification.
     */
    public void showWholeTree();

    /** Shows the previous tree going from the currently shown tree. */
    public void showPreviousTree();

    /**
     * Build a new tree using the parent of the currently tree's root node.
     */
    public void showOnLevelUp();

    /**
     * Draws a sub-tree using the given node as a root node for the new tree.
     * @param aNode The selected node to be a root for the new tree.
     */
    public void showSubTree(Node aNode);

    /**
     * Sets the given root to be the root node for the tree to be drawn.
     * @param aNode A node.
     */
    public void setCurrentRootNode(Node aNode);

    /** Returns the root node of the currently displayed tree. */
    public Node getCurrentRootNode();

    /**
     * Returns the root node for the whole classification.
     * @return Node The root node for the whole tree.
     */
    public Node getClassificationRootNode();

    /**
     * Collapses the given node.
     * @param aNode The node to be collapsed.
     */
    public void collapseNode(Node aNode);

    /**
     * Uncollapse the given node.
     * @param aNode The node to be collapsed;
     */
    public void uncollapseNode(Node aNode);

    /**
     * Find a node from a position using rectangular areas.
     * @param aNode The node to locate.
     * @param position Starting point.
     * @return The corresponding node.
     */
    public Node locateNode(Node aNode, Point2D position);

    /** Highlights the area surrounding the given node. */
    public void highlightSurroundingArea(Node aNode);

    /**
     * Shows or hides the popup menu depending on the value of the parameter
     b;
     * @param b If <i>True </i> the popup menu will be shown and hidden if
     * otherwise.
     */
    public void setPopupMenuVisible(boolean b);

    /** Add a popup menu to show on this component. */
    public void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Says wether the popup menu is shown or not.
     * @return <i>True </i> if showing the popup menu. <i>False </i <
     elswhere.
     */
    public boolean isShowingPopupMenu();

    /**
     * Returns the selection manager used in this component.
     * @return {@link SelectionManager}.
     */
    public SelectionManager getSelectionManager();

    /**
     * Returns the index of the slected column from the expression values
     * matrix.
     * @param point2D The mouse pointer location.
     * @return Index of the column under the mouse's pointer.
     */
    public int getExpressionColumnIndex(Point2D point2D);
}