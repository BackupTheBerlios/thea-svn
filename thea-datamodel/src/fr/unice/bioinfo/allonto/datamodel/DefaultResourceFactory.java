package fr.unice.bioinfo.allonto.datamodel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * An implementation of ResourceFactory for transcient objects
 */
public class DefaultResourceFactory implements ResourceFactory {
    private static Log log = LogFactory.getLog(DefaultResourceFactory.class);

    /**
     * A Hash table to store properties
     */
    protected static Map propHash = new HashMap();
    /**
     * a Hashtable to store resources
     */
    protected static Map resHash = new HashMap();
    /**
     * a Hashtable to store resource types
     */
    protected static Map resTypeHash = new HashMap();
    
    /**
     * Retrieve of create a property with a given key
     * @param key the property to be accessed
     * @return the property corresponding to the key
     */
    public synchronized Property getProperty(String key) {
	if (key == null) {
	    log.error("PropertyFactory cannot retrieve property with null id");
	    return null;
	}
	Property prop = (Property)propHash.get(key);
	if (prop != null) return prop; // already created
	//
	// Properties
	//
	if (key.equals("NAME")) {
	    prop = new Property("NAME");
	    prop.setTarget(prop, new StringValue("name"));
	    propHash.put(key, prop);
	} else if (key.equals("EVIDENCE")) {
	    prop = new Property("EVIDENCE");
	    prop.setName("evidence");
	    propHash.put(key, prop);
	} else if (key.equals("USER")) {
	    prop = new Property("USER");
	    prop.setName("user");
	    propHash.put(key, prop);
	}  else if (key.equals("ARC_LENGTH")) {
	    prop = new Property("ARC_LENGTH");
	    prop.setName("arc length");
	    propHash.put(key, prop);
	}  else if (key.equals("DEFINITION")) {
	    prop = new Property("DEFINITION");
	    prop.setName("definition");
	    propHash.put(key, prop);
	}  else if (key.equals("SYNONYM")) {
	    prop = new Property("SYNONYM");
	    prop.setName("synonym");
	    propHash.put(key, prop);
	}  else if (key.equals("DBNAME")) {
	    prop = new Property("DBNAME");
	    prop.setName("database name");
	    propHash.put(key, prop);
	}  else if (key.equals("DBKEY")) {
	    prop = new Property("DBKEY");
	    prop.setName("database key");
	    propHash.put(key, prop);
	}  else if (key.equals("DBXREF")) {
	    prop = new Property("DBXREF");
	    prop.setName("database refs");
	    propHash.put(key, prop);
	}  else if (key.equals("INVERSE")) {
	    prop = new Property("INVERSE");
	    prop.setName("inverse property");
	    propHash.put(key, prop);
// 	}  else if (key.equals("TRUE")) {
// 	    prop = new Property("TRUE");
// 	    prop.setName("always true");
// 	    propHash.put(key, prop);
	}  else if (key.equals("ISOBSOLETE")) {
	    prop = new Property("ISOBSOLETE");
	    prop.setName("is obsolete");
	    propHash.put(key, prop);
	}  else if (key.equals("FULLNAME")) {
	    prop = new Property("FULLNAME");
	    prop.setName("full name");
	    propHash.put(key, prop);
	}  else if (key.equals("SPECIE")) {
	    prop = new Property("SPECIE");
	    prop.setName("specie");
	    propHash.put(key, prop);
	}  else if (key.equals("CHROMOSOME")) {
	    prop = new Property("CHROMOSOME");
	    prop.setName("chromosome");
	    propHash.put(key, prop);
	}  else if (key.equals("STARTPOS")) {
	    prop = new Property("STARTPOS");
	    prop.setName("start position");
	    propHash.put(key, prop);
	}  else if (key.equals("ENDPOS")) {
	    prop = new Property("ENDPOS");
	    prop.setName("end position");
	    propHash.put(key, prop);
	}  else if (key.equals("STRAND")) {
	    prop = new Property("STRAND");
	    prop.setName("strand");
	    propHash.put(key, prop);
	}  else if (key.equals("EVIDENCE_SOURCE")) {
	    prop = new Property("EVIDENCE_SOURCE");
	    prop.setName("evidence's source");
	    propHash.put(key, prop);
	}
	//
	// Relationships
	//
	else if (key.equals("SUBSUME")) {
	    prop = new Property("SUBSUME");
	    prop.setName("is a");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("SUBSUMED_BY"));
	} else if (key.equals("SUBSUMED_BY")) {
	    prop = new Property("SUBSUMED_BY");
	    prop.setName("subsumed by");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("SUBSUME"));
	} else if (key.equals("SPECIALIZES")) {
	    prop = new Property("SPECIALIZES");
	    prop.setName("specializes");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("IS_SPECIALIZED_BY"));
	} else if (key.equals("IS_SPECIALIZED_BY")) {
	    prop = new Property("IS_SPECIALIZED_BY");
	    prop.setName("is specialized by");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("SPECIALIZES"));
	} else if (key.equals("PARTOF")) {
	    prop = new Property("PARTOF");
	    prop.setName("part of");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("CONTAINS"));
	} else if (key.equals("CONTAINS")) {
	    prop = new Property("CONTAINS");
	    prop.setName("contains");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("PARTOF"));
	} else if (key.equals("DERIVES_FROM")) {
	    prop = new Property("DERIVES_FROM");
	    prop.setName("derives from");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("DERIVED_IN"));
	} else if (key.equals("DERIVED_IN")) {
	    prop = new Property("DERIVED_IN");
	    prop.setName("derived in");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("DERIVES_FROM"));
	} else if (key.equals("CHILD_OF")) {
	    prop = new Property("CHILD_OF");
	    prop.setName("child of");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("PARENT_OF"));
	} else if (key.equals("PARENT_OF")) {
	    prop = new Property("PARENT_OF");
	    prop.setName("parent of");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("CHILD_OF"));
	}  else if (key.equals("IDENTIFIED_BY")) {
	    // relationship from an object to an external identifier
	    prop = new Property("IDENTIFIED_BY");
	    prop.setName("identified by");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("IDENTIFIES"));
	}  else if (key.equals("IDENTIFIES")) {
	    // relationship from an external identifier to an object
	    prop = new Property("IDENTIFIES");
	    prop.setName("identifies");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("IDENTIFIED_BY"));
	}  else if (key.equals("TRANSCRIBED_IN")) {
	    // relationship from a gene to a transcript
	    prop = new Property("TRANSCRIBED_IN");
	    prop.setName("transcribed in");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("TRANSCRIBED_FROM"));
	}  else if (key.equals("TRANSCRIBED_FROM")) {
	    // relationship from a transcript to a gene
	    prop = new Property("TRANSCRIBED_FROM");
	    prop.setName("transcribed from");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("TRANSCRIBED_IN"));
	}  else if (key.equals("TRANSLATED_IN")) {
	    // relationship from a transcript to a protein
	    prop = new Property("TRANSLATED_IN");
	    prop.setName("translated in");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("TRANSLATED_FROM"));
	}  else if (key.equals("TRANSLATED_FROM")) {
	    // relationship from a protein to a transcript
	    prop = new Property("TRANSLATED_FROM");
	    prop.setName("translated from");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("TRANSLATED_IN"));
	}  else if (key.equals("INTERACTS_WITH")) {
	    // interaction between gene products
	    prop = new Property("INTERACTS_WITH");
	    prop.setName("interacts with");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("REV_INTERACTS_WITH"));
	}  else if (key.equals("REV_INTERACTS_WITH")) {
	    // interaction between gene products
	    prop = new Property("REV_INTERACTS_WITH");
	    prop.setName("interacts with (rev)");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("INTERACTS_WITH"));
	}  else if (key.equals("ACTIVATES")) {
	    // interaction between gene products
	    prop = new Property("ACTIVATES");
	    prop.setName("activates");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("IS_ACTIVATED_BY"));
            prop.addTarget(this.getProperty("SUBSUME"), this.getProperty("INTERACTS_WITH")); 
	}  else if (key.equals("IS_ACTIVATED_BY")) {
	    // interaction between gene products
	    prop = new Property("IS_ACTIVATED_BY");
	    prop.setName("is activated by");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("ACTIVATES"));
            prop.addTarget(this.getProperty("SUBSUME"), this.getProperty("REV_INTERACTS_WITH")); 
	}  else if (key.equals("INHIBITS")) {
	    // interaction between gene products
	    prop = new Property("INHIBITS");
	    prop.setName("inhibits");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("IS_INHIBITED_BY"));
            prop.addTarget(this.getProperty("SUBSUME"), this.getProperty("INTERACTS_WITH")); 
	}  else if (key.equals("IS_INHIBITED_BY")) {
	    // interaction between gene products
	    prop = new Property("IS_INHIBITED_BY");
	    prop.setName("is inhibited by");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("INHIBITS"));
            prop.addTarget(this.getProperty("SUBSUME"), this.getProperty("REV_INTERACTS_WITH")); 
	}  else if (key.equals("GOANNOTATION")) {
	    // relationship from a gene product to a go term
	    prop = new Property("GOANNOTATION");
	    prop.setName("annotated by go with");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("GOANNOTATE"));
	}  else if (key.equals("GOANNOTATE")) {
	    // relationship from a term to a gene product
	    prop = new Property("GOANNOTATE");
	    prop.setName("annotate");
	    propHash.put(key, prop);
	    prop.setInverse(this.getProperty("GOANNOTATION"));
	}  else if (key.equals("EXPRESSED_IN")) {
	    prop = new Property("EXPRESSED_IN");
	    prop.setName("expressed in");
	    propHash.put(key, prop);
	}  else if (key.equals("EXPRESSED_AT")) {
	    prop = new Property("EXPRESSED_AT");
	    prop.setName("expressed at");
	    propHash.put(key, prop);
	}
	if (prop == null) {
	    log.error("The property '"+key+"' cannot be served by ResourceFactory");
	    return null;
	}
	return prop;
    }

    /**
     * Retrieve of create a resource with a given key
     * @param key the resource to be accessed
     * @return the resource corresponding to the key
     */
    public synchronized Resource getResource(String key) {
	if (key == null) {
	    log.error("PropertyFactory cannot retrieve resource with null id");
	    return null;
	}
	Resource res = (Resource)resHash.get(key);
	if (res != null) return res; // already created
	//
	// database names
	//
	if (key.equals("GO")) {
	    res = new Resource("GO");
	    res.setName("gene ontology");
	    resHash.put(key, res);
	}  else if (key.equals("ENSG")) {
	    res = new Resource("ENSG");
	    res.setName("ensembl gene");
	    resHash.put(key, res);
	}  else if (key.equals("SYMBOL")) {
	    res = new Resource("SYMBOL");
	    res.setName("symbol");
	    resHash.put(key, res);
	}  else if (key.equals("TRUE")) {
	    res = new Resource("TRUE");
	    res.setName("true");
	    resHash.put(key, res);
	}  else if (key.equals("TRUECONTEXT")) {
	    res = new Resource("TRUECONTEXT");
	    res.setName("true context");
	    resHash.put(key, res);
	} else {
	    res = new Resource(key);
	    res.setName(key);
	    resHash.put(key, res);
	}
	return res;
    }
}
