package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * @author SAÏD, EL KASMI.
 */
public class NodesUtilities {

    /**
     * Creates and returns the instance of the node representing the status
     * 'WAIT' of the node. It is used when it spent more time to create elements
     * hierarchy.
     * 
     * @return the wait node.
     */
    public static Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(NodesUtilities.class, "WaitNode")); // NOI18N
        n
                .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/waitIcon16"); // NOI18N
        return n;
    }

}