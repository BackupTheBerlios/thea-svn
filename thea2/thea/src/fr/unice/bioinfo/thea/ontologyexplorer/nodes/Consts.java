package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.HashSet;
import java.util.Set;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;

/**
 * @author SAÏD, EL KASMI.
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
    
    private static Resource subClassOf = resourceFactory
    .getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");

    public static Set getListOfProperties() {
        properties.add(subsumeProperty);
        properties.add(partofProperty);
        
        //properties.add(subClassOf);
        return properties;
    }
}