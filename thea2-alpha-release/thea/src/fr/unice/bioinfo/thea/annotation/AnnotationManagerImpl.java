package fr.unice.bioinfo.thea.annotation;

import fr.unice.bioinfo.thea.classification.Node;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class AnnotationManagerImpl {

    /** The root node of the classification. */
    private Node classificationRootNode;

    public AnnotationManagerImpl(Node classificationRootNode) {
        this.classificationRootNode = classificationRootNode;
    }

}