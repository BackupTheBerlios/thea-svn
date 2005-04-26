package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.Entity;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowAnnotetdGenesAction;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * @author Saïd El Kasmi
 */
public class ResourceNode extends AbstractNode implements Node.Cookie {

    private ResourceNodeInfo info;

    /** A resource associated with a node in the Ontology Explorer. */
    private Resource resource;

    /** List of childs */
    private Set targets;

    public ResourceNode(Resource resource, Set targets) {
        super((targets == null) ? Children.LEAF : new ResourceNodeChildren(
                resource));
        this.resource = resource;
        this.targets = targets;
        // Set system name and display name of this node
        // using the resource one
        setName(resource.getName());
    }

    public ResourceNode(Resource resource) {
        super((resource == null) ? Children.LEAF : new ResourceNodeChildren(
                resource));
        this.resource = resource;
        // Set system name and display name of this node
        // using the resource one
        //setName(resource.getName()+" ("+resource.getId()+")");
        setName("" + resource.getId());
    }

    /** Returns cookie */
    public ResourceNodeInfo getInfo() {
        return info;
    }

    /** Sets cookie */
    public void setInfo(ResourceNodeInfo info) {
        this.info = info;
        info.setName(getName());
        //processNodeInfo(info);
    }

    private void processNodeInfo(final ResourceNodeInfo info) {
        //        RequestProcessor.getDefault().post(new Runnable() {
        //            public void run() {
        Iterator mapIt = ((Resource) resource).getArcs().entrySet().iterator();
        String accessor;
        Resource resource;
        Entity e;
        Map.Entry entry;
        while (mapIt.hasNext()) {
            entry = (Map.Entry) mapIt.next();
            e = (Entity) entry.getValue();
            //System.out.println("key = " + entry.getKey());
            // Since values in the Map are not all instances of
            // StringValue,
            // do tests using the instanceof operator
            if (e instanceof StringValue) {
                resource = (Resource) entry.getKey();
                accessor = resource.getAcc();
                info.setProperty(accessor, ((StringValue) e).getValue());
            }
        }
        //            }
        //        }, 0);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getCookie(java.lang.Class)
     */
    public Node.Cookie getCookie(Class klas) {
        if (klas.isInstance(info))
            return info;
        return super.getCookie(klas);
    }

    /** Creates properties sheet for this node. */
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        // Create properties of node
        Sheet.Set props = Sheet.createPropertiesSet();
        props.put(new PropertySupport.Name(this));
        sheet.put(props);

        // Create properties extracted from the ontology
        Sheet.Set advanced = new Sheet.Set();
        advanced.setDisplayName("Resource properties");//NOI18N
        advanced.setName("advanced");//NOI18N
        advanced.setShortDescription("Properties from Ontology");//NOI18N
        processNodeInfo(info);
        Enumeration enum = info.keys();
        while (enum.hasMoreElements()) {
            String key = (String) enum.nextElement();
            // name property is already build in the properties sub sheet
            if (key.equals(ResourceNodeInfo.NAME)) {
                continue;
            }
            advanced.put(new ResourcePropertySupport(key, String.class, key,
                    "", info, false));//NOI18N
        }

        // Add advanced properties
        sheet.put(advanced);

        return sheet;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getPreferredAction()
     */
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        Action[] actions = new Action[] { SystemAction
                .get(ShowAnnotetdGenesAction.class) };
        return actions;
    }
    /** Returns the resource represented by this node.*/
    public Resource getResource() {
        return resource;
    }
    /** Sets the resource represented by this node.*/
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}