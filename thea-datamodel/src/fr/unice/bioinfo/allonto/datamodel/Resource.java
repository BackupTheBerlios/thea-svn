package fr.unice.bioinfo.allonto.datamodel;

import fr.unice.bioinfo.allonto.util.AllontoFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;

/** This class is a superclass of all ressources modelized in Allonto
 * @author Claude Pasquier
 */
public class Resource extends Node {
    private static Log log = LogFactory.getLog(Resource.class);
    /**
     * convenient memorization of resource factory instance
     */
    protected static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();


    public static int USE_SUBPROPERTIES = 1;
    public static int TRANSITIVE_SEARCH = 2;
    
    /** The symbolic accessor for this resource
     */
    private String acc = null;
    /**
     * The arcs originating from this resource
     */
    protected Map arcs = null;
    private Resource resourceClass = null;

    /** Class constructor (cannot be executed externally without a resourceType) */
    public Resource() {
    	arcs = new HashMap();
    	this.acc = null;
    	this.resourceClass = null;
    }

    /** Class constructor with the accessor key given as parameter
     * @param acc The accessor key for the resource
     */
    public Resource(String acc) {
	arcs = new HashMap();
	this.acc = acc;
        this.resourceClass = null;
    }

    /** Class constructor with the resource type given as parameter
     * @param resType The resource type
     */
    public Resource(Resource resourceClass) {
	arcs = new HashMap();
	this.acc = null;
        this.resourceClass =  resourceClass;
    }

    /** Class constructor with the accessor key and resource type given as parameter
     * @param acc The accessor key for the resource
     * @param resType The resource type
     */
    public Resource(String acc, Resource resourceClass) {
	arcs = new HashMap();
	this.acc = acc;
	this.resourceClass =  resourceClass;
    }

    /** Gets the map of arcs
     * @return a Map representing the resources linked to this resource
     */    
    public Map getArcs() {
	return arcs;
    }

    /** Sets the Map representing the resources linked to this resource
     * @param arcs the Map of resources linked to this resource
     */    
    public void setArcs(Map arcs) {
	this.arcs = arcs;
    }

    /** Gets the accessor for this resource
     * @return the accessor key for the resource
     */    
    public String getAcc() {
	return acc;
    }

    /** Sets the accessorfor this resource.
     * @param acc The accessor key for this resource
     */    
    public void setAcc(String acc) {
	this.acc = acc;
    }

    /** Gets the type of this resource
     * @return the type of this resource
     */    
    public Resource getResourceClass() {
	return resourceClass;
    }

    /** Sets the type of this resource
     * @param resType The type of this resource
     */    
    public void setResourceClass(Resource resourceClass) {
	this.resourceClass = resourceClass;
    }

    /** Gets the name of this resource
     * @return the name of the resource
     */    
    public String getName() {
	StringValue nameTarget = (StringValue)getTarget(resourceFactory.getProperty("NAME"));
	if (nameTarget == null) return null;
	return (nameTarget.getValue());
    }

    /** Sets the name of resource.
     * @param name The name of this resource
     */    
    public void setName(String name) {
	setTarget(resourceFactory.getProperty("NAME"), new StringValue(name));
    }


