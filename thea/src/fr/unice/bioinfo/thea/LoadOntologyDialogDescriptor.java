package fr.unice.bioinfo.thea;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * @author SAÏD, EL KASMI.
 */
public class LoadOntologyDialogDescriptor extends WizardDescriptor {
    
    // Iterator on the sequence of panels used to build
    // the Load Ontology Wizard
    private final LoadOntologyPanesIter iterator;
    
    // Settings to connect to datavase
    private LoadOntologySettings loadOntologySettings;
    
    public LoadOntologyDialogDescriptor(){
        this(new LoadOntologyPanesIter());
        this.iterator.setLoadOntologyDialogDescriptor(this);
    }
    
    /**
     * @param iterator Iterator on the sequence of panels.
     * @see fr.unice.bioinfo.thea.LoadOntologyPanesIter
     */
    private LoadOntologyDialogDescriptor(LoadOntologyPanesIter iterator) {
        super(iterator);
        this.iterator = iterator;
        // Give a title to the Dialog
        setTitle(NbBundle.getMessage(LoadOntologyDialogDescriptor.class,
                "LBL_LoadOntologyDialogDescriptor_WizardTitle"));
        //Make the left pane appear:
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
        putProperty("WizardPanel_contentData", iterator.getSteps()); // NOI18N
        putProperty("WizardPanel_contentSelectedIndex", new Integer(iterator
                .getIndex())); // NOI18N
    }
    /**
     * @return Returns the loadOntologySettings.
     */
    public LoadOntologySettings getLoadOntologySettings() {
        return loadOntologySettings;
    }
}
