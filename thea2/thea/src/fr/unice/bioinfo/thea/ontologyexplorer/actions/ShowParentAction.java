package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ShowParentAction extends NodeAction {

    private Dialog dialog;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();
        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];
        final ResourceNodeInfo rni = (ResourceNodeInfo) node
                .getCookie(ResourceNodeInfo.class);

        Node pa = node.getParentNode();
        final ResourceNodeInfo parni = (ResourceNodeInfo) pa
                .getCookie(ResourceNodeInfo.class);

        Resource resource = rni.getResource();

        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();

        Set set = createAncestorsList(resource, resourceFactory);
        /*
         * if (set != null) { Iterator iterator = set.iterator(); while
         * (iterator.hasNext()) { Resource entity = (Resource) iterator.next();
         * StringValue sv = (StringValue) entity .getTarget(resourceFactory
         * .getResource("http://www.w3.org/2000/01/rdf-schema#label"));
         * System.out.println("ancestor -> " + sv.getValue()); } }
         */
        // Set properties = new HashSet();
        // Configuration con =
        // TheaConfiguration.getDefault().getConfiguration();
        // Object o = con.getProperty("ontologyexplorer.hierarchy.uri");//NOI18N
        // if (o instanceof Collection) {
        // ArrayList al = new ArrayList((Collection) o);
        // Object[] names = al.toArray();
        // for (int counter = 0; counter < al.size(); counter++) {
        // String name = (String) names[counter];
        // Resource r = resourceFactory.getProperty(name).getInverse();
        // properties.add(r);
        // }
        // }
        // Set childs = ((Resource) resource).getTargets(properties);
        // if (childs != null) {
        // Iterator iterator = childs.iterator();
        // while (iterator.hasNext()) {
        // Resource entity = (Resource) iterator.next();
        // StringValue sv = (StringValue) entity
        // .getTarget(resourceFactory
        // .getProperty("http://www.w3.org/2000/01/rdf-schema#label"));
        // System.out.println("ancestor -> " + sv.getValue());
        // }
        // }
    }

    private Set createAncestorsList(Resource aResource,
            ResourceFactory resourceFactory) {

        Set ancestors = new HashSet();

        Set properties = new HashSet();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.hierarchy.uri");// NOI18N
        if (o instanceof Collection) {
            ArrayList al = new ArrayList((Collection) o);
            Object[] names = al.toArray();
            for (int counter = 0; counter < al.size(); counter++) {
                String name = (String) names[counter];
                try {
                    Resource r = resourceFactory.getResource(name).getInverse();
                    properties.add(r);
                } catch (AllontoException ae) {
                }
            }
        }
        Set s = ((Resource) aResource).getTargets(properties);
        if (s != null) {
            Iterator iterator = s.iterator();
            while (iterator.hasNext()) {
                Resource entity = (Resource) iterator.next();
                ancestors.addAll(createAncestorsList(entity, resourceFactory));
            }
            ancestors.addAll(s);
        }
        return ancestors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
        // Enable this action only for the OntologyNode
        nodes = OntologyExplorer.findDefault().getExplorerManager()
                .getSelectedNodes();
        Node node;
        if ((nodes != null) && (nodes.length == 1)) {
            node = nodes[0];
        } else {
            return false;
        }
        // enable only if this is a Resource node.
        if (node instanceof ResourceNode) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return "Show Parent";// NOI18N
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    // Closes dialog
    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
}