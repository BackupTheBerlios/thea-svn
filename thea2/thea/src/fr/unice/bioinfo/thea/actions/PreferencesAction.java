package fr.unice.bioinfo.thea.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.thea.dlg.PreferencesContainer;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class PreferencesAction extends SystemAction {

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.actions.Bundle"); // NOI18N;

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_PreferencesAction");// NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#iconResource()
     */
    // protected String iconResource() {
    // return "fr/unice/bioinfo/thea/resources/PreferencesIconIcon16.gif";
    // //NOI18N
    // }
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
        // create the main panel for Classification Viewer Settings
        PreferencesContainer p = new PreferencesContainer();

        // create the hide button
        JButton hideBtn = new JButton(bundle.getString("HideButton_Name"));
        hideBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        Object[] options = { hideBtn };
        DialogDescriptor dd = new DialogDescriptor(p, bundle
                .getString("PreferencesDialog_Title"), false, options, null,
                DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP, null);
        dd.setClosingOptions(new Object[] { hideBtn });
        DialogDisplayer.getDefault().createDialog(dd).show();
    }

}