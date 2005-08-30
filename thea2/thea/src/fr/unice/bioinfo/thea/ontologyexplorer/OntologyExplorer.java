package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerManager.Provider;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationsRootNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologiesRootNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.RootNode;

/**
 * This component is used to view the currently loaded ontology. When a little
 * plus sign appears, it means that you can expand the tree to see chlids.
 * Clicking this sign will makes childs visible.
 * @author Saïd El Kasmi
 */
public class OntologyExplorer extends TopComponent implements Provider,
        org.openide.util.Lookup.Provider {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 8011368407050953911L;

    /** A hint to the window system for generating a unique id */
    private static final String PREFERRED_ID = "ontologyexplorer"; // NOI18N

    /** Singleton pattern instance. */
    private static OntologyExplorer DEFAULT = null;

    /**
     * The widget that allow this component visualize the ontology as a tree.
     * @see org.openide.explorer.view.BeanTreeView
     */
    private BeanTreeView beanTreeView;

    /** An expolorer manager to associate the bean tree view */
    private ExplorerManager explorerManager;

    /**
     * Create an {@link OntologyExplorer}instance
     */
    public OntologyExplorer() {
        super();
        setName(NbBundle.getMessage(OntologyExplorer.class,
                "LBL_OntologyExplorer_Name")); // NOI18N
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyExplorer16.png")); // NOI18N

        // Instanciate an explorer manager
        explorerManager = new ExplorerManager();

        // Create the root node
        RootNode rn = new RootNode();

        // Add the root node for all ontologies
        rn.getChildren().add(new Node[] { new OntologiesRootNode() });

        // Add the root node for all classifications
        rn.getChildren().add(new Node[] { new ClassificationsRootNode() });
        explorerManager.setRootContext(rn);

        // Build and add th BeanTreeView widget
        beanTreeView = new BeanTreeView();

        // Make the root node hidden
        beanTreeView.setRootVisible(true);
        setLayout(new BorderLayout());
        add(beanTreeView, BorderLayout.CENTER);
    }

    /**
     * Finds the singleton instance of OntologyExplorer.
     */
    public static OntologyExplorer findDefault() {
        if (DEFAULT == null) {
            if (SwingUtilities.isEventDispatchThread()) {
                TopComponent tc = WindowManager.getDefault().findTopComponent(
                        "ontologyexplorer"); // NOI18N

                if (tc != null) {
                    DEFAULT = (OntologyExplorer) tc;
                }
            } else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            TopComponent tc = WindowManager.getDefault()
                                    .findTopComponent(PREFERRED_ID);

                            if (tc != null) {
                                DEFAULT = (OntologyExplorer) tc;
                            }
                        }
                    });
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }

        if (DEFAULT == null) {
            OntologyExplorer.getInstance();
        }

        return DEFAULT;
    }

    /**
     * Static access to the unique Instance of the <i>OntologyExplorer </i>
     * class.
     */
    public static OntologyExplorer getInstance() {
        if (DEFAULT == null) {
            DEFAULT = new OntologyExplorer();
        }

        return DEFAULT;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.windows.TopComponent#preferredID()
     */
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.windows.TopComponent#getPersistenceType()
     */
    public int getPersistenceType() {
        return super.PERSISTENCE_ALWAYS;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.explorer.ExplorerManager.Provider#getExplorerManager()
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}