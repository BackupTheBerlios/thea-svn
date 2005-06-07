package fr.unice.bioinfo.thea.classification.settings;

import java.awt.Color;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openide.options.SystemOption;

/**
 * Defines all options for the classification viewer customization. Never
 * instanciate this class directly but use the the static method <i>
 * getInstance() </i> to get an instance ot it.
 * @author SAÏD, EL KASMI.
 */
public class CESettings extends SystemOption {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 801136840705717911L;

    /** lastSelectedEvidences property name */
    public static final String PROP_EVIDENCES = "lastSelectedEvidences"; // NOI18N

    /** zoomFactorX property name */
    public static final String ZOOM_FACTOR_X = "zoomFactorX";//NOI18N

    /** zoomFactorY property name */
    public static final String ZOOM_FACTOR_Y = "zoomFactorY";//NOI18N

    /** showTermID property name */
    public static final String PROP_SHOW_TERM_ID = "showTermID"; // NOI18N

    /** showTermName property name */
    public static final String PROP_SHOW_TERM_NAME = "showTermName"; // NOI18N

    /** showOverRepresented property name */
    public static final String PROP_SHOW_OVER_REPRESENTED = "showOverRepresented"; // NOI18N

    /** showUnderRepresented property name */
    public static final String PROP_SHOW_UNDER_REPRESENTED = "showUnderRepresented"; // NOI18N

    /** showChromosomeName property name */
    public static final String PROP_SHOW_CHROMOSOME_NAME = "showChromosomeName"; // NOI18N

    /** showStartPosition property name */
    public static final String PROP_SHOW_START_POSITION = "showStartPosition"; // NOI18N

    /** showEndPosition property name */
    public static final String PROP_SHOW_END_POSITION = "showEndPosition"; // NOI18N

    /** nodeLabel property name */
    public static final String PROP_NODE_LABEL = "nodeLabel"; // NOI18N

    /** hideSimilarAnnotation property name */
    public static final String PROP_HIDE_SIMILAR_ANNOTATION = "hideSimilarAnnotation"; // NOI18N

    /** alignTerminalNodes property name */
    public static final String PROP_ALIGN_TERMINAL_NODES = "alignTerminalNodes"; // NOI18N

    /** showBranchLength property name */
    public static final String PROP_SHOW_BRANCH_LENGTH = "showBranchLength"; // NOI18N

    /** terminals boxed property name */
    public static final String PROP_TERMINALS_BOXED = "terminalsBoxed"; // NOI18N

    /** Non terminals boxed property name */
    public static final String PROP_NON_TERMINALS_BOXED = "nonTerminalsBoxed"; // NOI18N

    /** showPhysicallyAdjacent property name */
    public static final String PROP_SHOW_ADJACENT = "showPhysicallyAdjacent"; // NOI18N

    /** highlightingMode property name */
    public static final String PROP_HIGHLIGHTING_MODE = "highlightingMode"; // NOI18N

    /** termsCountSelected property name */
    public static final String PROP_TERMS_COUNT_SELECT = "termsCountSelected"; // NOI18N

    /** termsDensityInClusterSelected property name */
    public static final String PROP_DENSITY_IN_CLUSTER_SELECT = "termsDensityInClusterSelected"; // NOI18N

    /** termsDensityInPopulationSelected property name */
    public static final String PROP_DENSITY_IN_POPULATION_SELECT = "termsDensityInPopulationSelected"; // NOI18N

    /** termsRelativeDensitySelected property name */
    public static final String PROP_RELATIVE_DENSITY_SELECT = "termsRelativeDensitySelected"; // NOI18N

    /** statisticalCalculationSelected property name */
    public static final String PROP_STATISTICAL_CAL_SELECT = "statisticalCalculationSelected"; // NOI18N

    /** termsCount property name */
    public static final String PROP_TERMS_COUNT = "termsCount"; // NOI18N

    /** densityInCluster property name */
    public static final String PROP_DENSITY_IN_CLUSTER = "densityInCluster"; // NOI18N

    /** densityInPopulation property name */
    public static final String PROP_DENSITY_IN_POPULATION = "densityInPopulation"; // NOI18N

    /** relativeDensity property name */
    public static final String PROP_RELATIVE_DENSITY = "relativeDensity"; // NOI18N

    /** stat property name */
    public static final String PROP_STATISTICAL_CALCULATION = "stat"; // NOI18N

    /** method1Selected property name */
    public static final String PROP_METHOD1_SELECT = "method1Selected"; // NOI18N

    /** method2Selected property name */
    public static final String PROP_METHOD2_SELECT = "method2Selected"; // NOI18N

    /** binomialLawSelected property name */
    public static final String PROP_BINOMIAL_SELECT = "binomialLawSelected"; // NOI18N

    /** hypergeometricLawSelected property name */
    public static final String PROP_HYPERGEOMETRIC_SELECT = "hypergeometricLawSelected"; // NOI18N

    /** bonferonniCorrectionSelected property name */
    public static final String PROP_BONFERONNI_SELECT = "bonferonniCorrectionSelected"; // NOI18N

    /** sidakCorrectionSelected property name */
    public static final String PROP_SIDAK_SELECT = "sidakCorrectionSelected"; // NOI18N

    /** classifBaseSelected property name */
    public static final String PROP_CLASSIF_BASE_SELECT = "classifBaseSelected"; // NOI18N

    /** ontologyBaseSelected property name */
    public static final String PROP_ONTOLOGY_BASE_SELECT = "ontologyBaseSelected"; // NOI18N

    /** userSpecifiedBaseSelected property name */
    public static final String PROP_FILE_BASE_SELECT = "userSpecifiedBaseSelected"; // NOI18N

