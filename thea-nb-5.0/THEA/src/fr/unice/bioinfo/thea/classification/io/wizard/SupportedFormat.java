package fr.unice.bioinfo.thea.classification.io.wizard;

/**
 * <p>
 * All constants used in the wizard.
 * </p>
 * <p>
 * This utility class defines supported files format and a description of each
 * format.
 * </p>
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class SupportedFormat {

    /** Unclustered format property. */
    public static String UNCLUSTERED = "Unclustered";// NOI18N

    /** Unclustered format description. */
    public static String UNCLUSTERED_DESCRIPTION = "Only the tabular file is considered";// NOI18N

    /** Eisen format description. */
    public static String EISEN = "Eisen";// NOI18N

    /** Eisen format property. */
    public static String EISEN_DESCRIPTION = "Both clustered and tabular files required";// NOI18N

    /** Sota format property. */
    public static String SOTA = "Sota";// NOI18N

    /** Sota format description. */
    public static String SOTA_DESCRIPTION = "Sota";// NOI18N

    /** Newick format property. */
    public static String NEWICK = "Newick";// NOI18N

    /** Newick format description. */
    public static String NEWICK_DESCRIPTION = "Newick";// NOI18N

    /** Returns supported files format. */
    public static String[] getExtensions() {
        return new String[] { UNCLUSTERED, EISEN, SOTA, NEWICK };
    }

    /** Returns a description for each supported file format. */
    public static String[] getDescriptions() {
        return new String[] { UNCLUSTERED_DESCRIPTION, EISEN_DESCRIPTION,
                SOTA_DESCRIPTION, NEWICK_DESCRIPTION };
    }
}