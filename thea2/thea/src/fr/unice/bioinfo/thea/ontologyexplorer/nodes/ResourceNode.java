package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Entity;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowAnnotetdGenesAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * This class uses the nodes API to build a node that represents a resource from
 * an ontology.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class ResourceNode extends AbstractNode implements Node.Cookie {

    private ResourceNodeInfo info;

    private ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();

    /** This node's display name. */
    private String name = "";// NOI18N

    /** A resource associated with a node in the Ontology Explorer. */
    private Resource resource;

    public ResourceNode(final Resource resource) {
        super((resource == null) ? Children.LEAF : new ResourceNodeChildren(
                resource));
        this.resource = resource;
        // // Build a display name:
        // try {
        // resourceFactory.setMemoryCached(true);
        // String nodeName =
        // OntologyProperties.getInstance().getNodeNameProperty(
        // getConfiguration());
        // Resource nodeNameProperty = null;
        // if (nodeName != null) {
        // nodeNameProperty = resourceFactory.getResource(nodeName);
        // }
        //
        // resourceFactory.setMemoryCached(false);
        // if (nodeNameProperty != null) {
        //
        // StringValue sv = (StringValue) resource
        // .getTarget(nodeNameProperty);
        // if (sv != null) {
        // name = sv.getValue();
        // } else {
        // name = "" + resource.getId();// NOI18N
        // }
        // }
        // } catch (AllontoException ae) {
        // resourceFactory.setMemoryCached(false);
        //
        // }
        // // Set the node name and display name:
        // setName(name);
        // setDisplayName(name);
    }

    public String getName() {
        String name = "";
        System.out.println("getName called");
        // Build the display name:
        try {
            resourceFactory.setMemoryCached(true);
            String nodeName = OntologyProperties.getInstance()
                    .getNodeNameProperty(getConfiguration());
            Resource nodeNameProperty = null;
            if (nodeName != null) {
                nodeNameProperty = resourceFactory.getResource(nodeName);
            }

            resourceFactory.setMemoryCached(false);
            if (nodeNameProperty != null) {

                StringValue sv = (StringValue) resource
                        .getTarget(nodeNameProperty);
                if (sv != null) {
                    name = sv.getValue();
                } else {
                    name = "" + resource.getId();// NOI18N
                }
            }
        } catch (AllontoException ae) {
            resourceFactory.setMemoryCached(false);

        }
        return name;
    }

    public String getDisplayName() {
        System.out.println("getDisplayName called");
        return getName();
    }

    /** Returns cookie */
    public ResourceNodeInfo getInfo() {
        return info;
    }

    /** Sets cookie */
    public void setInfo(ResourceNodeInfo info) {
        this.info = info;
        // info.setName(getName());
        // processNodeInfo(info);
    }

    private void processNodeInfo(final ResourceNodeInfo info) {
        Iterator mapIt = ((Resource) resource).getArcs().entrySet().iterator();
        String accessor;
        Resource resource;
        Entity e;
        Map.Entry entry;
        while (mapIt.hasNext()) {
            entry = (Map.Entry) mapIt.next();
            e = (Entity) entry.getValue();
            // System.out.println("key = " + entry.getKey());
            // Since values in the Map are not all instances of
            // StringValue,
            // do tests using the instanceof operator
            if (e instanceof StringValue) {
                resource = (Resource) entry.getKey();
                accessor = resource.getAcc();
                info.setProperty(accessor, ((StringValue) e).getValue());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
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
        advanced.setDisplayName("Resource properties");// NOI18N
        advanced.setName("advanced");// NOI18N
        advanced.setShortDescription("Properties from Ontology");// NOI18N
        processNodeInfo(info);
        Enumeration enum = info.keys();
        while (enum.hasMoreElements()) {
            String key = (String) enum.nextElement();
            // name property is already build in the properties sub sheet
            if (key.equals(ResourceNodeInfo.NAME)) {
                continue;
            }
            advanced.put(new ResourcePropertySupport(key, String.class, key,
                    "", info, false));// NOI18N
        }

        // Add advanced properties
        sheet.put(advanced);

        return sheet;
    }

    // /*
    // * (non-Javadoc)
    // * @see org.openide.nodes.Node#getPreferredAction()
    // */
    // public Action getPreferredAction() {
    // return SystemAction.get(PropertiesAction.class);
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        Action[] actions = new Action[] {
                SystemAction.get(ShowAnnotetdGenesAction.class), null,
                SystemAction.get(ShowResourceNodeProperties.class) };
        return actions;
    }

    /** Returns the resource represented by this node. */
    public Resource getResource() {
        return resource;
    }

    /** Sets the resource represented by this node. */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /** Make this node destroyable. */
    public boolean canDestroy() {
        return true;
    }

    private OntologyNode getOntologyNode() {
        Node ancestor = getParentNode();
        while ((ancestor != null) && !(ancestor instanceof OntologyNode)) {
            ancestor = ancestor.getParentNode();
        }
        return (OntologyNode) ancestor;
    }

    public Configuration getConfiguration() {

        Configuration localConfiguration = getOntologyNode().getConfiguration();

        Configuration defaultConfiguration = TheaConfiguration.getDefault()
                .getConfiguration();

        CompositeConfiguration cc = new CompositeConfiguration();
        if (defaultConfiguration != null) {
            cc.addConfiguration(defaultConfiguration);
        }
        if (localConfiguration != null) {
            cc.addConfiguration(localConfiguration);
        }
        return cc;

    }
}