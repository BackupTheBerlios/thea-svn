package fr.unice.bioinfo.allonto.util;

import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.DefaultResourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.discovery.tools.DiscoverSingleton;

/**
 * Factory designed to obtain objects corresponding to resources
 */
public class AllontoFactory {
    private static Log log = LogFactory.getLog(ResourceFactory.class);

    /**
     * static method used to get a reference to the factory
     * @return an instance of AllontoFactory
     */
    public static synchronized ResourceFactory getResourceFactory() {
	Object obj = null;
	try {
	    obj = DiscoverSingleton.find(ResourceFactory.class);
	}
	catch (org.apache.commons.discovery.DiscoveryException de) {
	    obj = new DefaultResourceFactory();
	}
	return (ResourceFactory)obj;
    }
}
