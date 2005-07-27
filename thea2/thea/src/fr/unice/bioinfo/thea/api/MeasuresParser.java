package fr.unice.bioinfo.thea.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface MeasuresParser {
    /**
     * Parses a file containing measures for a given classification.
     * @param file The measures's source files.
     * @return A map measures.
     * @throws FileNotFoundException Exception if the given file doesn't exists.
     */
    public Map getMeasures(File file) throws FileNotFoundException;
}