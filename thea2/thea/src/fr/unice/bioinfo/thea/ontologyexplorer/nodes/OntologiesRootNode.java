package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.ResourceBundle;

import javax.swing.Action;

import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.thea.ontologyexplorer.actions.NewOntologyLocationAction;

/**
 * The root node for all ontologies.
 * @author Saïd El Kasmi.
 */
public class OntologiesRootNode extends AbstractNode implements Comparable {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); // NOI18N;

    /**
     * Main constructor for {@link OntologiesRootNode}class.
     */
    public OntologiesRootNode() {
        super(new Children.Array());

        // Set the bean name
        setName(bundle.getString("LBL_OntologiesRootNode_Name")); // NOI18N

        // Add a nice icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologiesNodeIcon");// NOI18N
        // Set the display name
        setDisplayName(bundle.getString("LBL_OntologiesRootNode_DisplayName")); // NOI18N

        // Give a short description which is shown on the mouse rolls on
        setShortDescription(bundle.getString("LBL_OntologiesRootNode_Hint")); // NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean context) {
        return new Action[] { NodeAction.get(NewOntologyLocationAction.class) };
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
     * @see org.openide.nodes.AbstractNode#createSheet()
     */
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = Sheet.createPropertiesSet();
        sheet.put(props);

        props.put(new PropertySupport.Name(this));

        return sheet;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Node node = (Node) o;

        return this.getDisplayName().compareTo(node.getDisplayName());
    }
}