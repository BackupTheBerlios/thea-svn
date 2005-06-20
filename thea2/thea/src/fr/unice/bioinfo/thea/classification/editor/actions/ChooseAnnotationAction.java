package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ChooseAnnotationAction extends GenericAction {

    private Node aNode;

    public ChooseAnnotationAction(String name, String accelerator,
            ImageIcon icon, String shortDescription,
            DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;

        List tas = (List) (aNode.getProperty(Node.TERM_AND_SCORE));
        if ((tas != null) && (tas.size() > 1) && (aNode.getLabel() != null)
                && !aNode.getLabel().equals("")) {//NOI18N
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        drawable.updateGraphics();
    }

}