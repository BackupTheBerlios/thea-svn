/*
 * DbInterceptor.java
 *
 * Created on 5 juin 2004, 14:09
 */

package fr.unice.bioinfo.allonto.persistence;

import net.sf.hibernate.*;
import net.sf.hibernate.cfg.*;

/**
 * Utilities method for Hibernate
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory
	    Configuration cfg = new Configuration()
		.addClass(fr.unice.bioinfo.allonto.datamodel.Node.class);
	    cfg.setInterceptor(new DbInterceptor());
	    sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException ex) {
            throw new RuntimeException("Configuration problem: " + ex.getMessage(), ex);
        }
    }

    /**
     * Memorize the current Hibernate session
     */
    public static final ThreadLocal session = new ThreadLocal();

    /**
     * Gets the current session
     * (if no session exists, creates a new one)
     * @throws net.sf.hibernate.HibernateException 
     * @return the current session
     */
    public static Session currentSession() throws HibernateException {
        Session s = (Session) session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    /**
     * Close an open session
     * @throws net.sf.hibernate.HibernateException 
     */
    public static void closeSession() throws HibernateException {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
}
