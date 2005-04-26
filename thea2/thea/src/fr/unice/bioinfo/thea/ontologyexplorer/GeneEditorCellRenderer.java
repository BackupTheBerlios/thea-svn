package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneEditorCellRenderer extends DefaultTreeCellRenderer {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); //NOI18N

    public GeneEditorCellRenderer() {
        super();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        //        return super.getTreeCellRendererComponent(tree, value, sel, expanded,
        //                leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object o = node.getUserObject();

        if (o instanceof String) {
            setText(bundle.getString("LBL_GeneEditorRootName"));//NOI18N
            setIcon(new ImageIcon(
                    Utilities
                            .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/GeneEditorRootNodeIcon16.gif")));//NOI18N
        }

        TreePath path = tree.getPathForRow(row);
        return this;
    }
}