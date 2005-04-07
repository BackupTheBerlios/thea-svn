package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.HashSet;
import java.util.Set;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;

/**
 * @author SA�D, EL KASMI.
 */
public class Consts {

    private static Set properties = new HashSet();

    private static ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
            .getResourceFactory();

    public static Resource subsumeProperty = resourceFactory
            .getProperty("SUBSUMED_BY");

    public static Resource partofProperty = resourceFactory
            .getProperty("CONTAINS");

    private static Resource goannotationProperty = resourceFactory
            .getProperty("GOANNOTATION");

    public static Set getListOfProperties() {
        properties.add(subsumeProperty);
        properties.add(partofProperty);
        properties.add(goannotationProperty);
        return properties;
    }
}