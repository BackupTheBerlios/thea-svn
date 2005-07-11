package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.Action;

import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.thea.ontologyexplorer.actions.ConnectAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.DeleteOntologyNodeAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.DisconnectAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ExploreOntologyAction;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.OntologyNodeInfo;

/**
 * A node that represents an Ontology inside the <i>Ontology Explorer </i>.
 * @author Saïd El Kasmi.
 */
public class OntologyNode extends AbstractNode implements Node.Cookie {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); //NOI18N

    /**
     * A flag that indicates if the ontology represented by this node is loaded.
     */
    private boolean connected = false;

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
                if (event.getPropertyName().equals("connecting")) { //NOI18N
                }

                if (event.getPropertyName().equals("connected")) { //NOI18N
                    setConnected(true);
                }

                if (event.getPropertyName().equals("failed")) { //NOI18N
                    setConnected(false);
                }

                if (event.getPropertyName().equals("disconnected")) { //NOI18N
                    setConnected(false);
                }
            }
        };

        // Listen to the connection
        dbc.addPropertyChangeListener(connectionListener);

        // Set the bean name
        setName(name);

        // Add a nice icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyDisconnectedIcon"); //NOI18N

        // Set the display name: The display name comes from user input
        setDisplayName(name);

        // Give a short description which is shown on the mouse rolls on
        setShortDescription(description);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean context) {
        if (context) {
            return null;
        } else {
            return new Action[] { NodeAction.get(ConnectAction.class),
                    NodeAction.get(DisconnectAction.class), null,
                    NodeAction.get(ExploreOntologyAction.class), null,
                    NodeAction.get(DeleteOntologyNodeAction.class) };
        }
    }

    /*
     * (non-Javadoc)
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

    /**
     * Sets the {@link DatabaseConnection}correspending to this node
     * @param connection The connection to set.
     */
    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
        connection.addPropertyChangeListener(connectionListener);
    }

    /**
     * Tells whether a connection to the database is opened or not.
     * @return Returns the connected.
     */
    public boolean isConnected() {
        return connected;
    }

    /** Sets a name */
    public void setName(String name) {
        super.setName(name);
        //nodeInfo.setName(name);
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
     * @param b Sets the connection status.
     */
    public void setConnected(boolean b) {
        this.connected = b;

        if (b) {
            setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyConnectedIcon"); //NOI18N

            //setShortDescription(getShortDescription() + " Connected");
            // //NOI18N
        } else {
            setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyDisconnectedIcon"); //NOI18N

            //setShortDescription(getShortDescription() + " Disconnected");
            // //NOI18N
        }
    }

    /** Returns cookie */
    public OntologyNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    /** Sets a cookie */
    public void setNodeInfo(OntologyNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }
}