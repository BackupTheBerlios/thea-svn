package fr.unice.bioinfo.thea.core.connection;

import java.awt.Dialog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.Lookup;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

import fr.unice.bioinfo.thea.core.connection.wizard.ConnectionDialogDescriptor;

/**
 * An utility class that uses settings {@link ConnectionSettings}to create a
 * connection to a database.
 * <P>
 * The Connection Manager runs the following scenario: <br>
 * Each time a module needs to dial with the database, it asks the Connection
 * Manager for a {@link java.sql.Connection}.Following steps are to be
 * respected:
 * <ul>
 * <li>Call <code> showConnectionWizard()</code> to make the Connection
 * Manager show the connection wizard. This methods returns true if the
 * connection is succefully created and False elsewhere.
 * <li>Call <code> getConnection()</code> to get the created connection
 * </ul>
 * @author SAÏD, EL KASMI.
 */
public class ConnectionManager {

    /** JDBC Connection */
    private static Connection connection = null;

    /** Create a JDBC Connection. */
    private static void createConnection() {
        // reset connection
        connection = null;
        // Get settings:
        ConnectionSettings settings = ConnectionSettings.getInstance();
        String dataAdapter = settings.getDataAdapter();
        String dataBaseDriver = settings.getDataBaseDriver();
        String dataBaseUrl = settings.getDataBaseUrl();
        String username = settings.getUsername();
        String password = settings.getPassword();
        // test for Driver's existence !
        try {
            Class.forName(dataBaseDriver);
        } catch (ClassNotFoundException cnfe) {
            IOProvider ioProvider = (IOProvider) Lookup.getDefault().lookup(
                    IOProvider.class);
            InputOutput io = ioProvider.getIO("ConnectionManager", true);
            io.getOut().println("Driver not loaded ... ");
            //            IOProvider.getDefault().getStdOut().println(
            //                    "Driver not loaded ... ");
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, cnfe);
            return;
        }
        // Get the connection
        try {
            connection = DriverManager.getConnection(dataBaseUrl, username,
                    password);
        } catch (SQLException sqle) {
            //            OutputWindow ow = OutputWindow.getDefault ();
            //            Mode mode = WindowManager.getDefault().findMode ("output");
            //            mode.dockInto (ow);
            //            OutputWriter oWr = new NbIOProvider().getStdOut ();
            //            oWr.println("Connection not created ...");

            IOProvider ioProvider = (IOProvider) Lookup.getDefault().lookup(
                    IOProvider.class);
            InputOutput io = ioProvider.getIO("ConnectionManager", true);
            io.getOut().println("Connection not created: ");
            //            IOProvider.getDefault().getStdOut().println(
            //                    "Connection not created ...");
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, sqle);
            return;
        }
    }

    /**
     * Show the connection wizard to make the user enter necessary settings to
     * establish a connection.
     * @return <i>True <i>If the connection is succefully created, <i>False <i>
     *         elswhere.
     */
    public static boolean showConnectionWizard() {
        boolean b = false;
        ConnectionDialogDescriptor descriptor = new ConnectionDialogDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
        //      After the wizard finish
        if (descriptor.getValue() == WizardDescriptor.FINISH_OPTION) {
            // Try create the connection:
            createConnection();
            if (connection == null) {
                b = false;
            } else if (connection != null) {
                b = true;
            }
        }
        return b;
    }

    /** Returns the connection. */
    public static Connection getConnection() {
        return connection;
    }
}