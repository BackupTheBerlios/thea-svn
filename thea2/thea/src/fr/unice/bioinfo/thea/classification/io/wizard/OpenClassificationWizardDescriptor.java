package fr.unice.bioinfo.thea.classification.io.wizard;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class OpenClassificationWizardDescriptor extends WizardDescriptor {

    // Iterator on the sequence of panels used to build
    // the Load Ontology Wizard
    private final Wizard wizard;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.io.wizard.Bundle"); // NOI18N;

    public OpenClassificationWizardDescriptor() {
        this(new Wizard());
        this.wizard.setDialogDescriptor(this);
    }

    private OpenClassificationWizardDescriptor(Wizard wizard) {
        super(wizard);
        this.wizard = wizard;
        // Customize titling mode
        setTitleFormat(new MessageFormat(" {0}")); // NOI18N
        // Give a title to the Dialog
        setTitle(bundle.getString("LBL_OpenDialogDescriptor_WizardTitle"));
        // Make the left pane appear:
        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        // Make the left pane show list of steps etc.:
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        // Number the steps.
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        // Optional: show a help tab with special info about the pane:
        putProperty("WizardPanel_helpDisplayed", Boolean.TRUE); // NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor#updateState()
     */
    protected synchronized void updateState() {
        super.updateState();
        putProperty("WizardPanel_contentData", wizard.getSteps()); // NOI18N
        putProperty("WizardPanel_contentSelectedIndex", new Integer(wizard
                .getIndex())); // NOI18N
    }

}