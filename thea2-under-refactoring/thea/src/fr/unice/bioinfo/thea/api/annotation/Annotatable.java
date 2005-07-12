package fr.unice.bioinfo.thea.api.annotation;

/**
 * <p>
 * Defines services to be provided by annotatable components.
 * <p>
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface Annotatable {
    /**
     * annotates this objects using a list of evidences codes.
     * @param evidences Evidences codes.
     */
    public void annotate(final String[] evidences);
}