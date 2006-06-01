package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;


import org.openide.awt.MouseUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.util.actions.SystemAction;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.classification.editor.selection.SelectionEvent;
import fr.unice.bioinfo.thea.classification.editor.selection.SelectionListener;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.AbstractResourceNode;

/**
 * Dedicated view for ResourceNode properties
 * 
 * @author Claude Pasquier
 */

public class ResourceDetailedView extends JScrollPane {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -1639001987693376168L;

    //
    // components
    //

    transient protected JPanel panel;

    transient protected JLabel element;

    transient Set elementsWithActiveListeners = new HashSet();

    /** Explorer manager, valid when this view is showing */
    transient ExplorerManager manager;

    /** True, if the selection listener is attached. */
    transient boolean listenerActive;

    /** Listener to nearly everything */
    transient Listener managerListener;

    /**
     * weak variation of the listener for property change on the explorer
     * manager
     */
    transient PropertyChangeListener wlpc;

    /**
     * weak variation of the listener for vetoable change on the explorer
     * manager
     */
    transient VetoableChangeListener wlvc;

    /** popup */
    transient PopupSupport popupSupport;

    /**
     * Default constructor.
     */
    public ResourceDetailedView() {
        initialize();
        // no border, window system manages outer border itself
        setBorder(BorderFactory.createEmptyBorder());
        setViewportBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Initializes the view
     */
    private void initialize() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        setViewportView(panel);
        popupSupport = new PopupSupport();
        managerListener = new Listener();
        // ToolTipManager.sharedInstance().registerComponent(list);
        ToolTipManager.sharedInstance().registerComponent(panel);
    }

    /*
     * Initializes the view.
     */
    public void addNotify() {
        super.addNotify();
        // run under mutex

        ExplorerManager em = ExplorerManager.find(this);
        if (em != manager) {
            if (manager != null) {
                manager.removeVetoableChangeListener(wlvc);
                manager.removePropertyChangeListener(wlpc);
            }

            manager = em;

            manager
                    .addVetoableChangeListener(wlvc = org.openide.util.WeakListeners
                            .vetoableChange(managerListener, manager));
            manager
                    .addPropertyChangeListener(wlpc = org.openide.util.WeakListeners
                            .propertyChange(managerListener, manager));
            // model.setNode(manager.getExploredContext());
            //
            // updateSelection();
        } else {
            // bugfix #23509, the listener were removed --> add it again
            if (!listenerActive && (manager != null)) {
                manager.addVetoableChangeListener(wlvc = WeakListeners
                        .vetoableChange(managerListener, manager));
                // manager.addPropertyChangeListener(wlpc =
                // WeakListeners.propertyChange(managerListener, manager));
            }
        }

        if (!listenerActive) {
            listenerActive = true;
            // list.getSelectionModel().addListSelectionListener(managerListener);
            // model.addListDataListener(managerListener);

            Node diplayedResourceNode = manager.getExploredContext();
            JLabel rootElement = new JLabel("Details of "
                    + diplayedResourceNode.getName());
            panel.add(rootElement, BorderLayout.WEST);
            addResourceDetails(panel, diplayedResourceNode);
        }

    }

    /**
     * Removes listeners.
     */
    public void removeNotify() {
        super.removeNotify();
        listenerActive = false;
        Iterator it = elementsWithActiveListeners.iterator();
        while (it.hasNext()) {
            Component c = (Component) it.next();
            c.removeFocusListener(popupSupport);
            c.removeMouseListener(popupSupport);
        }
    }

    /**
     * Called when the list changed selection and the explorer manager should be
     * updated.
     * 
     * @param nodes
     *            list of nodes that should be selected
     * @param em
     *            explorer manager
     * @exception PropertyVetoException
     *                if the manager does not allow the selection
     */
    protected void selectionChanged(Node[] nodes, ExplorerManager em)
            throws PropertyVetoException {
        System.out.println("selection changed on view");
        em.setSelectedNodes(nodes);
    }

    /**
     * Called when explorer manager is about to change the current selection.
     * The view can forbid the change if it is not able to display such
     * selection.
     * 
     * @param nodes
     *            the nodes to select
     * @return false if the view is not able to change the selection
     */
    protected boolean selectionAccept(Node[] nodes) {
        System.out.println("selection changed on explorer");
        // if the selection is just the root context, confirm the selection
        if ((nodes.length == 1) && manager.getRootContext().equals(nodes[0])) {
            // XXX shouldn't this be exploredContext?
            return true;
        }

        return false;
    }