    /** Gets the targets attached to a property
     * @param prop the property to get
     * @param options the options used to perform the seach of targets
     * @return a Set with all targets attached with the property
     */    
    public Set getTargets(Resource prop, int options) {
        if ((options & TRANSITIVE_SEARCH) == TRANSITIVE_SEARCH) {
            Set targets = new HashSet();
            collectReachableTargets(prop, targets, options - TRANSITIVE_SEARCH);
            if (targets.isEmpty()) return null;
            return targets;
        }
        else if ((options & USE_SUBPROPERTIES) == USE_SUBPROPERTIES) {
            Set properties = new HashSet();
            ((Resource)prop).collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, TRANSITIVE_SEARCH);
            properties.add(prop);
            return getTargets(properties, options - USE_SUBPROPERTIES);
        }
        else {
            return getTargets(prop);
        }
    }

    public Set getContexts(Resource prop, Resource target, int options){
        Set contexts = new HashSet();
        Map targetsMap = getTargetsMap(prop, options);
        Iterator it = targetsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Resource key = (Resource)entry.getKey();
            Set values = (Set)entry.getValue();
            if (values.contains(target)) {
                contexts.add(key);
            }
        }
        if (contexts.isEmpty()) {
            return null;
        }
        else return contexts;
    }

        
        /** Retrieve the Maps (context, targets) attached to a property
     * @param prop the property to get
     * @param options the options used to perform the seach of targets
     * @return a Map of all pairs (context, targets) attached with the property
     */
    public Map getTargetsMap(Resource prop, int options) {
        if ((options & TRANSITIVE_SEARCH) == TRANSITIVE_SEARCH) {
            Map targetsMap = new HashMap();
            collectReachableTargetsMap(prop, targetsMap, options - TRANSITIVE_SEARCH);
            if (targetsMap.isEmpty()) return null;
            return targetsMap;
        }
        else if ((options & USE_SUBPROPERTIES) == USE_SUBPROPERTIES) {
            Set properties = new HashSet();
            ((Resource)prop).collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, TRANSITIVE_SEARCH);
            properties.add(prop);
            return getTargetsMap(properties, options - USE_SUBPROPERTIES);
        }
        else {
            Object dest = arcs.get(prop);
            if (dest == null) return null;
            else if (dest instanceof Connector) {
                Set targets = ((Connector)dest).getTargets();
                Map targetsMap = new HashMap();
                targetsMap.put(resourceFactory.getResource("TRUE"), targets);
                return targetsMap;
            }
            else if (dest instanceof ContextSwitch) {
                return ((ContextSwitch)dest).getTargetsMap();
            }
            else if (dest instanceof Node) {
                Map targetsMap = new HashMap();
                targetsMap.put(resourceFactory.getResource("TRUE"), dest);
                return targetsMap;
            }
            else {
                log.fatal("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
                System.exit(1);
            }
        }
        return null;
    }

    public Map getTargetsMap(Set properties, int options) {
	Map allTargetsMap = new HashMap();
	Iterator it = properties.iterator();
	while (it.hasNext()) {
	    Map targetsMap = getTargetsMap((Resource)it.next(), options);
	    if (targetsMap != null) {
                Iterator it2 = targetsMap.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry entry = (Map.Entry)it2.next();
                    Resource key = (Resource)entry.getKey();
                    Set values = (Set)entry.getValue();
                    Set assocTargets = (Set)allTargetsMap.get(key);
                    if (assocTargets != null) {
                        assocTargets = new HashSet();
                    }
                    assocTargets.addAll(values);
                    allTargetsMap.put(key, assocTargets);
                }
	    }
	}
	if (allTargetsMap.isEmpty()) return null;
	return allTargetsMap;
    }

    /** Gets the targets directly attached to a set of properties.
     * @return a Set with all targets attached to the properties
     * @param properties the properties to get
     * @param options the options used to perform the seach of targets
     * @return the set of connected targets
     */    
    public Set getTargets(Set properties, int options) {
	Set allTargets = new HashSet();
	Iterator it = properties.iterator();
	while (it.hasNext()) {
	    Set targets = getTargets((Resource)it.next(), options);
	    if (targets != null) {
		allTargets.addAll(targets);
	    }
	}
	if (allTargets.isEmpty()) return null;
	return allTargets;
    }
        
    public Set getCoveredTargets(Resource prop, int options) {
        Set directTargets = getTargets(prop, options);
        if (directTargets == null) {
            return null;
        }
        Set coveredTargets = new HashSet();
        Iterator it = directTargets.iterator();
        while (it.hasNext()) {
            Resource source = (Resource)it.next();
            coveredTargets.addAll(directTargets);
            source.collectReachableTargets(resourceFactory.getProperty("SUBSUME"), coveredTargets, USE_SUBPROPERTIES);
        }
        coveredTargets.addAll(directTargets);
        return coveredTargets;
    }
    
    public Set getCoveredTargets(Resource prop, Criterion criterion, int options) {
        Set directTargets = getTargets(prop, criterion, options);
        if (directTargets == null) {
            return null;
        }
        Set coveredTargets = new HashSet();
        Iterator it = directTargets.iterator();
        while (it.hasNext()) {
            Resource source = (Resource)it.next();
            coveredTargets.addAll(directTargets);
            source.collectReachableTargets(resourceFactory.getProperty("SUBSUME"), criterion, coveredTargets, USE_SUBPROPERTIES);
        }
        coveredTargets.addAll(directTargets);
        return coveredTargets;
    }

    /** Gets the targets attached to a property
     * @return a Set with all targets directly attached with the property
     * @param prop the property to get
     * @return the set of connected targets
     */    
    public Set getTargets(Resource prop) {
	Object dest = arcs.get(prop);
	if (dest == null) return null;
	else if (dest instanceof Connector) {
	    return ((Connector)dest).getTargets();
	}
	else if (dest instanceof ContextSwitch) {
	    return ((ContextSwitch)dest).getTargets();
	}
	else if (dest instanceof Node) {
	    Set targets = new HashSet();
	    targets.add(dest);
	    return targets;
	}
	else {
	    log.fatal("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
	    System.exit(1);
	}
	return null;
    }

    /** Gets the targets directly attached to a set of properties.
     * @return a Set with all targets attached to the properties
     * @param properties the properties to get
     */    
    public Set getTargets(Set properties) {
	Set allTargets = new HashSet();
	Iterator it = properties.iterator();
	while (it.hasNext()) {
	    Set targets = getTargets((Resource)it.next());
	    if (targets != null) {
		allTargets.addAll(targets);
	    }
	}
	if (allTargets.isEmpty()) return null;
	return allTargets;
    }

   
    /** Return the targets attached to a property
     * @return a Set with all targets attached to a property and its sub properties
     * @param prop the property to retrieve
     */    
    public Set getImpliedTargets(Resource prop) {
	Set properties = new HashSet();
        ((Resource)prop).collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, 0);
        properties.add(prop);
        // keep only properties used in namedTargets
        properties.retainAll(arcs.keySet());
        return getTargets(properties);
    }

    /** Gets the set of all target associated with this resource with a direct or transitive relationship
     * @return a Set with all targets reachable from the resource with a transitive closure
     * @param property the property to retrieve
     */    
    public Set getReachableTargets(Resource property) {
	Set targets = new HashSet();
        collectReachableTargets(property, targets, 0);
	if (targets.isEmpty()) return null;
	return targets;
    }

     /** Gets the set of all target associated with this resource with a direct or transitive relationship
    * @return a Set with all targets reachable from the resource with a transitive closure using the given property and optionally the sub
     * properties if the flag is set
     * @param property the property to use
     * @param lookInSubProperties indicates if targets associated with sub properties must be retrieved
     */    
    public Set getReachableTargets(Resource property, boolean lookInSubProperties) {
        if (!lookInSubProperties) return getReachableTargets(property);
        Set targets = new HashSet();
	Set properties = new HashSet();
        properties.add(property);
        ((Resource)property).collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, 0);
        collectReachableTargets(properties, targets);
	if (targets.isEmpty()) return null;
	return targets;
    }

    /** Gets the targets attached to a property
     * @return a Set with all targets attached with the property
     * @param prop the property to get
     * @param criterion the criterion used to perform the search
     * @param options the options used to perform the seach of targets
     * @return the set of connected targets
     */    
    public Set getTargets(Resource prop, Criterion criterion, int options) {
        if ((options & TRANSITIVE_SEARCH) == TRANSITIVE_SEARCH) {
            Set targets = new HashSet();
            collectReachableTargets(prop, criterion, targets, options - TRANSITIVE_SEARCH);
            if (targets.isEmpty()) return null;
            return targets;
        }
        else if ((options & USE_SUBPROPERTIES) == USE_SUBPROPERTIES) {
            Set properties = new HashSet();
            ((Resource)prop).collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, TRANSITIVE_SEARCH);
            properties.add(prop);
            return getTargets(properties, criterion, options - USE_SUBPROPERTIES);
        }
        else {
            return getTargets(prop, criterion);
        }
    }

    /** Gets the targets directly attached to a set of properties.
     * @return a Set with all targets attached to the properties
     * @param properties the properties to get
     * @param criterion the criterion used to perform the search
     * @param options the options used to perform the seach of targets
     * @return the set of connected targets
     */    
    public Set getTargets(Set properties, Criterion criterion, int options) {
	Set allTargets = new HashSet();
	Iterator it = properties.iterator();
	while (it.hasNext()) {
	    Set targets = getTargets((Resource)it.next(), criterion, options);
	    if (targets != null) {
		allTargets.addAll(targets);
	    }
	}
	if (allTargets.isEmpty()) return null;
	return allTargets;
    }

    /** Gets the targets attached to a property which satisfies the given context
     * @return a Set with all targets attached to the property
     * @param prop the property to get
     * @param criterion the criterion used to perform the search
     */    
    public Set getTargets(Resource prop, Criterion criterion) {
        Object dest = arcs.get(prop);
        if (dest == null) return null;
        else if (dest instanceof Connector) {
	    Set allTargets = new HashSet();
	    Set targets = ((Connector)dest).getTargets();
	    Iterator it = targets.iterator();
	    while (it.hasNext()) {
		dest = it.next();
		if (dest instanceof ContextSwitch) {
		    Set cTargets = ((ContextSwitch)dest).getTargets(criterion);
		    if (cTargets != null) {
			allTargets.addAll(cTargets);
		    }
		}
		else if (dest instanceof Node) {
		    if (criterion.eval(resourceFactory.getResource("TRUE"))) {
			allTargets.add(dest);
		    }
		}
	    }
	    if (allTargets.isEmpty()) return null;
	    return allTargets;
	}
	else if (dest instanceof ContextSwitch) {
	    return ((ContextSwitch)dest).getTargets(criterion);
	}
	else if (dest instanceof Node) {
	    Set targets = new HashSet();
	    if (criterion.eval(resourceFactory.getResource("TRUE"))) {
		targets.add(dest);
	    }
	    if (targets.isEmpty()) return null;
	    return targets;
	}
	else {
	    log.error("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
	}
	return null;
    }


    /** Gets the unique value attached to a property
     * @return an object attached to the property
     * @param prop the property to retrieve
     */    
    public Node getTarget(Resource prop) {
	Node dest = (Node)arcs.get(prop);
	if (dest instanceof Connector) {
	    log.warn("GetTarget is returning a Connector");
	    Exception e = new Exception();
	    e.printStackTrace();
	}
	return (Node)dest;
    }

    /**
     * Collect all target (direct and indirect) reachable with a property
     * @param property the property to retrieve
     * @param targets the set of target associated with the property
     * @param options search options
     */
    protected void collectReachableTargets(Resource property, Set targets, int options) {
        if ((options & USE_SUBPROPERTIES) == USE_SUBPROPERTIES) {
            Set properties = new HashSet();
            properties.add(property);
            property.collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, options - USE_SUBPROPERTIES);
            collectReachableTargets(properties, targets);
        }
        else {
            // retrieve new targets not included in targets
            Set newTargets = getTargets(property);
            if (newTargets == null) return;
            newTargets = new HashSet(getTargets(property));
            newTargets.removeAll(targets);
            // include them recursively in the set of targets
            while (!newTargets.isEmpty()) {
                Resource target = (Resource)newTargets.iterator().next();
                targets.add(target);
                target.collectReachableTargets(property, targets, options);
                newTargets.removeAll(targets);
            }
	}
    }

    protected void collectReachableTargetsMap(Resource property, Map targetsMap, int options) {
        if ((options & USE_SUBPROPERTIES) == USE_SUBPROPERTIES) {
            Set properties = new HashSet();
            properties.add(property);
            property.collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, options - USE_SUBPROPERTIES);
            collectReachableTargetsMap(properties, targetsMap);
        }
        else {
            // retrieve new targets map
            Map newTargetsMap = getTargetsMap(property, 0);
            if (newTargetsMap == null) return;
            newTargetsMap = new HashMap(newTargetsMap);
            Set allPreviousTargets = new HashSet();
            Iterator it = targetsMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                allPreviousTargets.addAll((Set)entry.getValue());
            }
            // include them recursively in the set of targetsMap
            while (!newTargetsMap.isEmpty()) {
                Map.Entry entry = (Map.Entry)newTargetsMap.entrySet().iterator().next();
                Resource key = (Resource)entry.getKey();
                Set values = new HashSet((Set)entry.getValue());
                values.removeAll(allPreviousTargets);
                if (values.isEmpty()) {
                    newTargetsMap.remove(key);
                    continue;
                }
                Set targets = (Set)targetsMap.get(key);
                if (targets == null) {
                    targets = new HashSet();
                }
                targets.addAll(values);
                targetsMap.put(key, entry.getValue());
                Iterator it2 = values.iterator();
                while (it2.hasNext()) {
                    Resource target = (Resource)it2.next();
                    target.collectReachableTargetsMap(property, targetsMap, options);
                }
                newTargetsMap.remove(key);
            }
	}
    }

    /**
     * Collect all target (direct and indirect) reachable with a property
     * @param property the property to retrieve
     * @param criterion the criterion used to perform the search
     * @param targets the set of target associated with the property
     * @param options search options
     */
    protected void collectReachableTargets(Resource property, Criterion criterion, Set targets, int options) {
        if ((options & USE_SUBPROPERTIES) == USE_SUBPROPERTIES) {
            Set properties = new HashSet();
            properties.add(property);
            property.collectReachableTargets(resourceFactory.getProperty("SUBSUMED_BY"), properties, options - USE_SUBPROPERTIES);
            collectReachableTargets(properties, criterion, targets);
        }
        else {
            // retrieve new targets not included in targets
            Set newTargets = getTargets(property, criterion);
            if (newTargets == null) return;
            newTargets = new HashSet(getTargets(property, criterion));
            newTargets.removeAll(targets);
            // include them recursively in the set of targets
            while (!newTargets.isEmpty()) {
                Resource target = (Resource)newTargets.iterator().next();
                targets.add(target);
                target.collectReachableTargets(property, criterion, targets, options);
                newTargets.removeAll(targets);
            }
	}
    }

    private void collectReachableTargets(Set properties, Set targets) {
	// retrieve new targets not included in targets
        // for each property included in the set
	Set newTargets = getTargets(properties);
	if (newTargets == null) return;
	newTargets = new HashSet(newTargets);
	newTargets.removeAll(targets);
	// include them recursively in the set of targets
	while (!newTargets.isEmpty()) {
	    Resource target = (Resource)newTargets.iterator().next();
	    targets.add(target);
	    target.collectReachableTargets(properties, targets);
	    newTargets.removeAll(targets);
	}
    }

    private void collectReachableTargetsMap(Set properties, Map targetsMap) {
        // retrieve new targets map
        Map newTargetsMap = getTargetsMap(properties, 0);
        if (newTargetsMap == null) return;
        newTargetsMap = new HashMap(newTargetsMap);
        Set allPreviousTargets = new HashSet();
        Iterator it = targetsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            allPreviousTargets.addAll((Set)entry.getValue());
        }
        // include them recursively in the set of targetsMap
        while (!newTargetsMap.isEmpty()) {
            Map.Entry entry = (Map.Entry)newTargetsMap.entrySet().iterator().next();
            Resource key = (Resource)entry.getKey();
            Set values = new HashSet((Set)entry.getValue());
            values.removeAll(allPreviousTargets);
            if (values.isEmpty()) {
                newTargetsMap.remove(key);
                continue;
            }
            Set targets = (Set)targetsMap.get(key);
            if (targets == null) {
                targets = new HashSet();
            }
            targets.addAll(values);
            targetsMap.put(key, entry.getValue());
            Iterator it2 = values.iterator();
            while (it2.hasNext()) {
                Resource target = (Resource)it2.next();
                target.collectReachableTargetsMap(properties, targetsMap);
            }
            newTargetsMap.remove(key);
        }
    
    }

    private void collectReachableTargets(Set properties, Criterion criterion, Set targets) {
	// retrieve new targets not included in targets
        // for each property included in the set
	Set newTargets = getTargets(properties, criterion, 0);
	if (newTargets == null) return;
	newTargets = new HashSet(newTargets);
	newTargets.removeAll(targets);
	// include them recursively in the set of targets
	while (!newTargets.isEmpty()) {
	    Resource target = (Resource)newTargets.iterator().next();
	    targets.add(target);
	    target.collectReachableTargets(properties, criterion, targets);
	    newTargets.removeAll(targets);
	}
    }

    /** Associates the target with the given property
     * @param prop the property to use
     * @param target the target associated with the property
     */    
    public void setTarget(Resource prop, Node target) {
	arcs.put(prop, target);
    }

    /** Adds a new target into the set of associated object fot the given property. This method also add the inverse relationship if the prop used has an inverse property
     * @param prop he property to use
     * @param target a target associated with the property
     */    
    public void addTarget(Resource prop, Node target) {
	connectTarget(prop, target);
	if ((prop instanceof Property) &&
	    (target instanceof Resource) &&
	    (target.getClass() != ContextSwitch.class)) {
	    Property inverseProp = ((Property)prop).getInverse();
	    if (inverseProp != null) {
		((Resource)target).connectTarget(inverseProp, this);
	    }
	}
    }

    /** Private method called by addTarget that really perform the job
     * @param prop he property to use
     * @param target a target associated with the property
     */    
    private void connectTarget(Resource prop, Node target) {
	Node dest = (Node)arcs.get(prop);
	if (dest == null) {
	    arcs.put(prop, target);
	}
	else if (dest instanceof Connector) {
	    if (((Connector)dest).getTargets().contains(target)) {
		return;
	    }
	    ((Connector)dest).addTarget(target);
	}
	else if (dest instanceof ContextSwitch) {
	    ((ContextSwitch)dest).addTarget(resourceFactory.getResource("TRUE"), target);
	}
	else if (dest instanceof Node) {
	    if (dest == target) {
		return;
	    }
	    Connector connector = new Connector();
	    connector.addTarget(dest);
	    connector.addTarget(target);
	    arcs.put(prop, connector);
	}
	else {
	    log.fatal("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
	    System.exit(1);
	}
    }

    /** Adds a new target into the set of associated object for the given property
     * and associated with the given context
     * @param prop he property to use
     * @param target a target associated with the property
     * @param context the context of the target
     */    
    public void addTarget(Resource prop, Node target, Context context) {
	if (context == null) {
	    addTarget(prop, target);
	    return;
	}
	connectTarget(prop, target, context);
	if ((prop instanceof Property) &&
	    (target instanceof Resource)) {
	    Property inverseProp = ((Property)prop).getInverse();
	    if (inverseProp != null) {
		((Resource)target).connectTarget(inverseProp, this, context);
	    }
	}
    }

	
    /** Private method called by addTarget that really perform the job
     * @param prop he property to use
     * @param target a target associated with the property
     * @param context the context of the target
     */    
    private void connectTarget(Resource prop, Node target, Context context) {
	Node dest = (Node)arcs.get(prop);
	if (dest == null) {
	    ContextSwitch contextSwitch = new ContextSwitch();
	    contextSwitch.addTarget(context, target);
	    arcs.put(prop, contextSwitch);
	}
	else if (dest instanceof Connector) {
	    ContextSwitch contextSwitch = new ContextSwitch();
	    contextSwitch.addTarget(context, target);
	    contextSwitch.addTarget(resourceFactory.getResource("TRUE"), dest);
	    arcs.put(prop, contextSwitch);
	}
	else if (dest instanceof ContextSwitch) {
	    ((ContextSwitch)dest).addTarget(context, target);
	}
	else if (dest instanceof Node) {
	    ContextSwitch contextSwitch = new ContextSwitch();
	    contextSwitch.addTarget(context, target);
	    contextSwitch.addTarget(resourceFactory.getResource("TRUE"), dest);
	    arcs.put(prop, contextSwitch);
	}
	else {
	    log.fatal("The target of a ressource MUST be an instance of class Node (found "+dest.getClass()+")");
	    System.exit(1);
	}
    }

    /** Implementation of equals method for instances of Resource. Equality is tested using only the accessor key
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */    
    public boolean equals(Object obj) {
    	if (this == obj) return true;
    	if (!(obj instanceof Resource)) return false;
    	if (acc == null) return super.equals(obj);
    	return this.getAcc().equals(((Resource)obj).getAcc());
    }
    
    /** Returns a hash code value for the object
     * @return a hash code value for this object
     */
    public int hashCode() {
    	if (acc == null) {
    		System.out.println("acc is null for Resource="+this);
    		return super.hashCode();
    	}
    	return acc.hashCode();
    }

    /** Gets the textual representation of this resource
     * @return the string used to identify the resource in a text display
     */    
    public String toString() {
	return super.toString()+"_"+acc;
    }
	
}

