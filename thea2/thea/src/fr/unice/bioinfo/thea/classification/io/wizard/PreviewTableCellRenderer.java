package fr.unice.bioinfo.thea.classification.io.wizard;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for the Preview Table.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class PreviewTableCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
            Object labelInfo, boolean isSelected, boolean hasFocus, int row,
            int column) {
        Component comp = super.getTableCellRendererComponent(table, labelInfo,
                isSelected, hasFocus, row, column);
        Color bgColor = null;

        if ((row >= ClassificationInfo.getInstance().getMinIgnoredRow())
                && (row <= ClassificationInfo.getInstance().getMaxIgnoredRow())) {
            bgColor = new Color(255, 192, 192);
        }

        if ((column >= ClassificationInfo.getInstance().getMinIgnoredColumn())
                && (column <= ClassificationInfo.getInstance()
                        .getMaxIgnoredColumn())) {
            bgColor = new Color(255, 128, 128);
        }

        if (column == ClassificationInfo.getInstance().getGeneLabels()) {
            bgColor = new Color(128, 255, 128);
        }

        if (row == ClassificationInfo.getInstance().getColumnLabels()) {
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