package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.OntologiesListPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologiesRootNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author Saïd El Kasmi
 */
public class SelectOntologyAction extends NodeAction {
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
        // If not the suitable type
        if (!(node instanceof ClassificationNode)) {
            return;
        }
        final ClassificationNodeInfo cni = (ClassificationNodeInfo) node
                .getCookie(ClassificationNodeInfo.class);
        // Get the root node of all ontologies:
        // First, get the root context node which is not visible
        final Node rc = OntologyExplorer.findDefault().getExplorerManager()
                .getRootContext();
        final Node[] children = rc.getChildren().getNodes();
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

        // Listener to the linking operation from here
        final PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("linked")) { // NOI18N
                    if (dialog != null) {
                        dialog.setVisible(false);
                    }
                } else if (event.getPropertyName().equals("failed")) { // NOI18N
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(
                                    "Linking to the ontlogy failed.",
                                    NotifyDescriptor.INFORMATION_MESSAGE));
                }
            }
        };
        // cni.getClassification().addPropertyChangeListener(pcl);

        // Create the panel
        final OntologiesListPanel panel = new OntologiesListPanel(
                ontologiesRootNode.getChildren().getNodes(), cni);

        // Create the listener for buttons actions/ Ok/Cancel
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION) {
                    OntologyNode ontologyNode = (OntologyNode) panel
                            .getSelectedOntologyNode();
                    // close dialog
                    closeDialog();
                    final DatabaseConnection dbc = ontologyNode.getConnection();
                    cni.setLinkedOntologyNode(ontologyNode);
                    cni.getClassification().createGeneProducts(
                            dbc.getConnection());
                }
            }
        };

        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                .getString("SelectOntologyDialogTitle"), true, al); // NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        descriptor.setClosingOptions(closingOptions);
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
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
        if (node instanceof ClassificationNode) {
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
        return bundle.getString("LBL_SelectOntologyAction_Name");// NO18N
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