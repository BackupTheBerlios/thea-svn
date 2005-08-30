package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.io.IOException;
import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;

/**
 * Delete classification.
 * @author Saïd El Kasmi.
 */
public class DeleteClNodeAction extends NodeAction {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        if (!(node instanceof ClassificationNode)) {
            return;
        }
        // Get its parent
        Node parent = node.getParentNode();
        // Remove its children
        node.getChildren().remove(node.getChildren().getNodes());
        // Close the windows that edit the classification if any:
        final ClassificationNodeInfo cni = (ClassificationNodeInfo) node
                .getCookie(ClassificationNodeInfo.class);

        parent.getChildren().remove(new Node[] { node });
        // This commented bloc is used to close
        // The window that edits the classif. It caused
        // an ecpetion
        // Iterator opened = TopComponent.getRegistry().getOpened().iterator();
        // while (opened.hasNext()) {
        // Object tc = opened.next();
        // if (tc instanceof CEditor) {
        // CEditor ce = (CEditor) tc;
        // if(ce.getName().equals(cni.getName())){
        // ce.close();
        // }
        // }
        // }

        try {
            node.destroy();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
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
        return bundle.getString("LBL_DeleteClAction_Name");
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
}