    /**
     * Shows selection.
     * 
     * @param indexes
     *            indexes of objects to select
     */
    protected void showSelection(int[] indexes) {
    }

    private void displayActions(Action[] a) {
        for (int i = 0; i < a.length; i++) {
            Action act = a[i];
            if (act instanceof SystemAction) {
                System.out.println("act=" + ((SystemAction) act).getName());
            }
            System.out.println(act);
        }
    }

    void createPopup(int xpos, int ypos, boolean contextMenu) {
        System.out.println("createPopup called");
        if (manager == null) {
            return;
        }

        JPopupMenu popup;
        contextMenu = true;
        System.out.println("createPopup called2, contextMenu=" + contextMenu);
        System.out.println("actions="
                + manager.getExploredContext().getActions(true));
        displayActions(manager.getExploredContext().getActions(true));
        System.out.println("contextactions="
                + manager.getExploredContext().getActions(false));
        displayActions(manager.getExploredContext().getActions(false));
        System.out.println("generalactions="
                + NodeOp.findActions(manager.getSelectedNodes()));
        displayActions(NodeOp.findActions(manager.getSelectedNodes()));
        if (contextMenu) {
            popup = Utilities.actionsToPopup(manager.getExploredContext()
                    .getActions(true), this);
        } else {
            Action[] actions = NodeOp.findActions(manager.getSelectedNodes());
            popup = Utilities.actionsToPopup(actions, this);
        }
        System.out.println("createPopup called3 (popup=" + popup);
        if ((popup != null) /* && (popup.getSubElements().length > 0) */) {
            java.awt.Point p = getViewport().getViewPosition();
            p.x = xpos - p.x;
            p.y = ypos - p.y;
            System.out.println("createPopup called-a");

            SwingUtilities.convertPointToScreen(p, ResourceDetailedView.this);
            Dimension popupSize = popup.getPreferredSize();
            System.out.println("createPopup called-b");
            Rectangle screenBounds = Utilities
                    .getUsableScreenBounds(getGraphicsConfiguration());
            System.out.println("createPopup called-c");
            if (p.x + popupSize.width > screenBounds.x + screenBounds.width)
                p.x = screenBounds.x + screenBounds.width - popupSize.width;
            if (p.y + popupSize.height > screenBounds.y + screenBounds.height)
                p.y = screenBounds.y + screenBounds.height - popupSize.height;
            System.out.println("createPopup called-d");
            SwingUtilities.convertPointFromScreen(p, ResourceDetailedView.this);
            System.out.println("createPopup called4 (popup=" + popup);
            System.out.println("popuplabel=" + popup.getLabel() + ", "
                    + popup.getComponentCount());
            popup.show(this, p.x, p.y);
        }
    }

    final class PopupSupport extends MouseUtils.PopupMouseAdapter implements
            ActionPerformer, Runnable, FocusListener {

        public void mouseClicked(MouseEvent e) {
            System.out.println("mouse clicked");
        }

        protected void showPopup(MouseEvent e) {
            // if (e.getSource() == element) {
            createPopup(e.getX(), e.getY(), true);
            // }
        }

        public void performAction(SystemAction act) {
            System.out.println("performAction");
            SwingUtilities.invokeLater(this);
        }

        public void run() {
            System.out.println("run");
        }

        CallbackSystemAction csa;

        public void focusGained(FocusEvent ev) {
            System.out.println("focus gained");
            // if (csa == null) {
            // System.out.println("csa is not null");
            // try {
            // Class popup = Class
            // .forName("org.openide.actions.PopupAction"); // NOI18N
            // csa = (CallbackSystemAction) CallbackSystemAction
            // .get(popup);
            // } catch (ClassNotFoundException e) {
            // Error err = new NoClassDefFoundError();
            // ErrorManager.getDefault().annotate(err, e);
            // throw err;
            // }
            // }
            // csa.setActionPerformer(this);
            // ev.consume();
        }

        public void focusLost(FocusEvent ev) {
            // if (csa != null
            // && (csa.getActionPerformer() instanceof PopupSupport)) {
            // csa.setActionPerformer(null);
            // }
        }
    }

