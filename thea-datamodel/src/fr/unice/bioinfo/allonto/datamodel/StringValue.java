package fr.unice.bioinfo.allonto.datamodel;

/**
 * A Node representing a String
 */
public class StringValue extends Node {
    private  String value;

    /** Class constructor
     */
    public StringValue() {
	value = new String();
    }

    /**
     * Class constructor with the value of the string passed in parameter
     * @param value the string value of the resource
     */
    public StringValue(String value) {
	this.value = value;
    }

    /** Gets the content of this string value
     * @return the value of the string
     */    
    public String getValue() {
	return value;
    }

    /** Sets the content of this string value
     * @param value the value of the string
     */    
    public void setValue(String value) {
	this.value = value;
    }

    /** Implementation of equals method for instances of StringValue.
     * @param obj the reference object with which to compare.
     * @return true if this object contains the same string that the obj argument; false otherwise.
     */    
    public boolean equals(Object obj) {
	if (this == obj) return true;
	if (!(obj instanceof StringValue)) return false;
	return this.getValue().equals(((StringValue)obj).getValue());
    }

    /** Returns a hash code value for the object
     * @return a hash code value for this object
     */
    public int hashCode() {
	return value.hashCode();
    }

    /** Gets the textual representation of this string value
     * @return the string used to identify the string value in a text display
     */    
    public String toString() {
	return super.toString()+"_"+value;
    }
}
