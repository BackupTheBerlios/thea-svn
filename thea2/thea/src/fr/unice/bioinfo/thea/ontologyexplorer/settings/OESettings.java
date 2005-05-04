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
            .getProperty("user.home");//NOI18N

    /** The configuration file for thea. */
    private static String configFilePath = "";//NOI18N

    /** Last selected evidences */
    private static String[] evidences = null;

    /** Last selected string values properties */
    private static String[] svnames = null;

    /** <i>lastBrowsedDirectory </i> property name. */
    public static final String PROP_LAST_BROWSED_DIRECTORY = "lastBrowsedDirectory"; // NOI18N

    /** <i>lastBrowsedDirectory </i> property name. */
    public static final String PROP_CONFIG_FILE_PATH = "configFilePath"; // NOI18N

    /** evidences property name */
    public static final String PROP_EVIDENCES = "evidences"; // NOI18N

    /** svnames property name */
    public static final String PROP_SV_NAMES = "svnames"; // NOI18N

    /** Returns the last browsed directory. */
    public String getLastBrowsedDirectory() {
        return lastBrowsedDirectory;
    }

    /** Store the last browsed directory's path. */
    public void setLastBrowsedDirectory(String value) {
        value = (value == null) ? "" : value;//NOI18N
        String old = getLastBrowsedDirectory();
        if (!value.equals(old)) {
            lastBrowsedDirectory = value;
            firePropertyChange(PROP_LAST_BROWSED_DIRECTORY, old, value);
        }
    }

    /** Returns the configuration file's absolute path. */
    public String getConfigFilePath() {
        return configFilePath;
    }

    /** Sets the configuration file's absolute path. */
    public void setConfigFilePath(String value) {
        String old = getConfigFilePath();
        if (!value.equals(old)) {
            configFilePath = value;
            firePropertyChange(PROP_CONFIG_FILE_PATH, old, value);
        }
    }

    /** retrurns evidences list. */
    public String[] getEvidences() {
        return evidences;
    }

    /** Sets evidences. */
    public void setEvidences(String[] value) {
        String[] old = getEvidences();
        evidences = value;
        firePropertyChange(PROP_EVIDENCES, old, value);
    }

    public String[] getSvnames() {
        return svnames;
    }

    public void setSvnames(String[] value) {
        String[] old = getSvnames();
        evidences = value;
        firePropertyChange(PROP_SV_NAMES, old, value);
    }
}