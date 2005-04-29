package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneEditor extends TopComponent {

    //  Serial Version UID
    static final long serialVersionUID = 6857108441469780252L;

    /** preferred ID:ce as Classification Editor */
    private String PREFERRED_ID = "geneeditor";//NOI18N

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); //NOI18N

    /** The Gene Editor loks for genes annotated by this resource. */
    private final Resource resource;

    /** The JTree to display the related gene products and the related evidences. */
    private JTree tree;

    /** The tree model. */
    private DefaultTreeModel model;

    /** The tree nodes. */
    private DefaultMutableTreeNode root;

    public GeneEditor(Node node) {
        super();
        resource = ((ResourceNode) node).getResource();
        // give a title to this window using the resource's name
        setName(node.getDisplayName());
        // icon
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/GeneEditorIcon16.gif")); // NOI18N
        // layout
        setLayout(new BorderLayout());
        add(createMainPanel(), BorderLayout.CENTER);
        createGenesList();
    }

    /** Builds and returns the main panel to be incorporated inside this window. */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        // create the root node of the tree
        root = new DefaultMutableTreeNode(bundle
                .getString("LBL_GeneEditorRootName"));//NOI18N
        model = new DefaultTreeModel(root);
        // create the tree
        tree = new JTree(model);
        tree.setLargeModel(true);
        tree.setCellRenderer(new GeneEditorCellRenderer());
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane jsp = new JScrollPane(tree);
        mainPanel.add(jsp, BorderLayout.CENTER);
        return mainPanel;
    }

    private void createGenesList() {
        String propertyName;
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        propertyName = (String) con
                .getProperty("ontologyexplorer.nodes.annotates");//NOI18N
        Set genes = resource.getTargets(resourceFactory
                .getProperty(propertyName));
        if (genes != null) {
            System.out.println(genes.size());
            DefaultMutableTreeNode rootTreeNode = (DefaultMutableTreeNode) tree
                    .getModel().getRoot();

            Iterator iterator = genes.iterator();
            while (iterator.hasNext()) {
                DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(
                        (Resource) iterator.next());
                rootTreeNode.add(dmtn);
            }

        }
        ((DefaultTreeModel) tree.getModel()).reload();
    }

    /** Returns persistence type. */
    public int getPersistenceType() {
        return super.PERSISTENCE_NEVER;
    }

    /** Returns the ID of this window. */
    protected String preferredID() {
        return PREFERRED_ID;
    }
}