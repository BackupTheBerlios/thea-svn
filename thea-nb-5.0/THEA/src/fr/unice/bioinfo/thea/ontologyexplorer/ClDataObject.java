package fr.unice.bioinfo.thea.ontologyexplorer;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;

import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClDataNode;

/**
 * @author SAÏD, EL KASMI.
 */
public class ClDataObject extends MultiDataObject {
    static final long serialVersionUID = 3216181533399358555L;

    public ClDataObject(FileObject file, MultiFileLoader ldr)
            throws DataObjectExistsException {
        super(file, ldr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.loaders.DataObject#createNodeDelegate()
     */
    protected Node createNodeDelegate() {
        return (new ClDataNode(this));
    }
}