package fr.unice.bioinfo.allonto.datamodel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;

/**
 * a context dependant link to other Nodes
 * @author Claude Pasquier
 */
public class ContextSwitch extends Resource {
    private static Log log = LogFactory.getLog(ContextSwitch.class);

    /** Class constructor */    
    public ContextSwitch() {
    }

    /** Gets the targets with no constraint on context
     * @return a Set with all targets connected to this resource
     */    
    public Set getTargets() {
	Set allTargets = new HashSet();
	Collection allDests = arcs.values();
	Iterator it = allDests.iterator();
	while (it.hasNext()) {
	    Object dest = it.next();
	    if (dest instanceof Connector) {
		Set targets = ((Connector)dest).getTargets();
		if (targets != null) {
		    allTargets.addAll(targets);
		}
	    }
	    else if (dest instanceof Node) {
		allTargets.add(dest);
	    }
	    else {
		log.fatal("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
		System.exit(1);
	    }
	}
	if (allTargets.isEmpty()) return null;
	return allTargets;
    }
    
    /** Gets the pairs (context, set of targets) represented by this contextSwitch
     * @return a Map of all context, set of targets connected to this resource
     */    
    public Map getTargetsMap() {
	Map targetsMap = new HashMap();
	Iterator it = arcs.entrySet().iterator();
	while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Resource key = (Resource)entry.getKey();
	    Object dest = entry.getValue();
	    if (dest instanceof Connector) {
		Set targets = ((Connector)dest).getTargets();
		if (targets != null) {
		    targetsMap.put(key, targets);
		}
	    }
	    else if (dest instanceof Node) {
                Set targets = new HashSet();
                targets.add(dest);
		targetsMap.put(key, targets);
	    }
	    else {
		log.fatal("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
		System.exit(1);
	    }
	}
	if (targetsMap.isEmpty()) return null;
	return targetsMap;
    }

    /** Gets the targets which satisfies a given criterion
     * @return a Set with all targets associated with a context satisfying the criteria
     * @param criterion the criterion used to filter the context
     */    
    public Set getTargets(Criterion criterion) {
	Set allTargets = new HashSet();
	Iterator it = arcs.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry entry = (Map.Entry)it.next();
	    Resource key = (Resource)entry.getKey();
	    if (criterion.eval(key)) {
		Set targets =  super.getTargets(key);
		if (targets != null) {
		    allTargets.addAll(targets);
		}
	    }
	}
	if (allTargets.isEmpty()) return null;
	return allTargets;
    }

    /**
     * MUST NOT BE USED
     */    
    public Set getReachableTargets(Property property) {
	log.error("this method must not be called on this object");
	return null;
    }

    /**
     * MUST NOT BE USED
     */    
    public Set getReachableTargets(Property property, boolean lookInSubProperties) {
	log.error("this method must not be called on this object");
	return null;
    }

    /**
     * MUST NOT BE USED
     */    
    public Node getTarget(Resource prop) {
	log.error("this method must not be called on this object");
	return null;
    }

//     /**
//      * MUST NOT BE USED
//      */    
//     public boolean isTarget(Resource prop, Object value) {
// 	log.error("this method must not be called on this object");
// 	return false;
//     }


    /**
     * MUST NOT BE USED
     */    
    public void setTarget(Resource prop, Node target) {
	log.fatal("this method must not be called on this object");
	log.fatal("",new Exception());
	System.exit(1);
    }

    /** Implementation of equals method for instances of Resource. Equality is tested using only the accessor key
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */    
    public boolean equals(Object obj) {
	return ((Object)obj).equals(this);
    }

    /** Returns a hash code value for the object
     * @return a hash code value for this object
     */
    public int hashCode() {
	return ((Object)this).hashCode();
    }

    /** Gets the textual representation of this resource
     * @return the string used to identify the resource in a text display
     */    
    public String toString() {
	return super.toString();
    }
	
}

