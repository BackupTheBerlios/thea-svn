package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerManager.Provider;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.AbstractResourceNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNodeProperty;

/**
 * This component is used to display a detailed view of a specific resource. The
 * tree displays all properties of the resource plus the associated targets and,
 * when needed, the context in which such a link is valid.
 * 
 * @author Claude Pasquier
 */
public class ResourceNodePropertiesExplorer extends TopComponent implements Provider {
    /**
     * generated Serialized Version UID
     */
    private static final long serialVersionUID = 5594651486653844339L;

    /** A hint to the window system for generating a unique id */
    private static final String PREFERRED_ID = "resourceexplorer"; // NOI18N

    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport;

    /**
     * The widget that allow this component visualize the resource as a tree.
     * 
     * @see org.openide.explorer.view.BeanTreeView
     */
    private ResourceDetailedView beanTreeView;

    /** An expolorer manager to associate the bean tree view */
    private ExplorerManager explorerManager;

    /**
     * Create an {@link ResourceNodePropertiesExplorer}instance
     * 
     * @param node
     *            a node that represents a Resource in the ontology explorer.
     */
    public ResourceNodePropertiesExplorer(Node node) {
        super();

        propertySupport = new PropertyChangeSupport(this);

        propertySupport.firePropertyChange("initializing", null, null);// NOI18N

        // give a title to this window using the resource's name
        setName(node.getDisplayName());
        // icon
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/PropertiesEditorIcon16.gif")); // NOI18N

        // layout
        setLayout(new BorderLayout());
        propertySupport.firePropertyChange("processing", null, null);// NOI18N

        // Instanciate an explorer manager
        explorerManager = new ExplorerManager();

        // the resource which is displayed
        Resource resource = (((AbstractResourceNode)node).getResource());
        
        // Create the root node
        Node rn = new ResourceNodeProperty(resource, (((AbstractResourceNode)node).getOntologyNode()));
                //resource);
        // Add the root node for all ontologies
        //rn.getChildren().add(populateTree((ResourceNode)node));

        explorerManager.setRootContext(node);

        // Build and add th BeanTreeView widget
        beanTreeView = new ResourceDetailedView();

        // Make the root node hidden
//        beanTreeView.setRootVisible(true);
        setLayout(new BorderLayout());
        add(beanTreeView, BorderLayout.CENTER);
        propertySupport.firePropertyChange("endprocessing", null, null);// NOI18N
    }
    
    private Node[] populateTree(ResourceNode node) {
        // the resource which is displayed
        Resource resource =  node.getResource();
        try {
            HibernateUtil.createSession(node.getConnection().getConnection());
            Session sess = HibernateUtil.currentSession();
            sess.update(resource);
        } catch (HibernateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        List propertyNodes = new ArrayList();
        Iterator mapIt = resource.getArcs().keySet().iterator();
        while (mapIt.hasNext()) {
            Resource key = (Resource)mapIt.next();
            if (!key.isNamedProperty()) continue;
            propertyNodes.add(new ResourceNode(key));
        }
        try {
            HibernateUtil.closeSession();
        } catch (HibernateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Node[] childs = new Node[propertyNodes.size()];
        for (int i = 0 ; i < propertyNodes.size() ; i++) {
            childs[i] = (Node)propertyNodes.get(i);
        }
        return childs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.windows.TopComponent#preferredID()
     */
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.windows.TopComponent#getPersistenceType()
     */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.explorer.ExplorerManager.Provider#getExplorerManager()
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}