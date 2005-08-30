package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class SelectAction extends GenericAction {

    private Node aNode;

    public SelectAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;
        this.setEnabled(!drawable.getSelectionManager().getSelectedNodes()
                .isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        drawable.getSelectionManager().createSelection();
        drawable.updateGraphics();
    }

}