package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class IntersectAction extends GenericAction {

    private Selection selection;

    public IntersectAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable,
            Selection selection) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.selection = selection;
        this.setEnabled(!drawable.getSelectionManager().getSelectedLeaves(true)
                .isEmpty());
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        drawable.getSelectionManager().intersect(selection);
        drawable.updateGraphics();
    }

}