package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;

/**
 * @author Saïd El Kasmi
 */
public class CompareAction extends NodeAction {
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
        //        return bundle.getString("LBL_AnnotateAction_Name");//NO18N
        return "Compare ...";
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