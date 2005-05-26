package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author SAÏD, EL KASMI.
 */
public class PreviewTableModel extends AbstractTableModel {
    /** a vector of vectors */
    private List data = new Vector();

    public PreviewTableModel() {
    }

    /**
     * Setter for data.
     */
    public void setData(List data) {
        this.data = data;
        fireTableStructureChanged();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        if (data.isEmpty()) {
            return 0;
        }

        return (((List) data.get(data.size() - 1)).size());
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return data.size();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        List row = (List) data.get(rowIndex);

        if (columnIndex < (row.size())) {
            return row.get(columnIndex);
        } else {
            return "";
        }
    }
}