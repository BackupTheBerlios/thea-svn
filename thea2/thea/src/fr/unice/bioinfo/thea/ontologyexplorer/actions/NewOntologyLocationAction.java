package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.MessageFormat;
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
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.NewOntologyPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.OntologyNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologiesRootNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author Saïd El Kasmi.
 */
public class NewOntologyLocationAction extends NodeAction {
    private Dialog dialog;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] nodes) {
        // Note: when adding nodes,test for redundance
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        // Create a DBC and listen to it
        final DatabaseConnection dbc = new DatabaseConnection();
        final PropertyChangeListener connectionListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("connected")) { // NOI18N
                } else if (evt.getPropertyName().equals("failed")) { // NOI18N

                    if (dialog != null) {
                        dialog.setVisible(false);
                    }

                    String message = MessageFormat.format(bundle
                            .getString("ERR_UnableToAddOntology"),
                            new String[] { dbc.getDatabase() }); // NOI18N
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(message,
                                    NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        };

        dbc.addPropertyChangeListener(connectionListener);

        // Create the panel
        final NewOntologyPanel panel = new NewOntologyPanel(dbc);

        // Create the listener for buttons actions/ Ok/Cancel
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION) {
                    String name = panel.getOntologyName();
                    String hint = panel.getOntologyDescription();
                    StringBuffer b = new StringBuffer();

                    // A name is allways required
                    if (name.equalsIgnoreCase("")) { // NOI18N
                        // b.append(bundle.getString("NewOntologyDialog_NoName"));
                        // //NOI18N
                    }

                    if (panel.getSelectedDatabaseName().equalsIgnoreCase("")) { // NOI18N
                        b.append(bundle.getString("NewOntologyDialog_NoDB")); // NOI18N
                    }

                    // Show a dialog with errors if any
                    if (b.length() > 0) {
                        String message = MessageFormat.format(bundle
                                .getString("NewOntologyDialog_ErrMsg"),
                                new String[] { b.toString() }); // NOI18N
                        DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(message,
                                        NotifyDescriptor.INFORMATION_MESSAGE));

                        return;
                    }

                    // close dialog
                    closeDialog();

                    // If everything is ok:
                    // Get information about Database and authentication
                    // and create a DatabaseConnection
                    panel.setConnectionInfo();

                    // Create a node that represents an Ontology
                    OntologyNode on = new OntologyNode(dbc.getDatabase(), hint,
                            dbc);
                    // Create its nodeInfo
                    OntologyNodeInfo oni = new OntologyNodeInfo();
                    on.setNodeInfo(oni);

                    try {
                        if ((dbc.getConnection() == null)
                                || dbc.getConnection().isClosed()) {
                            dbc.connect();
                        }
                    } catch (SQLException exc) {
                        // isClosed() method failed, try to connect
                        dbc.connect();
                    }

                    // Add it to the root node
                    ((OntologiesRootNode) node).getChildren().add(
                            new Node[] { on });
                }
            }
        };

        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                .getString("NewOntologyDialogTitle"), true, al); // NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        descriptor.setClosingOptions(closingOptions);
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
        // Enable this action only for the RootOntologyNode
        nodes = OntologyExplorer.findDefault().getExplorerManager()
                .getSelectedNodes();

        Node node;

        if ((nodes != null) && (nodes.length == 1)) {
            node = nodes[0];
        } else {
            return false;
        }

        if (node instanceof OntologiesRootNode) {
            return true;
        }

        return false;
    }

    // Closes dialog
    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_NewOntologyLocationAction_Name"); // NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#iconResource()
     */
    protected String iconResource() {
        return "fr/unice/bioinfo/thea/ontologyexplorer/resources/NewOntologyLocationIcon.png"; // NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }
}