    private final class Listener implements SelectionListener,
            VetoableChangeListener, PropertyChangeListener {

        public void selectionDone(SelectionEvent e) {
            System.out.println("selection done");
            // TODO Auto-generated method stub

        }

        public void selectionChanged(SelectionEvent e) {
            System.out.println("selection changed");
            // TODO Auto-generated method stub

        }

        public void selectionCleared(SelectionEvent e) {
            System.out.println("selection cleared");
            // TODO Auto-generated method stub

        }

        public void vetoableChange(PropertyChangeEvent evt)
                throws PropertyVetoException {
            System.out.println("vetoable change listener");

            // TODO Auto-generated method stub

        }

        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("property change listener");
            // TODO Auto-generated method stub

        }

    }

    /**
     * Called when selection has been changed. Make selection visible (at least
     * partly).
     */
    // private void updateSelection() {
    // Node[] sel = manager.getSelectedNodes();
    // System.out.println("updateselection for " + sel);
    // // int[] indices = new int[sel.length];
    // //
    // // // bugfix #27094, make sure a selection is visible
    // // int firstVisible = list.getFirstVisibleIndex();
    // // int lastVisible = list.getLastVisibleIndex();
    // // boolean ensureVisible = indices.length > 0;
    // // for (int i = 0; i < sel.length; i++) {
    // // VisualizerNode v = VisualizerNode.getVisualizer(null, sel[i]);
    // // indices[i] = model.getIndex(v);
    // // ensureVisible = ensureVisible
    // // && (indices[i] < firstVisible || indices[i] > lastVisible);
    // // }
    // //
    // // // going to change list because of E.M.'s order -- temp disable the
    // // // listener
    // // if (listenerActive)
    // // list.getSelectionModel().removeListSelectionListener(
    // // managerListener);
    // // try {
    // // showSelection(indices);
    // // if (ensureVisible) {
    // // list.ensureIndexIsVisible(indices[0]);
    // // }
    // // } finally {
    // // if (listenerActive)
    // // list.getSelectionModel().addListSelectionListener(
    // // managerListener);
    // // }
    // }
    private void addResourceDetails(Container panel, Node n) {
        Resource resource = ((AbstractResourceNode) n).getResource();

        Iterator prop_it = resource.getProperties().iterator();
        while (prop_it.hasNext()) {
            Resource prop = (Resource) prop_it.next();
            JLabel propLabel = new JLabel(prop.getName());
            propLabel.addFocusListener(popupSupport);
            propLabel.addMouseListener(popupSupport);
            propLabel.addPropertyChangeListener(managerListener);
            elementsWithActiveListeners.add(propLabel);
            panel.add(propLabel, BorderLayout.WEST);
            addTargetsDetails(panel, resource, prop);

        }
    }

    private void addTargetsDetails(Container panel, Resource resource,
            Resource prop) {
        // TODO implement a correct processing for this mathod
//        Set contextualTargets;
//        try {
//            contextualTargets = resource.getContextualTargets(prop);
//        } catch (AllontoException e) {
//            return;
//        }
//        if (contextualTargets == null) {
//            return;
//        }
//        String[] columnTitles = { "context", "target" };
//        Object[][] tableContent = new Object[contextualTargets.size()][2];
//        Iterator it = contextualTargets.iterator();
//        int ctr = 0;
//        while (it.hasNext()) {
//            Entity[] tuple = (Entity[]) it.next();
//            tableContent[ctr][0] = ((Resource)tuple[0]).getName();
//            if (tuple[1] instanceof Resource) {
//                Resource cntx = (Resource)tuple[1];
//                if (cntx.isConcrete()) {
//                    tableContent[ctr][1] = ((Resource)tuple[1]).getName();
//                } else {
//                    String content = "";
//                    Iterator cntx_it = cntx.getProperties().iterator();
//                    while (cntx_it.hasNext()) {
//                        Resource cntx_prop = (Resource)cntx_it.next();
//                        content += cntx_prop.getName() + " = ";
//                        content += cntx.getTargets(cntx_prop);
//                        if (cntx_it.hasNext()) {
//                            content += "\n";
//                        }
//                    }
//                    tableContent[ctr][1] = content;
//                }
//            } else if (tuple[1] instanceof StringValue) {
//                tableContent[ctr][1] = ((StringValue)tuple[1]).getValue();
//                
//            }
//            ctr++;
//        }
//        JTable p = new JTable(tableContent, columnTitles);
//        panel.add(p, BorderLayout.WEST);
    }

}