package fr.unice.bioinfo.allonto.datamodel;


/**
 * A special kind of resource representing a context
 */
public class Context extends Resource {

    /**
     * Constructor (cannot be used)
     */
    public Context() {
    }

    /** Class constructor with the accessor key given as parameter
     * @param acc The accessor key for the resource
     */
    public Context(String acc) {
	super(acc);
    }

    /** Class constructor with the resource type given as parameter
     * @param resType The resource type
     */
    public Context(Resource resourceClass) {
	super(resourceClass);
    }

    /**
     * Class constructor with an accessor key and a resource type given as parameter
     * @param acc The accessor key for the context
     * @param resType The resource type
     */
    public Context(String acc, Resource resourceClass) {
	super(acc, resourceClass);
    }
}
