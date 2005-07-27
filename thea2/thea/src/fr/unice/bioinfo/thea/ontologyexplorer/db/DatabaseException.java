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
package fr.unice.bioinfo.thea.ontologyexplorer.db;

/**
 * Generic database exception.
 * @author Slavek Psenicka
 */
public class DatabaseException extends Exception {
    static final long serialVersionUID = 7114326612132815401L;

    /**
     * Creates new exception
     * @param message The text describing the exception
     */
    public DatabaseException(String message) {
        super(message);
    }
}