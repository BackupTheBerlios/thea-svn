package fr.unice.bioinfo.thea.api;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class defines services to be provided by all parsers used to read a
 * classification. <br>
 * In the classification editor, we need a root node to ba able to build a tree
 * view if the classification. Hence, on of services provided by this interface
 * is the one that returns the root node of a classification.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface ClassificationParser {
    
    /** Returns the root node of the classification tree. 
     * @param file The classification file.
     * @throws FileNotFoundException If the given file doesn't exists.*/
    public Object getRootNode(File file) throws FileNotFoundException;
    
}