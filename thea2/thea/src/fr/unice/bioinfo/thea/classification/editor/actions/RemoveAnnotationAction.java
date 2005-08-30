package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class RemoveAnnotationAction extends GenericAction {

    private Node aNode;

    public RemoveAnnotationAction(String name, String accelerator,
            ImageIcon icon, String shortDescription,
            DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;

        // if the node is leaf, disable this menu item action:
        if (aNode.getProperty(Node.USER_ANNOTATIONS) != null) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        aNode.removeProperty(Node.USER_ANNOTATIONS);
        aNode.setLabel("");// NOI18N
        drawable.updateGraphics();
    }

}