package fr.unice.bioinfo.thea.classification.io.wizard;

import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

/**
 * <p>
 * This is the main class for the wizard. It defines the order of panels and the
 * way to iterate over them.
 * </p>
 * Iterates over the list of wizards.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Wizard implements WizardDescriptor.Iterator {

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.io.wizard.Bundle"); // NOI18N;

    /** List of panels */
    private transient WizardDescriptor.Panel[] panels = null;

    /** List of steps */
    private transient String[] steps = null;

    /** Index on panels to keep track on them */
    private transient int index = 0;

    private OpenClassificationWizardDescriptor descriptor;

    public Wizard() {
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
        return current().getComponent().getName();
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
            // fireChangeEvent ();
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
     * @return List of panels.
     */
    protected final WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = buildPanels();
        }
        return panels;
    }

    /**
     * Build list of panels.
     * @return List of panels.
     */
    private WizardDescriptor.Panel[] buildPanels() {
        return new WizardDescriptor.Panel[] { new DataFormatWizard(descriptor),
                new FilesChooserWizard(descriptor),
                new PreviewWizard(descriptor) };
    }

    /**
     * Returns steps' list.
     * @return List of steps.
     */
    private String[] buildSteps() {
        return new String[] { bundle.getString("LBL_DataFormatWizard"),
                bundle.getString("LBL_FilesChooserWizard"),
                bundle.getString("LBL_PreviewWizard") };
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

    private boolean showing(int index) throws NoSuchElementException {
        switch (index) {
        case 0:
            return true;
        case 1:
            return true;
        case 2:
            return true;
        default:
            throw new NoSuchElementException();
        }
    }

    public void setDialogDescriptor(
            OpenClassificationWizardDescriptor descriptor) {
        this.descriptor = descriptor;
    }

}