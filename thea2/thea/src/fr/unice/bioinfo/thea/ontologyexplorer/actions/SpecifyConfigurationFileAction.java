package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream.GetField;
import java.util.ResourceBundle;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.ConfigurationFileEntryPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class SpecifyConfigurationFileAction extends NodeAction {
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

        Configuration localConfiguration = ((OntologyNode) node)
                .getConfiguration();

        String filePath = "";
        if (localConfiguration != null) {
            filePath = ((XMLConfiguration) localConfiguration).getFile()
                    .getAbsolutePath();
        }
        // Create the panel
        final ConfigurationFileEntryPanel panel = new ConfigurationFileEntryPanel(
                filePath);
        // Create the listener for buttons actions/ Ok/Cancel
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION) {
                    String filePath = panel.getFilePath();

                    if ((filePath == null) || (filePath.equals(""))) {
                        ((OntologyNode) node).setConfiguration(null);
                        return;
                    } else {
                        try {
                            XMLConfiguration conf = new XMLConfiguration(
                                    filePath);
                            ((OntologyNode) node).setConfiguration(conf);
                        } catch (ConfigurationException ce) {
                            ((OntologyNode) node).setConfiguration(null);
                            ErrorManager.getDefault().notify(
                                    ErrorManager.INFORMATIONAL, ce);
                            String message = bundle
                                    .getString("ErrMsg_InvalidConfigurationFile"); // NOI18N
                            NotifyDescriptor d = new NotifyDescriptor.Message(
                                    message,
                                    NotifyDescriptor.INFORMATION_MESSAGE);
                            DialogDisplayer.getDefault().notify(d);
                        }
                    }
                }
            }
        };
        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                .getString("LBL_ConfigurationFileEntryPanelTitle"), true, al); // NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        // descriptor.setClosingOptions(closingOptions);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
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
            return true;
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
        return bundle.getString("LBL_SpecifyConfigurationFileAction_Name");// NOI18N
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