package fr.unice.bioinfo.thea.core.connection.wizard;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.thea.core.connection.ConnectionSettings;

/**
 * Implements a basic "wizard" GUI system. A list of wizard panels 
 * may be specified and these may be traversed at the proper times 
 * using the "Previous" and "Next" buttons (or "Finish" on the last one). 
 * @author SAÏD, EL KASMI.
 */
public class ConnectionDialogDescriptor extends WizardDescriptor {

    // Iterator on the sequence of panels used to build
    // the Load Ontology Wizard
    private final ConnectionPanesIter iterator;

    // Settings to connect to datavase
    private ConnectionSettings connectionSettings;

    public ConnectionDialogDescriptor() {
        this(new ConnectionPanesIter());
        this.iterator.setConnectionDialogDescriptor(this);
    }

    /**
     * @param iterator Iterator on the sequence of panels.
     * @see fr.unice.bioinfo.thea.ConnectionPanesIter
     */
    private ConnectionDialogDescriptor(ConnectionPanesIter iterator) {
        super(iterator);
        this.iterator = iterator;
        // Give a title to the Dialog
        setTitle(NbBundle.getMessage(ConnectionDialogDescriptor.class,
                "LBL_ConnectionDialogDescriptor_WizardTitle"));
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

    /** Returns the connectionSettings.*/
    public ConnectionSettings getConnectionSettings() {
        return connectionSettings;
    }
}