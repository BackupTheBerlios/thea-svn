package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class UnfreezeAction extends GenericAction {

    private Selection selection;

    private Boolean b;

    public UnfreezeAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable,
            Selection selection) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.selection = selection;
        Boolean property = (Boolean) selection.getProperty(Selection.FROZEN);
        b = (property == null) ? Boolean.FALSE : property;
        this.setEnabled(!b.equals(Boolean.FALSE));
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
            aNode.addProperty(Node.FROZEN,
                    b.equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
        }
        selection.addProperty(Selection.FROZEN, Boolean.FALSE);
        //ns.addProperty(Selection.BG_COLOR, null);
    }

}