package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.NodeSet;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class IntersectAction extends GenericAction {

    private NodeSet selection;

    public IntersectAction(String name, String accelerator,
            ImageIcon icon, String shortDescription,
            DrawableClassification drawable, NodeSet selection) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.selection = selection;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        drawable.getSelectionManager()
        .intersectSelectionWithCurrent(selection);
        drawable.updateGraphics();
    }

}