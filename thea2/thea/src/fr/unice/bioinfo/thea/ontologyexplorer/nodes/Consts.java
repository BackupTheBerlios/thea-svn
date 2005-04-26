package fr.unice.bioinfo.thea.ontologyexplorer.nodes;

import java.util.HashSet;
import java.util.Set;

import fr.unice.bioinfo.allonto.datamodel.Property;
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
            .getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");

    public static Resource identifiesProperty = resourceFactory
            .getProperty("http://www.unice.fr/bioinfo/owl/allonto#identifies");

    public static Resource partofProperty = resourceFactory
            .getProperty("http://www.w3.org/2000/01/rdf-schema#subPropertyOf");

    //
    //    private static Resource goannotationProperty = resourceFactory
    //            .getProperty("GOANNOTATION");
    //    private static Resource derivedInProperty = resourceFactory
    //    .getProperty("DERIVED_IN");
    //    
    //    private static Resource subClassOf = resourceFactory
    //    .getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");

    public static Set getListOfProperties() {
        properties.add(((Property)subsumeProperty).getInverse());
        //properties.add(identifiesProperty);
        //properties.add(partofProperty);
        //properties.add(derivedInProperty);

        //properties.add(subClassOf);
        return properties;
    }
}