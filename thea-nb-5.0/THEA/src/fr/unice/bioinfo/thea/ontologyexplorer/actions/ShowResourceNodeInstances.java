package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.ResourceNodeInstancesEditor;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ShowResourceNodeInstances extends NodeAction {

    static final long serialVersionUID = -7331431990714575233L;
;

    
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
//        final ResourceNodeInfo rni = (ResourceNodeInfo) node
//                .getCookie(ResourceNodeInfo.class);

        // Creates the panel and displays it in a thread

        Runnable doTopComponent = new Runnable() {
            public void run() {
                ResourceNodeInstancesEditor editor = new ResourceNodeInstancesEditor(
                        node);
                editor.open();
                editor.requestActive();
            }
        };
        SwingUtilities.invokeLater(doTopComponent);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
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
        return bundle.getString("LBL_ShowResourceNodeInstances");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.HelpCtx.Provider#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        // TODO Auto-generated method stub
        return false;
    }

}