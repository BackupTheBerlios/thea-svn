package fr.unice.bioinfo.thea.core.connection.wizard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openide.WizardDescriptor;

import fr.unice.bioinfo.thea.core.connection.ConnectionSettings;

/**
 * This class is used together with DataBaseSettingsWizardPanel to make the user
 * enter necessary settings to perform the DataBase connection.
 * @author SAÏD, EL KASMI.
 */
public class DataBaseSettingsPanel extends JPanel implements
        PropertyChangeListener {

    // widgets
    private JLabel dataAdapterLbl;

    private JComboBox dataAdapterCombo;

    private JLabel dataBaseDriverLbl;

    private JTextField dataBaseDriverTxtFld;

    private JLabel dataBaseUrlLbl;

    private JTextField dataBaseUrlTxtFld;

    /**
     * Builds a DataBaseSettingsPanel instance.
     */
    public DataBaseSettingsPanel() {
        super();
        init(this);
        ConnectionSettings.getInstance().addPropertyChangeListener(this);
    }

    // Initialize widgets and panel
    private void init(JPanel self) {
        // Bean Properties
        self.setName("DataBase Settings Panel");
        // sets Bounds
        self.setBounds(23, 18, 366, 145);
        // set Layout
        GridBagLayout gridBagLayout = new GridBagLayout();
        self.setLayout(gridBagLayout);
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
        //DataAdapaterLbl
        dataAdapterLbl = new JLabel("Data Adapter:");
        dataAdapterLbl.setToolTipText("Data adapter.");
        dataAdapterLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        dataAdapterLbl.setBounds(133, 173, 100, 22);
        dataAdapterLbl.setPreferredSize(new Dimension(100, 22));
        dataAdapterLbl.setMinimumSize(dataAdapterLbl.getPreferredSize());
        gridBagLayout.setConstraints(dataAdapterLbl, constraints);
        self.add(dataAdapterLbl);
        //DataAdapter ComboBox
        dataAdapterCombo = new JComboBox();
        dataAdapterCombo.setBounds(188, 173, 226, 22);
        dataAdapterCombo.setPreferredSize(new Dimension(226, 22));
        dataAdapterCombo.setMinimumSize(dataAdapterCombo.getPreferredSize());
        //      Get the settings instance
        ConnectionSettings settings = ConnectionSettings.getInstance();
        // List adapters
        for (int cnt = 0; cnt < settings.getAvailableAdapters().length; cnt++) {
            dataAdapterCombo.addItem(settings.getAvailableAdapters()[cnt]);
        }
        // Add a listener to listen to selected items
        dataAdapterCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                ConnectionSettings.getInstance().setLastSelectedDataAdapter(
                        dataAdapterCombo.getSelectedItem().toString());
            }
        });
        dataAdapterCombo.updateUI();
        constraints.gridx = 1;
        gridBagLayout.setConstraints(dataAdapterCombo, constraints);
        self.add(dataAdapterCombo);
        //DataBaseDriverLbl
        dataBaseDriverLbl = new JLabel("DataBase Driver:");
        dataBaseDriverLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        dataBaseDriverLbl
                .setToolTipText("Driver used to connect to the DataBase.");
        dataBaseDriverLbl.setBounds(78, 189, 100, 22);
        dataBaseDriverLbl.setPreferredSize(new Dimension(100, 22));
        dataBaseDriverLbl.setMinimumSize(dataBaseDriverLbl.getPreferredSize());
        constraints.gridx = 0;
        constraints.gridy = 1;
        gridBagLayout.setConstraints(dataBaseDriverLbl, constraints);
        self.add(dataBaseDriverLbl);
        //DataBaseDriverTxtFld
        dataBaseDriverTxtFld = new JTextField();
        dataBaseDriverTxtFld.setBounds(188, 189, 100, 22);
        dataBaseDriverTxtFld.setPreferredSize(new Dimension(100, 22));
        dataBaseDriverTxtFld.setMinimumSize(dataBaseDriverTxtFld
                .getPreferredSize());
        //        dataBaseDriverTxtFld.setText(ConnectionSettings.getInstance()
        //                .getDataBaseDriver());
        constraints.gridx = 1;
        gridBagLayout.setConstraints(dataBaseDriverTxtFld, constraints);
        self.add(dataBaseDriverTxtFld);
        //DataBaseUrlLbl
        dataBaseUrlLbl = new JLabel("DataBase URL:");
        dataBaseUrlLbl.setToolTipText("DataBase location.");
        dataBaseUrlLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        dataBaseUrlLbl.setBounds(78, 205, 100, 22);
        dataBaseUrlLbl.setPreferredSize(new Dimension(100, 22));
        dataBaseUrlLbl.setMinimumSize(dataBaseUrlLbl.getPreferredSize());
        constraints.gridx = 0;
        constraints.gridy = 2;
        gridBagLayout.setConstraints(dataBaseUrlLbl, constraints);
        self.add(dataBaseUrlLbl);
        //DataBaseUrlTxtFld
        dataBaseUrlTxtFld = new JTextField();
        dataBaseUrlTxtFld.setBounds(188, 205, 237, 22);
        dataBaseUrlTxtFld.setPreferredSize(new Dimension(237, 22));
        dataBaseUrlTxtFld.setMinimumSize(dataBaseUrlTxtFld.getPreferredSize());
        //        dataBaseUrlTxtFld.setText(ConnectionSettings.getInstance()
        //                .getDataBaseUrl());
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
        gridBagLayout.setConstraints(dataBaseUrlTxtFld, constraints);
        self.add(dataBaseUrlTxtFld);

        // Initilize text in text fields:
        updateTextFields();
    }

    public void initFromSettings(WizardDescriptor wd) {
        wd.addPropertyChangeListener(this);
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() instanceof WizardDescriptor) {
            ConnectionSettings settings = ConnectionSettings.getInstance();
            // Collect information from widgets
            settings.setDataAdapter(dataAdapterCombo.getSelectedItem()
                    .toString());
            settings.setDataBaseDriver(dataBaseDriverTxtFld.getText());
            settings.setDataBaseUrl(dataBaseUrlTxtFld.getText());
            // Save in settings according what is selected for the
            // next run
            String da = dataAdapterCombo.getSelectedItem().toString();
            settings.setLastSelectedDataAdapter(da);
            if (da.equalsIgnoreCase(ConnectionSettings.MYSQL_DA)) {
                ConnectionSettings.getInstance().setMysqlFormatDBDriver(
                        dataBaseDriverTxtFld.getText());
                ConnectionSettings.getInstance().setMysqlFormartURL(
                        dataBaseUrlTxtFld.getText());
            } else if (da.equalsIgnoreCase(ConnectionSettings.HSQLDB_DA)) {
                ConnectionSettings.getInstance().setHsqldbFormatDBDriver(
                        dataBaseDriverTxtFld.getText());
                ConnectionSettings.getInstance().setHsqldbFormartURL(
                        dataBaseUrlTxtFld.getText());
            }
        }
        if (e.getPropertyName().equalsIgnoreCase(
                ConnectionSettings.PROP_LAST_SELECTED_DA)) {
            updateTextFields();
        }
    }
    
    /** Updates text un text fields.*/
    private void updateTextFields() {
        String da = ConnectionSettings.getInstance()
                .getLastSelectedDataAdapter();
        if (da.equalsIgnoreCase(ConnectionSettings.MYSQL_DA)) {
            dataBaseUrlTxtFld.setText(ConnectionSettings.getInstance()
                    .getMysqlFormartURL());
            dataBaseDriverTxtFld.setText(ConnectionSettings.getInstance()
                    .getMysqlFormatDBDriver());
        } else if (da.equalsIgnoreCase(ConnectionSettings.HSQLDB_DA)) {
            dataBaseUrlTxtFld.setText(ConnectionSettings.getInstance()
                    .getHsqldbFormartURL());
            dataBaseDriverTxtFld.setText(ConnectionSettings.getInstance()
                    .getHsqldbFormatDBDriver());
        }
    }
}