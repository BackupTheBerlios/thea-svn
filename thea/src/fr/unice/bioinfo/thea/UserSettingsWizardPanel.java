package fr.unice.bioinfo.thea;

import java.awt.Component;

import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * @author SAÏD, EL KASMI.
 */
public class UserSettingsWizardPanel implements WizardDescriptor.Panel {

    private UserSettingsPanel userSettingsPanel = null;
    
    private LoadOntologyDialogDescriptor loadOntologyDialogDescriptor;

    public UserSettingsWizardPanel(
            LoadOntologyDialogDescriptor loadOntologyDialogDescriptor) {
        this.loadOntologyDialogDescriptor = loadOntologyDialogDescriptor;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getComponent()
     */
    public Component getComponent() {
        return getUserSettingsPanel();
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getHelp()
     */
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
        //return new HelpCtx(UserSettingsWizardPanel.class);
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#readSettings(java.lang.Object)
     */
    public void readSettings(Object arg0) {
        if (arg0 instanceof WizardDescriptor) {
            getUserSettingsPanel()
                    .initFromSettings((WizardDescriptor) arg0);
        }
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#storeSettings(java.lang.Object)
     */
    public void storeSettings(Object arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#isValid()
     */
    public boolean isValid() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener arg0) {
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener arg0) {
    }

    /**
     * @return Returns the userSettingsPanel.
     */
    private UserSettingsPanel getUserSettingsPanel() {
        if (userSettingsPanel == null) {
            userSettingsPanel = new UserSettingsPanel();
        }
        return userSettingsPanel;
    }
}