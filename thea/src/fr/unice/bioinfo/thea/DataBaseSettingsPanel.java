
package fr.unice.bioinfo.thea;

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


/**
 * This class is used together with DataBaseSettingsWizardPanel
 * to make the user enter necessary settings to perform the
 * DataBase connection.
 * @author SAÏD, EL KASMI.
 */
public class DataBaseSettingsPanel extends JPanel implements PropertyChangeListener {
    
    // widgets
    private JLabel dataAdapterLbl;
    private JComboBox dataAdapterCombo;
    private JLabel dataBaseDriverLbl;
    private JTextField dataBaseDriverTxtFld;
    private JLabel dataBaseUrlLbl;
    private JTextField dataBaseUrlTxtFld;
    
    /**
     * Build a DataBaseSettingsPanel instance.
     */
    public DataBaseSettingsPanel(){
        super();
        init(this);
    }
    
    // Initialize widgets and panel
    private void init(JPanel self){
        // Bean Properties
        self.setName("DataBase Settings Panel");
        // sets Bounds
        self.setBounds(23,18,366,145);
        // set Layout 
        GridBagLayout gridBagLayout = new GridBagLayout();
        self.setLayout(gridBagLayout);
        GridBagConstraints constraints = new GridBagConstraints();
        Insets defaultInsets = new Insets(5,5,5,5);
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
        dataAdapterLbl.setBounds(133,173,100,22);
        dataAdapterLbl.setPreferredSize(new Dimension(100,22));
        dataAdapterLbl.setMinimumSize(dataAdapterLbl.getPreferredSize());
        gridBagLayout.setConstraints(dataAdapterLbl, constraints);
        self.add(dataAdapterLbl);
        //DataAdapter ComboBox
        dataAdapterCombo = new JComboBox();
        dataAdapterCombo.setBounds(188,173,226,22);
        dataAdapterCombo.setPreferredSize(new Dimension(226,22));
        dataAdapterCombo.setMinimumSize(dataAdapterCombo.getPreferredSize());
        constraints.gridx = 1;
        gridBagLayout.setConstraints(dataAdapterCombo, constraints);
        self.add(dataAdapterCombo);
        //DataBaseDriverLbl
        dataBaseDriverLbl = new JLabel("DataBase Driver:");
        dataBaseDriverLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        dataBaseDriverLbl.setToolTipText("Driver used to connect to the DataBase.");
        dataBaseDriverLbl.setBounds(78,189,100,22);
        dataBaseDriverLbl.setPreferredSize(new Dimension(100,22));
        dataBaseDriverLbl.setMinimumSize(dataBaseDriverLbl.getPreferredSize());
        constraints.gridx = 0;
        constraints.gridy = 1;
        gridBagLayout.setConstraints(dataBaseDriverLbl, constraints);
        self.add(dataBaseDriverLbl);
        //DataBaseDriverTxtFld
        dataBaseDriverTxtFld = new JTextField();
        dataBaseDriverTxtFld.setBounds(188,189,100,22);
        dataBaseDriverTxtFld.setPreferredSize(new Dimension(100,22));
        dataBaseDriverTxtFld.setMinimumSize(dataBaseDriverTxtFld.getPreferredSize());       
        constraints.gridx = 1;
        gridBagLayout.setConstraints(dataBaseDriverTxtFld, constraints);
        self.add(dataBaseDriverTxtFld);
        //DataBaseUrlLbl
        dataBaseUrlLbl = new JLabel("DataBase URL:");
        dataBaseUrlLbl.setToolTipText("DataBase location.");
        dataBaseUrlLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        dataBaseUrlLbl.setBounds(78,205,100,22);
        dataBaseUrlLbl.setPreferredSize(new Dimension(100,22));
        dataBaseUrlLbl.setMinimumSize(dataBaseUrlLbl.getPreferredSize());
        constraints.gridx = 0;
        constraints.gridy = 2;
        gridBagLayout.setConstraints(dataBaseUrlLbl, constraints);
        self.add(dataBaseUrlLbl);
        //DataBaseUrlTxtFld
        dataBaseUrlTxtFld = new JTextField();
        dataBaseUrlTxtFld.setBounds(188,205,237,22);
        dataBaseUrlTxtFld.setPreferredSize(new Dimension(237,22));
        dataBaseUrlTxtFld.setMinimumSize(dataBaseUrlTxtFld.getPreferredSize());
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
        gridBagLayout.setConstraints(dataBaseUrlTxtFld, constraints);
        self.add(dataBaseUrlTxtFld);
    }
    
    public void initFromSettings(WizardDescriptor wd){
        wd.addPropertyChangeListener(this);
        // Get the settings instance
        LoadOntologySettings loadOntologySettings = (LoadOntologySettings) LoadOntologySettings
        .findObject(LoadOntologySettings.class, true);
        // List adapters
        for(int cnt=0; cnt<loadOntologySettings.getAvailableAdapters().length; cnt++){
            dataAdapterCombo.addItem(loadOntologySettings.getAvailableAdapters()[cnt]);
            
        }
        dataAdapterCombo.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
            }});
        // Make the combo box updates itself
        dataAdapterCombo.updateUI();
        
        dataBaseDriverTxtFld.setText(loadOntologySettings.getDataBaseDriver());
        dataBaseUrlTxtFld.setText(loadOntologySettings.getDataBaseUrl());
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() instanceof WizardDescriptor){
            LoadOntologySettings loadOntologySettings = (LoadOntologySettings)LoadOntologySettings.findObject(LoadOntologySettings.class,true);
            loadOntologySettings.setDataAdapter(dataAdapterCombo.getSelectedItem().toString());
            loadOntologySettings.setDataBaseDriver(dataBaseDriverTxtFld.getText());
            loadOntologySettings.setDataBaseUrl(dataBaseUrlTxtFld.getText());
        }
    } 
}
