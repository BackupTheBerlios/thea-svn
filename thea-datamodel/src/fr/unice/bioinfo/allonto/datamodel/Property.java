package fr.unice.bioinfo.allonto.datamodel;

/**
 * A resource representing a property
 */
public class Property extends Resource {

//     private Property inverse;

    /**
     * Class constructor
     */
    public Property() {
	super();
    }

    /** Class constructor with the accessor key given as parameter
     * @param key the accessor key for the property
     */
    public Property(String key) {
	super(key);
    }

    /** Gets the inverse of the property
     * @return the property which is the inverse of the current one
     */    
    public Property getInverse() {
	return (Property)getTarget(resourceFactory.getProperty("INVERSE"));
    }

    /** Sets the inverse of this resource.
     * @param property the property which is the inverse of the current one
     */    
    public void setInverse(Property property) {
	setTarget(resourceFactory.getProperty("INVERSE"), property);
    }

}
