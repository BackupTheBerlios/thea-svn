package fr.unice.bioinfo.allonto.datamodel;

import java.util.*;

/**
 * A qualified relationship between a source node and a set of targets
 */
public class Connector extends Node {
    /**
     * The set of connector's targets
     */
    protected  Set targets;
    /**
     * constructor
     */
    public Connector() {
	targets = new HashSet();
    }

    /** Gets the set of targets
     * @return a Set representing the targets of the connector
     */    
    public Set getTargets() {
	return targets;
    }

    /** Sets the Set of targets
     * @param targets the targets of this connector
     */    
    public void setTargets(Set targets) {
	this.targets = targets;
    }

    /** Adds a target to this connector
     * @param target the target to be added to this connector
     */    
    public void addTarget(Object target) {
	targets.add(target);
    }

}
