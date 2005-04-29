package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.configuration.Configuration;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.allonto.datamodel.Entity;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.GeneNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.RootNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneEditor extends TopComponent implements
        ExplorerManager.Provider {

    //  Serial Version UID
    static final long serialVersionUID = 6857108441469780252L;

    /** An expolorer manager to associate the bean tree view */
    private ExplorerManager explorerManager;

    /**
     * The widget that allow this component visualize annotated genes as a tree.
     * @see org.openide.explorer.view.BeanTreeView
     */
    private BeanTreeView beanTreeView;

    /** A root node: wil make it invisible */
    private AbstractNode hiddenRootNode;

    private AbstractNode genesRootNode;

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

        // Build and add th BeanTreeView widget
        beanTreeView = new BeanTreeView();
        // crate the root node
        hiddenRootNode = new RootNode();
        // Create the root node for the genes nodes list:
        genesRootNode = new AbstractNode(new Children.Array());
        genesRootNode
                .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/RootResourceIcon16");//NOI18N
        genesRootNode.setDisplayName(bundle.getString("LBL_GeneEditorRootName")
                + getName());//NOI18N
        hiddenRootNode.getChildren().add(new Node[] { genesRootNode });
        // Make the root node hidden
        beanTreeView.setRootVisible(true);

        // Instanciate an explorer manager
        explorerManager = new ExplorerManager();
        explorerManager.setRootContext(hiddenRootNode);

        // layout
        setLayout(new BorderLayout());
        add(beanTreeView, BorderLayout.CENTER);
        //        add(createMainPanel(), BorderLayout.CENTER);
        //        createGenesList();
        createNodes();
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

    private void createNodes() {
        String propertyName, propertyXref, propertyDB, propertyDBKey, propertyDBSVName;
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        propertyName = (String) con
                .getProperty("ontologyexplorer.nodes.annotates");//NOI18N
        propertyXref = (String) con
                .getProperty("ontologyexplorer.nodes.xrefname");//NOI18N
        propertyDB = (String) con.getProperty("ontologyexplorer.nodes.dbname");//NOI18N
        propertyDBKey = (String) con
                .getProperty("ontologyexplorer.nodes.dbkeyname");//NOI18N
        propertyDBSVName = (String) con
                .getProperty("ontologyexplorer.nodes.dbsvname");//NOI18N

        Set genes = resource.getTargets(resourceFactory
                .getProperty(propertyName));
        if (genes != null) {
            System.out.println(genes.size());
            Node[] nodes = new Node[genes.size()];
            // counter on the number of created nodes
            int counter = 0;
            Iterator iterator = genes.iterator();
            while (iterator.hasNext()) {
                // The current iteration's resource (gene)
                Resource aGene = (Resource) iterator.next();
                // create the node that represnets this resource
                nodes[counter] = new GeneNode(aGene);
                
                Iterator mapIt = ((Resource) aGene).getArcs().entrySet().iterator();
                String accessor;
                Resource rce;
                Entity e;
                Map.Entry entry;
                while (mapIt.hasNext()) {
                    entry = (Map.Entry) mapIt.next();
                    e = (Entity) entry.getValue();
                    //System.out.println("key = " + entry.getKey());
                    // Since values in the Map are not all instances of
                    // StringValue,
                    // do tests using the instanceof operator
                    if (e instanceof StringValue) {
                        rce = (Resource) entry.getKey();
                        accessor = rce.getAcc();
                        System.out.println(accessor);
                    }
                }

//                Set dbxrefs = aGene.getTargets(resourceFactory
//                        .getProperty(propertyXref));
//                System.out.println("dbxrefs.size = " + dbxrefs.size());
//
//                Iterator dbxrefIt = dbxrefs.iterator();
//                while (dbxrefIt.hasNext()) {
//                    Resource dbxref = (Resource) dbxrefIt.next();
//
//                    Resource db = (Resource) dbxref.getTarget(resourceFactory
//                            .getResource(propertyDB));
//
//                    StringValue dbname = (StringValue) db
//                            .getTarget(resourceFactory
//                                    .getResource(propertyDBSVName));
//                    if (dbname != null) {
//                        System.out.println(dbname.getValue());
//                    } else {
//                        System.out.println("null");
//                    }
//
//                    System.out.println("db = " + db + " dbk = ");
//                }

                // increment the counter
                counter++;
            }
            genesRootNode.getChildren().add(nodes);
        }
    }

    private Node createNode(String name, String dname) {
        AbstractNode an = new AbstractNode(Children.LEAF);
        an
                .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/empty");//NOI18N
        return an;
    }

    /** Returns persistence type. */
    public int getPersistenceType() {
        return super.PERSISTENCE_NEVER;
    }

    /** Returns the ID of this window. */
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.explorer.ExplorerManager.Provider#getExplorerManager()
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}