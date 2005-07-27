package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.db.driver.JDBCDriver;
import fr.unice.bioinfo.thea.ontologyexplorer.db.driver.JDBCDriverManager;
import fr.unice.bioinfo.thea.ontologyexplorer.db.util.DriverListUtil;

/**
 * A swing GUI that allows users to define a new ontology node. Createtion of
 * this node requires some information like the ontology node name, description,
 * a driver to be used to connect to the database, login name and password if
 * any. Users could add their own drivers from jar or zip files on the disk.
 * @author Saïd El Kasmi.
 */
public class NewOntologyPanel extends JPanel implements PropertyChangeListener {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N
    private Dialog dialog;
    private JComponent ontologySeparator;
    private JLabel ontologyNameLbl;
    private JTextField ontologyNameField;
    private JLabel ontologyDescriptionLbl;
    private JTextField ontologyDescriptionField;
    private JComponent databaseSeparator;
    private JLabel databaseDriverLbl;
    private JComboBox databaseNameComboBox;
    private JButton addDriverBtn;
    private JLabel databaseNameLbl;
    private JTextField databaseDriverField;
    private JLabel databaseUrlLbl;
    private JComboBox databaseUrlComboBox;
    private JComponent authenticationSeparator;
    private JLabel loginNameLbl;
    private JTextField loginNameField;
    private JLabel passwordLbl;
    private JPasswordField passwordField;
    private JLabel savePasswordLbl;
    private JCheckBox savePasswordCheckBox;

    // a Database connection
    private DatabaseConnection connection;

    /**
     * Main constructor fot this class.
     */
    public NewOntologyPanel(DatabaseConnection connection) {
        initComponents();
        databaseNameComboBoxActionPerformed(null);

        // Create a DatabaseConnection instance:
        this.connection = connection;
        JDBCDriverManager.getDefault().addPropertyChangeListener(this);
    }

    /**
     * Initializes inner components of this component.
     */
    private void initComponents() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        ontologySeparator = compFactory.createSeparator(bundle
                .getString("LBL_Ontology")); //NOI18N
        ontologyNameLbl = new JLabel();
        ontologyNameField = new JTextField();
        ontologyDescriptionLbl = new JLabel();
        ontologyDescriptionField = new JTextField();
        databaseSeparator = compFactory.createSeparator(bundle
                .getString("LBL_DataBase")); //NOI18N
        databaseDriverLbl = new JLabel();

