package fr.unice.bioinfo.thea.classification.io.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.openide.util.NbBundle;

/**
 * <p>
 * This panel correspends to the first step. It allows the user give a name and
 * a description of the classification it is about opening and select the format
 * in which the classification is stored.
 * </p>
 * 
 * @see {@link DataFormatWizard}
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class DataFormatPanel extends JPanel implements PropertyChangeListener {

    public DataFormatPanel() {
        super();
        init();
    }

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.io.wizard.Bundle"); // NOI18N;

    private JLabel nameLbl;

    private JTextField nameField;

    private JLabel hintLbl;

    private JTextField hintField;

    private JLabel descriptionLbl;

    private JPanel descriptionPanel;

    private JLabel formatLbl;

    private JComboBox comboBox;

    private String[] extensions;

    private String[] descriptions;

    private int WIDTH = 300;

    /** Creates components. */
    private void init() {
        setName(bundle.getString("LBL_DataFormatWizard"));
        extensions = SupportedFormat.getExtensions();
        descriptions = SupportedFormat.getDescriptions();
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

        nameLbl = new JLabel();
        nameLbl.setText(bundle.getString("LBL_Name"));// NOI18N
        nameLbl.setToolTipText(bundle.getString("TIP_Name"));// NOI18N
        gbl.setConstraints(nameLbl, constraints);
        add(nameLbl);

        constraints.gridx = 1;
        nameField = new JTextField();
        nameField.setMinimumSize(new Dimension(200, 22));
        nameField.setPreferredSize(nameField.getMinimumSize());
        gbl.setConstraints(nameField, constraints);
        add(nameField);

        constraints.gridx = 0;
        constraints.gridy = 1;

        hintLbl = new JLabel();
        hintLbl.setText(bundle.getString("LBL_Description"));// NOI18N
        hintLbl.setToolTipText(bundle.getString("TIP_Description"));// NOI18N
        gbl.setConstraints(hintLbl, constraints);
        add(hintLbl);

        constraints.gridx = 1;
        hintField = new JTextField();
        hintField.setMinimumSize(new Dimension(200, 22));
        hintField.setPreferredSize(hintField.getMinimumSize());
        gbl.setConstraints(hintField, constraints);
        add(hintField);

        constraints.gridx = 0;
        constraints.gridy = 2;

        formatLbl = new JLabel();
        formatLbl.setText(bundle.getString("LBL_DataFormat"));// NOI18N
        gbl.setConstraints(formatLbl, constraints);
        add(formatLbl);

        constraints.gridx = 1;
        comboBox = new JComboBox(extensions);
        comboBox.setMinimumSize(new Dimension(250, 22));
        comboBox.setPreferredSize(comboBox.getMinimumSize());
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                descriptionLbl
                        .setText(descriptions[comboBox.getSelectedIndex()]);
                ClassificationInfo.getInstance().setFileFormat(
                        extensions[comboBox.getSelectedIndex()]);
            }
        });
        gbl.setConstraints(comboBox, constraints);
        add(comboBox);

        constraints.gridy = 3;
        descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.setBorder(new TitledBorder(bundle
                .getString("LBL_DescriptionPanelTitle")));// NOI18N
        descriptionPanel.setMinimumSize(new Dimension(WIDTH, 80));
        descriptionPanel.setPreferredSize(descriptionPanel.getMinimumSize());
        gbl.setConstraints(descriptionPanel, constraints);
        add(descriptionPanel);

        descriptionLbl = new JLabel();
        descriptionLbl.setText(descriptions[comboBox.getSelectedIndex()]);
        descriptionLbl.setMaximumSize(new Dimension(WIDTH, 22));
        descriptionLbl.setPreferredSize(comboBox.getMinimumSize());
        gbl.setConstraints(descriptionLbl, constraints);
        descriptionPanel.add(descriptionLbl, BorderLayout.CENTER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub
    }
}