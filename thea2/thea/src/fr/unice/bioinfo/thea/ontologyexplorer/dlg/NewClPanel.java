package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * A swing GUI that allows users to add a new classification to an ontology
 * node. It contains buttons that allow users to browse files from disk and give
 * a preview view those files.
 * @author Saïd El Kasmi
 */
public class NewClPanel extends JPanel {

    private static int minIgnoredRow = -1;
    private static int maxIgnoredRow = -1;
    private static int minIgnoredColumn = -1;
    private static int maxIgnoredColumn = -1;
    private static int geneLabels = -1;
    private static int columnLabels = -1;

    public static int TYPE_UNDEFINED = 0;
    public static int TYPE_EISEN = 1;
    public static int TYPE_NEWICK = 2;
    public static int TYPE_SOTA = 3;
    public static int TYPE_XML = 4;
    public static int TYPE_UNCLUSTERED = 5;
    public static int SEL_NO_SELECTION = 0;
    public static final int SEL_IGNORED_COLUMNS = 1;
    public static final int SEL_IGNORED_ROWS = 2;
    public static final int SEL_GENE_LABELS = 3;
    public static final int SEL_COLUMNS_LABELS = 4;
    
    
    public static String[] format = { "Eisen: GTR & CDT)",//NOI18N
            "New Hampshire: newick", //NOI18N
            "Sota", "Unclustered data" //NOI18N
    };

    /** selected data format. */
    private static int selectedFormat;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N
    private JComponent clSeparator;
    private JLabel clNameLbl;
    private JTextField clNameField;
    private JLabel clHintLbl;
    private JTextField clHintField;

    private JComponent dataSeparator;
    private JLabel cdfLbl;
    private JTextField cdfField;
    private JButton cdfBtn;
    private JLabel tdfLbl;
    private JTextField tdfField;
    private JButton tdfBtn;
    private JLabel dataFormatLbl;
    private JComboBox dataFormatComboBox;
    private JComponent previewSeparator;
    private JTable previewTable;
    private JPanel self;
    private ClPreviewPanel previewPanel;

    //  Files
    private File clusteredDataFile = null;
    private File tabularDataFile = null;

    //  user's actions
    private boolean typeSelectionMadeByUser = false;
    private boolean tabularDataFileSelectionMadeByUser = false;

    public NewClPanel() {
        initComponents();
        self = this;
    }

