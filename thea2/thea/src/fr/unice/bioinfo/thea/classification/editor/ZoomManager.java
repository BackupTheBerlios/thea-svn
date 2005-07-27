package fr.unice.bioinfo.thea.classification.editor;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.MouseInputAdapter;

import fr.unice.bioinfo.thea.classification.settings.CESettings;

/**
 * This class implements a zooming manager that works on objects of type
 * {@link Zoomable}.<br>
 * {@link Zoomable}components that have a mouse listener that is an instance of
 * this class would get a special cursor that shows a zooming icon. <br>
 * Zooming scenarios:
 * <ul>
 * <li>Zoom in: To zoom in the two directions (x-axis and y-axis), simply
 * activate the left button of the mouse. To zoom only on the x-axis direction,
 * keep the Ctrl key pressed and activate the mouse's left button and finally,
 * to zoom only on the y-direction, keep the Shif key pressed and activate the
 * mouse's left button.
 * <li>
 * <li>Zoom out:To zoom out the two directions (x-axis and y-axis), simply
 * activate the right button of the mouse. To zoom out only on the x-axis
 * direction, keep the Ctrl key pressed and activate the mouse's right button
 * and finally, to zoom only on the y-direction, keep the Shif key pressed and
 * activate the mouse's right button.</li>
 * <ul>
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ZoomManager extends MouseInputAdapter {
    /** The {@link Zoomable}components to be managet by this adapter. */
    private Zoomable zoomable;
    /** The zoom factor used for zooming using the zooming icon. */
    private double zoomFactorX;
    private double zoomFactorY;

    public ZoomManager(Zoomable zoomable) {
        this.zoomable = zoomable;
        zoomFactorX = CESettings.getInstance().getZoomFactorX();
        zoomFactorY = CESettings.getInstance().getZoomFactorY();
        CESettings.getInstance().addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        if (e.getPropertyName().equalsIgnoreCase(
                                CESettings.ZOOM_FACTOR_X)) {
                            zoomFactorX = ((Double) e.getNewValue())
                                    .doubleValue();
                        }
                        if (e.getPropertyName().equalsIgnoreCase(
                                CESettings.ZOOM_FACTOR_Y)) {
                            zoomFactorY = ((Double) e.getNewValue())
                                    .doubleValue();
                        }
                    }
                });
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        final double currentZoomX = zoomable.getCurrentZoomX();
        final double currentZoomY = zoomable.getCurrentZoomY();
        // zoom in when the user activate the left button
        if (e.getButton() == MouseEvent.BUTTON1) {
            // If the Shif key is pressed, zoom only on the x-axis
            if (e.isShiftDown()) {
                zoomable.zoom(currentZoomX + zoomFactorX, currentZoomY);
            }
            // If Ctrl key is pressed, zoom only on the y-axis
            if (e.isControlDown()) {
                zoomable.zoom(currentZoomX, currentZoomY + zoomFactorY);
            }
            // else, zoom over the whole rectangle
            zoomable.zoom(currentZoomX + zoomFactorX, currentZoomY
                    + zoomFactorY);
        }
        // zoom out when the user activate the left button
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (((currentZoomX - zoomFactorX) < 0)
                    || ((currentZoomY - zoomFactorY) < 0)) {
                return;
            }
            //If the Shif key is pressed, zoom only on the x-axis
            if (e.isShiftDown()) {
                zoomable.zoom(currentZoomX - zoomFactorX, currentZoomY);
            }
            // If Ctrl key is pressed, zoom only on the y-axis
            if (e.isControlDown()) {
                zoomable.zoom(currentZoomX, currentZoomY - zoomFactorY);
            }
            // else, zoom over the whole rectangle
            zoomable.zoom(currentZoomX - zoomFactorX, currentZoomY
                    - zoomFactorY);
        }
    }
}