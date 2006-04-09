package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.io.IOException;
import java.util.ResourceBundle;

import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * Deletes the Ontology node from the list of all ontologies.
 * 
 * @author Saïd El Kasmi
 */
public class DeleteOntologyNodeAction extends NodeAction {
    static final long serialVersionUID = -7091685261935072418L;

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

        if (!(node instanceof OntologyNode)) {
            return;
        }

        final OntologyNode on = (OntologyNode) node;

        // If a connection to a database opened, close it.
        if (on.isConnected()) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    DatabaseConnection dbc = on.getConnection();
                    dbc.disconnect();
                }
            }, 0);
        }

        // Get the parent of the node to delete
        Node parent = node.getParentNode();

        // Remove all the children of node
        node.getChildren().remove(node.getChildren().getNodes());

        // remove node
        parent.getChildren().remove(new Node[] { node });

        try {
            node.destroy();
        } catch (IOException e1) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e1);
        }
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

        if (node instanceof OntologyNode) {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_DeleteOntologyAction_Name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}