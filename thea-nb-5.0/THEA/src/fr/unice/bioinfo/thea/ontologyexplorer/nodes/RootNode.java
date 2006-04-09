package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.ResourceBundle;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * The root node for all ontologies and classifications nodes.
 * 
 * @author Saïd El Kasmi.
 */
public class RootNode extends AbstractNode implements Comparable {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.nodes.Bundle"); // NOI18N;

    /**
     * Main constructor for {@link RootNode}class.
     */
    public RootNode() {
        super(new Children.Array());

        // Set the bean name
        setName("RootNode"); // NOI18N

        // Add a nice icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/empty");// NOI18N

        // Set the display name
        setDisplayName("");// NOI18N

        // Give a short description which is shown on the mouse rolls on
        setShortDescription("");// NOI18N
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Node node = (Node) o;

        return this.getDisplayName().compareTo(node.getDisplayName());
    }
}