    /** specifiedBaseFileName property name */
    public static final String PROP_FILE_BASE = "specifiedBaseFileName"; // NOI18N

    /** ignoreUnknownSelected property name */
    public static final String PROP_IGNORE_UNKNOWN_SELECT = "ignoreUnknownSelected"; // NOI18N

    /** ignoreNotAnnotatedSelected property name */
    public static final String PROP_IGNORE_NOT_ANNOTATED_SELECT = "ignoreNotAnnotatedSelected"; // NOI18N

    /** minClusterSize property name */
    public static final String PROP_MIN_CLUSTER_SIZE = "minClusterSize"; // NOI18N

    /** minAssociatedGenes property name */
    public static final String PROP_MIN_ASSOC_GENES = "minAssociatedGenes"; // NOI18N

    /** nbrMaxDistance property name */
    public static final String PROP_NBR_MAX_DISTANCE = "nbrMaxDistance"; // NOI18N

    /** nbrMaxClusterSize property name */
    public static final String PROP_NBR_MAX_CLUSTER_SIZE = "nbrMaxClusterSize"; // NOI18N

    /** nbrPValue property name */
    public static final String PROP_P_VALUE = "nbrPValue"; // NOI18N

    /** nbrSeparateStrandSelected property name */
    public static final String PROP_SEPARATE_STRAND_SELECT = "nbrSeparateStrandSelected"; // NOI18N

    /** xpColorAutomaticSelected property name */
    public static final String PROP_XPCOLOR_AUTO_SELECT = "xpColorAutomaticSelected"; // NOI18N

    /** xpColorCustomizedSelected property name */
    public static final String PROP_XPCOLOR_CUSTOM_SELECT = "xpColorCustomizedSelected"; // NOI18N

    /** GBRColoringSelected property name */
    public static final String PROP_GBR_SELECT = "GBRColoringSelected"; // NOI18N

    /** BWYColoringSelected property name */
    public static final String PROP_BWY_SELECT = "BWYColoringSelected"; // NOI18N

    /** BWPColoringSelected property name */
    public static final String PROP_BWP_SELECT = "BWPColoringSelected"; // NOI18N

    /** slots property name */
    public static final String PROP_SLOTS = "slots"; // NOI18N

    /** showExpressionValues property name */
    public static final String PROP_SHOW_EXP_VALUES_SELECT = "showExpressionValues"; // NOI18N

    /** showAnnotation property name */
    public static final String PROP_SHOW_ANNOTATION = "showAnnotation"; // NOI18N

    /** currentDirectory property name */
    public static final String PROP_CURRENT_DIRECTORY = "currentDirectory"; // NOI18N

    /** paletteLowerBounds property name */
    public static final String PROP_PAL_LOWER = "paletteLowerBounds"; // NOI18N

    /** paletteColors property name */
    public static final String PROP_PAL_COLORS = "paletteColors"; // NOI18N

    /** accTable property name */
    public static final String PROP_ACC_TABLE = "accTable"; // NOI18N

    /** accessor property name */
    public static final String PROP_ACCESSOR = "accessor"; // NOI18N

    /** Zooming factor on X direction */
    private double zoomFactorX = 3;

    /** Zooming factor on Y direction */
    private double zoomFactorY = 10;

    /*
     * The following variables corresponds to Ontology Terms Properties Panel
     */

    /** Show Term ID property */
    private static boolean showTermID = false;

    /** Show Term name property */
    private static boolean showTermName = true;

    /** Show Over Represented Terms property */
    private static boolean showOverRepresented = true;

    /** Show Under Represented Term name property */
    private static boolean showUnderRepresented = false;

    /*
     * The following variables corresponds to Gene Product Labelling Panel
     */

    //    /** Show Chromosome Name property */
    //    private static boolean showChromosomeName = false;
    //
    //    /** Show Start Position property */
    //    private static boolean showStartPosition = false;
    //
    //    /** Show End Position property */
    //    private static boolean showEndPosition = false;
    /** The accessor for String values */
    private static String accessor;

    /** Displayed Label property */
    private static String nodeLabel = "Classification ID";

    /*
     * The following variables corresponds to Genes and Annotation's Parameters
     * Panel
     */

    /** Hide Annotation when similar to parent */
    private static boolean hideSimilarAnnotation = false;

    /** Align terminal nodes in classification viewer */
    private static boolean alignTerminalNodes = true;

    /** Show length of branches property */
    private static boolean showBranchLength = false;

    /** Surround terminal nodes property */
    private static boolean terminalsBoxed = false;

    /** Surround non terminal nodes property */
    private static boolean nonTerminalsBoxed = true;

    /** Show physically adjacent genes property */
    private static boolean showPhysicallyAdjacent = false;

    /*
     * The following variables corresponds to Highlighting Mode Panel
     */

    /** Highlighting mode property */
    private static int highlightingMode = 1;

    /*
     * The following variables corresponds to Score Calculation Panel
     */

    /** Ontology Terms Count Slection property */
    private static boolean termsCountSelected = false;

    /** Density Of Ontology Terms In Cluster property */
    private static boolean termsDensityInClusterSelected = false;

    /** Density Of Ontology Terms In Population property */
    private static boolean termsDensityInPopulationSelected = false;

    /** Relative Density Of Ontology Terms property */
    private static boolean termsRelativeDensitySelected = false;

    /** Statistical calculation property */
    private static boolean statisticalCalculationSelected = true;

    /** Terms count */
    private static double termsCount = 10;

    /** Density of terms in cluster */
    private static double densityInCluster = 1;

    /** Density of terms in pomulation */
    private static double densityInPopulation = 0.1;

    /** Relative density */
    private static double relativeDensity = 10;

