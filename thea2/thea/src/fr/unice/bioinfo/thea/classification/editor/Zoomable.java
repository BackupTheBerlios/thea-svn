package fr.unice.bioinfo.thea.classification.editor;

/**
 * Defines the behaviour of a zoomable component.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public interface Zoomable {
    /**
     * Zooms in or out ine this component using given zooming factors.
     * @param zoomx Zoom factor on the x-axis.
     * @param zoomy Zomm factor on the y-axis.
     */
    public void zoom(double zoomx, double zoomy);

    /**
     * Returns the current zooming factor on the X direction.
     * @return Zooming factor on the X direction.
     */
    public double getCurrentZoomX();

    /**
     * Returns the current zooming factor on the Y direction.
     * @return Zooming factor on the Y direction.
     */
    public double getCurrentZoomY();

    /** Sets a customized cursor for zoomable components. */
    public void setZoomingMode();

    /** Sets the system default cursor. */
    public void setStandardMode();

    /**
     * Sets a zoom manager for this zoomable component.
     * @param zm {@link ZoomManager}instance.
     */
    public void setZoomManager(ZoomManager zm);

    /** Returns the zoom manager of this zoomable component. */
    public ZoomManager getZoomManager();
}