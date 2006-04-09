package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;
import fr.unice.bioinfo.thea.classification.editor.dlg.TermChooser;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ChooseAnnotationAction extends GenericAction {

    private Node aNode;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.Bundle"); // NOI18N;

    public ChooseAnnotationAction(String name, String accelerator,
            ImageIcon icon, String shortDescription,
            DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;

        List l = (List) (aNode.getProperty(Node.TERM_AND_SCORE));
        if ((l != null) && (l.size() > 1) && (aNode.getLabel() != null)
                && !aNode.getLabel().equals("")) {// NOI18N
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
        TermChooser chooser = new TermChooser(aNode, "", drawable);
        // create the hide button
        JButton hideBtn = new JButton(bundle.getString("LBL_HideButton_Name"));
        hideBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        Object[] options = { hideBtn };
        DialogDescriptor dd = new DialogDescriptor(chooser, aNode.getLabel(),
                false, options, null, DialogDescriptor.DEFAULT_ALIGN,
                HelpCtx.DEFAULT_HELP, null);
        dd.setClosingOptions(new Object[] { hideBtn });
        DialogDisplayer.getDefault().createDialog(dd).show();
        drawable.updateGraphics();
    }

}