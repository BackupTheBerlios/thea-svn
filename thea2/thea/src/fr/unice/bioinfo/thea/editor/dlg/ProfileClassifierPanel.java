package fr.unice.bioinfo.thea.editor.dlg;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.editor.Node;
import fr.unice.bioinfo.thea.editor.util.Discretization;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ProfileClassifierPanel extends JPanel {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.editor.dlg.Bundle"); //NOI18N

    private Node aNode;
    private JLabel baseLbl;
    private JTextField geneNameField;
    private JButton sortBtn;
    private JTable table;
    private ClTableModel model;
    private JScrollPane scroll;

    /** Create a profile classifier panel using a selected node. */
    public ProfileClassifierPanel(Node aNode) {
        // First of all,create all widgets.
        initComponents();

        this.aNode = aNode;
        geneNameField.setText(aNode.getName());
        List nodes = new LinkedList();
        nodes.add(aNode);
        model.setNodesList(nodes);

        TableColumnModel tcm = table.getColumnModel();

        for (int cnt = 3; cnt < tcm.getColumnCount(); cnt++) {
            TableColumn tc = tcm.getColumn(cnt);
            tc.setPreferredWidth(table.getRowHeight());
            tc.setMaxWidth(table.getRowHeight());
            tc.setMinWidth(table.getRowHeight());
            tc.setResizable(false);
        }
    }

    private void initComponents() {

        baseLbl = new JLabel();
        geneNameField = new JTextField();
        sortBtn = new JButton();
        CellConstraints cc = new CellConstraints();

        // border and layout
        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.PREF_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.PREF_COLSPEC },
                new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT,
                                FormSpec.DEFAULT_GROW) }));

        //---- baseLbl ----
        baseLbl.setText(bundle.getString("LBL_Base"));//NOI18N
        baseLbl.setToolTipText(bundle.getString("HINT_Base"));//NOI18N
        add(baseLbl, cc.xywh(1, 1, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- geneNameField ----
        geneNameField.setEditable(false);
        geneNameField.setBackground(Color.WHITE);
        add(geneNameField, cc.xywh(3, 1, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- sortBtn ----
        sortBtn.setText(bundle.getString("LBL_Sort"));//NOI18N
        sortBtn.setToolTipText(bundle.getString("HINT_Sort"));//NOI18N
        sortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Node root = aNode;
                while (root.getParent() != null) {
                    root = root.getParent();
                }
                List nodesList = root.getLeaves();
                model.setNodesList(nodesList);
                model.sortNodes(aNode);
                TableColumnModel tcm = table.getColumnModel();
                for (int i = 3; i < tcm.getColumnCount(); i++) {
                    TableColumn tc = tcm.getColumn(i);
                    tc.setPreferredWidth(table.getRowHeight());
                    tc.setMaxWidth(table.getRowHeight());
                    tc.setMinWidth(table.getRowHeight());
                    tc.setResizable(false);
                }
            }
        });
        add(sortBtn, cc.xywh(5, 1, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        // table
        model = new ClTableModel();
        table = new JTable(model);
        // No header for table.
        table.setTableHeader(null);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setDefaultRenderer(Double.class, new ExpValuesCellRenderer());
        scroll = new JScrollPane(table);
        add(scroll, cc.xywh(1, 3, 5, 1, CellConstraints.FILL,
                CellConstraints.FILL));
    }

    private class ClTableModel extends AbstractTableModel {
        private List rows = new Vector();

        public void setNodesList(List nodes) {
            rows = new Vector();
            Iterator iterator = nodes.iterator();
            while (iterator.hasNext()) {
                Node aNode = (Node) iterator.next();
                RowContent rowContent = new RowContent(aNode, 0, (List) aNode
                        .getUserData("measures"));
                rows.add(rowContent);
            }
            // fire changes
            fireTableStructureChanged();
        }

        public Node getNodeAt(int rowIndex) {
            RowContent rowContent = (RowContent) rows.get(rowIndex);
            return rowContent.getNode();
        }

        public int getColumnCount() {
            if (rows.isEmpty()) {
                return 0;
            }
            RowContent rowContent = (RowContent) rows.get(0);
            List measures = rowContent.getMeasures();
            if (measures == null) {
                return 3;
            } else {
                return measures.size() + 3;
            }
        }

        public Class getColumnClass(int columnIndex) {
            if (columnIndex < 3) {
                return String.class;
            } else {
                return Double.class;
            }
        }

        public int getRowCount() {
            return rows.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Object value = null;
            RowContent rowContent = (RowContent) rows.get(rowIndex);
            if (columnIndex == 0) {
                value = String.valueOf(rowIndex + 1);
            } else if (columnIndex == 1) {
                value = rowContent.getNode().getName();
            } else if (columnIndex == 2) {
                value = String.valueOf(rowContent.getScore());
            } else {
                List measures = rowContent.getMeasures();
                value = ((Double) measures.get(columnIndex - 3));
            }
            return value;
        }

        public void sortNodes(Node aNode) {
            List baseMeasures = (List) aNode.getUserData("measures");
            int nbMeasures = baseMeasures.size();
            Iterator it = rows.iterator();
            while (it.hasNext()) {
                RowContent rowContent = (RowContent) it.next();
                List measures = rowContent.getMeasures();
                double score = 0;
                for (int i = 0; i < nbMeasures; i++) {
                    double val1 = ((Double) baseMeasures.get(i)).doubleValue();
                    double val2 = ((Double) measures.get(i)).doubleValue();
                    score += Math.pow(val1 - val2, 2);
                }
                score = Math.sqrt(score);
                rowContent.setScore(score);
            }
            Collections.sort(rows, new RowComparator());
            Collections.reverse(rows);
            fireTableStructureChanged();
        }
    }

    private class RowContent { // the content of a row
        private double score;
        private Node node;
        private List measures;

        public RowContent(Node node, double score, List measures) {
            this.node = node;
            this.score = score;
            this.measures = measures;
        }

        public Node getNode() {
            return node;
        }

        public double getScore() {
            return score;
        }

        public List getMeasures() {
            return measures;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }

    private class RowComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            double score1 = ((RowContent) o1).getScore();
            double score2 = ((RowContent) o2).getScore();

            if (score1 > score2) {
                return -1;
            } else if (score1 == score2) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    class ExpValuesCellRenderer extends JLabel implements TableCellRenderer {
        public ExpValuesCellRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            setBackground(Discretization.getColor((Double) value));
            return this;
        }
    }
}