package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.connection.ConnectionManager;

/**
 * This component is used to view the currently loaded ontology. When a little
 * plus sign appears, it means that you can expand the tree to see chlids.
 * Clicking this sign will makes childs visible.
 * @author SAÏD, EL KASMI.
 */
public class OntologyExplorer extends TopComponent implements
        ExplorerManager.Provider, Lookup.Provider, Serializable {

    /** A hint to the window system for generating a unique id */
    private static final String PREFERRED_ID = "ontologyexplorer"; // NOI18N

    /** Used by the IDE stored settings. */
    private static final String MODE = "explorer"; // NOI18N

    private static ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();

    private Resource root;

    private Node rootNode = null;

    //  An expolorer manager
    private ExplorerManager explorerManager;

    // A OntologyExplorer instance to use with the
    // Singleton Pattern.
    private static OntologyExplorer ontologyExplorer = null;

    private BeanTreeView beanTreeView;

    public OntologyExplorer() {
        super();
        this.setName(NbBundle.getMessage(OntologyExplorer.class,
                "LBL_OntologyExplorer_Name")); // NOI18N

        // Instanciate an explorer manager
        explorerManager = new ExplorerManager();
        
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(explorerManager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(explorerManager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils
                .actionPaste(explorerManager));
        map.put("delete", ExplorerUtils.actionDelete(explorerManager, true)); // or
                                                                      // false
        associateLookup(ExplorerUtils.createLookup(explorerManager, map));

        // Build and add th BeanTreeView widget
        beanTreeView = new BeanTreeView();
        setLayout(new BorderLayout());
        add(beanTreeView, BorderLayout.CENTER);

        ontologyExplorer = this;
    }

    public void explorOntology() {
        List result = null;
        try {
            // Create a Session using a Connection
            HibernateUtil.createSession(ConnectionManager.getConnection());
            Session sess = HibernateUtil.currentSession();

            Query q = sess
                    .createQuery("select res from Resource res, StringValue sv where res.arcs[:name] = sv and sv.value = :nm");

            q.setString("nm", "top");
            q.setEntity("name", resourceFactory.getProperty("NAME"));

            result = q.list();
            root = (Resource) result.iterator().next();

        } catch (StackOverflowError s) {
            s.printStackTrace(System.err);
        } catch (HibernateException he) {
            he.printStackTrace(System.err);
        }
        Set childs = ((Resource) root).getTargets(Consts.getListOfProperties());

        //Build a root node and set the root context
        rootNode = new ResourceNode(root, childs);
        ((ResourceNode) rootNode).setRootNode(true);
        ((AbstractNode) rootNode)
                .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/catalog");
        explorerManager.setRootContext(rootNode);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.windows.TopComponent#getIcon()
     */
    public Image getIcon() {
        return super.getIcon();
    }

    /*
     * (non-Javadoc)
     * @see org.openide.windows.TopComponent#getPersistenceType()
     */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
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
     * @see org.openide.explorer.ExplorerManager.Provider#getExplorerManager()
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#addNotify()
     */
    public void addNotify() {
        super.addNotify();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#removeNotify()
     */
    public void removeNotify() {
        super.removeNotify();
    }
    
    /* (non-Javadoc)
     * @see org.openide.windows.TopComponent#componentActivated()
     */
    protected void componentActivated() {
        ExplorerUtils.activateActions(explorerManager, true);
    }
    /* (non-Javadoc)
     * @see org.openide.windows.TopComponent#componentDeactivated()
     */
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(explorerManager, false);
    }
//    /**
//     * Create an explorer, if not done yet, and return it.
//     * @return OntologyExplorer instance.
//     */
//    public static OntologyExplorer getDefault() {
//        OntologyExplorer.getInstance();
//        if (OntologyExplorer.ontologyExplorer != null) {
//            Mode m = WindowManager.getDefault().findMode(MODE);
//            if (m != null)
//                m.dockInto(ontologyExplorer);
//            OntologyExplorer.ontologyExplorer.open();
//            OntologyExplorer.ontologyExplorer.requestActive();
//        }
//        return OntologyExplorer.ontologyExplorer;
//    }

    /**
     * @return Returns the ontologyExplorer.
     * @deprecated Use <i>getInstance() </i> method instead.
     */
    public static OntologyExplorer getOntologyExplorer() {
        // Use the WindoManager to get this ontologyExplorer if
        // already created by the layer
        if (OntologyExplorer.ontologyExplorer == null) {
            if (SwingUtilities.isEventDispatchThread()) {
                TopComponent component = null; //WindowManager.getDefault().findTopComponent(COMPONENT_NAME);
                if (component != null) {
                    OntologyExplorer.ontologyExplorer = (OntologyExplorer) component;
                }
            } else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            TopComponent component = null;//WindowManager.getDefault().findTopComponent(COMPONENT_NAME);
                            if (component != null) {
                                OntologyExplorer.ontologyExplorer = (OntologyExplorer) component;
                            }
                        }
                    });
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
        if (OntologyExplorer.ontologyExplorer == null) {
            OntologyExplorer.ontologyExplorer = new OntologyExplorer();
        }
        return ontologyExplorer;
    }

    /**
     * Allows static access to the unique Instance of the <i>OntologyExplorer
     * </i> class.
     */
    public static OntologyExplorer getInstance() {
        // look for an open instance
        Iterator opened = TopComponent.getRegistry().getOpened().iterator();
        while (opened.hasNext()) {
            Object tc = opened.next();
            if (tc instanceof OntologyExplorer) {
                return (OntologyExplorer) tc;
            }
        }
        // none found, make a new one
        return new OntologyExplorer();
    }

    /**
     * @return Returns the rootNode.
     */
    public Node getRootNode() {
        return rootNode;
    }
}