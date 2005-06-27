package fr.unice.bioinfo.thea.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

import fr.unice.bioinfo.allonto.datamodel.Resource;
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

    /** List of hierarchyProperties to be used to compute keys. */
    private static Set hierarchyProperties = new HashSet();
    /** The partof relationship property name */
    private static String partofPropertyName;
    /** The is a relationship property name */
    private static String isaPropertyName;
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
        Object ob = con.getProperty("ontologyexplorer.hierarchy.uri");//NOI18N
        if (ob instanceof Collection) {
            ArrayList al = new ArrayList((Collection) ob);
            Object[] names = al.toArray();
            for (int counter = 0; counter < al.size(); counter++) {
                String name = (String) names[counter];
                Resource aProperty = resourceFactory.getProperty(name);
                hierarchyProperties.add(aProperty);
            }
        }
        // get the partof and is a hierarchyProperties names:
        Object partof = con.getProperty("ontologyexplorer.hierarchy.partof");//NOI18N
        partofPropertyName = (String) partof;
        Object isa = con.getProperty("ontologyexplorer.hierarchy.isa");//NOI18N
        isaPropertyName = (String) isa;

        Object obj = con.getProperty("ontologyexplorer.nodes.nodename");//NOI18N
        nodeNameProperty = (String) obj;

        Object o = con.getProperty("ontologyexplorer.nodes.xrefname");//NOI18N
        xrefPropertyName = (String) o;
        o = con.getProperty("annotation.propdbkeyname");//NOI18N
        propdbkeyPropertyName = (String) o;
        o = con.getProperty("annotation.annotatedbyname");//NOI18N
        annotatedByPropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.nodes.annotates");//NOI18N
        annotatePropertyName = (String) o;
        o = con.getProperty("ontologyexplorer.nodes.hasevidence");//NOI18N
        hasEvidenceProperty = (String) o;
        o = con.getProperty("annotation.chromosomename");//NOI18N
        chromosomePropertyName = (String) o;
        o = con.getProperty("annotation.endPosname");//NOI18N
        endPosPropertyName = (String) o;
        o = con.getProperty("annotation.strandname");//NOI18N
        strandPropertyName = (String) o;
        o = con.getProperty("annotation.transcribedFromname");//NOI18N
        transcribedFromPropertyName = (String) o;
        o = con.getProperty("annotation.startPosname");//NOI18N
        startPosPropertyName = (String) o;
        o = con.getProperty("annotation.symbolname");//NOI18N
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

    public Set getHierarchyProperties() {
        return hierarchyProperties;
    }

    public String getIsaPropertyName() {
        return isaPropertyName;
    }

    public String getNodeNameProperty() {
        return nodeNameProperty;
    }

    public String getPartofPropertyName() {
        return partofPropertyName;
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