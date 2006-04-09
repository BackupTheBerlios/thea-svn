package fr.unice.bioinfo.thea.classification.editor.selection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * A graphical component that extends {@link TopComponent}and works as an
 * add-on in the classification viewer. It simply draws selections made by the
 * user in the classification viewer.
 * 
 * @author Claude Pasquier.
 * @author Saïd El Kasmi.
 */
public class SelectionEditor extends TopComponent implements SelectionListener {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 8011348427057189911L;

    /** Singleton instance */
    private static SelectionEditor DEFAULT = null;

    /** preferred ID */
    private String PREFERRED_ID = "selectioneditor";

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.selection.Bundle"); // NOI18N;

    /** A {@link JTabbedPane}used to hold multiples selections. */
    private JTabbedPane tabbedPane;

    /** List of all tables the Selection Viewer wil create. */
    private List tables;

    /** comment this */
    private Map selId2Index;

    /**
     * Creates a swing compoenents that extends {@link TopCompoenent}
     */
    public SelectionEditor() {
        setName(bundle.getString("LBL_SelectionComponent_Name")); // NOI18N
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/editor/resources/SelectionEditor16.gif"));
        // Make background white
        setBackground(Color.WHITE);
        // Initialize map
        selId2Index = new HashMap();
        // Initialize list of tables
        tables = new Vector();
        // Add tabbedPane:
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, tabbedPane);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.windows.TopComponent#preferredID()
     */
    protected String preferredID() {
        return PREFERRED_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.windows.TopComponent#getPersistenceType()
     */
    public int getPersistenceType() {
        return super.PERSISTENCE_NEVER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.unice.bioinfo.thea.classificationviewer.selection.SelectionListener#selectionDone(fr.unice.bioinfo.thea.classificationviewer.selection.SelectionEvent)
     */
    public void selectionDone(SelectionEvent e) {
        String selId = e.getSelectionId();
        String selName = e.getSelectionName();
        String selLabel = e.getSelectionLabel();
        Color selColor = e.getSelectionColor();
        Color selBgColor = e.getSelectionBgColor();
        String globalHits = e.getNbGlobalHits();
        String localHits = e.getNbLocalHits();
        List selectedNodes = e.getSelectedNodes();
        Integer index = (Integer) selId2Index.get(selId);

        if (index == null) {
            SelectionTableModel model = new SelectionTableModel();
            JTable table = new JTable(model);
            table.addMouseListener(new SelectionTableMouseListener());
            table.setDefaultRenderer(Double.class, new EVCellRenderer());
            table.setTableHeader(null);
            tables.add(0, table);

            JScrollPane jsp = new JScrollPane(table,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jsp.getViewport().setBackground(Color.WHITE);
            tabbedPane.add(jsp, 0);

            Set entrySet = selId2Index.entrySet();
            Iterator it = entrySet.iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();

                if (entry.getValue() == null) {
                    continue;
                }

                int newValue = ((Integer) entry.getValue()).intValue() + 1;
                entry.setValue(new Integer(newValue));
            }

            index = new Integer(0);
            selId2Index.put(selId, index);
        }

        int indx = index.intValue();

        if ((selBgColor != null) && (selLabel != null)) {
            tabbedPane.setBackgroundAt(indx, selBgColor);
            tabbedPane.setForegroundAt(indx, selColor);
            tabbedPane.setTitleAt(indx, selLabel);
        } else {
            tabbedPane.setBackgroundAt(indx, selColor);
            tabbedPane.setForegroundAt(indx, selColor);
            tabbedPane.setTitleAt(indx, "Untitled");
        }

        if ((selectedNodes != null)) {
            JTable table = (JTable) tables.get(indx);
            SelectionTableModel model = (SelectionTableModel) table.getModel();
            model.setNodes(selectedNodes);

            TableColumnModel tableCM = table.getColumnModel();

            for (int i = 1; i < tableCM.getColumnCount(); i++) {
                TableColumn tableCol = tableCM.getColumn(i);
                tableCol.setPreferredWidth(table.getRowHeight());
                tableCol.setMaxWidth(table.getRowHeight());
                tableCol.setMinWidth(table.getRowHeight());
                tableCol.setResizable(false);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.unice.bioinfo.thea.classificationviewer.selection.SelectionListener#selectionChanged(fr.unice.bioinfo.thea.classificationviewer.selection.SelectionEvent)
     */
    public void selectionChanged(SelectionEvent e) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.unice.bioinfo.thea.classificationviewer.selection.SelectionListener#selectionCleared(fr.unice.bioinfo.thea.classificationviewer.selection.SelectionEvent)
     */
    public void selectionCleared(SelectionEvent e) {
        String selId = e.getSelectionId();
        Integer index = (Integer) selId2Index.get(selId);

        if (index == null) {
            // IOProvider ioProvider = (IOProvider)
            // Lookup.getDefault().lookup(IOProvider.class);
            // InputOutput io = ioProvider.getIO("Selection Viewer", true);
            // io.getOut().println("Trying to delete a non existant row ... ");

            return;
        }

        int indx = index.intValue();
        tabbedPane.removeTabAt(indx);
        tables.remove(indx);

        selId2Index.remove(selId);

        Set entrySet = selId2Index.entrySet();
        Iterator it = entrySet.iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();

            if (entry.getValue() == null) {
                continue;
            }

            int value = ((Integer) entry.getValue()).intValue();

            if (value > indx) {
                entry.setValue(new Integer(value - 1));
            }
        }
    }
}