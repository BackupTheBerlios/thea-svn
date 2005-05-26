package fr.unice.bioinfo.thea.classification.editor.selection;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Claude Pasquier.
 * @author Saïd El Kasmi.
 */
public class SelectionTableMouseListener extends MouseAdapter {
    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) {
            return;
        }
    }
}