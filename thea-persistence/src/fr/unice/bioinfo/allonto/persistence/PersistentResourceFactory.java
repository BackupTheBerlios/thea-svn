package fr.unice.bioinfo.allonto.persistence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Hibernate;

import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.DefaultResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.Resource;


import fr.unice.bioinfo.allonto.persistence.HibernateUtil;

/**
 * An implementation of ResourceFactory which deals with persistence
 */
public class PersistentResourceFactory extends DefaultResourceFactory implements ResourceFactory {
    private static Log log = LogFactory.getLog(PersistentResourceFactory.class);

    /**
     * Retrieve a Property, given its ID
     * @param key the ID of the property to retrieve
     * @return the Property
     */
    public synchronized Property getProperty(String key) {
	if (key == null) {
	    log.error("PersistentResourceFactory cannot retrieve property with null id");
	    return null;
	}
	Property prop = (Property)propHash.get(key);
	if (prop != null) return prop; // already created
	try {
	    Session sess = HibernateUtil.currentSession();
	    //	    Transaction t = sess.beginTransaction();
	    List list = sess.find("from Property as prop where prop.acc = ?", key, Hibernate.STRING);
	    log.info("querying property="+key+" (already created="+!list.isEmpty());
	    if (list.size() == 0) {
		Transaction t = sess.beginTransaction();
		Set previousPropertySet = new HashSet(propHash.values());
		ResourceFactory resourceFactory = new DefaultResourceFactory();
		prop = resourceFactory.getProperty(key);
		Set propertySet = new HashSet(propHash.values());
		propertySet.removeAll(previousPropertySet);
		Iterator it = propertySet.iterator();
		while (it.hasNext()) {
		    log.error("creating property '"+key+"'");
		    sess.save((Property)it.next());
		}
		t.commit();
	    }
	    else if (list.size() == 1) {
		prop = (Property)list.iterator().next();
		log.info("retrieved property in database : "+key+", "+prop);
		propHash.put(key, prop);
	    }
	    else {
		log.fatal("multiple properties found with accessor='"+key+"'");
	    }
	    //	    t.commit();
	} catch (HibernateException he) {
	    he.printStackTrace();
	}
	return prop;
    }

    /**
     * Retrieve a Resource, given its ID
     * @param key the ID of the resource to retrieve
     * @return the Resource
     */
    public synchronized Resource getResource(String key) {
	if (key == null) {
	    log.error("PersistentResourceFactory cannot retrieve resource with null id");
	    return null;
	}
	Resource res = (Resource)resHash.get(key);
	if (res != null) return res; // already created
	try {
	    Session sess = HibernateUtil.currentSession();
	    List list = sess.find("from Resource as res where res.acc = ?", key, Hibernate.STRING);
	    log.info("querying resource="+key+" (already created="+!list.isEmpty());
	    if (list.size() == 0) {
		Transaction t = sess.beginTransaction();
		Set previousResourceSet = new HashSet(resHash.values());
		ResourceFactory resourceFactory = new DefaultResourceFactory();
		res = resourceFactory.getResource(key);
		Set resourceSet = new HashSet(resHash.values());
		resourceSet.removeAll(previousResourceSet);
		Iterator it = resourceSet.iterator();
		while (it.hasNext()) {
		    log.error("creating resource '"+key+"'");
		    sess.save((Resource)it.next());
		}
		t.commit();
	    }
	    else if (list.size() == 1) {
		res = (Resource)list.iterator().next();
		log.info("retrieved resource in database : "+key+", "+res);
		resHash.put(key, res);
	    }
	    else {
		log.fatal("multiple resources found with accessor='"+key+"'");
	    }
	    //	    t.commit();
	} catch (HibernateException he) {
	    he.printStackTrace();
	}
	return res;
    }
}
