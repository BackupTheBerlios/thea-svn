package fr.unice.bioinfo.allonto.datamodel;

/**
 * A factory to manipulate resources
 */
public interface ResourceFactory {
    /**
     * Retrieve of create a property with a given key
     * @param key the property to be accessed
     * @return the property corresponding to the key
     */
    public Property getProperty(String key);
    /**
     * Retrieve of create a resource with a given key
     * @param key the reource to be accessed
     * @return the resource corresponding to the key
     */
    public Resource getResource(String key);
}
