package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.ConnectPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * Connect to the database.
 * 
 * @author Saïd El Kasmi
 */
public class ConnectAction extends NodeAction {
    static final long serialVersionUID = 2441554183821391282L;

    private Dialog dialog;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        if (!(node instanceof OntologyNode)) {
            return;
        }

        OntologyNode on = (OntologyNode) node;
        final DatabaseConnection dbc = on.getConnection();
        final ConnectPanel cp = new ConnectPanel(dbc);

        // Listener to the connection from here
        final PropertyChangeListener connectionListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("connected")) { // NOI18N

                    if (dialog != null) {
                        dialog.setVisible(false);
                    }
                } else if (event.getPropertyName().equals("failed")) { // NOI18N
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message("Connection failed.",
                                    NotifyDescriptor.INFORMATION_MESSAGE));
                }
            }
        };

        dbc.addPropertyChangeListener(connectionListener);

        // Create the action listener for Ok/Cancel buttons
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (event.getSource() == DialogDescriptor.OK_OPTION) {
                    dbc.setDatabase(cp.getUrl());
                    dbc.setUser(cp.getLoginName());
                    dbc.setPassword(cp.getPassword());

                    try {
                        if ((dbc.getConnection() == null)
                                || dbc.getConnection().isClosed()) {
                            dbc.connect();
                        }
                    } catch (SQLException exc) {
                        // isClosed() method failed, try to connect
                        dbc.connect();
                    }
                }
            }
        };

        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(cp, bundle
                .getString("ConnectDialog_Title"), true, actionListener); // NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        descriptor.setClosingOptions(closingOptions);
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
        // Enable this action only for the OntologyNode
        nodes = OntologyExplorer.findDefault().getExplorerManager()
                .getSelectedNodes();

        Node node;

        if ((nodes != null) && (nodes.length == 1)) {
            node = nodes[0];
        } else {
            return false;
        }

        if (node instanceof OntologyNode) {
            if (!((OntologyNode) node).isConnected()) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_ConnectAction_Name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}