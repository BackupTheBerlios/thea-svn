package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Set;

import javax.swing.Action;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.HideResourceNodeAction;

/**
 * This class represents a node in the Ontology Explorer. Using a </i>Resource
 * </i>, and its targets.
 * @author SAÏD, EL KASMI.
 */
public class ResourceNode extends AbstractNode implements Comparable {
    
    /** A resource associated with a node in the Ontology Explorer. */
    private Resource resource;

    /** List of childs */
    private Set targets;

    /** Tells whether this node is root or not. */
    private boolean rootNode = false;

    /** Create a Resource node. */
    public ResourceNode(Resource resource, Set targets) {
        //super(s.isEmpty() ? Children.LEAF : new ResourceChildren(resource));
        super((targets == null) ? Children.LEAF
                : new ResourceChildren(resource));
        this.resource = resource;
        this.targets = targets;
        // Set system name and display name of this node
        // using the resource one
        setName(resource.getName());
        OntologyExplorerSettings ontologyExplorerSettings = SettingsFactory
                .getOntologyExplorerSettings();
        updateDisplayName(ontologyExplorerSettings.getShowResourceID(),
                ontologyExplorerSettings.getShowResourceName());
    }

    /** Returns the resource associated with this node. */
    public Resource getResource() {
        return resource;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#canDestroy()
     */
    public boolean canDestroy() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        return new Action[] { 
                SystemAction.get(HideResourceNodeAction.class) 
                };
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.HelpCtx.Provider#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Node otherNode = (Node) o;
        return this.getDisplayName().compareTo(otherNode.getDisplayName());
    }

    /**
     * Update the display name of this nodes depending on the user's choices
     * @param showID If <i>true </i> Show resource's ID
     * @param showName If <i>True </i> Show resourc's Name
     */
    public void updateDisplayName(boolean showID, boolean showName) {
        if ((showID == true) && (showName == true)) {
            setDisplayName(resource.getName() + "-" + resource.getId());
        } else if ((showID == false) && (showName == false)) {
            setDisplayName("");
        } else if ((showID == false) && (showName == true)) {
            setDisplayName(resource.getName());
        } else if ((showID == true) && (showName == false)) {
            setDisplayName("" + resource.getId());
        }
    }

    /** Returns the targets. */
    public Set getTargets() {
        return targets;
    }

    /** Says if thgis node is root nr not. */
    public boolean isRootNode() {
        return rootNode;
    }

    /** rootNode The rootNode to set. */
    public void setRootNode(boolean rootNode) {
        this.rootNode = rootNode;
    }
}