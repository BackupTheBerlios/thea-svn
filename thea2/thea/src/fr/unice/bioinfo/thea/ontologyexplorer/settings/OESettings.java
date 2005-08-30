package fr.unice.bioinfo.thea.ontologyexplorer.settings;

import java.util.Hashtable;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;

/**
 * Common options for the ontology explorer.
 * 
 * @author Saïd El Kasmi
 */
/**
 * @author claude
 */
public class OESettings extends SystemOption {

    /** generated Serialized Version UID */
    static final long serialVersionUID = 801907850705717911L;

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.options.SystemOption#displayName()
     */
    public String displayName() {
        return NbBundle.getMessage(OESettings.class, "LBL_OESettings_Name"); // NOI18N;
    }

    /*
     * (non-Javadoc)
     * 
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
            .getProperty("user.home");// NOI18N

    /** The configuration file for thea. */
    private static String configFilePath = "";// NOI18N

    /** Last selected lastSelectedEvidences */
    private static String[] lastSelectedEvidences = null;

    /** Last selected string values properties */
    private static String[] lastSelectedSvnames = null;

    /** <i>lastBrowsedDirectory </i> property name. */
    public static final String PROP_LAST_BROWSED_DIRECTORY = "lastBrowsedDirectory"; // NOI18N

    /** <i>configFilePath</i> property name. */
    public static final String PROP_CONFIG_FILE_PATH = "configFilePath"; // NOI18N

    /**
     * The map of configuration files associated with each Knowledge bases
     */
    public static final String PROP_KB_CONFIG_FILE_PATHS = "kbConfigFilePaths"; // NOI18N

    public static final String PROP_TEST = "test";

    /** lastSelectedEvidences property name */
    public static final String PROP_EVIDENCES = "lastSelectedEvidences"; // NOI18N

    /** lastSelectedSvnames property name */
    public static final String PROP_SV_NAMES = "lastSelectedSvnames"; // NOI18N

    /** Returns the last browsed directory. */
    public String getLastBrowsedDirectory() {
        return lastBrowsedDirectory;
    }

    /** Store the last browsed directory's path. */
    public void setLastBrowsedDirectory(String value) {
        value = (value == null) ? "" : value;// NOI18N
        String old = getLastBrowsedDirectory();
        if (!value.equals(old)) {
            lastBrowsedDirectory = value;
            firePropertyChange(PROP_LAST_BROWSED_DIRECTORY, old, value);
        }
    }

    /** retrurns lastSelectedEvidences list. */
    public String[] getLastSelectedEvidences() {
        return lastSelectedEvidences;
    }

    /** Sets lastSelectedEvidences. */
    public void setLastSelectedEvidences(String[] value) {
        String[] old = getLastSelectedEvidences();
        lastSelectedEvidences = value;
        firePropertyChange(PROP_EVIDENCES, old, value);
    }

    public String[] getLastSelectedSvnames() {
        return lastSelectedSvnames;
    }

    public void setLastSelectedSvnames(String[] value) {
        String[] old = getLastSelectedSvnames();
        lastSelectedSvnames = value;
        firePropertyChange(PROP_SV_NAMES, old, value);
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

    /**
     * @return A hashtable associating a configuration file paths to nodes
     */
    public Hashtable getKbConfigFilePaths() {
        return (Hashtable) getProperty(PROP_KB_CONFIG_FILE_PATHS);
    }

    /**
     * Associates to a given node, a path to a configuration file
     * 
     * @param h
     *            The Hashtable associating configuration file path to nodes
     */
    public void setKbConfigFilePaths(Hashtable h) {
        putProperty(PROP_KB_CONFIG_FILE_PATHS, h, true);
    }

}