package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

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
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowAnnotatedGenesAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeInstances;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.ShowResourceNodeProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;

/**
 * This class uses the nodes API to build a node that represents a resource from
 * an ontology.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public abstract class AbstractResourceNode extends AbstractNode implements Node.Cookie {

    private ResourceNodeInfo info;

    private ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();


    /** A resource associated with a node in the Ontology Explorer. */
    private Resource resource;

    public AbstractResourceNode(final Children children) {
        super(children);
    }

    public String getName() {
        String n = super.getName();
        if ((n != null) && !n.equals("")) return n; 
        // Build the display name:
        try {
            HibernateUtil.createSession(getConnection().getConnection());
            Session sess = HibernateUtil.currentSession();
            sess.update(resource);
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
                    n = sv.getValue();
                } else {
                    n = resource.getAcc();// NOI18N
                }
            }
        } catch (HibernateException e1) {
            System.out.println("Hibernate exception when updating resource");
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (AllontoException ae) {
            resourceFactory.setMemoryCached(false);
        }
        finally {
            try {
                HibernateUtil.closeSession();
            } catch (HibernateException he) {
                he.printStackTrace(System.out);
            }
        }
        
        setName(n);
        return n;
    }

    public String getDisplayName() {
        return getName();
    }

    /** Returns cookie */
    public ResourceNodeInfo getInfo() {
        return info;
    }

    /** Sets cookie */
    public void setInfo(ResourceNodeInfo info) {
        this.info = info;
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
                SystemAction.get(ShowAnnotatedGenesAction.class), null,
                SystemAction.get(ShowResourceNodeProperties.class),
                SystemAction.get(ShowResourceNodeInstances.class)};
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

    public OntologyNode getOntologyNode() {
        Node ancestor = getParentNode();
        while ((ancestor != null) && !(ancestor instanceof OntologyNode)) {
            ancestor = ancestor.getParentNode();
        }
        if (ancestor == null) {
            System.out.println("problem when calling getOntologyNode for class="+getClass());
            System.out.println("resource="+resource.getAcc());
        }
        return (OntologyNode) ancestor;
    }

    public DatabaseConnection getConnection() {
        return getOntologyNode().getConnection();
    }

    public Configuration getConfiguration() {

        Configuration localConfiguration = getOntologyNode().getConfiguration();

        Configuration defaultConfiguration = TheaConfiguration.getDefault()
                .getConfiguration();

        CompositeConfiguration cc = new CompositeConfiguration();
        if (localConfiguration != null) {
            cc.addConfiguration(localConfiguration);
        }
        if (defaultConfiguration != null) {
            cc.addConfiguration(defaultConfiguration);
        }
        return cc;

    }
}