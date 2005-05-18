package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.OntologiesListPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologiesRootNode;

/**
 * @author Saïd El Kasmi
 */
public class AnnotateAction extends NodeAction {
    private Dialog dialog;
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); //NOI18N

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();
        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];
        // If not the suitable type
        if (!(node instanceof ClassificationNode)) {
            return;
        }
        // Get the root node of all ontologies:
        // First, get the root context node which is not visible
        final Node rt = OntologyExplorer.findDefault().getExplorerManager()
                .getRootContext();
        final Node[] children = rt.getChildren().getNodes();
        // The root for all ontologies belongs to children list.
        Node ontologiesRootNode = null;
        for (int cnt = 0; cnt < children.length; cnt++) {
            if (children[cnt] instanceof OntologiesRootNode) {
                ontologiesRootNode = children[cnt];
            }
        }
        if (ontologiesRootNode == null) {
            return;
        }

        //Create the panel
        final OntologiesListPanel panel = new OntologiesListPanel(
                ontologiesRootNode.getChildren().getNodes());

        // Create the listener for buttons actions/ Ok/Cancel
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION) {

                    Node n = panel.getSelectedOntologyNode();
                    // close dialog
                    closeDialog();
                }
            }
        };

        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                .getString("SelectOntologyDialogTitle"), true, al); //NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        descriptor.setClosingOptions(closingOptions);
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
    }

    /*
     * (non-Javadoc)
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
        if (node instanceof ClassificationNode) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_AnnotateAction_Name");//NO18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    //  Closes dialog
    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
}