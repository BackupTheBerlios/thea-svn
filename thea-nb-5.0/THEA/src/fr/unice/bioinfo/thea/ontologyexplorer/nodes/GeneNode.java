package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneNode extends AbstractNode {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); // NOI18N;

    /***/
    private Resource resource;

    static {
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.nodes.genename");// NOI18N
        nodeNameProperty = (String) o;
        fullNameProperty = (String) con
                .getProperty("ontologyexplorer.nodes.genefullname");// NOI18N
    }

    /** The property to use to reach for a node's display name. */
    private static String nodeNameProperty;

    private static String fullNameProperty;

    private String displayName = "";// NOI18N

    private String toolTip = "";// NOI18N

    private Hashtable properties = new Hashtable();

    public GeneNode(Resource resource) {
        // call super constructor to create place for children
        super(new Children.Array());
        // fix the resource
        this.resource = resource;
        Resource res = null;
        try {
            res = resource.getTarget(nodeNameProperty);
        } catch (AllontoException ae) {
        }
        if (res != null) {
            displayName = res.getAcc();
        } else {
            displayName = "" + resource.getResource_id();// NOI18N
        }
        Resource tt = null;
        try {
            tt = resource.getTarget(fullNameProperty);
        } catch (AllontoException ae) {
        }
        if (tt != null) {
            toolTip = tt.getAcc();
        } else {
            toolTip = "The full name of this gene is not available";// NOI18N
        }
        // set the display name
        setDisplayName(displayName);
        setName(displayName);
        setShortDescription(toolTip);
        // Icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/empty");// NOI18N
    }

    /** Returns the resource represented by this node. */
    public Resource getResource() {
        return resource;
    }

    /** Sets the resource represented by this node. */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    protected ClassificationNode getClassificationNode() {
        Node ancestor = getParentNode();
        while ((ancestor != null) && !(ancestor instanceof ClassificationNode)) {
            ancestor = ancestor.getParentNode();
        }
        if (ancestor == null) {
            System.out.println("problem when calling getClassificationNode for class="+getClass());
            System.out.println("resource="+resource.getAcc());
        }
        return (ClassificationNode) ancestor;
    }

    protected DatabaseConnection getConnection() {
        return ((OntologyNode)getClassificationNode().getInfo().getLinkedOntologyNode()).getConnection();
    }
    
}