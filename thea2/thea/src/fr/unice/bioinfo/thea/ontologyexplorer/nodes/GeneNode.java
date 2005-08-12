package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.commons.configuration.Configuration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneNode extends AbstractNode {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); //NOI18N;

    /***/
    private Resource resource;

    private ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();

    static {
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object o = con.getProperty("ontologyexplorer.nodes.genename");//NOI18N
        nodeNameProperty = (String) o;
        fullNameProperty = (String) con
                .getProperty("ontologyexplorer.nodes.genefullname");//NOI18N
    }

    /** The property to use to reach for a node's display name. */
    private static String nodeNameProperty;
    private static String fullNameProperty;

    private String displayName = "";//NOI18N
    private String toolTip = "";//NOI18N

    private Hashtable properties = new Hashtable();

    public GeneNode(Resource resource) {
        // call super constructor to create place for children
        super(new Children.Array());
        // fix the resource
        this.resource = resource;
        StringValue sv = null;
        try {
            sv = (StringValue) resource.getTarget(resourceFactory
                    .getResource(nodeNameProperty));
        } catch (AllontoException ae) {
        }
        if (sv != null) {
            displayName = sv.getValue();
        } else {
            displayName = "" + resource.getId();//NOI18N
        }
        StringValue tt = null;
        try {
            tt = (StringValue) resource.getTarget(resourceFactory
                    .getResource(fullNameProperty));
        } catch (AllontoException ae) {
        }
        if (tt != null) {
            toolTip = tt.getValue();
        } else {
            toolTip = "The full name of this gene is not available";//NOI18N
        }
        // set the display name
        setDisplayName(displayName);
        setName(displayName);
        setShortDescription(toolTip);
        // Icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/empty");//NOI18N
    }

    /** Returns the resource represented by this node. */
    public Resource getResource() {
        return resource;
    }

    /** Sets the resource represented by this node. */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}