package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class UncollapseAction extends GenericAction {

    private Node aNode;

    public UncollapseAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;
        // if the node is leaf, disable this menu item action:
        if (aNode.isLeaf()) {
            this.setEnabled(false);
        } else {
            this.setEnabled(aNode.isCollapsed());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        synchronized (aNode) {
            boolean b = !aNode.isCollapsed();
            aNode.setCollapsed(b);
            if (b == true) {
                // drawable.collapseNode(aNode);
            } else {
                drawable.uncollapseNode(aNode);
            }
        }
        drawable.updateGraphics();
    }

}