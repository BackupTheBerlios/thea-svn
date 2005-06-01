package fr.unice.bioinfo.thea.classification.editor;

import java.awt.Graphics;
import java.beans.PropertyChangeListener;

/**
 * Objects to be drawn should implemnts this interface.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface Drawable extends PropertyChangeListener{
    /**
     * Draw using the given graphics context.
     * @param graphics A graphics context.
     */
    public void draw(Graphics graphics, boolean doClipping);

    /**
     * Updates the drawn graphics. The implementation of this method could
     * simply calls </i> invalidate() </i> and </i> repaint() </i> inherited
     * from the {@link javax.swing.JComponent }.
     */
    public void updateGraphics();

    /**
     * Repaint the specified rectangle. The implementation of this method could
     * simply calls the <i>repaint(int x,int y, int width, int height) </i> from
     * the {@link javax.swing.JComponent }.
     */
    public void repaintRectangle(int x, int y, int width, int height);
}