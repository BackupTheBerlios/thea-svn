package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowAnnotatedGenesAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeInstances;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * This class uses the nodes API to build a node that represents a resource from
 * an ontology.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ResourceNode extends AbstractResourceNode{

    public ResourceNode(final Resource resource) {
        super((resource == null) ? Children.LEAF : new ResourceNodeChildren());
        setResource(resource);
    }

    // /*
    // * (non-Javadoc)
    // * @see org.openide.nodes.Node#getPreferredAction()
    // */
    // public Action getPreferredAction() {
    // return SystemAction.get(PropertiesAction.class);
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        Action[] actions = new Action[] {
                SystemAction.get(ShowAnnotatedGenesAction.class), null,
                SystemAction.get(ShowResourceNodeProperties.class),
                SystemAction.get(ShowResourceNodeInstances.class) };
        return actions;
    }

}