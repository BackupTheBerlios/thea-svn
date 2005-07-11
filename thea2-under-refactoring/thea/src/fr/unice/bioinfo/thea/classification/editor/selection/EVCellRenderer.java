package fr.unice.bioinfo.thea.classification.editor.selection;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import fr.unice.bioinfo.thea.classification.editor.util.Discretization;

/**
 * A customized cell renderer for tables used to hold selections.
 * @author Claude Pasquier.
 * @author Saïd El Kasmi.
 */
public class EVCellRenderer extends JLabel implements TableCellRenderer {
    public EVCellRenderer() {
        setOpaque(true);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object o,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(Discretization.getColor((Double) o));

        return this;
    }
}