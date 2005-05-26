/*
 * Sun Public License Notice The contents of this file are subject to the Sun
 * Public License Version 1.0 (the "License"). You may not use this file except
 * in compliance with the License. A copy of the License is available at
 * http://www.sun.com/ The Original Code is NetBeans. The Initial Developer of
 * the Original Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package fr.unice.bioinfo.thea.ontologyexplorer.db.driver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;

public class JDBCDriver {
    private PropertyChangeSupport supp;
    private URL[] urls;
    private String clazz;
    private String name;

    /*
     * Creates a new instance of the JDBCDriver @param name the name of the
     * driver @param clazz the JDBC driver class @param urls the array of the
     * JDBC driver files URLs
     */
    public JDBCDriver(String name, String clazz, URL[] urls) {
        this.clazz = clazz;
        this.name = name;
        this.urls = urls;
    }

    /*
     * Creates a new instance of the JDBCDriver @param name the name of the
     * driver @param clazz the JDBC driver class
     */
    public JDBCDriver(String name, String clazz) {
        this.clazz = clazz;
        this.name = name;
    }

    /*
     * Returns the array of the JDBC driver files URLs. @return the array of the
     * JDBC driver files URLs
     */
    public URL[] getURLs() {
        return urls;
    }

    /*
     * Returns the JDBC driver class name. @return the JDBC driver class name
     */
    public String getClassName() {
        return clazz;
    }

    /*
     * Return the user defined driver name. @return the user defined driver name
     */
    public String getName() {
        return name;
    }

    /*
     * Return if the driver file(s) exists and can be loaded. @return true if
     * defined driver file(s) exists; otherwise false
     */
    public boolean isAvailable() {
        URL[] urls = getURLs();

        for (int i = 0; i < urls.length; i++) {
            File f = new File(urls[i].getFile());

            if (!f.exists()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Registers a listener to be notified when some of the platform's
     * properties change.
     */
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        synchronized (this) {
            if (supp == null) {
                supp = new PropertyChangeSupport(this);
            }
        }

        supp.addPropertyChangeListener(l);
    }

    /**
     * Removes a listener registered previously
     */
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        if (supp != null) {
            supp.removePropertyChangeListener(l);
        }
    }

    void firePropertyChange(String propName, Object v1, Object v2) {
        if (supp != null) {
            supp.firePropertyChange(propName, v1, v2);
        }
    }

    public String toString() {
        return getName();
    }
}