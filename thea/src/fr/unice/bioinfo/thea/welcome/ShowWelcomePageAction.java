package fr.unice.bioinfo.thea.welcome;

import java.awt.event.ActionEvent;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * @author SAÏD, EL KASMI.
 */
public class ShowWelcomePageAction extends SystemAction {

    /* (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return NbBundle.getMessage(ShowWelcomePageAction.class,
        "LBL_ShowWelcomePageAction_Name");
    }

    /* (non-Javadoc)
     * @see org.openide.util.HelpCtx.Provider#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        WelcomeComponent welcome = WelcomeComponent.getWelcomeComponent();
        welcome.open();
        welcome.requestActive();
    }

}
