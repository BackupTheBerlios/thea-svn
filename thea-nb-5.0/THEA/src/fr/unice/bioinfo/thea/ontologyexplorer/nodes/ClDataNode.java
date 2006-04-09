package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import javax.swing.Action;

import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.EditAction;
import org.openide.actions.RenameAction;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.thea.ontologyexplorer.ClDataObject;

/**
 * @author SAÏD, EL KASMI.
 */
public class ClDataNode extends DataNode {
    public ClDataNode(ClDataObject obj) {
        super(obj, Children.LEAF);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        Action[] result = new Action[] { SystemAction.get(EditAction.class),
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(RenameAction.class),
                SystemAction.get(DeleteAction.class), };

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node#getPreferredAction()
     */
    public Action getPreferredAction() {
        return SystemAction.get(EditAction.class);
    }
}