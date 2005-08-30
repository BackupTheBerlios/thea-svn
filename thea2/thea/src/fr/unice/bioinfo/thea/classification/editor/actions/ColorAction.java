package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;

import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ColorAction extends GenericAction {

    private Selection selection;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.Bundle"); // NOI18N;

    public ColorAction(String name, String accelerator, ImageIcon icon,
            String shortDescription, DrawableClassification drawable,
            Selection selection) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.selection = selection;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Color color = JColorChooser.showDialog(WindowManager.getDefault()
                .getMainWindow(), bundle.getString("Title_ColorDialog"),
                selection.getColor());
        selection.setColor(color);
        drawable.updateGraphics();
    }

}