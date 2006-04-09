package fr.unice.bioinfo.thea.classification.io.wizard;

import java.awt.Dialog;

import javax.swing.JFrame;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

/**
 * An utility java class to test the wizard.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class TestWizard extends JFrame {

    public TestWizard() {
        super();
    }

    public void showIt() {
        OpenClassificationWizardDescriptor wd = new OpenClassificationWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.show();
        if (wd.getValue() == WizardDescriptor.FINISH_OPTION) {
        }
    }

    public static void main(String[] args) {
        TestWizard tw = new TestWizard();
        tw.pack();
        tw.setVisible(true);
        tw.showIt();
    }
}