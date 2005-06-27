package fr.unice.bioinfo.thea.classification.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.JScrollPane;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.selection.SelectionEditor;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;

/**
 * The main swing GUI used by the classification viewer module. It extends the
 * {@link TopComponent}from the netbeans open API and encapsulates a {@link
 * CECanvas}object.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class CEditor extends TopComponent {
    //  Serial Version UID
    static final long serialVersionUID = 6855188441469780252L;
    /** preferred ID:ce as Classification Editor */
    private String PREFERRED_ID = "ce";//NOI18N
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.Bundle"); //NOI18N
    /** A bean that contains informations about the classification to be edited */
    private ClassificationNodeInfo cni;
    /** A Canvas to draw into */
    private Canvas canvas;
    /** A window to show selected nodes of the classification tree */
    private SelectionEditor selectionEditor;

    private CEditor() {
        super();
        // build the scroll pane and fit the CECanvas
        // into it
        canvas = new Canvas();
        ModeBar modeBar = new ModeBar((Zoomable) canvas);
        JScrollPane scrollPane = new JScrollPane(canvas);
        // Create the selection editor and dok it in the explorer mode
        //        selectionEditor = new SelectionEditor();
        //        Mode m = WindowManager.getDefault().findMode("explorer");//NOI18N
        //        if (m != null) {
        //            m.dockInto(selectionEditor);
        //        }
        //        selectionEditor.open();
        //        selectionEditor.requestActive();
        // Put the canvas in a JScrollPane
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(modeBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public CEditor(ClassificationNodeInfo cni) {
        this();
        this.cni = cni;
        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals("annotationChanged")) { //NOI18N
                    canvas.updateGraphics();
                }
                if (event.getPropertyName().equals("showPhysicallyAdjacents")) {//NOI18N
                    canvas.getSelectionManager().removeSelectedNodes();
                    canvas.updateGraphics();
                }
                if (event.getPropertyName().equals("colocalized")) {//NOI18N
                    canvas.getSelectionManager().removeSelectedNodes();
                    canvas.getSelectionManager().setSelected(
                            (Collection) event.getNewValue(), 1);
                    canvas.updateGraphics();
                }
            }
        };
        cni.getClassification().addPropertyChangeListener(pcl);
        // Give a title to this window
        setName(cni.getName());
        // Icon
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/CEditorIcon16.gif"));//NOI18N
        //        selectionEditor.setName(selectionEditor.getName() + "[" +
        // cni.getName()
        //                + "]");//NOI18N
        // Load classification
        // load();
        canvas.setCurrentRootNode(cni.getClassification()
                .getClassificationRootNode());
    }

    public CEditor(Node aNode) {
        this();
        //Give a title to this window
        setName(aNode.getName());
        //Node rootNode = canvas.getTreeRoot();
        aNode.init();
        canvas.setCurrentRootNode(aNode);
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
        //selectionEditor.close();
    }

    /** Mange persistence for this windows */
    public int getPersistenceType() {
        return super.PERSISTENCE_ALWAYS;
    }

    //    public void load() {
    //        Node rootNode = canvas.getClassificationRootNode();
    //        File cf = cni.getCFile();
    //        File tf = cni.getTFile();
    //        int type = cni.getSelectedFormat();
    //        int indexOfFirstIgnoredRow = cni.getIndexOfFirstIgnoredRow();
    //        int indexOfLastIgnoredRow = cni.getIndexOfLastIgnoredRow();
    //        int indexOfFirstIgnoredColumn = cni.getIndexOfFirstIgnoredColumn();
    //        int indexOfLastIgnoredColumn = cni.getIndexOfLastIgnoredColumn();
    //        int indexOfGeneColumn = cni.getIndexOfGeneColumn();
    //        int indexOfTitleRow = cni.getIndexOfTitleRow();
    //        int nbColumns = cni.getNbColumns();
    //
    //        long mem = 0;
    //        long t = 0;
    //
    //        if (type == Consts.TYPE_NEWICK) {
    //            // load new hampshire (newick) format
    //            Runtime.getRuntime().gc();
    //            mem = Runtime.getRuntime().totalMemory()
    //                    - Runtime.getRuntime().freeMemory();
    //            t = System.currentTimeMillis();
    //            rootNode = new NewickUtil().load(cf);
    //            System.err.println("Time to read="
    //                    + (System.currentTimeMillis() - t));
    //            Runtime.getRuntime().gc();
    //            System.err.println("Memory needed="
    //                    + (Runtime.getRuntime().totalMemory()
    //                            - Runtime.getRuntime().freeMemory() - mem));
    //
    //            if (rootNode == null) {
    //                return;
    //            }
    //
    //            if (tf != null) {
    //                Map geneId2Measures = new MeasuresFileReader().load(tf,
    //                        indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
    //                        indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
    //                        indexOfGeneColumn, indexOfTitleRow, nbColumns);
    //                Iterator it = rootNode.getLeaves().iterator();
    //
    //                while (it.hasNext()) {
    //                    Node leaf = (Node) it.next();
    //                    // leaf.setUserData("measures", geneId2Measures.get(leaf
    //                    // .getUserData("idInClassif")));
    //                    leaf.addProperty(Node.MEASURES, geneId2Measures.get(leaf
    //                            .getProperty(Node.ID_IN_CLASSIF)));
    //                }
    //            }
    //
    //            rootNode.init();
    //        } else if (type == Consts.TYPE_EISEN) {
    //            // cluster format
    //            rootNode = new EisenUtil().load(cf, tf);
    //            rootNode.init();
    //        } else if (type == Consts.TYPE_SOTA) {
    //            // sota format
    //            rootNode = new SotaUtil().load(cf);
    //
    //            if (rootNode == null) {
    //                return;
    //            }
    //
    //            if (tf != null) {
    //                Map geneId2Measures = new MeasuresFileReader().load(tf,
    //                        indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
    //                        indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
    //                        indexOfGeneColumn, indexOfTitleRow, nbColumns);
    //                Iterator it = rootNode.getLeaves().iterator();
    //
    //                while (it.hasNext()) {
    //                    Node leaf = (Node) it.next();
    //                    // leaf.setUserData("measures", geneId2Measures.get(leaf
    //                    // .getUserData("idInClassif")));
    //                    leaf.addProperty(Node.MEASURES, geneId2Measures.get(leaf
    //                            .getProperty(Node.ID_IN_CLASSIF)));
    //                }
    //            }
    //
    //            rootNode.init();
    //        } else if (type == Consts.TYPE_UNCLUSTERED) {
    //            Map geneId2Measures = new MeasuresFileReader().load(tf,
    //                    indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
    //                    indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
    //                    indexOfGeneColumn, indexOfTitleRow, nbColumns);
    //            List nodes = new Vector();
    //            Iterator it = geneId2Measures.entrySet().iterator();
    //
    //            while (it.hasNext()) {
    //                Map.Entry entry = (Map.Entry) it.next();
    //                String idInClassif = (String) entry.getKey();
    //
    //                if ("*TITLE*".equals(idInClassif)) {
    //                    continue;
    //                }
    //
    //                Node node = new Node();
    //                node.setName(idInClassif);
    //                // node.setUserData("idInClassif", idInClassif);
    //                node.addProperty(Node.ID_IN_CLASSIF, idInClassif);
    //                // node.setUserData("measures", entry.getValue());
    //                node.addProperty(Node.MEASURES, entry.getValue());
    //                node.setBranchLength(0);
    //                nodes.add(node);
    //            }
    //
    //            rootNode = new Node();
    //            rootNode.setName("");
    //            // rootNode.setUserData("idInClassif", "");
    //            rootNode.addProperty(Node.ID_IN_CLASSIF, "");
    //            rootNode.setChildren(nodes);
    //            rootNode.init();
    //        }
    //        canvas.setCurrentRootNode(rootNode);
    //        cni.setClassification(new Classification(rootNode));
    //        Runtime.getRuntime().gc();
    //
    //        long mem2 = Runtime.getRuntime().freeMemory();
    //        long t2 = System.currentTimeMillis();
    //        repaint();
    //        setVisible(true);
    //        System.err.println("Time to draw=" + (System.currentTimeMillis() - t2));
    //        Runtime.getRuntime().gc();
    //        System.err.println("Memory needed="
    //                + (Runtime.getRuntime().totalMemory()
    //                        - Runtime.getRuntime().freeMemory() - mem2));
    //        System.err.println("Memory needed (total)="
    //                + (Runtime.getRuntime().totalMemory()
    //                        - Runtime.getRuntime().freeMemory() - mem));
    //    }

    public Canvas getCanvas() {
        return canvas;
    }

    public SelectionEditor getSelectionEditor() {
        return selectionEditor;
    }
}