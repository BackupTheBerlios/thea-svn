package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;

public class ConnectPanel extends JPanel {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N
    private JComponent databaseSeparator;
    private JLabel driverLbl;
    private JTextField driverField;
    private JLabel urlLbl;
    private JTextField urlField;
    private JComponent authenticationSeparator;
    private JLabel loginLbl;
    private JTextField loginField;
    private JLabel passwordLbl;
    private JPasswordField passwordField;
    private JComponent barSeparator;
    private JLabel progressionLbl;
    private JProgressBar bar;
    private DatabaseConnection connection;

    public ConnectPanel(DatabaseConnection connection) {
        this.connection = connection;
        initComponents();

        // Get data from connection:
        this.driverField.setText(connection.getDriver());
        this.urlField.setText(connection.getDatabase());
        this.loginField.setText(connection.getUser());

        // Add a connection listener
        PropertyChangeListener connectionListener = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (event.getPropertyName().equals("connecting")) { //NOI18N
                            startProgress();
                        }

                        if (event.getPropertyName().equals("connected")) { //NOI18N
                            stopProgress(true);
                        }

                        if (event.getPropertyName().equals("failed")) { //NOI18N
                            stopProgress(false);
                        }
                    }
                });
            }
        };

        this.connection.addPropertyChangeListener(connectionListener);
    }

    private void initComponents() {
        // Create separators
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        databaseSeparator = compFactory.createSeparator(bundle
                .getString("LBL_DataBase")); //NOI18N
        authenticationSeparator = compFactory.createSeparator(bundle
                .getString("LBL_Athentication")); //NOI18N
        barSeparator = compFactory.createSeparator(bundle
                .getString("LBL_Connection")); //NOI18N

        driverLbl = new JLabel();
        driverField = new JTextField();
        urlLbl = new JLabel();
        urlField = new JTextField();

        loginLbl = new JLabel();
        loginField = new JTextField();
        passwordLbl = new JLabel();
        passwordField = new JPasswordField();
        progressionLbl = new JLabel();
        bar = new JProgressBar();

        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);

        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec("max(min;120px)"), //NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(min;250px)") }, new RowSpec[] { //NOI18N

                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
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
                        FormFactory.DEFAULT_ROWSPEC }));

        add(databaseSeparator, cc.xywh(1, 1, 3, 1));

        //---- driverLbl ----
        driverLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        driverLbl.setText(bundle.getString("LBL_DataBaseDriver")); //NOI18N
        add(driverLbl, cc.xy(1, 3));

        //---- driverField ----
        driverField.setEditable(false);
        add(driverField, cc.xy(3, 3));

        //---- urlLbl ----
        urlLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        urlLbl.setText(bundle.getString("LBL_DataBaseURL")); //NOI18N
        add(urlLbl, cc.xy(1, 5));
        add(urlField, cc.xy(3, 5));
        add(authenticationSeparator, cc.xywh(1, 7, 3, 1));

        //---- loginLbl ----
        loginLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        loginLbl.setText(bundle.getString("LBL_LoginName")); //NOI18N
        add(loginLbl, cc.xy(1, 9));
        add(loginField, cc.xy(3, 9));

        //---- passwordLbl ----
        passwordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLbl.setText(bundle.getString("LBL_Password")); //NOI18N
        add(passwordLbl, cc.xy(1, 11));
        add(passwordField, cc.xy(3, 11));

        // ---- progressionLbl ----
        progressionLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        progressionLbl.setText(bundle.getString("LBL_ConnectionStatus")); //NOI18N
        add(progressionLbl, cc.xy(1, 15));
        add(bar, cc.xy(3, 15));
    }

    private void startProgress() {
        bar.setBorderPainted(true);
        bar.setIndeterminate(true);
        bar.setString(bundle.getString("ConnectionProgress_Connecting")); //NOI18N
    }

    private void stopProgress(boolean connected) {
        bar.setIndeterminate(false);

        if (connected) {
            bar.setValue(bar.getMaximum());
            bar.setString(bundle.getString("ConnectionProgress_Established")); //NOI18N
        } else {
            bar.setValue(bar.getMinimum());
            bar.setString(bundle.getString("ConnectionProgress_Failed")); //NOI18N
        }
    }

    /**
     * Returns login name
     */
    public String getLoginName() {
        return this.loginField.getText();
    }

    /**
     * Retusn database URL
     */
    public String getUrl() {
        return this.urlField.getText();
    }

    /**
     * returns password
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
}