package fr.unice.bioinfo.thea;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.sql.Connection;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.thea.connection.ConnectionManager;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.OntologyExplorerAction;

/**
 * @author SAÏD, EL KASMI.
 */
public class LoadOntologyAction extends SystemAction {

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return NbBundle.getMessage(LoadOntologyAction.class,
                "LBL_LoadOntologyAction_Name"); // NOI18N;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#iconResource()
     */
    protected String iconResource() {
        return "fr/unice/bioinfo/thea/resources/open24.gif";
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.HelpCtx.Provider#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        LoadOntologyDialogDescriptor descriptor = new LoadOntologyDialogDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
        // After the wizard finish
        if (descriptor.getValue() == WizardDescriptor.FINISH_OPTION) {
            // Get Settings
            LoadOntologySettings loadOntologySettings = (LoadOntologySettings) LoadOntologySettings
                    .findObject(LoadOntologySettings.class, true);
            // Get a Connection:
            Connection connection = ConnectionManager
                    .initConnection(loadOntologySettings);
            if (connection == null) {
                // no connection established, make the user try again
                NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle
                        .getMessage(LoadOntologyAction.class,
                                "MSG_ConnectionError"),
                        NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            } else if (connection != null) {
                // Enable the "Ontology Explorer" menu item
                (SystemAction.get(OntologyExplorerAction.class))
                        .setEnabled(true);
                //                TopComponent tc = OntologyExplorer.getDefault();
                //                if (!tc.isOpened())
                //                    tc.open();
                //                tc.requestActive();
                //                ((OntologyExplorer)tc).explorOntology();
            }
        }
    }

}