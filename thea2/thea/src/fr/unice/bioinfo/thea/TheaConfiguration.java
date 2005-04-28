package fr.unice.bioinfo.thea;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.thea.ontologyexplorer.dlg.ConfigBrowserPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

public class TheaConfiguration {
    private Dialog dialog;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); //NOI18N

    /** Instance for the singleton pattern. */
    private static TheaConfiguration instance = null;
    /** A java object that would encapsulate the whole configuration. */
    private static Configuration configuration = null;

    private String path = null;
    private URL configURL = null;

    private TheaConfiguration() {
    }

    /** Returns default configuration manager. */
    static public TheaConfiguration getDefault() {
        if ((instance == null) || (configuration == null)) {
            instance = new TheaConfiguration();
        }
        return instance;
    }

    /** Returns a configuration. */
    public Configuration getConfiguration() {
        if (configuration == null) {
            createConfiguration();
        }
        return configuration;
    }

    /** Creates the configuration.*/
    public Configuration createConfiguration() {
        path = OESettings.getInstance().getConfigFilePath();
        if (path.equalsIgnoreCase("")) {//NOI18N
            // Create the panel
            final ConfigBrowserPanel panel = new ConfigBrowserPanel();
            //          Create the listener for buttons actions/ Ok/Cancel
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == DialogDescriptor.OK_OPTION) {
                        path = OESettings.getInstance().getConfigFilePath();
                        File cfgFile = new File(path);
                        try {
                            configURL = cfgFile.toURL();
                        } catch (MalformedURLException e1) {
                            ErrorManager.getDefault().notify(e1);
                        }
                        // close dialog
                        closeDialog();
                    }
                }
            };
            //Use DialogDescriptor to show the panel
            DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                    .getString("LBL_BrowseDialogTitle"), true, al); //NOI18N
            Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
            descriptor.setClosingOptions(closingOptions);
            dialog = DialogDisplayer.getDefault().createDialog(descriptor);
            dialog.show();
        }
        // and if the path is not null:
        File cfgFile = new File(path);
        try {
            configURL = cfgFile.toURL();
        } catch (MalformedURLException e1) {
            ErrorManager.getDefault().notify(e1);
        }

        if (configURL != null) {
            try {
                //            configURL = TheaConfiguration.class
                //                    .getResource("resources/thea.cfg.xml");
                ConfigurationFactory factory = new ConfigurationFactory();
                factory.setConfigurationURL(configURL);
                configuration = factory.getConfiguration();
            } catch (ConfigurationException ce) {
                configuration = null;
                configURL = null;
                OESettings.getInstance().setConfigFilePath("");//NOI18N
                ErrorManager.getDefault()
                        .notify(ErrorManager.INFORMATIONAL, ce);
            }
        }
        return configuration;
    }

    //  Closes dialog
    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
}