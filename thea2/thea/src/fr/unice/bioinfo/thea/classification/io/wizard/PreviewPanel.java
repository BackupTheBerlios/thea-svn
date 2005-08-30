package fr.unice.bioinfo.thea.classification.io.wizard;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.openide.util.NbBundle;

/**
 * A swing component based on the {@link JTable}component used to pre-view the
 * classification befor opening it.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class PreviewPanel extends JPanel implements PropertyChangeListener {

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.io.wizard.Bundle"); // NOI18N;

    private JTable table;

    private PreviewTableModel model;

    private JScrollPane jsp;

    public PreviewPanel() {
        super();
        init();
        if (ClassificationInfo.getInstance().getTabularDataFile() != null) {
            preview(ClassificationInfo.getInstance().getTabularDataFile());
        }
    }

    private void init() {
        setName(bundle.getString("LBL_PreviewWizard"));// NOI18N
        setLayout(new BorderLayout());
        model = new PreviewTableModel();
        table = new JTable(model);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setTableHeader(null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setDefaultRenderer(String.class, new PreviewTableCellRenderer());
        jsp = new JScrollPane(table);
        add(jsp, BorderLayout.CENTER);
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
    }

    /**
     * Builds a pre-view of tabular data file using a {@link JTable}.
     */
    private void preview(File tabularDataFile) {
        if (ClassificationInfo.getInstance().getFileFormat() == SupportedFormat.EISEN) {
            ClassificationInfo.getInstance().setMinIgnoredRow(1);
            ClassificationInfo.getInstance().setMaxIgnoredRow(1);
            ClassificationInfo.getInstance().setMinIgnoredColumn(0);
            ClassificationInfo.getInstance().setMaxIgnoredColumn(3);
            ClassificationInfo.getInstance().setGeneLabels(1);
            ClassificationInfo.getInstance().setColumnLabels(0);
        } else {
            ClassificationInfo.getInstance().setMinIgnoredRow(-1);
            ClassificationInfo.getInstance().setMaxIgnoredRow(-1);
            ClassificationInfo.getInstance().setMinIgnoredColumn(-1);
            ClassificationInfo.getInstance().setMaxIgnoredColumn(-1);
            ClassificationInfo.getInstance().setGeneLabels(0);
            ClassificationInfo.getInstance().setColumnLabels(-1);
        }

        try {
            BufferedReader dataIO = new BufferedReader(new FileReader(
                    tabularDataFile));
            int nbLinesToRead = 20;
            String line = null;
            List rowList = new Vector();
            while (((line = dataIO.readLine()) != null) && (nbLinesToRead > 0)) {
                // insert blank between two consecutive tabs
                while (line.indexOf("\t\t") != -1) {
                    line = line.replaceAll("\t\t", "\tNA\t");
                }
                if (line.startsWith("#")) {
                    ClassificationInfo.getInstance().setMaxIgnoredRow(
                            rowList.size() - 1);
                    if (ClassificationInfo.getInstance().getMaxIgnoredRow() >= 0) {
                        ClassificationInfo.getInstance().setMinIgnoredRow(0);
                    }
                    ClassificationInfo.getInstance().setColumnLabels(
                            rowList.size());
                }
                StringTokenizer st = new StringTokenizer(line, "\t");
                List row = new Vector();
                while (st.hasMoreTokens()) {
                    try {
                        String cellData = st.nextToken();
                        row.add(cellData);
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                    }
                }
                rowList.add(row);
                nbLinesToRead -= 1;
            }
            ClassificationInfo.getInstance().setNbColumns(
                    model.getColumnCount());
            model.setData(rowList);
        } catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
    }

}