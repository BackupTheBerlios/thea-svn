package fr.unice.bioinfo.thea.classification.editor;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.Selection;

/**
 * Provides necessary function for selection nodes in the classification editor.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface SelectionManager {

    /**
     * Update the selection's state of the given collection of nodes.
     * 
     * @param nodes
     *            Selected nodes.
     * @param state
     *            state Indicates the new selected state of the node.
     *            <ul>
     *            <li>state < 0 : unselect</li>
     *            <li>state = 0 : invert</li>
     *            <li>state > 0 : select</li>
     *            </ul>
     */
    public void setSelected(Collection nodes, int state);

    /**
     * Tels wether the given belongs the list to the list of slected nodes.
     * 
     * @param aNode
     *            The node to get the value
     * @return The selected state of the parameter node
     */
    public boolean isNodeSelected(Node aNode);

    /**
     * Update the selection's state of the given. If the given node is not leaf
     * then update also the selection's state if its children.
     * 
     * @param aNode
     *            The node to update.
     * @param state
     *            Indicates the new selected state of the node.
     *            <ul>
     *            <li>state < 0 : unselect</li>
     *            <li>state = 0 : invert</li>
     *            <li>state > 0 : select</li>
     *            </ul>
     */
    public void setSelected(Node aNode, int state);

    /** Sets the list of selections. */
    public void setSelectionsList(Collection collection);

    /** Returns the list of selections. */
    public List getSelections();

    /**
     * Returns a list of selected leaves
     * 
     * @param b
     *            indicates if the function must return the list of selected
     *            leaves in the whole tree
     * @return the list of selected leaves
     */
    public List getSelectedLeaves(boolean b);

    /**
     * Returns a list of selected leaves in the displayed tree
     * 
     * @return the list of selected leaves
     */
    public List getSelectedLeaves();

    public int getNumberOfSelections();

    /** Create a selection using the selected nodes. */
    public void createSelection();

    /**
     * Creates a new selection by merging the given selection and the nodes
     * currently selected.
     */
    public void group(Selection selection);

    /** Removes (delete) the given selection. */
    public void remove(Selection selection);

    /** Moves the given selection to the current one. */
    public void move(Selection selection);

    /** Copy the given selection to the current one. */
    public void copy(Selection selection);

    /** Union between the given selection and the current one. */
    public void union(Selection selection);

    /** Intersection between the given selection and the current one. */
    public void intersect(Selection selection);

    /** Sets all nodes as non selected. */
    public void removeSelectedNodes();

    /**
     * Returns a collection of all selected nodes
     * 
     * @return the collection of selected nodes
     */
    public Collection getSelectedNodes();

    /**
     * Add property change listener Registers a listener for the PropertyChange
     * event. The selection manager object should fire a PropertyChange event
     * whenever a selection is done.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Remove property change listener Remove a listener for the PropertyChange
     * event.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);

}