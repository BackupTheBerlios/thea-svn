package fr.unice.bioinfo.thea;

import java.awt.Component;

import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * This class represents data of wizard DataBase step.
 * It uses an instance of DataBaseSettingsPanel.
 * @author SAÏD, EL KASMI.
 */
public class DataBaseSettingsWizardPanel implements WizardDescriptor.Panel{
    
    public DataBaseSettingsWizardPanel(LoadOntologyDialogDescriptor loadOntologyDialogDescriptor){
        this.loadOntologyDialogDescriptor = loadOntologyDialogDescriptor;
    }
    
    private LoadOntologyDialogDescriptor loadOntologyDialogDescriptor;
     
    private DataBaseSettingsPanel dataBaseSettingsPanel;

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getComponent()
     */
    public Component getComponent() {
        return getDataBaseSettingsPanel();
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getHelp()
     */
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
        //return new HelpCtx(DataBaseSettingsWizardPanel.class);
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#readSettings(java.lang.Object)
     */
    public void readSettings(Object arg0) {
        if (arg0 instanceof WizardDescriptor) {
            getDataBaseSettingsPanel()
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
        // TODO Auto-generated method stub
        return true;
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener arg0) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener arg0) {
        // TODO Auto-generated method stub     
    }

    /**
     * @return Returns the dataBaseSettingsPanel.
     */
    private DataBaseSettingsPanel getDataBaseSettingsPanel() {
        if(dataBaseSettingsPanel == null){
            dataBaseSettingsPanel = new DataBaseSettingsPanel();
        }
        return dataBaseSettingsPanel;
    }
}
