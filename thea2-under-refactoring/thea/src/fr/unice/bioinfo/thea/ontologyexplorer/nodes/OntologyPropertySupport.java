package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.lang.reflect.InvocationTargetException;

import org.openide.nodes.PropertySupport;

import fr.unice.bioinfo.thea.ontologyexplorer.infos.OntologyNodeInfo;

/**
 * Support class for {@link OntologyNode}. It is used to build properties of a
 * node and add them to the properties panel.
 * @author Saïd El Kasmi
 */
public class OntologyPropertySupport extends PropertySupport {

    private Object oni;

    /**
     * @param name The name of the property
     * @param type The class type of the property
     * @param displayName The display name of the property
     * @param canW Whether the property is writable
     */
    public OntologyPropertySupport(String name, Class type, String displayName,
            String shortDescription, Object oni, boolean canW) {
        // Call super constructor
        //  Properties are all readable: true is passed to super
        super(name, type, displayName, shortDescription, true, canW);
        this.oni = oni;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node.Property#getValue()
     */
    public Object getValue() throws IllegalAccessException,
            InvocationTargetException {
        String code = getName();
        Object rval = ((OntologyNodeInfo) oni).getProperty(code);
        return rval;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node.Property#setValue(java.lang.Object)
     */
    public void setValue(Object o) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String code = getName();
        ((OntologyNodeInfo) oni).setProperty(code, o);
    }

}