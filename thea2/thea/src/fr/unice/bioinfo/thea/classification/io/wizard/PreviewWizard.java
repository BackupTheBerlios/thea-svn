package fr.unice.bioinfo.thea.classification.io.wizard;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class PreviewWizard implements WizardDescriptor.Panel {

    private OpenClassificationWizardDescriptor descriptor;
    private PreviewPanel panel;

    public PreviewWizard(OpenClassificationWizardDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getComponent()
     */
    public Component getComponent() {
        return createPanel();
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#getHelp()
     */
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#readSettings(java.lang.Object)
     */
    public void readSettings(Object arg0) {
        //        if (arg0 instanceof WizardDescriptor) {
        //            WizardDescriptor wd = (WizardDescriptor) arg0;
        //            wd.addPropertyChangeListener(panel);
        //        }
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#storeSettings(java.lang.Object)
     */
    public void storeSettings(Object arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#isValid()
     */
    public boolean isValid() {
        //        ClassificationInfo info = ClassificationInfo.getInstance();
        //        String string = info.getFileFormat();
        //        if(string.equals(SupportedFormat.EISEN)){
        //            if((info.getClusteredDataFile() == null) ||
        // (info.getTabularDataFile() == null)){
        //                return false;
        //            }
        //        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Panel#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener arg0) {
        // TODO Auto-generated method stub
    }

    private JPanel createPanel() {
        if (panel == null) {
            panel = new PreviewPanel();
        }
        return panel;
    }

}