    /** statistical calculation for statistical calculation */
    private static double stat = 1e-5;

    /*
     * The following variables corresponds to Statistical Calculation Panel
     */

    /** Method1 slection property */
    private static boolean method1Selected = true;

    /** Method2 slection property */
    private static boolean method2Selected = false;

    /** Binomial Law slection property */
    private static boolean binomialLawSelected = true;

    /** Hupergeometric law selection property */
    private static boolean hypergeometricLawSelected = false;

    /** Bonferonni correction selection property */
    private static boolean bonferonniCorrectionSelected = true;

    /** Sidak correction selection property */
    private static boolean sidakCorrectionSelected = false;

    /* Variables corresponding to Base Of Calculation panel */

    /** Gene Products in classification property */
    private static boolean classifBaseSelected = false;

    /** Gene Product in ontology */
    private static boolean ontologyBaseSelected = true;

    /** User specified base of calculation */
    private static boolean userSpecifiedBaseSelected = false;

    /** User specified base of calculation file name property */
    private static String specifiedBaseFileName = "";

    /* The following variables corresponds to Gene Product Selection panel */

    /** Ignore Gene Product annotated as Unknown property */
    private static boolean ignoreUnknownSelected = false;

    /** Ignore not annotated gene products property */
    private static boolean ignoreNotAnnotatedSelected = true;

    /* Variables for Cluster Selection panel */

    /** Minimum cluster size property */
    private static int minClusterSize = 2;

    /** Minimum associated genes */
    private static int minAssociatedGenes = 2;

    /* vars corresponding to Neighbouring Selection panel */

    /** Maximum distance between adjacents genes property */
    private static int nbrMaxDistance = 5;

    /** Maximul cluster size property */
    private static int nbrMaxClusterSize = 100;

    /** P-Value property */
    private static double nbrPValue = 1E-5;

    /* Groups panel fields */

    /** Separete groups located on different strands property */
    private static boolean nbrSeparateStrandSelected = false;

    /* Fields for: Expression values Tab */

    /** Automatic coloring for expression values */
    private static boolean xpColorAutomaticSelected = true;

    /** Customized coloring for expression values */
    private static boolean xpColorCustomizedSelected = false;

    /** Gree, Black, Red */
    private static boolean GBRColoringSelected = true;

    /** Blue, White, Yellow */
    private static boolean BWYColoringSelected = false;

    /** Blue ,White, Pink */
    private static boolean BWPColoringSelected = false;

    /** Number of slots for the palette used for coloring. */
    private static int slots = 9;

    /***/
    private static List paletteLowerBounds = new Vector();

    /***/
    private static List paletteColors = new Vector();

    /** Flag used to show/hide expression values. */
    private static boolean showExpressionValues = false;

    /*
     * does not apper yet in any GUI
     */

    /**
     * Flag to indicate if annotation should be displayed concurrently with the
     * label
     */
    private static boolean showAnnotation = false;

    /** The last used directory to open files. */
    private static String currentDirectory = System.getProperty("user.home");

    /** A table that contains StringValues and correspebdibg accessors */
    private Hashtable accTable = null;

    /** Last selected lastSelectedEvidences */
    private static String[] lastSelectedEvidences = null;

    /*
     * (non-Javadoc)
     * @see org.openide.options.SystemOption#displayName()
     */
    public String displayName() {
        return ""; // NOI18N;
    }

    protected void initialize() {
        super.initialize();

        // Initialize colors palette
        if ((slots == 9) && paletteColors.isEmpty()) {
            List colors = new Vector();
            colors.add(new Color((float) 0.0, (float) 1.0, (float) 0.0));
            colors.add(new Color((float) 0.0, (float) 0.75, (float) 0.0));
            colors.add(new Color((float) 0.0, (float) 0.5, (float) 0.0));
            colors.add(new Color((float) 0.0, (float) 0.25, (float) 0.0));
            colors.add(new Color((float) 0.0, (float) 0.0, (float) 0.0));
            colors.add(new Color((float) 0.25, (float) 0.0, (float) 0.0));
            colors.add(new Color((float) 0.5, (float) 0.0, (float) 0.0));
            colors.add(new Color((float) 0.75, (float) 0.0, (float) 0.0));
            colors.add(new Color((float) 1.0, (float) 0.0, (float) 0.0));
            setPaletteColors(colors);
        }

        // Initialize palette
        if ((slots == 9) && paletteLowerBounds.isEmpty()) {
            List lowerBounds = new Vector();
            lowerBounds.add(new Double(Double.NEGATIVE_INFINITY));
            lowerBounds.add(new Double(-5.0));
            lowerBounds.add(new Double(-3.0));
            lowerBounds.add(new Double(-2.0));
            lowerBounds.add(new Double(-1.0));
            lowerBounds.add(new Double(1.0));
            lowerBounds.add(new Double(2.0));
            lowerBounds.add(new Double(3.0));
            lowerBounds.add(new Double(5.0));
            setPaletteLowerBounds(lowerBounds);
        }

        accTable = null;
    }

    /**
     * Returns a java object that conatins options for the classifiocation
     * viewer customization.
     * @return Instance of CESettings class.
     */
    public static CESettings getInstance() {
        return ((CESettings) CESettings.findObject(CESettings.class, true));
    }

    /**
     * Returns the showTermID.
     * @return showTermID.
     */
    public boolean isShowTermID() {
        return showTermID;
    }

