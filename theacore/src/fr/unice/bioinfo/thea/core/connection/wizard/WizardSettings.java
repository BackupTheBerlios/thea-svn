package fr.unice.bioinfo.thea.core.connection.wizard;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/**
 * Store settings related to the connection wizard.
 * @author SAÏD, EL KASMI.
 */
public class WizardSettings extends SystemOption {

    /*
     * (non-Javadoc)
     * @see org.openide.options.SystemOption#displayName()
     */
    public String displayName() {
        return NbBundle.getMessage(WizardSettings.class,
                "LBL_WizardSettings_Name");
    }

    protected void initialize() {
        super.initialize();
    }

    /** Returns an the unique instance of this class.*/
    public static WizardSettings getInstance() {
        return ((WizardSettings) WizardSettings.findObject(
                WizardSettings.class, true));
    }

}