package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class SaveAction extends GenericAction {

    private Selection selection;

    public SaveAction(String name, String accelerator, ImageIcon icon,
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
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(WindowManager.getDefault()
                .getMainWindow());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file == null) {
            return;
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(file)));
            Collection collection = selection.getNodes();
            Iterator collectionIt = collection.iterator();
            Node aNode = null;
            while (collectionIt.hasNext()) {
                aNode = (Node) collectionIt.next();
                out.println(aNode.getName());
            }
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
        drawable.getSelectionManager().copy(selection);
    }

}