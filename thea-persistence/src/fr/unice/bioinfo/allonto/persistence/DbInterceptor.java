/*
 * DbInterceptor.java
 *
 * Created on 5 juin 2004, 14:09
 */

package fr.unice.bioinfo.allonto.persistence;

import net.sf.hibernate.Interceptor;
import net.sf.hibernate.type.Type;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Collection;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.Connector;
import fr.unice.bioinfo.allonto.datamodel.StringValue;

/**
 * A subclass if Hibernate's Interceptor class
 * @author claude
 */
public class DbInterceptor implements Interceptor, Serializable {
    
    /** Creates a new instance of DbInterceptor */
    public DbInterceptor() {
    }

    public Object instantiate(Class clazz, Serializable id) {
	return null; // default behavior
    }

    public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
	return null; // default behavior
    }

    public Boolean isUnsaved(Object entity) {
	return null; // default behavior
    }

    public void postFlush(Iterator entities) {
    }

    public void preFlush(Iterator entities) {
    }

    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    }
    /**
     * Interceptor for save operation which save of update target associated to a Connector
     */
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
	if (entity instanceof fr.unice.bioinfo.allonto.datamodel.Resource) {
	    Collection potentiallyUnsavedTargets = ((Resource)entity).getArcs().values();
	    Iterator it = potentiallyUnsavedTargets.iterator();
	    while (it.hasNext()) {
		try {
		    Object target = it.next();
		    HibernateUtil.currentSession().saveOrUpdate(target);
// 		    if (target instanceof Connector) {
// 			HibernateUtil.currentSession().saveOrUpdate((Connector)target);
// 		    }
//  		    if (target instanceof StringValue) {
//  			HibernateUtil.currentSession().saveOrUpdate((StringValue)target);
//  		    }
		}
		catch (net.sf.hibernate.HibernateException he) {}
	    }
	}
	else if (entity instanceof fr.unice.bioinfo.allonto.datamodel.Connector) {
	    Collection potentiallyUnsavedTargets = ((Connector)entity).getTargets();
	    Iterator it = potentiallyUnsavedTargets.iterator();
	    while (it.hasNext()) {
		try {
		    Object target = it.next();
		    if (target instanceof StringValue) {
			HibernateUtil.currentSession().saveOrUpdate(target);
		    }
		}
		catch (net.sf.hibernate.HibernateException he) {}
	    }
	}
	return false;
    }

    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
	return false;
    }

    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
	//	System.out.println("reading entity///////////////"+entity);
	return false;
    }
}
