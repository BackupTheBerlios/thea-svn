package fr.unice.bioinfo.thea.editor.selection;

import java.util.EventListener;

/**
 * This interface defines patterns to be implemented by any component that
 * listens to <i>slections </i> from the <i>classificationviewer </i> module.
 * @author Claude Pasquier.
 * @author Saïd El Kasmi.
 */
public interface SelectionListener extends EventListener {
    /**
     * Logic to implement when a selection is done.
     */
    public void selectionDone(SelectionEvent e);

    /**
     * Logic to implement when a selection changes.
     */
    public void selectionChanged(SelectionEvent e);

    /**
     * Logic to implement when a selection is cleared.
     */
    public void selectionCleared(SelectionEvent e);
}