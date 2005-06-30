package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import fr.unice.bioinfo.thea.ontologyexplorer.dlg.NewClPanel;

/**
 * @author SAÏD, EL KASMI.
 */
public class PreviewTableCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
            Object labelInfo, boolean isSelected, boolean hasFocus, int row,
            int column) {
        Component comp = super.getTableCellRendererComponent(table, labelInfo,
                isSelected, hasFocus, row, column);
        Color bgColor = null;

        if ((row >= NewClPanel.getMinIgnoredRow())
                && (row <= NewClPanel.getMaxIgnoredRow())) {
            bgColor = new Color(255, 192, 192);
        }

        if ((column >= NewClPanel.getMinIgnoredColumn())
                && (column <= NewClPanel.getMaxIgnoredColumn())) {
            bgColor = new Color(255, 128, 128);
        }

        if (column == NewClPanel.getGeneLabels()) {
            bgColor = new Color(128, 255, 128);
        }

        if (row == NewClPanel.getColumnLabels()) {
            bgColor = new Color(192, 255, 192);
        }

        if (bgColor != null) {
            comp.setBackground(bgColor);
        } else if (isSelected) {
            comp.setBackground(table.getSelectionBackground());
        } else {
            comp.setBackground(Color.white);
        }

        return comp;
    }
}