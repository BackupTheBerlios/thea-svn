package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.swing.Action;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ConnectAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.DeleteOntologyNodeAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.DisconnectAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ExploreOntologyAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ImportOwlAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.InitializeKnowledgeBaseAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.SpecifyConfigurationFileAction;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.OntologyNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * A node that represents an Ontology inside the <i>Ontology Explorer </i>.
 * 
 * @author Saïd El Kasmi.
 */
public class OntologyNode extends AbstractNode implements Node.Cookie {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); // NOI18N

    /**
     * A flag that indicates if the database represented by this node is
     * connected.
     */
    private boolean connected = false;

    /**
     * A flag that indicates if the database represented by this node contains a
     * compatible knowledge base.
     */
    private boolean compatibleKB = false;

    /**
     * The number of nodes (resources + literals) stored in the database.
     */
//    private int nbResources = 0;

    /**
     * The local configuration associated with this node
     */
    private Configuration config = null;

    private OntologyNodeInfo nodeInfo;

    /** A {@link DatabaseConnection}to allow loading ontology from a Database */
    private DatabaseConnection connection;

    /***/
    private PropertyChangeListener connectionListener;

    /**
     * Main constructor for {@link OntologyNode}class.
     */
    public OntologyNode(String name, String description, DatabaseConnection dbc) {
        super(new Children.Array());
        this.connection = dbc;
        connectionListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("connecting")) { // NOI18N
                    setConnected(false);
                }

                if (event.getPropertyName().equals("connected")) { // NOI18N
                    setConnected(true);
                }

                if (event.getPropertyName().equals("failed")) { // NOI18N
                    setConnected(false);
                }

                if (event.getPropertyName().equals("disconnected")) { // NOI18N
                    setConnected(false);
                }
            }
        };

        // Listen to the connection
        dbc.addPropertyChangeListener(connectionListener);

        // Set the bean name
        setName(name);

        // Add a nice icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyDisconnectedIcon"); // NOI18N

        // Set the display name: The display name comes from user input
        setDisplayName(name);

        // Give a short description which is shown on the mouse rolls on
        setShortDescription(description);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean context) {
        if (context) {
            return null;
        } else {
            return new Action[] { NodeAction.get(ConnectAction.class),
                    NodeAction.get(DisconnectAction.class),
                    NodeAction.get(DeleteOntologyNodeAction.class), null,
                    NodeAction.get(ExploreOntologyAction.class), null,
                    NodeAction.get(SpecifyConfigurationFileAction.class), null,
                    NodeAction.get(InitializeKnowledgeBaseAction.class),
                    NodeAction.get(ImportOwlAction.class) };
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node#getPreferredAction()
     */
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }

    /**
     * @return Returns the connection.
     */
    public DatabaseConnection getConnection() {
        return connection;
    }
    
    protected ResourceFactory getResourceFactory() {
        return AllontoFactory.getResourceFactory(getConnection().getName());
    }

    /**
     * Sets the {@link DatabaseConnection}correspending to this node
     * 
     * @param connection
     *            The connection to set.
     */
    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
        connection.addPropertyChangeListener(connectionListener);
    }

    /**
     * Tells whether a connection to the database is opened or not.
     * 
     * @return Returns the connected.
     */
    public boolean isConnected() {
        return connected;
    }

    /** Sets a name */
    public void setName(String name) {
        super.setName(name);
    }

    /** Returns cookies for this node */
    public Node.Cookie getCookie(Class klas) {
        if (klas.isInstance(nodeInfo))
            return nodeInfo;
        return super.getCookie(klas);
    }

    /** Creates properties sheet for this node. */
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = Sheet.createPropertiesSet();
        sheet.put(props);
        props.put(new PropertySupport.Name(this));
        return sheet;
    }

    /**
     * @param b
     *            Sets the connection status.
     */
    public void setConnected(boolean b) {
        this.connected = b;

        if (b) {
            setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyConnectedIcon"); // NOI18N
            computeCompatibleKB();
            setCompatibleKB(isCompatibleKB());
        } else {
            setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyDisconnectedIcon"); // NOI18N
            setCompatibleKB(false);
            setShortDescription("");
        }
    }

    /**
     * @return Returns the compatibleKB.
     */
    public boolean isCompatibleKB() {
        return compatibleKB;
    }

    /**
     * Computes the compatibility of the Knowledge base
     */
    public void computeCompatibleKB() {
        getConnection();
        ResourceFactory resourceFactory = getResourceFactory();
        try {
            // The following query is only used to test the correctness of the db schema
            resourceFactory.getResource("ANYRESOURCE", false);
            compatibleKB = true;
            setShortDescription("");
        } catch (AllontoException ae) {
            compatibleKB = false;
            setShortDescription(bundle.getString("LBL_IncompatibleKB")); // NOI18N
        }
    }

    /**
     * @param compatibleKB
     *            The compatibleKB to set.
     */
    public void setCompatibleKB(boolean compatibleKB) {
        this.compatibleKB = compatibleKB;
    }

//    /**
//     * @return Returns the nbResources.
//     */
//    public synchronized int getNbResources() {
//        return nbResources;
//    }
//
//    /**
//     * @param nbResources
//     *            The nbResources to set.
//     */
//    public synchronized void setNbResources(int nbResources) {
//        this.nbResources = nbResources;
//    }

    /** Returns cookie */
    public OntologyNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    /** Sets a cookie */
    public void setNodeInfo(OntologyNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    /** Returns the configuration associated with the node */
    public Configuration getConfiguration() {
        if (config != null)
            return config;
        Hashtable h = OESettings.getInstance().getKbConfigFilePaths();
        if (h == null)
            return null;
        String filePath = (String) h.get(getKBIdentifier());
        try {
            config = new XMLConfiguration(filePath);
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return config;
    }

    /** Sets a configuration */
    public void setConfiguration(XMLConfiguration conf) {
        config = conf;
        Hashtable h = OESettings.getInstance().getKbConfigFilePaths();
        if (h == null) {
            h = new Hashtable();
        }
        if (conf == null) {
            h.remove(getKBIdentifier());
        } else {
            h.put(getKBIdentifier(), conf.getFile().getAbsolutePath());
        }
        OESettings.getInstance().setKbConfigFilePaths(h);
    }

    /**
     * @return a String identifying the Knowledge base associated with the node
     */
    public String getKBIdentifier() {
        DatabaseConnection dbc = getConnection();
        if (dbc == null)
            return null;
        return dbc.getUser() + ":" + dbc.getDatabase() + ":" + dbc.getSchema();
    }

}