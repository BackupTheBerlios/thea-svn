package fr.unice.bioinfo.allonto.datamodel;

/**
 * The superclass of all objects modelized in Allonto
 * @author Claude Pasquier
 */
public class Node {
    private int id;

    /** Class constructor */    
    public Node() {
	id = 0;
    }

    /** Gets the unique Id of the node
     * @return the Id of the node
     */    
    public int getId() {
	return id;
    }

    /** Sets the node Id
     * @param id the Id of the node
     */    
    public void setId(int id) {
	this.id = id;
    }


    /** Implementation of equals method for instances of Node. Equality is tested only be comparing id of both objects
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */    
    public boolean equals(Object obj) {
    	if (this == obj) return true;
    	if (!(obj instanceof Node)) return false;
    	if ((this.getId() == 0) && (((Node)obj).getId() == 0)) return false;
    	if (this.getId() == ((Node)obj).getId()) return true;
    	return super.equals(obj);
    }
    
    /** Returns a hash code value for the object
     * @return a hash code value for this object
     */
    public int hashCode() {
    	return (id == 0 ? super.hashCode() : id);
    }

    /** Gets the textual representation of this node
     * @return the string used to identify the node in a text display
     */    
    public String toString() {
	return "#"+id;
    }
	
}
