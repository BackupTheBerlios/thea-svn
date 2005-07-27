package fr.unice.bioinfo.thea.classification.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import fr.unice.bioinfo.thea.api.ClassificationParser;

/**
 * An utility class that provides a parser for files in the eisen format.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 * @author Claude Pasquier.
 */
public class EisenParser implements ClassificationParser {

    private BufferedReader buffer = null;

    private EisenParser() {
    }

    /** Returns a parser for files in the sota format. */
    public static ClassificationParser getDefault() {
        return (new EisenParser());
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.api.ClassificationParser#getRootNode(java.io.File)
     */
    public Object getRootNode(File file) throws FileNotFoundException {
        buffer = new BufferedReader(new FileReader(file));
        return null;
    }

}