/*
 * DbInterceptor.java
 *
 * Created on 5 juin 2004, 14:09
 */

package fr.unice.bioinfo.allonto.persistence;

import java.sql.Connection;

import net.sf.hibernate.*;
import net.sf.hibernate.cfg.*;

/**
 * Utilities method for Hibernate.
 * Allways call the 
 * <code>public static createSession(Connection connection)</code>
 * method to create a session before trying to get using the 
 * <code>public static Session currentSession()</code> method.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory
            System.out.println("HibernateUtil: BIGIN");
            Configuration cfg = new Configuration()
                    .addClass(fr.unice.bioinfo.allonto.datamodel.Node.class);
            cfg.setInterceptor(new DbInterceptor());
            sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException ex) {
            System.out.println("HibernateUtil: Create session pb");
            throw new RuntimeException("Configuration problem: "
                    + ex.getMessage(), ex);
        }
    }

    /**
     * Memorize the current Hibernate session
     */
    public static final ThreadLocal session = new ThreadLocal();

    /**
     * Gets the current session (if no session exists, creates a new one)
     * 
     * @throws net.sf.hibernate.HibernateException
     * @return the current session
     */
    public static Session currentSession() throws HibernateException {
        Session s = (Session) session.get();
        return s;
    }

    /**
     * Create a session using a user-defined Connection.
     * @param connection JDBC Connection.
     */
    public static void createSession(Connection connection) throws HibernateException {
        Session s = (Session) session.get();
        if (s == null) {
            s = sessionFactory.openSession(connection);
            session.set(s);
        }
    }

    /**
     * Close an open session
     * 
     * @throws net.sf.hibernate.HibernateException
     */
    public static void closeSession() throws HibernateException {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
}