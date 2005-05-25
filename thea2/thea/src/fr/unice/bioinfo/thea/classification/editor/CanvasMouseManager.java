package fr.unice.bioinfo.thea.classification.editor;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.event.MouseInputAdapter;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.NodeSet;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class CanvasMouseManager extends MouseInputAdapter {

    /***/
    private DrawableClassification drawable;

    public CanvasMouseManager(DrawableClassification drawable) {
        super();
        this.drawable = drawable;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        final Point2D point2D = (Point2D) e.getPoint();
        Node rn = drawable.getCurrentRootNode();
        Node aNode = drawable.locateNode(rn, point2D);
        if (aNode == null) {
            return;
        }
        // get the selection's name.
        synchronized (drawable) {
            String name = drawable.getSelectionManager().getSelectionName();
            drawable.getSelectionManager().setSelectionName("Manual Selection");//??
        }

        if (e.isControlDown()) {
            drawable.getSelectionManager().setSelected(aNode, 0);
        } else if (e.isShiftDown()) {
            drawable.getSelectionManager().setSelected(aNode, -1);
        } else {
            drawable.getSelectionManager().setSelected(aNode, 1);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        final Point2D point2D = (Point2D) e.getPoint();
        Node rn = drawable.getCurrentRootNode();
        Node aNode = drawable.locateNode(rn, point2D);
        if (aNode != null) {
            return;
        }
        drawable.highlightSurroundingArea(null);
        drawable.setPopupMenuVisible(false);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        //        if (drawable.isShowingPopupMenu())
        //            return;

        final Point2D point2D = (Point2D) e.getPoint();
        Node rn = drawable.getCurrentRootNode();
        Node aNode = drawable.locateNode(rn, point2D);
        drawable.highlightSurroundingArea(aNode);
        // Calling updateGraphics() on drwable make the GUI
        // takes some seconds to refresh to view,
        // i'll see later whether to keep this call or not:
        //        drawable.updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3)
            return;
        final Point2D point2D = (Point2D) e.getPoint();
        Node rn = drawable.getCurrentRootNode();
        Node aNode = drawable.locateNode(rn, point2D);

        if (aNode == null) {
            List l = drawable.getSelectionManager().getSelections();
            // ns : number of selections.
            int ns = ((l == null) ? 0 : l.size());
            // Get the index of the expression value column
            // under the mouse pointer
            int expressionColumnIndex = drawable
                    .getExpressionColumnIndex(point2D);
            if ((expressionColumnIndex >= ns) || (expressionColumnIndex < 0)) {
                return;
            }
            NodeSet selection = (NodeSet) l.get(expressionColumnIndex);
            // create the popup menu to show the user activates
            // the mouse's left button on a set of nodes.
            // and show it.
            PopupMenuProviderImpl p = new PopupMenuProviderImpl();
            p.showSelectionPopupMenu(e, drawable, selection);
            return;
        } else if (aNode != null) {
            // create the popup menu to show the user activates
            // the mouse's left button on a Node.
            // and show it.
            PopupMenuProviderImpl p = new PopupMenuProviderImpl();
            p.showNodePopupMenu(e, drawable, aNode);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        //drawable.updateGraphics();
    }
}