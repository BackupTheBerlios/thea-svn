package fr.unice.bioinfo.thea.classification.editor;

import java.util.Collection;
import java.util.List;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.NodeSet;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface SelectionManager {

    /**
     * Update the selection's state of the given collection of nodes.
     * @param nodes Selected nodes.
     * @param state state Indicates the new selected state of the node.
     *        <ul>
     *        <li>state < 0 : unselect</li>
     *        <li>state = 0 : invert</li>
     *        <li>state > 0 : select</li>
     *        </ul>
     */
    public void setSelected(Collection nodes, int state);
    
    /**
     * Tels wether the given belongs the list to the list of slected nodes.
     * @param aNode The node to get the value
     * @return The selected state of the parameter node
     */
    public boolean isNodeSelected(Node aNode);

    /**
     * Update the selection's state of the given. If the given node is not leaf
     * then update also the selection's state if its children.
     * @param aNode The node to update.
     * @param state Indicates the new selected state of the node.
     *        <ul>
     *        <li>state < 0 : unselect</li>
     *        <li>state = 0 : invert</li>
     *        <li>state > 0 : select</li>
     *        </ul>
     */
    public void setSelected(Node aNode, int state);

    /**
     * pdate the selection's state of the given collection of nodes.
     * @param nodes The new list of selected nodes
     * @param state Indicates the new selected state of the node.
     *        <ul>
     *        <li>state < 0 : unselect</li>
     *        <li>state = 0 : invert</li>
     *        <li>state > 0 : select</li>
     *        </ul>
     * @param name The name of the selection
     */
    public void setSelected(Collection nodes, int state, String name);

    /**
     * Sets a name for the selection using the given string.
     * @param name A name for the selection.
     */
    public void setSelectionName(String name);

    /** Returns the name of the selection.*/
    public String getSelectionName();

    /** Sets the list of selections.*/
    public void setSelectionsList(Collection collection);

    /** Returns the list of selections.*/
    public List getSelections();
    
    /**
     * Returns a list of selected leaves
     * 
     * @param b indicates if the function must return the list of selected
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
    
    /**
     * Memorize the last selection
     */
    public void keepSelection();
    
    public void groupSelection(NodeSet selection);
    
    public void removeSelection(NodeSet selection);
    
    public void moveSelectionToCurrent(NodeSet sel);
    
    public void copySelectionToCurrent(NodeSet sel);
    
    public void unionSelectionWithCurrent(NodeSet sel);
    
    public void intersectSelectionWithCurrent(NodeSet sel);
    
    /**
     * Sets all nodes as non selected
     */
    public void removeSelectedNodes();
    
    /**
     * Returns a collection of all selected nodes
     * 
     * @return the collection of selected nodes
     */
    public Collection getSelectedNodes() ;

}