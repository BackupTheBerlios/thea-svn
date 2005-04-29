package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.commons.configuration.Configuration;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneEditorCellRenderer extends DefaultTreeCellRenderer {

    private ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();

    static {
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.nodes.genename");//NOI18N
        nodeNameProperty = (String) o;
    }

    /** The property to use to reach for a node's display name.*/
    private static String nodeNameProperty;

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
        
        setOpenIcon(new ImageIcon(
                Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/collapse.gif")));//NOI18N
        setClosedIcon(new ImageIcon(
                Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/expand.gif")));//NOI18N

        if (o instanceof String) {
            setText(bundle.getString("LBL_GeneEditorRootName"));//NOI18N
            setIcon(new ImageIcon(
                    Utilities
                            .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/GeneEditorRootNodeIcon16.gif")));//NOI18N
        }

        if (o instanceof Resource) {
            Resource resource = (Resource) o;
            String label = "";//NOI18N
            StringValue sv = (StringValue) resource.getTarget(resourceFactory
                    .getProperty(nodeNameProperty));
            if (sv != null) {
                label = sv.getValue();
            } else {
                label = "" + resource.getId();//NOI18N
            }
            System.out.println(label);
            setText(label);
            setIcon(null);
        }

        TreePath path = tree.getPathForRow(row);
        
        return this;
    }
}