        // Create the databaseComboBox and initialise it
        initDatabaseNameComboBox();
        databaseNameComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                databaseNameComboBoxActionPerformed(e);
            }
        });
        addDriverBtn = new JButton();
        addDriverBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDriverBtnActionPerformed(e);
            }
        });
        databaseNameLbl = new JLabel();
        databaseDriverField = new JTextField();
        databaseUrlLbl = new JLabel();
        databaseUrlComboBox = new JComboBox();
        databaseUrlComboBox.setEditable(true); //Make editable
        authenticationSeparator = compFactory.createSeparator(bundle
                .getString("LBL_Athentication"));
        loginNameLbl = new JLabel();
        loginNameField = new JTextField();
        passwordLbl = new JLabel();
        passwordField = new JPasswordField();
        savePasswordLbl = new JLabel();
        savePasswordCheckBox = new JCheckBox();

        CellConstraints cc = new CellConstraints();

        // Add a border
        setBorder(Borders.DIALOG_BORDER);

        // Set layout
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("150px:grow"), //NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW) }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC }));

        // Add Ontolog separator
        add(ontologySeparator, cc.xywh(1, 1, 5, 1));

        //---- ontologyNameLbl ----
        ontologyNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        ontologyNameLbl.setText(bundle.getString("LBL_OntologyName")); //NOI18N
        add(ontologyNameLbl, cc.xy(1, 3));
        add(ontologyNameField, cc.xywh(3, 3, 3, 1));

        //---- ontologyDescriptionLbl ----
        ontologyDescriptionLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        ontologyDescriptionLbl.setText(bundle
                .getString("LBL_OntologyDescription")); //NOI18N
        add(ontologyDescriptionLbl, cc.xy(1, 5));
        add(ontologyDescriptionField, cc.xywh(3, 5, 3, 1));
        add(databaseSeparator, cc.xywh(1, 7, 5, 1));

        //---- databaseNameLbl ----
        databaseNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        databaseNameLbl.setText(bundle.getString("LBL_DataBaseName")); //NOI18N
        add(databaseNameLbl, cc.xy(1, 9));

        //------ databaseNameComboBox
        add(databaseNameComboBox, cc.xy(3, 9));

        //---- addDriverBtn ----
        addDriverBtn.setText(bundle.getString("LBL_AddDriver")); //NOI18N
        add(addDriverBtn, cc.xy(5, 9));

        //---- databaseDriverLbl ----
        databaseDriverLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        databaseDriverLbl.setText(bundle.getString("LBL_DataBaseDriver")); //NOI18N
        add(databaseDriverLbl, cc.xy(1, 11));

        add(databaseDriverField, cc.xywh(3, 11, 3, 1));

        //---- databaseUrlLbl ----
        databaseUrlLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        databaseUrlLbl.setText(bundle.getString("LBL_DataBaseURL")); //NOI18N
        add(databaseUrlLbl, cc.xy(1, 13));
        add(databaseUrlComboBox, cc.xywh(3, 13, 3, 1));
        add(authenticationSeparator, cc.xywh(1, 15, 5, 1));

        //---- loginNameLbl ----
        loginNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        loginNameLbl.setText(bundle.getString("LBL_LoginName")); //NOI18N
        add(loginNameLbl, cc.xy(1, 17));
        add(loginNameField, cc.xywh(3, 17, 3, 1));

        //---- passwordLbl ----
        passwordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLbl.setText(bundle.getString("LBL_Password")); //NOI18N
        add(passwordLbl, cc.xy(1, 19));
        add(passwordField, cc.xywh(3, 19, 3, 1));

        //---- savePasswordLbl ----
        savePasswordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        savePasswordLbl.setText(bundle.getString("LBL_SavePassword")); //NOI18N
        add(savePasswordLbl, cc.xy(1, 21));

        //---- savePasswordCheckBox ----
        savePasswordCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        add(savePasswordCheckBox, cc.xy(3, 21));
    }

    // The action performed for addDriverBtn button
    private void addDriverBtnActionPerformed(ActionEvent e) {
        final AddDriverPanel dlgPanel = new AddDriverPanel();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (event.getSource() == DialogDescriptor.OK_OPTION) {
                    String name = dlgPanel.getName();
                    List drvLoc = dlgPanel.getDriverLocation();
                    String drvClass = dlgPanel.getDriverClass();

                    StringBuffer err = new StringBuffer();

                    if (drvLoc.size() < 1) {
                        err.append(bundle
                                .getString("AddDriverDialog_MissingFile")); //NOI18N
                    }

                    if ((drvClass == null) || drvClass.equals("")) {
                        if (err.length() > 0) {
                            err.append(", "); //NOI18N
                        }

                        err.append(bundle
                                .getString("AddDriverDialog_MissingClass")); //NOI18N
                    }

                    if (err.length() > 0) {
                        String message = MessageFormat.format(bundle
                                .getString("AddDriverDialog_ErrorMessage"),
                                new String[] { err.toString() }); //NOI18N
                        DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(message,
                                        NotifyDescriptor.INFORMATION_MESSAGE));

                        return;
                    }

                    closeDialog();

                    //create driver instance and save it in the XML format
                    if ((name == null) || name.equals("")) {
                        name = drvClass;
                    }

                    try {
                        JDBCDriverManager.getDefault().addDriver(
                                new JDBCDriver(name, drvClass, (URL[]) drvLoc
                                        .toArray(new URL[drvLoc.size()])));
                    } catch (IOException exc) {
                        //PENDING
                    }
                }
            }
        };

        DialogDescriptor descriptor = new DialogDescriptor(dlgPanel, bundle
                .getString("AddDriverDialogTitle"), true, actionListener); //NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        descriptor.setClosingOptions(closingOptions);
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
    }

    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }

    // Initializes databaseNameComboBox
    private void initDatabaseNameComboBox() {
        //com.mysql.jdbc.Driver
        Vector drvs = new Vector();
        JDBCDriver[] drivers = JDBCDriverManager.getDefault().getDrivers();

        for (int i = 0; i < drivers.length; i++) {
            if (drivers[i].isAvailable()) {
                drvs.add(drivers[i]);
            }
        }

        // Add a default driver
        drvs.add(new JDBCDriver("MySQL (MM.MySQL driver)", //NOI18N
                "org.gjt.mm.mysql.Driver")); //NOI18N

        databaseNameComboBox = new JComboBox(drvs);
    }

    // actionPerformed for:databaseNameComboBox
    private void databaseNameComboBoxActionPerformed(ActionEvent e) {
        JDBCDriver drv = (JDBCDriver) databaseNameComboBox.getSelectedItem();
        List urls = null;
        String driver = null;

        if (drv != null) {
            driver = drv.getClassName();
            urls = DriverListUtil.getURLs(driver);
        }

        databaseUrlComboBox.removeAllItems();

        if (urls != null) {
            for (int i = 0; i < urls.size(); i++)
                databaseUrlComboBox.addItem((String) urls.get(i));
        }

        if (driver != null) {
            databaseDriverField.setText(driver);
        }
    }

    /**
     * Collects information about the connection.
     */
    public void setConnectionInfo() {
        connection.setDriver(((JDBCDriver) databaseNameComboBox
                .getSelectedItem()).getClassName());
        connection.setDatabase((String) databaseUrlComboBox.getSelectedItem());
        connection.setUser(loginNameField.getText());
        connection.setPassword(getPassword());
        connection.setRememberPassword(savePasswordCheckBox.isSelected());
    }

    /**
     * Returns selected Database's name
     */
    public String getSelectedDatabaseName() {
        String db = ""; //NOI18N

        if (databaseNameComboBox.getSelectedItem() != null) {
            db = ((JDBCDriver) databaseNameComboBox.getSelectedItem())
                    .getClassName();
        }

        return db;
    }

    /**
     * Returns the Password for authentication
     */
    public String getPassword() {
        String password;
        String tempPassword = new String(passwordField.getPassword());

        if (tempPassword.length() > 0) {
            password = tempPassword;
        } else {
            password = null;
        }

        return password;
    }

    /**
     * Returns the name of the ontology to create.
     */
    public String getOntologyName() {
        return ontologyNameField.getText();
    }

    /**
     * Returns the description of the ontology to create.
     */
    public String getOntologyDescription() {
        return ontologyDescriptionField.getText();
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("add")) { //NOI18N
            databaseNameComboBox.addItem(((JDBCDriver) evt.getNewValue()));
        } else if (evt.getPropertyName().equalsIgnoreCase("remove")) { //NOI18N
            databaseNameComboBox.removeItem(((JDBCDriver) evt.getOldValue()));
        }
    }
}