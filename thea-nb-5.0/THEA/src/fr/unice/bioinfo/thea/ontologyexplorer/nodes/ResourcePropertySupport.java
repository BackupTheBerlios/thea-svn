package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.lang.reflect.InvocationTargetException;

import org.openide.nodes.PropertySupport;

import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * Support class for {@link OntologyNode}. It is used to build properties of a
 * node and add them to the properties panel.
 * 
 * @author Sa�d El Kasmi
 */
public class ResourcePropertySupport extends PropertySupport {

    private Object rni;

    /**
     * @param name
     *            The name of the property
     * @param type
     *            The class type of the property
     * @param displayName
     *            The display name of the property
     * @param canW
     *            Whether the property is writable
     */
    public ResourcePropertySupport(String name, Class type, String displayName,
            String shortDescription, Object rni, boolean canW) {
        // Call super constructor
        // Properties are all readable: true is passed to super
        super(name, type, displayName, shortDescription, true, canW);
        this.rni = rni;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node.Property#getValue()
     */
    public Object getValue() throws IllegalAccessException,
            InvocationTargetException {
        String code = getName();
        Object rval = ((ResourceNodeInfo) rni).getProperty(code);
        return rval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node.Property#setValue(java.lang.Object)
     */
    public void setValue(Object o) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String code = getName();
        ((ResourceNodeInfo) rni).setProperty(code, o);
    }

}