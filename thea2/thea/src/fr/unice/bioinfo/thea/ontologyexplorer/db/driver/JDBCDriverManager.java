/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package fr.unice.bioinfo.thea.ontologyexplorer.db.driver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.openide.ErrorManager;
import org.openide.util.Lookup;

/**
 * JDBCDriverManager manages the instances of the JDBCDriver objects.
 */
public final class JDBCDriverManager {
    private static JDBCDriverManager instance = null;

    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport(
            this);

    /**
     * Gets an instance of JDBCDriverManager. If the instance doesn't exist it
     * will be created.
     * @return the instance of JDBCDriverManager
     */
    public static synchronized JDBCDriverManager getDefault() {
        if (instance == null) {
            instance = new JDBCDriverManager();
        }

        return instance;
    }

    /**
     * Gets an array of JDBCDriver objects.
     * @return the array of registered JDBC drivers.
     */
    public JDBCDriver[] getDrivers() {
        Lookup.Result r = Lookup.getDefault().lookup(
                new Lookup.Template(JDBCDriver.class));
        Collection c = r.allInstances();

        return (JDBCDriver[]) c.toArray(new JDBCDriver[c.size()]);
    }

    /**
     * Gets an array of JDBCDriver objects specified by a class name.
     * @param drvClass driver class name
     * @return the array of registered JDBC drivers.
     */
    public JDBCDriver[] getDriver(String drvClass) {
        LinkedList ret = new LinkedList();
        JDBCDriver[] drvs = getDrivers();

        for (int i = 0; i < drvs.length; i++)
            if (drvs[i].getClassName().equals(drvClass)) {
                ret.add(drvs[i]);
            }

        return (JDBCDriver[]) ret.toArray(new JDBCDriver[ret.size()]);
    }

    public void addDriver(JDBCDriver drv) throws IOException {
        JDBCDriverConvertor.create(drv);
        propertySupport.firePropertyChange("add", null, drv); // NOI18N
    }

    public void removeDriver(JDBCDriver drv) {
        try {
            JDBCDriverConvertor.remove(drv);
            propertySupport.firePropertyChange("remove", drv, null); // NOI18N
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
        }
    }

    /**
     * Add property change listener Registers a listener for the PropertyChange
     * event. The JDBCDriverManager should fire a PropertyChange event whenever
     * somebody adds or removes driver registration.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    /**
     * Remove property change listener Remove a listener for the PropertyChange
     * event.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }
}