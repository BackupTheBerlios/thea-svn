package fr.unice.bioinfo.thea.ontologyexplorer.loaders;

import java.io.IOException;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;

import fr.unice.bioinfo.thea.ontologyexplorer.ClDataObject;

/**
 * @author SAÏD, EL KASMI.
 */
public class ClDataLoader extends UniFileLoader {
    public ClDataLoader() {
        super("fr.unice.bioinfo.thea.ontologyexplorer.ClDataObject");

        ExtensionList list = new ExtensionList();
        list.addExtension("sot"); // NOI18N
        list.addExtension("sdt"); // NOI18N
        setExtensions(list);

        // setDisplayName(NbBundle.getMessage(ClDataLoader.class,
        // "TYPE_Povray")); //NOI18N
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.loaders.UniFileLoader#createMultiObject(org.openide.filesystems.FileObject)
     */
    protected MultiDataObject createMultiObject(FileObject fo)
            throws DataObjectExistsException, IOException {
        return (new ClDataObject(fo, this));
    }
}