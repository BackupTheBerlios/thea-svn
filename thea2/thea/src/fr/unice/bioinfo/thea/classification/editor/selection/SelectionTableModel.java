package fr.unice.bioinfo.thea.classification.editor.selection;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.unice.bioinfo.thea.classification.Node;

/**
 * A customized model for tables the Selection Viewer will create to hold
 * slections.
 * @author SAÏD, EL KASMI.
 */
public class SelectionTableModel extends AbstractTableModel {
    /** A list to contain nodes corresponding the selection. */
    private List nodes = new Vector();

    /**
     * Set a list of nodes.
     */
    public void setNodes(List nodes) {
        this.nodes = nodes;
        fireTableStructureChanged();
    }

    /**
     * Returns the {@link Node}correspending to the given index
     */
    public Node getNodeAt(int rowIndex) {
        Node node = (Node) nodes.get(rowIndex);

        return node;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        if (nodes.isEmpty()) {
            return 0;
        }

        Node node = (Node) nodes.get(0);
//        List measures = (List) node.getUserData("measures");
        List measures = (List) node.getProperty(Node.MEASURES);
        //        List measures = (List) node.getMeasures();

        if (measures == null) {
            return 1;
        } else {
            return measures.size() + 1;
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return nodes.size();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        Node node = (Node) nodes.get(rowIndex);

        if (columnIndex == 0) {
            value = node.getName();
        } else {
//            List measures = (List) node.getUserData("measures");
            List measures = (List) node.getProperty(Node.MEASURES);
            //List measures = (List) node.getMeasures();
            value = ((Double) measures.get(columnIndex - 1));
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        } else {
            return Double.class;
        }
    }
}