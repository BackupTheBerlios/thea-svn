package fr.unice.bioinfo.thea.classification.io.wizard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * <p>
 * This panel correspends to the second step in the wizard. It allows the user
 * choose files that form the classification.
 * </p>
 * @see {@link FilesChooserWizard}
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class FilesChooserPanel extends JPanel implements PropertyChangeListener {

    public FilesChooserPanel() {
        super();
        init();
    }

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.io.wizard.Bundle"); // NOI18N;

    private JButton cdfBtn;

    private JButton tdfBtn;

    private JLabel cdfLbl;

    private JLabel tdfLbl;

    private JTextField cdfField;

    private JTextField tdfField;

    private boolean isTabularDataFileSelected = false;

    private void init() {
        setName(bundle.getString("LBL_FilesChooserWizard"));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();
        Insets defaultInsets = new Insets(5, 5, 5, 5);
        // Add Children
        constraints.insets = defaultInsets;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;

        cdfLbl = new JLabel(bundle.getString("LBL_ClusterdDataFile"));
        gbl.setConstraints(cdfLbl, constraints);
        add(cdfLbl);

        constraints.gridx = 1;
        cdfField = new JTextField();
        cdfField.setMinimumSize(new Dimension(250, 22));
        cdfField.setPreferredSize(cdfField.getMinimumSize());
        gbl.setConstraints(cdfField, constraints);
        add(cdfField);

        constraints.gridx = 2;
        cdfBtn = new JButton();
        cdfBtn.setToolTipText(bundle.getString("TIP_BrowseBtn"));
        cdfBtn.setBorder(new EtchedBorder());
        cdfBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif"))); // NOI18N
        cdfBtn.addActionListener(new BrowseCDFBtnActionListener());
        gbl.setConstraints(cdfBtn, constraints);
        add(cdfBtn);

        constraints.gridx = 0;
        constraints.gridy = 1;

        tdfLbl = new JLabel(bundle.getString("LBL_TabularDataFile"));
        gbl.setConstraints(tdfLbl, constraints);
        add(tdfLbl);

        constraints.gridx = 1;
        tdfField = new JTextField();
        tdfField.setMinimumSize(new Dimension(250, 22));
        tdfField.setPreferredSize(tdfField.getMinimumSize());
        gbl.setConstraints(tdfField, constraints);
        add(tdfField);

        constraints.gridx = 2;
        tdfBtn = new JButton();
        tdfBtn.setToolTipText(bundle.getString("TIP_BrowseBtn"));
        tdfBtn.setBorder(new EtchedBorder());
        tdfBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif"))); // NOI18N
        tdfBtn.addActionListener(new BrowseTDFBtnActionListener());
        gbl.setConstraints(tdfBtn, constraints);
        add(tdfBtn);

    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
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
            int r = chooser.showOpenDialog(WindowManager.getDefault()
                    .getMainWindow());
            if (r != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();

            // Save the new used path
            OESettings.getInstance().setLastBrowsedDirectory(file.getParent());
            performCDFAction(file);
        }
    }

    /**
     * Processes the selected clustered data file
     */
    private void performCDFAction(File file) {
        // No file available:
        if (file == null) {
            cdfField.setText("");// NOI18N
            return;
        }
        // read access:
        if (!file.canRead()) {
            cdfField.setText("");// NOI18N
            return;
        }
        // store file for next steps:
        ClassificationInfo.getInstance().setClusteredDataFile(file);
        // Print the absolute path:
        String fileName = file.getAbsolutePath();
        cdfField.setText(fileName);
        // tries to guess the name of the tabularDataFileTfd
        // when the format is UNCLUSTERED
        if (ClassificationInfo.getInstance().getFileFormat().equalsIgnoreCase(
                SupportedFormat.UNCLUSTERED)) {
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
            String string = null;
            if (extension.equalsIgnoreCase(".gtr")) {// NOI18N
                // cluster format
                string = "cdt";// NOI18N
            } else if (extension.equalsIgnoreCase(".sot")) {// NOI18N
                // sota format
                string = "sdt";// NOI18N
            } else if (extension.equalsIgnoreCase(".nw")// NOI18N
                    || extension.equalsIgnoreCase(".nh")) {// NOI18N
            }
            if (isTabularDataFileSelected == true) {
                return;
            }
            try {
                File tdf = new File(baseFileName + "."// NOI18N
                        + string);
                if (tdf.canRead()) {
                    ClassificationInfo.getInstance().setTabularDataFile(tdf);
                } else {
                    tdf = new File(baseFileName + ".txt");// NOI18N
                    if (tdf.canRead()) {
                        ClassificationInfo.getInstance()
                                .setTabularDataFile(tdf);
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace(System.out);
            }

            if (ClassificationInfo.getInstance().getTabularDataFile() == null) {
                return;
            }
            tdfField.setText(ClassificationInfo.getInstance()
                    .getTabularDataFile().getAbsolutePath());
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
            tdfField.setText("");// NOI18N
            return;
        }
        // if (!typeSelectionMadeByUser && (clusteredDataFile == null)) {
        // dataFormatComboBox.setSelectedIndex(TYPE_UNCLUSTERED);
        // selectedFormat = TYPE_UNCLUSTERED;
        // typeSelectionMadeByUser = false;
        // }
        ClassificationInfo.getInstance().setTabularDataFile(file);
        tdfField.setText(file.getAbsolutePath());
        isTabularDataFileSelected = true;
        // ClPreviewPanel.previewDataFile(ClassificationInfo.getInstance()
        // .getTabularDataFile());
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
            chooser.setCurrentDirectory(new File(OESettings.getInstance()
                    .getLastBrowsedDirectory()));
            int r = chooser.showOpenDialog(WindowManager.getDefault()
                    .getMainWindow());
            if (r != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();
            // Save the new used path
            OESettings.getInstance().setLastBrowsedDirectory(file.getParent());
            performTDFAction(file);
        }
    }
}