package fr.unice.bioinfo.thea.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.editor.dlg.ZoomingPanel;
import fr.unice.bioinfo.thea.editor.selection.SelectionEditor;
import fr.unice.bioinfo.thea.editor.settings.CESettings;
import fr.unice.bioinfo.thea.editor.util.Consts;
import fr.unice.bioinfo.thea.editor.util.Discretization;
import fr.unice.bioinfo.thea.editor.util.EisenUtil;
import fr.unice.bioinfo.thea.editor.util.MeasuresFileReader;
import fr.unice.bioinfo.thea.editor.util.NewickUtil;
import fr.unice.bioinfo.thea.editor.util.SotaUtil;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;

/**
 * The main swing GUI used by the classification viewer module. It extends the
 * {@link TopComponent}from the netbeans open API and encapsulates a {@link
 * CECanvas}object.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class CEditor extends TopComponent implements TreeSelectionListener,
        ChangeListener {
    //  Serial Version UID
    static final long serialVersionUID = 6855188441469780252L;
    /** preferred ID:ce as Classification Editor */
    private String PREFERRED_ID = "ce";//NOI18N
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.editor.Bundle"); //NOI18N
    /** A scrolle pane to contain the CECanvas */
    private JScrollPane scrollPane;
    /** the popup menu currently displayed */
    private JPopupMenu popup = null;
    /** A bean that contains informations about the classification to be edited */
    private ClassificationNodeInfo cni;
    /** A Canvas to draw into */
    private CECanvas canvas;
    /** A window to show selected nodes of the classification tree */
    private SelectionEditor selectionEditor;

    public CEditor(ClassificationNodeInfo cni) {
        super();
        this.cni = cni;
        // Give a title to this window
        setName(cni.getName());
        // Icon
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/editor/resources/ClassificationNodeIcon.gif"));//NOI18N
        // build the scroll pane and fit the CECanvas
        // into it
        canvas = new CECanvas();
        scrollPane = new JScrollPane(canvas);
        canvas.activate(true);
        canvas.addTreeSelectionListener(this);
        // Create the selection editor and dok it in the explorer mode
        selectionEditor = new SelectionEditor();
        selectionEditor.setName(selectionEditor.getName() + "[" + cni.getName()
                + "]");//NOI18N
        canvas.addSelectionListener(selectionEditor);
        Mode m = WindowManager.getDefault().findMode("explorer");//NOI18N
        if (m != null) {
            m.dockInto(selectionEditor);
        }
        selectionEditor.open();
        selectionEditor.requestActive();
        // Put the canvas in a JScrollPane
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        // Load classification
        load();
    }

    /** returns the preferred ID */
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /** This windows can be closed */
    public boolean canClose() {
        return true;
    }

    /** Implements what to do when this window's closed */
    protected void componentClosed() {
        super.componentClosed();
        // close the selection editor attached to this window
        selectionEditor.close();
    }

    /** Mange persistence for this windows */
    public int getPersistenceType() {
        return super.PERSISTENCE_ALWAYS;
    }

    public void load() {
        Node rootNode = canvas.getTreeRoot();
        File cf = cni.getCFile();
        File tf = cni.getTFile();
        int type = cni.getSelectedFormat();
        int indexOfFirstIgnoredRow = cni.getIndexOfFirstIgnoredRow();
        int indexOfLastIgnoredRow = cni.getIndexOfLastIgnoredRow();
        int indexOfFirstIgnoredColumn = cni.getIndexOfFirstIgnoredColumn();
        int indexOfLastIgnoredColumn = cni.getIndexOfLastIgnoredColumn();
        int indexOfGeneColumn = cni.getIndexOfGeneColumn();
        int indexOfTitleRow = cni.getIndexOfTitleRow();
        int nbColumns = cni.getNbColumns();

        long mem = 0;
        long t = 0;

        if (type == Consts.TYPE_NEWICK) {
            // load new hampshire (newick) format
            Runtime.getRuntime().gc();
            mem = Runtime.getRuntime().totalMemory()
                    - Runtime.getRuntime().freeMemory();
            t = System.currentTimeMillis();
            rootNode = new NewickUtil().load(cf);
            System.err.println("Time to read="
                    + (System.currentTimeMillis() - t));
            Runtime.getRuntime().gc();
            System.err.println("Memory needed="
                    + (Runtime.getRuntime().totalMemory()
                            - Runtime.getRuntime().freeMemory() - mem));

            if (rootNode == null) {
                return;
            }

            if (tf != null) {
                Map geneId2Measures = new MeasuresFileReader().load(tf,
                        indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
                        indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
                        indexOfGeneColumn, indexOfTitleRow, nbColumns);
                Iterator it = rootNode.getLeaves().iterator();

                while (it.hasNext()) {
                    Node leaf = (Node) it.next();
                    leaf.setUserData("measures", geneId2Measures.get(leaf
                            .getUserData("idInClassif")));
                }
            }

            collectInfo(rootNode);
        } else if (type == Consts.TYPE_EISEN) {
            // cluster format
            rootNode = new EisenUtil().load(cf, tf);
            collectInfo(rootNode);
        } else if (type == Consts.TYPE_SOTA) {
            // sota format
            rootNode = new SotaUtil().load(cf);

            if (rootNode == null) {
                return;
            }

            if (tf != null) {
                Map geneId2Measures = new MeasuresFileReader().load(tf,
                        indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
                        indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
                        indexOfGeneColumn, indexOfTitleRow, nbColumns);
                Iterator it = rootNode.getLeaves().iterator();

                while (it.hasNext()) {
                    Node leaf = (Node) it.next();
                    leaf.setUserData("measures", geneId2Measures.get(leaf
                            .getUserData("idInClassif")));
                }
            }

            collectInfo(rootNode);
        } else if (type == Consts.TYPE_UNCLUSTERED) {
            Map geneId2Measures = new MeasuresFileReader().load(tf,
                    indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
                    indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
                    indexOfGeneColumn, indexOfTitleRow, nbColumns);
            List nodes = new Vector();
            Iterator it = geneId2Measures.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String idInClassif = (String) entry.getKey();

                if ("*TITLE*".equals(idInClassif)) {
                    continue;
                }

                Node node = new Node();
                node.setName(idInClassif);
                node.setUserData("idInClassif", idInClassif);
                node.setUserData("measures", entry.getValue());
                node.setBranchLength(0);
                nodes.add(node);
            }

            rootNode = new Node();
            rootNode.setName("");
            rootNode.setUserData("idInClassif", "");
            rootNode.setChildren(nodes);
            collectInfo(rootNode);
        }
        canvas.setCurrentRootNode(rootNode);
        Runtime.getRuntime().gc();

        long mem2 = Runtime.getRuntime().freeMemory();
        long t2 = System.currentTimeMillis();
        repaint();
        setVisible(true);
        System.err.println("Time to draw=" + (System.currentTimeMillis() - t2));
        Runtime.getRuntime().gc();
        System.err.println("Memory needed="
                + (Runtime.getRuntime().totalMemory()
                        - Runtime.getRuntime().freeMemory() - mem2));
        System.err.println("Memory needed (total)="
                + (Runtime.getRuntime().totalMemory()
                        - Runtime.getRuntime().freeMemory() - mem));

        // show a dialog to ask if the user want to start annotation
        // immedialtely.
        //showAnnotationDialog();
    }

    /**
     * Collects info about the tree specified and store them as atributes of the
     * node
     * @param node the node to be examined
     */
    private void collectInfo(Node node) {
        /*
         * This method is called from load() method. It allways takes as
         * argument to root node. It should be placed inside the class Node
         * since it works on the comportment of a Node instance.
         */
        List leaves = node.getLeaves();
        if (leaves == null) {
            return;
        }
        if (leaves.size() == 0) {
            return;
        }
        Node firstLeaf = (Node) leaves.get(0);
        List measures = (List) firstLeaf.getUserData("measures");
        int nbMeasures = (measures == null) ? 0 : measures.size();
        node.setUserData("nbMeasures", new Integer(nbMeasures));
        if (nbMeasures == 0) {
            return;
        }
        Iterator it = leaves.iterator();
        List measuresTable = new Vector();

        while (it.hasNext()) {
            Node leaf = (Node) it.next();
            measures = (List) leaf.getUserData("measures");
            int nbMeas = (measures == null) ? 0 : measures.size();
            if (nbMeas != nbMeasures) {
                System.err
                        .println("Error : The measures of expression specified are not coherent throughout the file");
                System.err.println("     => verify the file");
                System.err.println("leaf=" + leaf.getName() + " has " + nbMeas
                        + " values");
                node.setUserData("nbMeasures", new Integer(0));
                return;
            }
            measuresTable.addAll(measures);
        }
        double minMeasure = ((Double) Collections.min(measuresTable))
                .doubleValue();
        Double medianValue = null;
        boolean logValues = false;

        if (minMeasure < 0) { //log values
            medianValue = new Double(0);
            logValues = true;
        } else {
            medianValue = new Double(1);
        }

        // The following bloc is to be moved/deleted
        if (CESettings.getInstance().isXpColorAutomaticSelected()) {
            Discretization d = new Discretization(measuresTable, logValues,
                    CESettings.getInstance().getSlots());
        } else if (CESettings.getInstance().isXpColorCustomizedSelected()) {
            Discretization d = new Discretization(measuresTable, CESettings
                    .getInstance().getPaletteLowerBounds(), CESettings
                    .getInstance().getPaletteColors(), CESettings.getInstance()
                    .getSlots());
        }

        // end of bloc
        measuresTable.add(medianValue);
        Collections.sort(measuresTable);

        int medianPos = measuresTable.indexOf(medianValue);
        int underExpDecileValue = medianPos / 10;
        int overExpDecileValue = (measuresTable.size() - medianPos) / 10;
        List underExpDeciles = new Vector();
        List overExpDeciles = new Vector();

        for (int i = 1; i < 10; i++) {
            underExpDeciles.add(measuresTable.get(medianPos
                    - (underExpDecileValue * i)));
            overExpDeciles.add(measuresTable.get(medianPos
                    + (overExpDecileValue * i)));
        }

        node.setUserData("underExpDeciles", underExpDeciles);
        node.setUserData("overExpDeciles", overExpDeciles);
        node.setUserData("minMeasure", Collections.min(measuresTable));
        node.setUserData("maxMeasure", Collections.max(measuresTable));
        System.err.println("Expression values (min="
                + Collections.min(measuresTable) + ", max="
                + Collections.max(measuresTable) + ")");
        System.err.println("underDecile=" + underExpDeciles);
        System.err.println("overDecile=" + overExpDeciles);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classificationviewer.TreeSelectionListener#nodeSelected(fr.unice.bioinfo.thea.classificationviewer.TreeSelectionEvent)
     */
    public void nodeSelected(TreeSelectionEvent e) {
        final Node node = (Node) e.getSelected();
        MouseEvent event = e.getMouseEvent();

        if (event.getButton() != MouseEvent.BUTTON3) {
            return;
        }

        if ((popup != null) && popup.isShowing()) {
            popup.setVisible(false);
        }

        popup = PopupFactory.getNodeSelectedPopup(e, this);

        canvas.setPopupMenu(popup);
        popup.show(event.getComponent(), event.getX(), event.getY());
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classificationviewer.TreeSelectionListener#nodeDeselected(fr.unice.bioinfo.thea.classificationviewer.TreeSelectionEvent)
     */
    public void nodeDeselected(TreeSelectionEvent e) {
        Node n = (Node) e.getSelected();
        MouseEvent event = e.getMouseEvent();

        if (event.getButton() != MouseEvent.BUTTON3) {
            return;
        }

        if (popup != null) {
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classificationviewer.TreeSelectionListener#nodeClicked(fr.unice.bioinfo.thea.classificationviewer.TreeSelectionEvent)
     */
    public void nodeClicked(TreeSelectionEvent e) {
        MouseEvent event = e.getMouseEvent();

        if (event.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        Node aNode = (Node) e.getSelected();
        String selectionName = canvas.getSelectionName();
        canvas.setSelectionName("Manual Selection");

        if (event.isControlDown()) {
            canvas.setSelected(aNode, 0);
        } else if (event.isShiftDown()) {
            canvas.setSelected(aNode, -1);
        } else {
            canvas.setSelected(aNode, 1);
        }

        List tas = (List) ((Node) e.getSelected()).getUserData("termAndScore");

        if (tas != null) {
            Iterator it = tas.iterator();

            //            while (it.hasNext()) {
            //                TermAndScore termAndScore = (TermAndScore) it.next();
            //                System.err.println(termAndScore.getTerm().getTerm() + "("
            //                        + termAndScore.getScore() + ")");
            //            }
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classificationviewer.TreeSelectionListener#nodeSetSelected(fr.unice.bioinfo.thea.classificationviewer.TreeSelectionEvent)
     */
    public void nodeSetSelected(TreeSelectionEvent e) {
        final NodeSet ns = (NodeSet) e.getSelected();
        MouseEvent event = e.getMouseEvent();

        if (event.getButton() != MouseEvent.BUTTON3) {
            return;
        }

        popup = PopupFactory.getNodeSetSelectedPopup(e, canvas);
        popup.show(event.getComponent(), event.getX(), event.getY());
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int zoom = (int) source.getValue();

        if (source == ZoomingPanel.getHorizontalZoomingSlider()) {
            canvas.zoom((double) source.getValue() / 10, ZoomingPanel
                    .getVerticalZoomingSlider().getValue());
        } else {
            canvas.zoom((double) ZoomingPanel.getHorizontalZoomingSlider()
                    .getValue() / 10, source.getValue());
        }
    }

    public CECanvas getCanvas() {
        return canvas;
    }

    public SelectionEditor getSelectionEditor() {
        return selectionEditor;
    }
}