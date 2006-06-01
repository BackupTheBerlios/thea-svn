package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import javax.swing.Action;

import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowAnnotatedGenesAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeInstances;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeProperties;

/**
 * This class uses the nodes API to build a node that represents a resource from
 * an ontology (used in ResourceNodePropertiesExplorer.
 * 
 * @author <a href="mailto:claude.pasquier@unice.fr"> Claude Pasquier </a>
 */
public class ResourceNodeProperty extends AbstractResourceNode {

    /**
     * Reference to the ontologynode where this node is defined
     */
    private OntologyNode ontologyNode;

    public ResourceNodeProperty(final Resource resource, OntologyNode ont) {
        //super((resource == null) ? Children.LEAF : new ResourceNodePropertyChildren());
        super(Children.LEAF);
        ontologyNode = ont;
        setResource(resource);
    }

    public Action[] getActions(boolean arg0) {
        Action[] actions = new Action[] {
                SystemAction.get(ShowResourceNodeProperties.class),
                SystemAction.get(ShowResourceNodeInstances.class) };
        return actions;
    }

    /**
     * @return Returns the ontologyNode.
     */
    public OntologyNode getOntologyNode() {
        return ontologyNode;
    }
    
    /**
     * @param ontologyNode The ontologyNode to set.
     */
    public void setOntologyNode(OntologyNode ontologyNode) {
        this.ontologyNode = ontologyNode;
    }

}