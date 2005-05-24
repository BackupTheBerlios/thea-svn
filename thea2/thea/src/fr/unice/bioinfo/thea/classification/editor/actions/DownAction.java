package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class DownAction extends GenericAction {

    private Node aNode;

    public DownAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;
        this.setEnabled(drawable.getCurrentRootNode() != aNode);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        drawable.showSubTree(aNode);
    }

}