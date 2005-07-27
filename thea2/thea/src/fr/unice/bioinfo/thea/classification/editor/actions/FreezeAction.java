package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class FreezeAction extends GenericAction {

    private Selection selection;

    public FreezeAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable,
            Selection selection) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.selection = selection;
        this.setEnabled(!selection.isFrozen());
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Collection nodes = selection.getNodes();
        Iterator iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Node aNode = (Node) iterator.next();
            aNode.addProperty(Node.FROZEN, new Boolean(selection.isFrozen()));
        }
        selection.setFrozen(true);
        selection.setBackgroundColor(new Color(192, 255, 255));
        drawable.updateGraphics();
    }

}