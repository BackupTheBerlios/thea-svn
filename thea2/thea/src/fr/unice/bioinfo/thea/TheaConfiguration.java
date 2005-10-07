package fr.unice.bioinfo.thea;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.openide.ErrorManager;

import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;
import fr.unice.bioinfo.util.Config;

public class TheaConfiguration implements Config {
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
        if (instance == null) {// || (configuration == null)) {
            instance = new TheaConfiguration();
        }
        return instance;
    }

    /**
     * @return The singleton
     */
    static public Config getInstance() {
        return getDefault();
    }

    /** Returns a configuration. */
    public Configuration getConfiguration() {
        if (configuration == null) {
            createConfiguration();
        }
        return configuration;
    }

    /** Creates the configuration. */
    public void createConfiguration() {
        path = OESettings.getInstance().getConfigFilePath();
        if (path.equalsIgnoreCase("")) {// NOI18N
            configuration = null;
            ;
            return;
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
                // configURL = TheaConfiguration.class
                // .getResource("resources/thea.cfg.xml");
                ConfigurationFactory factory = new ConfigurationFactory();
                factory.setConfigurationURL(configURL);
                configuration = factory.getConfiguration();
            } catch (ConfigurationException ce) {
                configuration = null;
                configURL = null;
                OESettings.getInstance().setConfigFilePath("");// NOI18N
                ErrorManager.getDefault()
                        .notify(ErrorManager.INFORMATIONAL, ce);
            }
        } else {
            configuration = null;
        }
    }

}