    /**
     * Initialize the inner components of this swing compoenent.
     */
    private void initComponents() {
        // Create separators
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        dataSeparator = compFactory.createSeparator(bundle
                .getString("LBL_DataSeparator")); //NOI18N
        previewSeparator = compFactory.createSeparator(bundle
                .getString("LBL_PreviewSeparator")); //NOI18N
        clSeparator = compFactory.createSeparator(bundle
                .getString("LBL_ClSeparator"));//NOI18N
        clNameLbl = new JLabel();
        clNameField = new JTextField();
        clHintLbl = new JLabel();
        clHintField = new JTextField();

        cdfLbl = new JLabel();
        cdfField = new JTextField();
        cdfBtn = new JButton();
        tdfLbl = new JLabel();
        tdfField = new JTextField();
        tdfBtn = new JButton();
        dataFormatLbl = new JLabel();
        dataFormatComboBox = new JComboBox(format);

        //      Add action listener to buttons:
        cdfBtn.addActionListener(new BrowseCDFBtnActionListener());
        tdfBtn.addActionListener(new BrowseTDFBtnActionListener());

        // previewTable initialization
        previewTable = new JTable();
        previewPanel = new ClPreviewPanel();

        CellConstraints cc = new CellConstraints();

        // add border
        setBorder(Borders.DIALOG_BORDER);

        // set a layout
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;150dlu):grow"),//NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(min;10px)") }, new RowSpec[] {//NOI18N
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
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                                FormSpec.DEFAULT_GROW) }));

        add(clSeparator, cc.xywh(1, 1, 5, 1));
        //      ---- clNameLbl ----
        clNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        clNameLbl.setText(bundle.getString("LBL_ClName"));//NOI18N
        add(clNameLbl, cc.xy(1, 3));
        clNameField.setToolTipText(bundle.getString("HINT_ClName"));
        add(clNameField, cc.xywh(3, 3, 3, 1));

        //---- clHintLbl ----
        clHintLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        clHintLbl.setText(bundle.getString("LBL_ClHint"));//NOI18N
        add(clHintLbl, cc.xy(1, 5));
        clHintField.setToolTipText(bundle.getString("HINT_ClHint"));
        add(clHintField, cc.xywh(3, 5, 3, 1));
        add(dataSeparator, cc.xywh(1, 7, 5, 1));

        //		---- dataFormatLbl ----
        dataFormatLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        dataFormatLbl.setText(bundle.getString("LBL_DF")); //NOI18N
        add(dataFormatLbl, cc.xy(1, 9));
        add(dataFormatComboBox, cc.xywh(3, 9, 3, 1));

        //---- cdfLbl ----
        cdfLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cdfLbl.setText(bundle.getString("LBL_CDF")); //NOI18N
        add(cdfLbl, cc.xy(1, 11));
        add(cdfField, cc.xy(3, 11));

        //---- cdfBtn ----
        cdfBtn.setBorder(new EtchedBorder());
        cdfBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif"))); //NOI18N
        add(cdfBtn, cc.xy(5, 11));

        //---- tdfLbl ----
        tdfLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        tdfLbl.setText(bundle.getString("LBL_TDF")); //NOI18N
        add(tdfLbl, cc.xy(1, 13));
        add(tdfField, cc.xy(3, 13));

        //---- tdfBtn ----
        tdfBtn.setBorder(new EtchedBorder());
        tdfBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif"))); //NOI18N
        add(tdfBtn, cc.xy(5, 13));
        add(previewSeparator, cc.xywh(1, 15, 5, 1));
        add(previewPanel, cc.xywh(1, 17, 5, 1, CellConstraints.DEFAULT,
                CellConstraints.FILL));

    }

    /**
     * Processes the selected clustered data file
     */
    private void performCDFAction(File file) {
        if (file == null) {
            cdfField.setText("");//NOI18N

            return;
        }

        if (!file.canRead()) {
            cdfField.setText("");//NOI18N

            return;
        }

        clusteredDataFile = file;

        String fileName = file.getAbsolutePath();
        cdfField.setText(fileName);

        // tries to guess the name of the tabularDataFileTfd
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            return;
        }

        String baseFileName = fileName.substring(0, dotIndex);
        String extension = fileName.substring(dotIndex);

        if (baseFileName.length() == 0) {
            return;
        }

        if (extension.length() == 0) {
            return;
        }

        String assoclusteredDataFileExtension = null;

        if (extension.equalsIgnoreCase(".gtr")) {//NOI18N
            // cluster format
            assoclusteredDataFileExtension = "cdt";//NOI18N

            if (!typeSelectionMadeByUser) {
                dataFormatComboBox.setSelectedIndex(TYPE_EISEN);
                selectedFormat = TYPE_EISEN;
            }
        } else if (extension.equalsIgnoreCase(".sot")) {//NOI18N
            // sota format
            assoclusteredDataFileExtension = "sdt";//NOI18N

            if (!typeSelectionMadeByUser) {
                dataFormatComboBox.setSelectedIndex(TYPE_SOTA);
                selectedFormat = TYPE_SOTA;
            }
        } else if (extension.equalsIgnoreCase(".nw")//NOI18N
                || extension.equalsIgnoreCase(".nh")) {//NOI18N
            // newick format
            if (!typeSelectionMadeByUser) {
                dataFormatComboBox.setSelectedIndex(TYPE_NEWICK);
                selectedFormat = TYPE_NEWICK;
            }
        } else if (extension.equalsIgnoreCase(".xml")) {//NOI18N
            // xml
            if (!typeSelectionMadeByUser) {
                //                dataFormatComboBox.setSelectedIndex(TYPE_XML);
                //                selectedFormat = TYPE_XML;
            }
        } else {
            if (!typeSelectionMadeByUser) {
                //                dataFormatComboBox.setSelectedIndex(TYPE_UNDEFINED);
                //                selectedFormat = TYPE_UNDEFINED;
            }
        }

        if (tabularDataFileSelectionMadeByUser == true) {
            return;
        }

        if ((selectedFormat == TYPE_EISEN) || (selectedFormat == TYPE_NEWICK)
                || (selectedFormat == TYPE_SOTA)) {
            try {
                File assoclusteredDataFile = new File(baseFileName + "."//NOI18N
                        + assoclusteredDataFileExtension);

                if (assoclusteredDataFile.canRead()) {
                    tabularDataFile = assoclusteredDataFile;

                    ClPreviewPanel.previewDataFile(tabularDataFile);
                } else {
                    assoclusteredDataFile = new File(baseFileName + ".txt");//NOI18N

                    if (assoclusteredDataFile.canRead()) {
                        tabularDataFile = assoclusteredDataFile;

                        ClPreviewPanel.previewDataFile(tabularDataFile);
                    }
                }
            } catch (NullPointerException npe) {
            }

            if (tabularDataFile == null) {
                return;
            }

            tdfField.setText(tabularDataFile.getAbsolutePath());
        }
    }

    /**
     * Processes the selected tabular data file
     */
    private void performTDFAction(File file) {
        if (file == null) {
            tdfField.setText("");

            return;
        }

        if (!file.canRead()) {
            tdfField.setText("");//NOI18N

            return;
        }

        if (!typeSelectionMadeByUser && (clusteredDataFile == null)) {
            dataFormatComboBox.setSelectedIndex(TYPE_UNCLUSTERED);
            selectedFormat = TYPE_UNCLUSTERED;
            typeSelectionMadeByUser = false;
        }

        tabularDataFile = file;
        tdfField.setText(file.getAbsolutePath());
        tabularDataFileSelectionMadeByUser = true;

        ClPreviewPanel.previewDataFile(tabularDataFile);
    }

    /**
     * @return Returns the clusteredDataFile.
     */
    public File getClusteredDataFile() {
        return clusteredDataFile;
    }

    /**
     * @return Returns the tabularDataFile.
     */
    public File getTabularDataFile() {
        return tabularDataFile;
    }

    /**
     * ActionListener attached to the button used to browse to select a
     * clustered data file.
     */
    private class BrowseCDFBtnActionListener implements ActionListener {
        public BrowseCDFBtnActionListener() {
        }

        /*
         * (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            chooser.setCurrentDirectory(new File(OESettings.getInstance()
                    .getLastBrowsedDirectory()));
            int r = chooser.showOpenDialog(self);

            if (r != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();

            //          Save the new used path
            OESettings.getInstance().setLastBrowsedDirectory(file.getParent());
            performCDFAction(file);
        }
    }

    /**
     * ActionListener attached to the button used to browse to select a tabular
     * data file.
     */
    private class BrowseTDFBtnActionListener implements ActionListener {
        public BrowseTDFBtnActionListener() {
        }

        /*
         * (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            //            chooser.setCurrentDirectory(new
            // File(CESettings.getInstance()
            //                                                                             .getCurrentDirectory()));
            int r = chooser.showOpenDialog(self);

            if (r != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();

            // Save the new used path
            //CESettings.getInstance().setCurrentDirectory(file.getParent());
            performTDFAction(file);
        }
    }

    /**
     * @return Returns the columnLabels.
     */
    public static int getColumnLabels() {
        return columnLabels;
    }

    /**
     * @param columnLabels The columnLabels to set.
     */
    public static void setColumnLabels(int columnLabels) {
        NewClPanel.columnLabels = columnLabels;
    }

    /**
     * @return Returns the geneLabels.
     */
    public static int getGeneLabels() {
        return geneLabels;
    }

    /**
     * @param geneLabels The geneLabels to set.
     */
    public static void setGeneLabels(int geneLabels) {
        NewClPanel.geneLabels = geneLabels;
    }

    /**
     * @return Returns the maxIgnoredColumn.
     */
    public static int getMaxIgnoredColumn() {
        return maxIgnoredColumn;
    }

    /**
     * @param maxIgnoredColumn The maxIgnoredColumn to set.
     */
    public static void setMaxIgnoredColumn(int maxIgnoredColumn) {
        NewClPanel.maxIgnoredColumn = maxIgnoredColumn;
    }

    /**
     * @return Returns the maxIgnoredRow.
     */
    public static int getMaxIgnoredRow() {
        return maxIgnoredRow;
    }

    /**
     * @param maxIgnoredRow The maxIgnoredRow to set.
     */
    public static void setMaxIgnoredRow(int maxIgnoredRow) {
        NewClPanel.maxIgnoredRow = maxIgnoredRow;
    }

    /**
     * @return Returns the minIgnoredColumn.
     */
    public static int getMinIgnoredColumn() {
        return minIgnoredColumn;
    }

    /**
     * @param minIgnoredColumn The minIgnoredColumn to set.
     */
    public static void setMinIgnoredColumn(int minIgnoredColumn) {
        NewClPanel.minIgnoredColumn = minIgnoredColumn;
    }

    /**
     * @return Returns the minIgnoredRow.
     */
    public static int getMinIgnoredRow() {
        return minIgnoredRow;
    }

    /**
     * @param minIgnoredRow The minIgnoredRow to set.
     */
    public static void setMinIgnoredRow(int minIgnoredRow) {
        NewClPanel.minIgnoredRow = minIgnoredRow;
    }

    /**
     * @return Returns the selectedFormat.
     */
    public static int getSelectedFormat() {
        return selectedFormat;
    }

    /**
     * @param selectedFormat The selectedFormat to set.
     */
    public static void setSelectedFormat(int selectedFormat) {
        NewClPanel.selectedFormat = selectedFormat;
    }

    /**
     * @return
     */
    public static int getNbColumns() {
        return ClPreviewPanel.getNbColumns();
    }

    public String getClName() {
        return clNameField.getText();
    }

    public String getClDescription() {
        return clHintField.getText();
    }
}