package fr.unice.bioinfo.thea.classification.editor;

import java.awt.event.MouseEvent;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.Selection;

/**
 * This interface defines behaviour for the managing pop-up menus in the
 * classification editor.
 * @see {@link PopupMenuProviderImpl}for an implemantation.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface PopupMenuProvider {

    /**
     * Creates a popup menu to show when the mouse's right button is activated
     * on a Node from the shown tree in the classification editor.
     * @param e A {@link java.awt.event.MouseEvent}
     * @param drawable {@link Drawable}
     * @param aNode {@link Node}
     */
    public abstract void showNodePopupMenu(MouseEvent e,
            final DrawableClassification drawable, final Node aNode);

    /**
     * Creates a popup menu to show when the mouse's right button is activated
     * on a Node from the shown tree in the classification editor.
     * @param e A {@link java.awt.event.MouseEvent}
     * @param drawable {@link Drawable}
     * @param selection {@link Selection}
     */
    public abstract void showSelectionPopupMenu(MouseEvent e,
            final DrawableClassification drawable, final Selection selection);
}