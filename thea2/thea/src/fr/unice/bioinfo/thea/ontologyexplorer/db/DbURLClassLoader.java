package fr.unice.bioinfo.thea.ontologyexplorer.db;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;

/**
 * This class loader is used to load JDBC drivers from their locations.
 */
public class DbURLClassLoader extends URLClassLoader {
    /**
     * Creates a new instance of DbURLClassLoader
     */
    public DbURLClassLoader(URL[] urls) {
        super(urls);
    }

    protected PermissionCollection getPermissions(CodeSource codesource) {
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        permissions.setReadOnly();

        return permissions;
    }
}