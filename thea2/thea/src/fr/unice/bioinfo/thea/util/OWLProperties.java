package fr.unice.bioinfo.thea.util;

import org.apache.commons.configuration.Configuration;

import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public final class OWLProperties {

    static {
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
    }
}