package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import javax.swing.Action;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.thea.ontologyexplorer.actions.DeleteClNodeAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.EditClAction;
import fr.unice.bioinfo.thea.ontologyexplorer.actions.SelectOntologyAction;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;

/**
 * A node that represents a classification inside the <i>Ontology Explorer </i>.
 * @author Saïd El Kasmi.
 */
public class ClassificationNode extends AbstractNode implements Node.Cookie {

    /** Cookie */
    private ClassificationNodeInfo info;

    /**
     * Main constructor for {@link ClassificationNode}class.
     */
    public ClassificationNode(String name, String description) {
        super(new Children.Array());

        // add it as a cookie
        //getCookieSet().add(this);
        // Set the bean name
        setName(name);

        // Add a nice icon
        setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/ClassificationNodeIcon"); //NOI18N

        // Set the display name: The display name comes from user input
        setDisplayName(name);

        // Give a short description which is shown on the mouse rolls on
        setShortDescription(description);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#getActions(boolean)
     */
    public Action[] getActions(boolean arg0) {
        Action[] result = new Action[] { SystemAction.get(EditClAction.class),
                SystemAction.get(SelectOntologyAction.class), null,
                SystemAction.get(DeleteClNodeAction.class) };

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.nodes.Node#canDestroy()
     */
    public boolean canDestroy() {
        return true;
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

    /** Returns cookie */
    public ClassificationNodeInfo getInfo() {
        return info;
    }

    /** Sets cookie */
    public void setInfo(ClassificationNodeInfo info) {
        this.info = info;
        info.setNode(this);

    }
}