package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.PreviewTableCellRenderer;
import fr.unice.bioinfo.thea.ontologyexplorer.PreviewTableModel;
import fr.unice.bioinfo.thea.util.Consts;

public class ClPreviewPanel extends JPanel {

    private JButton ignoredColumnsBtn;
    private JTable previewTable;
    private JButton ignoredRowsBtn;
    private JButton geneIDBtn;
    private JButton ignoredColTitlesBtn;
    private JScrollPane jsp;
    private JButton resetBtn;
    //  table model
    private static PreviewTableModel previewTableModel;
    /** Selection made by user. */
    private static int tableSelectionOp = Consts.SEL_NO_SELECTION;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N

    public ClPreviewPanel() {
        initComponents();
    }

    /** Initializes inner components. */
    private void initComponents() {
        ignoredColumnsBtn = new JButton();
        // the JTable initialization
        previewTable = new JTable();
        previewTableModel = new PreviewTableModel();
        previewTable = new JTable(previewTableModel);
        previewTable.setColumnSelectionAllowed(false);
        previewTable.setRowSelectionAllowed(false);
        previewTable.setTableHeader(null); //no table header
        previewTable.setDefaultRenderer(String.class,
                new PreviewTableCellRenderer());
        jsp = new JScrollPane(previewTable);
        jsp
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // personalize the selection process
        ListSelectionModel selM = previewTable.getSelectionModel();
        selM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                if (lsm.isSelectionEmpty()) {
                } else {
                    switch (tableSelectionOp) {
                    case Consts.SEL_IGNORED_ROWS:
                        NewClPanel.setMinIgnoredRow(lsm.getMinSelectionIndex());
                        NewClPanel.setMaxIgnoredRow(lsm.getMaxSelectionIndex());
                        previewTable.invalidate();
                        previewTable.repaint();

                        break;

                    case Consts.SEL_COLUMNS_LABELS:
                        NewClPanel.setColumnLabels(lsm.getMinSelectionIndex());
                        previewTable.invalidate();
                        previewTable.repaint();

                        break;
                    }
                }
            }
        });

        ListSelectionModel colSelM = previewTable.getColumnModel()
                .getSelectionModel();
        colSelM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                if (lsm.isSelectionEmpty()) {
                } else {
                    switch (tableSelectionOp) {
                    case Consts.SEL_IGNORED_COLUMNS:
                        NewClPanel.setMinIgnoredColumn(lsm
                                .getMinSelectionIndex());
                        NewClPanel.setMaxIgnoredColumn(lsm
                                .getMaxSelectionIndex());
                        previewTable.invalidate();
                        previewTable.repaint();

                        break;

                    case Consts.SEL_GENE_LABELS:
                        NewClPanel.setGeneLabels(lsm.getMinSelectionIndex());
                        previewTable.invalidate();
                        previewTable.repaint();

                        break;
                    }
                }
            }
        });
        ignoredRowsBtn = new JButton();
        geneIDBtn = new JButton();
        ignoredColTitlesBtn = new JButton();
        resetBtn = new JButton();
        CellConstraints cc = new CellConstraints();

        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec("max(min;100px)"),//NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(pref;200dlu):grow") }, new RowSpec[] {//NOI18N
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                                FormSpec.DEFAULT_GROW) }));

        //---- ignoredColumnsBtn ----
        ignoredColumnsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performIgnoredColumnsBtnAction(e);
            }
        });
        ignoredColumnsBtn.setText(bundle.getString("BTN_IgnoredColumns"));//NOI18N
        add(ignoredColumnsBtn, cc.xy(1, 1));
        add(jsp, cc.xywh(3, 1, 1, 11));

        //---- ignoredRowsBtn ----
        ignoredRowsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performIgnoredRowsBtnAction(e);
            }
        });
        ignoredRowsBtn.setText(bundle.getString("BTN_IgnoredRows"));//NOI18N
        add(ignoredRowsBtn, cc.xy(1, 3));

        //---- geneIDBtn ----
        geneIDBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performGeneIDBtnAction(e);
            }
        });
        geneIDBtn.setText(bundle.getString("BTN_GeneID"));//NOI18N
        add(geneIDBtn, cc.xy(1, 5));

        //---- ignoredColTitlesBtn ----
        ignoredColTitlesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performIgnoredColTitlesBtnAction(e);
            }
        });
        ignoredColTitlesBtn.setText(bundle.getString("BTN_IgnoredTitles"));//NOI18N
        add(ignoredColTitlesBtn, cc.xy(1, 7));

        //---- resetBtn ----
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performResetBtnAction(e);
            }
        });
        resetBtn.setText(bundle.getString("BTN_Reset"));//NOI18N
        add(resetBtn, cc.xy(1, 9));
    }

    public static int getNbColumns() {
        return previewTableModel.getColumnCount();
    }

    /**
     * Builds a pre-view of tabular data file using a JTable.
     */
    public static void previewDataFile(File tabularDataFile) {
        if (NewClPanel.getSelectedFormat() == Consts.TYPE_EISEN) {
            NewClPanel.setMinIgnoredRow(1);
            NewClPanel.setMaxIgnoredRow(1);
            NewClPanel.setMinIgnoredColumn(0);
            NewClPanel.setMaxIgnoredColumn(3);
            NewClPanel.setGeneLabels(1);
            NewClPanel.setColumnLabels(0);
        } else {
            NewClPanel.setMinIgnoredRow(-1);
            NewClPanel.setMaxIgnoredRow(-1);
            NewClPanel.setMinIgnoredColumn(-1);
            NewClPanel.setMaxIgnoredColumn(-1);
            NewClPanel.setGeneLabels(0);
            NewClPanel.setColumnLabels(-1);
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
                    NewClPanel.setMaxIgnoredRow(rowList.size() - 1);

                    if (NewClPanel.getMaxIgnoredRow() >= 0) {
                        NewClPanel.setMinIgnoredRow(0);
                    }

                    NewClPanel.setColumnLabels(rowList.size());
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

            previewTableModel.setData(rowList);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void performGeneIDBtnAction(ActionEvent e) {
        tableSelectionOp = Consts.SEL_GENE_LABELS;
        previewTable.setColumnSelectionAllowed(true);
        previewTable.setRowSelectionAllowed(false);
        previewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        previewTable.setSelectionBackground(new Color(128, 255, 128));

        if (NewClPanel.getGeneLabels() >= 0) {
            previewTable.setColumnSelectionInterval(NewClPanel.getGeneLabels(),
                    NewClPanel.getGeneLabels());
        }
    }

    private void performIgnoredColTitlesBtnAction(ActionEvent e) {
        tableSelectionOp = Consts.SEL_COLUMNS_LABELS;
        previewTable.setColumnSelectionAllowed(false);
        previewTable.setRowSelectionAllowed(true);
        previewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        previewTable.getSelectionModel().setLeadSelectionIndex(
                NewClPanel.getColumnLabels());
        previewTable.setSelectionBackground(new Color(192, 255, 192));

        if (NewClPanel.getColumnLabels() >= 0) {
            previewTable.setColumnSelectionInterval(NewClPanel
                    .getColumnLabels(), NewClPanel.getColumnLabels());
        }
    }

    private void performIgnoredColumnsBtnAction(ActionEvent e) {
        tableSelectionOp = Consts.SEL_IGNORED_COLUMNS;
        previewTable.setColumnSelectionAllowed(true);
        previewTable.setRowSelectionAllowed(false);
        previewTable
                .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        previewTable.setSelectionBackground(new Color(255, 128, 128));

        if (NewClPanel.getMinIgnoredColumn() >= 0) {
            previewTable.setColumnSelectionInterval(NewClPanel
                    .getMinIgnoredColumn(), NewClPanel.getMaxIgnoredColumn());
        }
    }

    private void performIgnoredRowsBtnAction(ActionEvent e) {
        tableSelectionOp = Consts.SEL_IGNORED_ROWS;
        previewTable.setColumnSelectionAllowed(false);
        previewTable.setRowSelectionAllowed(true);
        previewTable
                .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        previewTable.setSelectionBackground(new java.awt.Color(255, 192, 192));

        if (NewClPanel.getMinIgnoredRow() >= 0) {
            previewTable.setRowSelectionInterval(NewClPanel.getMinIgnoredRow(),
                    NewClPanel.getMaxIgnoredRow());
        }
    }

    private void performResetBtnAction(ActionEvent e) {
        NewClPanel.setMinIgnoredRow(-1);
        NewClPanel.setMaxIgnoredRow(-1);
        NewClPanel.setMinIgnoredColumn(-1);
        NewClPanel.setMaxIgnoredColumn(-1);
        NewClPanel.setGeneLabels(-1);
        NewClPanel.setColumnLabels(-1);
        previewTable.removeRowSelectionInterval(0,
                previewTable.getRowCount() - 1);
        previewTable.removeColumnSelectionInterval(0, previewTable
                .getColumnCount() - 1);
        previewTable.invalidate();
        previewTable.repaint();
    }

}