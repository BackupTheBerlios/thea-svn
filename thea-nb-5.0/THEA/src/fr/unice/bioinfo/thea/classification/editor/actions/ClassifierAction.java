package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;
import fr.unice.bioinfo.thea.classification.editor.dlg.ProfileClassifierPanel;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ClassifierAction extends GenericAction {

    private Node aNode;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.Bundle"); // NOI18N;

    public ClassifierAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;
        this.setEnabled((aNode.isLeaf())
                && (aNode.getProperty(Node.MEASURES) != null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ProfileClassifierPanel p = new ProfileClassifierPanel(aNode);
        // create the hide button
        JButton hideBtn = new JButton(bundle.getString("LBL_HideButton_Name"));
        hideBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        Object[] options = { hideBtn };
        DialogDescriptor dd = new DialogDescriptor(p, aNode.getName(), false,
                options, null, DialogDescriptor.DEFAULT_ALIGN,
                HelpCtx.DEFAULT_HELP, null);
        dd.setClosingOptions(new Object[] { hideBtn });
        DialogDisplayer.getDefault().createDialog(dd).show();
    }

}