package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.util.ResourceBundle;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.editor.CEditor;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;

/**
 * @author Saïd El Kasmi.
 */
public class EditClAction extends NodeAction {

    static final long serialVersionUID = -894644054833609687L;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); //NOI18N

    private CEditor editor;

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] nodes) {
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        if (!(node instanceof ClassificationNode)) {
            return;
        }

        final ClassificationNodeInfo cni = (ClassificationNodeInfo) node
                .getCookie(ClassificationNodeInfo.class);

        editor = new CEditor(cni);
        editor.open();
        editor.requestActive();
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
        return bundle.getString("LBL_EditClAction_Name");//NOI18N
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