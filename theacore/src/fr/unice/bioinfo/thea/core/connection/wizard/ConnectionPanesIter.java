package fr.unice.bioinfo.thea.core.connection.wizard;

import java.util.NoSuchElementException;

import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

/**
 * Iterator on the sequence of panels used by the Load Ontology Wizard.
 * @author SAÏD, EL KASMI.
 */
public class ConnectionPanesIter implements WizardDescriptor.Iterator {

    private ConnectionDialogDescriptor connectionDescriptor;

    // List of panels
    private transient WizardDescriptor.Panel[] panels = null;

    // List of steps
    private transient String[] steps = null;

    // Index on panels to keep track on them
    private transient int index = 0;

    // Define panels here
    public ConnectionPanesIter() {
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#current()
     */
    public Panel current() {
        return getPanels()[index];
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#name()
     */
    public String name() {
        return NbBundle.getMessage(ConnectionPanesIter.class,
                "LBL_WizardSteps_X_OF_Y", new Integer(index + 1), new Integer(
                        getPanels().length));
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#hasNext()
     */
    public boolean hasNext() {
        for (int i = index + 1; i < panels.length; i++) {
            if (showing(i)) {
                return true;
            }
        }
        return false;

    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#hasPrevious()
     */
    public boolean hasPrevious() {
        return index > 0;

    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#nextPanel()
     */
    public void nextPanel() {
        index++;
        while (!showing(index))
            index++;
        if (index == 1) {
            // User finished intro panel, list of panels may have changed:
            //fireChangeEvent ();
        }

    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#previousPanel()
     */
    public void previousPanel() {
        index--;
        while (!showing(index))
            index--;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener arg0) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see org.openide.WizardDescriptor.Iterator#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * Build list of panels.
     * @return List of panels.
     */
    protected WizardDescriptor.Panel[] buildPanels() {
        return new WizardDescriptor.Panel[] {
                new DataBaseSettingsWizardPanel(connectionDescriptor),
                new UserSettingsWizardPanel(connectionDescriptor) };
    }

    // Give a name to each step
    // try to use Bundles here
    protected String[] buildSteps() {
        return new String[] { "DataBase Settings", "User Settings" };
    }

    /**
     * @return List of panels.
     */
    protected final WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = buildPanels();
        }
        return panels;
    }

    /** Return list of steps */
    public final String[] getSteps() {
        if (steps == null) {
            steps = buildSteps();
        }
        return steps;
    }

    /** Return the index of panels */
    protected final int getIndex() {
        return index;
    }

    public void setConnectionDialogDescriptor(
            ConnectionDialogDescriptor descriptor) {
        this.connectionDescriptor = descriptor;
    }

    private boolean showing(int index) throws NoSuchElementException {
        switch (index) {
        case 0:
            return true;
        case 1:
            return true;
        default:
            throw new NoSuchElementException();
        }
    }

}