package fr.unice.bioinfo.thea;

import java.net.URL;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.openide.ErrorManager;

public class TheaConfiguration {

    /** Instance for the singleton pattern. */
    static private TheaConfiguration instance = null;
    private Configuration configuration = null;

    private TheaConfiguration() {
        URL configURL = null;
        try {
            configURL = TheaConfiguration.class
                    .getResource("resources/thea.cfg.xml");
            ConfigurationFactory factory = new ConfigurationFactory();
            factory.setConfigurationURL(configURL);
            configuration = factory.getConfiguration();
        } catch (ConfigurationException ce) {
            configuration = new BaseConfiguration();
            ErrorManager.getDefault().notify(ce);
        }
    }

    /** Returns default configuration manager. */
    static public TheaConfiguration getDefault() {
        if (instance == null) {
            instance = new TheaConfiguration();
        }
        return instance;
    }

    /** Returns a configuration. */
    public Configuration getConfiguration() {
        return configuration;
    }
}