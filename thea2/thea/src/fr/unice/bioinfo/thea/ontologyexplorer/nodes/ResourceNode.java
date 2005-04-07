package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Set;

import javax.swing.Action;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * @author Sa�d El Kasmi
 */
public class ResourceNode extends AbstractNode implements Node.Cookie {

    private ResourceNodeInfo info;

    /** A resource associated with a node in the Ontology Explorer. */
    private Resource resource;

    /** List of childs */
    private Set targets;

    public ResourceNode(Resource resource, Set targets) {
        super((targets == null) ? Children.LEAF : new ResourceNodeChildren(
                resource));
        this.resource = resource;
        this.targets = targets;
        // Set system name and display name of this node
        // using the resource one
        setName(resource.getName());
    }

    /** Returns vookie */
    public ResourceNodeInfo getInfo() {
        return info;
    }

    /** Sets cookie */
    public void setInfo(ResourceNodeInfo info) {
        this.info = info;
    }

    /*
     * (non-Javadoc)
     * @see java.beans.FeatureDescriptor#setName(java.lang.String)
     */
    public void setName(String newname) {
        super.setName(newname);
        //info.setName(newname);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getCookie(java.lang.Class)
     */
    public Node.Cookie getCookie(Class klas) {
        if (klas.isInstance(info))
            return info;
        return super.getCookie(klas);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        return super.getActions(arg0);
    }
}