    /**
     * Setter for <i>showTermID </i> property.
     * @param showTermID The showTermID to set.
     */
    public void setShowTermID(boolean value) {
        if (showTermID == value) {
            return;
        }

        showTermID = value;
        firePropertyChange(PROP_SHOW_TERM_ID, (!showTermID) ? Boolean.TRUE
                : Boolean.FALSE, showTermID ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns the showTermName.
     * @return showTermName.
     */
    public boolean isShowTermName() {
        return showTermName;
    }

    /**
     * Setter for <i>showTermName </i> property.
     * @param showTermName The showTermName to set.
     */
    public void setShowTermName(boolean value) {
        if (showTermName == value) {
            return;
        }

        showTermName = value;
        firePropertyChange(PROP_SHOW_TERM_NAME, (!showTermName) ? Boolean.TRUE
                : Boolean.FALSE, showTermName ? Boolean.TRUE : Boolean.FALSE);
    }

    //    /**
    //     * Returns the showChromosomeName.
    //     * @return showChromosomeName.
    //     */
    //    public boolean isShowChromosomeName() {
    //        return showChromosomeName;
    //    }
    //
    //    /**
    //     * Setter for <i>showChromosomeName </i> property.
    //     * @param showChromosomeName The showChromosomeName to set.
    //     */
    //    public void setShowChromosomeName(boolean value) {
    //        if (showChromosomeName == value)
    //            return;
    //        showChromosomeName = value;
    //        firePropertyChange(PROP_SHOW_CHROMOSOME_NAME,
    //                !showChromosomeName ? Boolean.TRUE : Boolean.FALSE,
    //                showChromosomeName ? Boolean.TRUE : Boolean.FALSE);
    //    }
    //
    //    /**
    //     * Returns the showEndPosition.
    //     * @return showEndPosition.
    //     */
    //    public boolean isShowEndPosition() {
    //        return showEndPosition;
    //    }
    //
    //    /**
    //     * Setter for <i>showEndPosition </i> property.
    //     * @param value The showEndPosition to set.
    //     */
    //    public void setShowEndPosition(boolean value) {
    //        if (showEndPosition == value)
    //            return;
    //        showEndPosition = value;
    //        firePropertyChange(PROP_SHOW_END_POSITION,
    //                !showEndPosition ? Boolean.TRUE : Boolean.FALSE,
    //                showEndPosition ? Boolean.TRUE : Boolean.FALSE);
    //    }

    /**
     * Returns the showOverRepresented.
     * @return showOverRepresented.
     */
    public boolean isShowOverRepresented() {
        return showOverRepresented;
    }

    /**
     * Setter for showOverRepresented property.
     * @param value The showOverRepresented to set.
     */
    public void setShowOverRepresented(boolean value) {
        if (showOverRepresented == value) {
            return;
        }

        showOverRepresented = value;
        firePropertyChange(PROP_SHOW_OVER_REPRESENTED,
                (!showOverRepresented) ? Boolean.TRUE : Boolean.FALSE,
                showOverRepresented ? Boolean.TRUE : Boolean.FALSE);
    }

    //    /**
    //     * Returns the showStartPosition.
    //     * @return showStartPosition.
    //     */
    //    public boolean isShowStartPosition() {
    //        return showStartPosition;
    //    }
    //
    //    /**
    //     * @param value The showStartPosition to set.
    //     */
    //    public void setShowStartPosition(boolean value) {
    //        if (showStartPosition == value)
    //            return;
    //        showStartPosition = value;
    //        firePropertyChange(PROP_SHOW_START_POSITION,
    //                !showStartPosition ? Boolean.TRUE : Boolean.FALSE,
    //                showStartPosition ? Boolean.TRUE : Boolean.FALSE);
    //    }

    /**
     * @return Returns the showUnderRepresented.
     */
    public boolean isShowUnderRepresented() {
        return showUnderRepresented;
    }

    /**
     * @param showUnderRepresented The showUnderRepresented to set.
     */
    public void setShowUnderRepresented(boolean value) {
        if (showUnderRepresented == value) {
            return;
        }

        showUnderRepresented = value;
        firePropertyChange(PROP_SHOW_UNDER_REPRESENTED,
                (!showUnderRepresented) ? Boolean.TRUE : Boolean.FALSE,
                showUnderRepresented ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns the nodeLabel
     */
    public String getNodeLabel() {
        return nodeLabel;
    }

    /**
     * Setter for nodeLabel
     */
    public void setNodeLabel(String value) {
        value = (value == null) ? "" : value;

        String oldNodeLabel = getNodeLabel();

        if (!value.equals(oldNodeLabel)) {
            nodeLabel = value;
            firePropertyChange(PROP_NODE_LABEL, oldNodeLabel, value);
        }
    }

    /**
     * Returns hideSimilarAnnotation
     */
    public boolean isHideSimilarAnnotation() {
        return hideSimilarAnnotation;
    }

    /**
     * Setter for hideSimilarAnnotation
     */
    public void setHideSimilarAnnotation(boolean value) {
        if (hideSimilarAnnotation == value) {
            return;
        }

        hideSimilarAnnotation = value;
        firePropertyChange(PROP_HIDE_SIMILAR_ANNOTATION,
                (!hideSimilarAnnotation) ? Boolean.TRUE : Boolean.FALSE,
                hideSimilarAnnotation ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns alignTerminalNodes
     */
    public boolean isAlignTerminalNodes() {
        return alignTerminalNodes;
    }

    /**
     * Setter for alignTerminalNodes
     */
    public void setAlignTerminalNodes(boolean value) {
        if (alignTerminalNodes == value) {
            return;
        }

        alignTerminalNodes = value;
        firePropertyChange(PROP_ALIGN_TERMINAL_NODES,
                (!alignTerminalNodes) ? Boolean.TRUE : Boolean.FALSE,
                alignTerminalNodes ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns nonTerminalsBoxed
     */
    public boolean isNonTerminalsBoxed() {
        return nonTerminalsBoxed;
    }

    /**
     * Setter for nonTerminalsBoxed
     */
    public void setNonTerminalsBoxed(boolean value) {
        if (nonTerminalsBoxed == value) {
            return;
        }
        nonTerminalsBoxed = value;
        firePropertyChange(PROP_NON_TERMINALS_BOXED,
                (!nonTerminalsBoxed) ? Boolean.TRUE : Boolean.FALSE,
                nonTerminalsBoxed ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns terminalsBoxed
     */
    public boolean isTerminalsBoxed() {
        return terminalsBoxed;
    }

    /**
     * Setter for terminalsBoxed
     */
    public void setTerminalsBoxed(boolean value) {
        if (terminalsBoxed == value) {
            return;
        }
        terminalsBoxed = value;
        firePropertyChange(PROP_TERMINALS_BOXED,
                (!terminalsBoxed) ? Boolean.TRUE : Boolean.FALSE,
                terminalsBoxed ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns showBranchLength
     */
    public boolean isShowBranchLength() {
        return showBranchLength;
    }

    /**
     * Setter for showBranchLength
     */
    public void setShowBranchLength(boolean value) {
        if (showBranchLength == value) {
            return;
        }
        showBranchLength = value;
        firePropertyChange(PROP_SHOW_BRANCH_LENGTH,
                (!showBranchLength) ? Boolean.TRUE : Boolean.FALSE,
                showBranchLength ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns showPhysicallyAdjacent
     */
    public boolean isShowPhysicallyAdjacent() {
        return showPhysicallyAdjacent;
    }

    /**
     * Setter for showPhysicallyAdjacent
     */
    public void setShowPhysicallyAdjacent(boolean value) {
        if (showPhysicallyAdjacent == value) {
            return;
        }

        showPhysicallyAdjacent = value;
        firePropertyChange(PROP_SHOW_ADJACENT,
                (!showPhysicallyAdjacent) ? Boolean.TRUE : Boolean.FALSE,
                showPhysicallyAdjacent ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns highlightingMode property
     */
    public int getHighlightingMode() {
        return highlightingMode;
    }

    /**
     * Setter for highlightingMode
     */
    public void setHighlightingMode(int value) {
        int oldValue = highlightingMode;
        highlightingMode = value;
        firePropertyChange(PROP_HIGHLIGHTING_MODE, new Integer(oldValue),
                new Integer(value));
    }

    /**
     * Returns statisticalCalculationSelected property
     */
    public boolean isStatisticalCalculationSelected() {
        return statisticalCalculationSelected;
    }

    /**
     * Setter for statisticalCalculationSelected
     */
    public void setStatisticalCalculationSelected(boolean value) {
        if (statisticalCalculationSelected == value) {
            return;
        }

        statisticalCalculationSelected = value;
        firePropertyChange(PROP_STATISTICAL_CAL_SELECT,
                (!statisticalCalculationSelected) ? Boolean.TRUE
                        : Boolean.FALSE,
                statisticalCalculationSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns termsCountSelected property
     */
    public boolean isTermsCountSelected() {
        return termsCountSelected;
    }

    /**
     * Setter for termsCountSelected
     */
    public void setTermsCountSelected(boolean value) {
        if (termsCountSelected == value) {
            return;
        }

        termsCountSelected = value;
        firePropertyChange(PROP_TERMS_COUNT_SELECT,
                (!termsCountSelected) ? Boolean.TRUE : Boolean.FALSE,
                termsCountSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * returns termsDensityInClusterSelected property
     */
    public boolean isTermsDensityInClusterSelected() {
        return termsDensityInClusterSelected;
    }

    /**
     * Setter for termsDensityInClusterSelected
     */
    public void setTermsDensityInClusterSelected(boolean value) {
        if (termsDensityInClusterSelected == value) {
            return;
        }

        termsDensityInClusterSelected = value;
        firePropertyChange(
                PROP_DENSITY_IN_CLUSTER_SELECT,
                (!termsDensityInClusterSelected) ? Boolean.TRUE : Boolean.FALSE,
                termsDensityInClusterSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * returns termsDensityInPopulationSelected property
     */
    public boolean isTermsDensityInPopulationSelected() {
        return termsDensityInPopulationSelected;
    }

    /**
     * Setter for termsDensityInPopulationSelected
     */
    public void setTermsDensityInPopulationSelected(boolean value) {
        if (termsDensityInPopulationSelected == value) {
            return;
        }

        termsDensityInPopulationSelected = value;
        firePropertyChange(PROP_DENSITY_IN_POPULATION_SELECT,
                (!termsDensityInPopulationSelected) ? Boolean.TRUE
                        : Boolean.FALSE,
                termsDensityInPopulationSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns termsRelativeDensitySelected property
     */
    public boolean isTermsRelativeDensitySelected() {
        return termsRelativeDensitySelected;
    }

    /**
     * Setter for termsRelativeDensitySelected
     */
    public void setTermsRelativeDensitySelected(boolean value) {
        if (termsRelativeDensitySelected == value) {
            return;
        }

        termsRelativeDensitySelected = value;
        firePropertyChange(PROP_RELATIVE_DENSITY_SELECT,
                (!termsRelativeDensitySelected) ? Boolean.TRUE : Boolean.FALSE,
                termsRelativeDensitySelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns densityInCluster
     */
    public double getDensityInCluster() {
        return densityInCluster;
    }

    /**
     * Setter for densityInCluster
     */
    public void setDensityInCluster(double value) {
        double oldValue = densityInCluster;
        densityInCluster = value;
        firePropertyChange(PROP_DENSITY_IN_CLUSTER, new Double(oldValue),
                new Double(value));
    }

    /**
     * Returns densityInPopulation property
     */
    public double getDensityInPopulation() {
        return densityInPopulation;
    }

    /**
     * Setter for densityInPopulation
     */
    public void setDensityInPopulation(double value) {
        double oldValue = densityInPopulation;
        densityInPopulation = value;
        firePropertyChange(PROP_DENSITY_IN_POPULATION, new Double(oldValue),
                new Double(value));
    }

    /**
     * Returns relativeDensity
     */
    public double getRelativeDensity() {
        return relativeDensity;
    }

    /**
     * Setter for relativeDensity
     */
    public void setRelativeDensity(double value) {
        double oldValue = relativeDensity;
        relativeDensity = value;
        firePropertyChange(PROP_RELATIVE_DENSITY, new Double(oldValue),
                new Double(value));
    }

    /**
     * Returns stat
     */
    public double getStat() {
        return stat;
    }

    /**
     * Setter for stat
     */
    public void setStat(double value) {
        double oldValue = stat;
        stat = value;
        firePropertyChange(PROP_STATISTICAL_CALCULATION, new Double(oldValue),
                new Double(value));
    }

    /**
     * Returns termsCount
     */
    public double getTermsCount() {
        return termsCount;
    }

    /**
     * Setter for termsCount
     */
    public void setTermsCount(double value) {
        double oldValue = termsCount;
        termsCount = value;
        firePropertyChange(PROP_TERMS_COUNT, new Double(oldValue), new Double(
                value));
    }

    /**
     * Returns binomialLawSelected property
     */
    public boolean isBinomialLawSelected() {
        return binomialLawSelected;
    }

    /**
     * Setter for binomialLawSelected
     */
    public void setBinomialLawSelected(boolean value) {
        if (binomialLawSelected == value) {
            return;
        }

        binomialLawSelected = value;
        firePropertyChange(PROP_BINOMIAL_SELECT,
                (!binomialLawSelected) ? Boolean.TRUE : Boolean.FALSE,
                binomialLawSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns bonferonniCorrectionSelected property
     */
    public boolean isBonferonniCorrectionSelected() {
        return bonferonniCorrectionSelected;
    }

    /**
     * Setter for bonferonniCorrectionSelected
     */
    public void setBonferonniCorrectionSelected(boolean value) {
        if (bonferonniCorrectionSelected == value) {
            return;
        }

        bonferonniCorrectionSelected = value;
        firePropertyChange(PROP_BONFERONNI_SELECT,
                (!bonferonniCorrectionSelected) ? Boolean.TRUE : Boolean.FALSE,
                bonferonniCorrectionSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns hypergeometricLawSelected property
     */
    public boolean isHypergeometricLawSelected() {
        return hypergeometricLawSelected;
    }

    /**
     * Setter for hypergeometricLawSelected
     */
    public void setHypergeometricLawSelected(boolean value) {
        if (hypergeometricLawSelected == value) {
            return;
        }

        hypergeometricLawSelected = value;
        firePropertyChange(PROP_HYPERGEOMETRIC_SELECT,
                (!hypergeometricLawSelected) ? Boolean.TRUE : Boolean.FALSE,
                hypergeometricLawSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns method1Selected property
     */
    public boolean isMethod1Selected() {
        return method1Selected;
    }

    /**
     * Setter for method1Selected
     */
    public void setMethod1Selected(boolean value) {
        if (method1Selected == value) {
            return;
        }

        method1Selected = value;
        firePropertyChange(PROP_METHOD1_SELECT,
                (!method1Selected) ? Boolean.TRUE : Boolean.FALSE,
                method1Selected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns method2Selected property
     */
    public boolean isMethod2Selected() {
        return method2Selected;
    }

    /**
     * Setter for method2Selected
     */
    public void setMethod2Selected(boolean value) {
        if (method2Selected == value) {
            return;
        }

        method2Selected = value;
        firePropertyChange(PROP_METHOD2_SELECT,
                (!method2Selected) ? Boolean.TRUE : Boolean.FALSE,
                method2Selected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns sidakCorrectionSelected property
     */
    public boolean isSidakCorrectionSelected() {
        return sidakCorrectionSelected;
    }

    /**
     * Setter for sidakCorrectionSelected property
     */
    public void setSidakCorrectionSelected(boolean value) {
        if (sidakCorrectionSelected == value) {
            return;
        }

        sidakCorrectionSelected = value;
        firePropertyChange(PROP_SIDAK_SELECT,
                (!sidakCorrectionSelected) ? Boolean.TRUE : Boolean.FALSE,
                sidakCorrectionSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns classifBaseSelected property
     */
    public boolean isClassifBaseSelected() {
        return classifBaseSelected;
    }

    /**
     * Setter for classifBaseSelected
     */
    public void setClassifBaseSelected(boolean value) {
        if (classifBaseSelected == value) {
            return;
        }

        classifBaseSelected = value;
        firePropertyChange(PROP_CLASSIF_BASE_SELECT,
                (!classifBaseSelected) ? Boolean.TRUE : Boolean.FALSE,
                classifBaseSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns ontologyBaseSelected property
     */
    public boolean isOntologyBaseSelected() {
        return ontologyBaseSelected;
    }

    /**
     * Setter for ontologyBaseSelected
     */
    public void setOntologyBaseSelected(boolean value) {
        if (ontologyBaseSelected == value) {
            return;
        }

        ontologyBaseSelected = value;
        firePropertyChange(PROP_ONTOLOGY_BASE_SELECT,
                (!ontologyBaseSelected) ? Boolean.TRUE : Boolean.FALSE,
                ontologyBaseSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns specifiedBaseFileName property
     */
    public String getSpecifiedBaseFileName() {
        return specifiedBaseFileName;
    }

    /**
     * Setter for specifiedBaseFileName
     */
    public void setSpecifiedBaseFileName(String value) {
        value = (value == null) ? "" : value;

        String oldFileName = getSpecifiedBaseFileName();

        if (!value.equals(oldFileName)) {
            specifiedBaseFileName = value;
            firePropertyChange(PROP_FILE_BASE, oldFileName, value);
        }
    }

    /**
     * Returns userSpecifiedBaseSelected property
     */
    public boolean isUserSpecifiedBaseSelected() {
        return userSpecifiedBaseSelected;
    }

    /**
     * Setter for userSpecifiedBaseSelected
     */
    public void setUserSpecifiedBaseSelected(boolean value) {
        if (userSpecifiedBaseSelected == value) {
            return;
        }

        userSpecifiedBaseSelected = value;
        firePropertyChange(PROP_FILE_BASE_SELECT,
                (!userSpecifiedBaseSelected) ? Boolean.TRUE : Boolean.FALSE,
                userSpecifiedBaseSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns ignoreNotAnnotatedSelected property
     */
    public boolean isIgnoreNotAnnotatedSelected() {
        return ignoreNotAnnotatedSelected;
    }

    /**
     * Setter for ignoreNotAnnotatedSelected
     */
    public void setIgnoreNotAnnotatedSelected(boolean value) {
        if (ignoreNotAnnotatedSelected == value) {
            return;
        }

        ignoreNotAnnotatedSelected = value;
        firePropertyChange(PROP_IGNORE_NOT_ANNOTATED_SELECT,
                (!ignoreNotAnnotatedSelected) ? Boolean.TRUE : Boolean.FALSE,
                ignoreNotAnnotatedSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns ignoreUnknownSelected
     */
    public boolean isIgnoreUnknownSelected() {
        return ignoreUnknownSelected;
    }

    /**
     * Setter for ignoreUnknownSelected
     */
    public void setIgnoreUnknownSelected(boolean value) {
        if (ignoreUnknownSelected == value) {
            return;
        }

        ignoreUnknownSelected = value;
        firePropertyChange(PROP_IGNORE_UNKNOWN_SELECT,
                (!ignoreUnknownSelected) ? Boolean.TRUE : Boolean.FALSE,
                ignoreUnknownSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns minAssociatedGenes property
     */
    public int getMinAssociatedGenes() {
        return minAssociatedGenes;
    }

    /**
     * Setter for minAssociatedGenes
     */
    public void setMinAssociatedGenes(int value) {
        int oldValue = minAssociatedGenes;
        minAssociatedGenes = value;
        firePropertyChange(PROP_MIN_ASSOC_GENES, new Integer(oldValue),
                new Integer(value));
    }

    /**
     * Returns minClusterSize property
     */
    public int getMinClusterSize() {
        return minClusterSize;
    }

    /**
     * Setter for minClusterSize
     */
    public void setMinClusterSize(int value) {
        int oldValue = minClusterSize;
        minClusterSize = value;
        firePropertyChange(PROP_MIN_CLUSTER_SIZE, new Integer(oldValue),
                new Integer(value));
    }

    /**
     * Returns nbrMaxDistance property
     */
    public int getNbrMaxDistance() {
        return nbrMaxDistance;
    }

    /**
     * Setter for nbrMaxDistance
     */
    public void setNbrMaxDistance(int value) {
        int oldValue = nbrMaxDistance;
        nbrMaxDistance = value;
        firePropertyChange(PROP_NBR_MAX_DISTANCE, new Integer(oldValue),
                new Integer(value));
    }

    /**
     * Returns nbrPValue property
     */
    public double getNbrPValue() {
        return nbrPValue;
    }

    /**
     * Setter for nbrPValue
     */
    public void setNbrPValue(double value) {
        double oldValue = nbrPValue;
        nbrPValue = value;
        firePropertyChange(PROP_P_VALUE, new Double(oldValue),
                new Double(value));
    }

    /**
     * Returns nbrSeparateStrandSelected property
     */
    public boolean isNbrSeparateStrandSelected() {
        return nbrSeparateStrandSelected;
    }

    /**
     * Setter for nbrSeparateStrandSelected
     */
    public void setNbrSeparateStrandSelected(boolean value) {
        if (nbrSeparateStrandSelected == value) {
            return;
        }

        nbrSeparateStrandSelected = value;
        firePropertyChange(PROP_SEPARATE_STRAND_SELECT,
                (!nbrSeparateStrandSelected) ? Boolean.TRUE : Boolean.FALSE,
                nbrSeparateStrandSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns nbrMaxClusterSize property
     */
    public int getNbrMaxClusterSize() {
        return nbrMaxClusterSize;
    }

    /**
     * Setter for nbrMaxClusterSize
     */
    public void setNbrMaxClusterSize(int value) {
        int oldValue = nbrMaxClusterSize;
        nbrMaxClusterSize = value;
        firePropertyChange(PROP_NBR_MAX_CLUSTER_SIZE, new Integer(oldValue),
                new Integer(value));
    }

    /**
     * Returns BWPColoringSelected
     */
    public boolean isBWPColoringSelected() {
        return BWPColoringSelected;
    }

    /**
     * Setter for BWPColoringSelected
     */
    public void setBWPColoringSelected(boolean value) {
        if (BWPColoringSelected == value) {
            return;
        }

        BWPColoringSelected = value;
        firePropertyChange(PROP_BWP_SELECT,
                (!BWPColoringSelected) ? Boolean.TRUE : Boolean.FALSE,
                BWPColoringSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns BWYColoringSelected
     */
    public boolean isBWYColoringSelected() {
        return BWYColoringSelected;
    }

    /**
     * Setter for BWYColoringSelected
     */
    public void setBWYColoringSelected(boolean value) {
        if (BWYColoringSelected == value) {
            return;
        }

        BWYColoringSelected = value;
        firePropertyChange(PROP_BWY_SELECT,
                (!BWYColoringSelected) ? Boolean.TRUE : Boolean.FALSE,
                BWYColoringSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns GBRColoringSelected
     */
    public boolean isGBRColoringSelected() {
        return GBRColoringSelected;
    }

    /**
     * Setter for GBRColoringSelected
     */
    public void setGBRColoringSelected(boolean value) {
        if (GBRColoringSelected == value) {
            return;
        }

        GBRColoringSelected = value;
        firePropertyChange(PROP_GBR_SELECT,
                (!GBRColoringSelected) ? Boolean.TRUE : Boolean.FALSE,
                GBRColoringSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns showExpressionValues
     */
    public boolean isShowExpressionValues() {
        return showExpressionValues;
    }

    /**
     * Setter for showExpressionValues
     */
    public void setShowExpressionValues(boolean value) {
        if (showExpressionValues == value) {
            return;
        }

        showExpressionValues = value;
        firePropertyChange(PROP_SHOW_EXP_VALUES_SELECT,
                (!showExpressionValues) ? Boolean.TRUE : Boolean.FALSE,
                showExpressionValues ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns slots
     */
    public int getSlots() {
        return slots;
    }

    /**
     * Setter for slots
     */
    public void setSlots(int value) {
        int oldValue = slots;
        slots = value;
        firePropertyChange(PROP_SLOTS, new Integer(oldValue),
                new Integer(value));
    }

    /**
     * Returns xpColorAutomaticSelected
     */
    public boolean isXpColorAutomaticSelected() {
        return xpColorAutomaticSelected;
    }

    /**
     * Setter for xpColorAutomaticSelected
     */
    public void setXpColorAutomaticSelected(boolean value) {
        if (xpColorAutomaticSelected == value) {
            return;
        }

        xpColorAutomaticSelected = value;
        firePropertyChange(PROP_XPCOLOR_AUTO_SELECT,
                (!xpColorAutomaticSelected) ? Boolean.TRUE : Boolean.FALSE,
                xpColorAutomaticSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns xpColorCustomizedSelected
     */
    public boolean isXpColorCustomizedSelected() {
        return xpColorCustomizedSelected;
    }

    /**
     * Setter for xpColorCustomizedSelected
     */
    public void setXpColorCustomizedSelected(boolean value) {
        if (xpColorCustomizedSelected == value) {
            return;
        }

        xpColorCustomizedSelected = value;
        firePropertyChange(PROP_XPCOLOR_CUSTOM_SELECT,
                (!xpColorCustomizedSelected) ? Boolean.TRUE : Boolean.FALSE,
                xpColorCustomizedSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns showAnnotation
     */
    public boolean isShowAnnotation() {
        return showAnnotation;
    }

    /**
     * Setter for showAnnotation
     */
    public void setShowAnnotation(boolean value) {
        if (showAnnotation == value) {
            return;
        }

        showAnnotation = value;
        firePropertyChange(PROP_SHOW_ANNOTATION,
                (!showAnnotation) ? Boolean.TRUE : Boolean.FALSE,
                showAnnotation ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns the last used directory path as a String
     */
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Setter for currentDirectory
     */
    public void setCurrentDirectory(String value) {
        value = (value == null) ? "" : value;

        String old = getCurrentDirectory();

        if (!value.equals(old)) {
            currentDirectory = value;
            firePropertyChange(PROP_CURRENT_DIRECTORY, old, value);
        }
    }

    /**
     * Returns paletteColors
     */
    public List getPaletteColors() {
        return paletteColors;
    }

    /**
     * Setter for paletteColors property
     */
    public void setPaletteColors(List value) {
        List old = paletteColors;
        paletteColors = value;
        firePropertyChange(PROP_PAL_COLORS, old, value);
    }

    /**
     * Returns paletteLowerBounds
     */
    public List getPaletteLowerBounds() {
        return paletteLowerBounds;
    }

    /**
     * Setter for paletteLowerBounds
     */
    public void setPaletteLowerBounds(List value) {
        List old = paletteLowerBounds;
        paletteLowerBounds = value;
        firePropertyChange(PROP_PAL_LOWER, old, value);
    }

    /**
     * @return Returns the accTable.
     */
    public Hashtable getAccTable() {
        return accTable;
    }

    /**
     * @param value The accTable to set.
     */
    public void setAccTable(Hashtable value) {
        Hashtable oldValue = accTable;
        accTable = value;
        firePropertyChange(PROP_ACC_TABLE, oldValue, value);
    }

    /**
     * @return Returns the accessor.
     */
    public String getAccessor() {
        return accessor;
    }

    /**
     * @param accessor The accessor to set.
     */
    public void setAccessor(String value) {
        value = (value == null) ? "" : value;

        String old = getAccessor();

        if (!value.equals(old)) {
            accessor = value;
            firePropertyChange(PROP_ACCESSOR, old, value);
        }
    }

    public double getZoomFactorX() {
        return zoomFactorX;
    }

    public void setZoomFactorX(double value) {
        double oldValue = zoomFactorX;
        zoomFactorX = value;
        firePropertyChange(ZOOM_FACTOR_X, new Double(oldValue), new Double(
                value));
    }

    public double getZoomFactorY() {
        return zoomFactorY;
    }

    public void setZoomFactorY(double value) {
        double oldValue = zoomFactorY;
        zoomFactorY = value;
        firePropertyChange(ZOOM_FACTOR_Y, new Double(oldValue), new Double(
                value));
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
}