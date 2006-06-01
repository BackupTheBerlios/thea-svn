package fr.unice.bioinfo.thea.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
//import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public final class OWLProperties {

    private static OWLProperties instance = null;

    public static OWLProperties getInstance() {
        if (instance == null) {
            instance = new OWLProperties();
        }
        return instance;
    }

    /**
     * List of hierarchy relationships to be used to compute keys. Each
     * hierarchical relationship is mapped to a list of the following three
     * elements: 1- the predicate to follow 2- the Criterion to use (optional) 3 -
     * the path to the icon representing the relationship
     */

    private static Map hierarchyDescription = new HashMap();

    /** The property to use to reach for a node's display name. */
    private static String nodeNameProperty;

    /** The partof relationship property name */
    private static String propdbkeyPropertyName;

    /** The is a relationship property name */
    private static String xrefPropertyName;

    /** annotatedbyname property name */
    private static String annotatedByPropertyName;

    private static String annotatePropertyName;

    private static String hasEvidenceProperty;

    private static String chromosomePropertyName;

    private static String endPosPropertyName;

    private static String strandPropertyName;

    private static String transcribedFromPropertyName;

    private static String startPosPropertyName;

    private static String symbolPropertyName;

    static {
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        Object obj = con.getProperty("ontologyexplorer.nodes.nodename");// NOI18N
        nodeNameProperty = (String) obj;

        Object o = con.getProperty("ontologyexplorer.nodes.xrefname");// NOI18N
        xrefPropertyName = (String) o;
        o = con.getProperty("annotation.propdbkeyname");// NOI18N
        propdbkeyPropertyName = (String) o;
        o = con.getProperty("annotation.annotatedbyname");// NOI18N
        annotatedByPropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.nodes.annotates");// NOI18N
        annotatePropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.nodes.hasevidence");// NOI18N
        hasEvidenceProperty = (String) o;
        o = con.getProperty("annotation.chromosomename");// NOI18N
        chromosomePropertyName = (String) o;
        o = con.getProperty("annotation.endPosname");// NOI18N
        endPosPropertyName = (String) o;
        o = con.getProperty("annotation.strandname");// NOI18N
        strandPropertyName = (String) o;
        o = con.getProperty("annotation.transcribedFromname");// NOI18N
        transcribedFromPropertyName = (String) o;
        o = con.getProperty("annotation.startPosname");// NOI18N
        startPosPropertyName = (String) o;
        o = con.getProperty("annotation.symbolname");// NOI18N
        symbolPropertyName = (String) o;
    }

    public String getAnnotatedByPropertyName() {
        return annotatedByPropertyName;
    }

    public String getChromosomePropertyName() {
        return chromosomePropertyName;
    }

    public String getEndPosPropertyName() {
        return endPosPropertyName;
    }

    public String getHasEvidenceProperty() {
        return hasEvidenceProperty;
    }

    // TODO a supprimer et à remplacer par une méthode dans OntologyProperties
    public synchronized String getNodeNameProperty() {
        return nodeNameProperty;
    }

    public String getPropdbkeyPropertyName() {
        return propdbkeyPropertyName;
    }

    public String getStartPosPropertyName() {
        return startPosPropertyName;
    }

    public String getStrandPropertyName() {
        return strandPropertyName;
    }

    public String getSymbolPropertyName() {
        return symbolPropertyName;
    }

    public String getTranscribedFromPropertyName() {
        return transcribedFromPropertyName;
    }

    public String getXrefPropertyName() {
        return xrefPropertyName;
    }

    public String getAnnotatePropertyName() {
        return annotatePropertyName;
    }
}