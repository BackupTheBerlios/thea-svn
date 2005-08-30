package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.api.components.TableSorter;
import fr.unice.bioinfo.thea.classification.Score;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class TermChooserTableView extends JPanel {

    private JTable table;

    private JScrollPane jsp;

    private List scores;

    private Resource selectedTerm;

    /** Columns names. */
    private String[] columnNames;

    /** Data */
    private Object[][] data;

    private Vector vector = new Vector();

    public TermChooserTableView(List scores) {
        this.scores = scores;
        init();
    }

    private void init() {
        TermsTableModel model = new TermsTableModel();
        TableSorter sorter = new TableSorter(model);
        table = new JTable(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter.setTableHeader(table.getTableHeader());

        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // Ignore extra messages.
                if (e.getValueIsAdjusting())
                    return;

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    // no rows are selected
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    // selectedRow is selected
                    // selectedTerm = (Resource) data[selectedRow][0];
                    selectedTerm = (Resource) vector.get(selectedRow);
                }
            }
        });

        jsp = new JScrollPane(table);
        jsp.getViewport().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(jsp, BorderLayout.CENTER);
    }

    /**
     * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
     */
    public class TermsTableModel extends AbstractTableModel {

        public TermsTableModel() {
            columnNames = new String[] { "Term", "Label", "Score",// NOI18N
                    "Represented" };// NOI18N
            data = new Object[scores.size()][columnNames.length];
            Iterator scoresIt = scores.iterator();
            int i = -1;
            int j = 0;
            int rowSelected = 0;

            while (scoresIt.hasNext()) {
                j = 0;
                i++;
                Score s = (Score) scoresIt.next();
                Resource aResource = s.getTerm();
                ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                        .getResourceFactory();
                data[i][j] = aResource.getDisplayAcc();
                vector.add(i, aResource);
                try {
                    StringValue sv = (StringValue) aResource
                            .getTarget(resourceFactory
                                    .getResource(OWLProperties.getInstance()
                                            .getNodeNameProperty()));

                    if (sv != null) {
                        data[i][j + 1] = new String(sv.getValue());
                    } else {
                        data[i][j + 1] = new String("");// NOI18N
                    }
                } catch (AllontoException ae) {
                }
                String score = s.getFormattedScore();
                data[i][j + 2] = score;
                String expression = String.valueOf(s.isOverexpressed());

                if (expression == "true") {// NOI18N
                    data[i][j + 3] = "over";// NOI18N
                } else {
                    data[i][j + 3] = "under";// NOI18N
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            if (data == null) {
                return 0;
            }
            return data.length;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    /** Returns the selected term. */
    public Resource getSelectedTerm() {
        return selectedTerm;
    }
}