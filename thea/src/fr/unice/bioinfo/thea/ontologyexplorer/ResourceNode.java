package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.List;
import java.util.Set;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.Resource;

/**
 * @author SAÏD, EL KASMI.
 */
public class ResourceNode extends AbstractNode implements Comparable {
    
    /** A resource associated with a node in the Ontology Explorer.*/
    private Resource resource;
    /** List of childs*/
    private List childs;
    
    /** Create a Resource node.*/
    public ResourceNode(Resource resource,Set s) {
        //super(s.isEmpty() ? Children.LEAF : new ResourceChildren(resource));
        super((s == null) ? Children.LEAF : new ResourceChildren(resource));
        this.resource = resource;
        // Set system name and display name of this node
        // using the resource one
        setName(resource.getName());
        setDisplayName(resource.getName());       
    }
    
    /** Returns the resource associated with this node. */
    protected Resource getResource() {
        return resource;
    }
    
    /* (non-Javadoc)
     * @see org.openide.nodes.Node#getActions()
     */
    public SystemAction[] getActions() {
        return new SystemAction[] {};
    }

    /* (non-Javadoc)
     * @see org.openide.util.HelpCtx.Provider#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Node otherNode = (Node) o;
        return this.getDisplayName().compareTo(otherNode.getDisplayName());
    }
}