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

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.OntologyRootsListPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author Saïd El Kasmi
 */
public class ShowAnnotationsAction extends NodeAction {
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
        final ClassificationNodeInfo cni = (ClassificationNodeInfo) node
                .getCookie(ClassificationNodeInfo.class);
        Node ontologyNode = cni.getLinkedOntologyNode();
        Node[] children = ontologyNode.getChildren().getNodes();
        //Create the panel
        final OntologyRootsListPanel panel = new OntologyRootsListPanel(
                children);
        //Create the listener for buttons actions/ Ok/Cancel
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION) {
                    ResourceNode rn = (ResourceNode) panel
                            .getSelectedOntologyRoot();
                    // close dialog
                    closeDialog();
                    Resource resource = rn.getResource();
                    cni.getClassification().compareWithClassification(resource,
                            rn.getDisplayName());
                }
            }
        };

        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                .getString("SelectOntologyRootDialogTitle"), true, al); //NOI18N
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
            final ClassificationNodeInfo cni = (ClassificationNodeInfo) node
                    .getCookie(ClassificationNodeInfo.class);
            if (cni.getClassification().isAnnotated()) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_ShowAnnotationsAction_Name");//NO18N
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