package fr.unice.bioinfo.thea.ontologyexplorer.settings;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/**
 * Common options for the ontology explorer.
 * @author Saïd El Kasmi
 */
public class OESettings extends SystemOption {

    /** generated Serialized Version UID */
    static final long serialVersionUID = 801907850705717911L;

    /*
     * (non-Javadoc)
     * @see org.openide.options.SystemOption#displayName()
     */
    public String displayName() {
        return NbBundle.getMessage(OESettings.class, "LBL_OESettings_Name"); // NOI18N;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.SharedClassObject#initialize()
     */
    protected void initialize() {
        super.initialize();
    }

    /** return an instance of this class using the Singleton pattern. */
    public static OESettings getInstance() {
        return ((OESettings) OESettings.findObject(OESettings.class, true));
    }

    /** The last browsed directory when loading a classification */
    private static String lastBrowsedDirectory = System
            .getProperty("user.home");

    /** <i>lastBrowsedDirectory </i> property name. */
    public static final String PROP_LAST_BROWSED_DIRECTORY = "lastBrowsedDirectory"; // NOI18N

    /** Returns the last browsed directory. */
    public String getLastBrowsedDirectory() {
        return lastBrowsedDirectory;
    }

    /** Store the last browsed directory's path. */
    public void setLastBrowsedDirectory(String value) {
        value = (value == null) ? "" : value;
        String old = getLastBrowsedDirectory();
        if (!value.equals(old)) {
            lastBrowsedDirectory = value;
            firePropertyChange(PROP_LAST_BROWSED_DIRECTORY, old, value);
        }

    }
}