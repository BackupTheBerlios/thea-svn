package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.util.AllontoFactory;

public class OntologyProperties {
    private static ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
    .getResourceFactory();

    
    private static OntologyProperties instance = null;

	public static OntologyProperties getInstance() {
		if (instance == null) {
			instance = new OntologyProperties();
		}
		return instance;
	}

	public synchronized String getNodeNameProperty(Configuration config) {
		return config.getString("ontologyexplorer.nodes.nodename");// NOI18N;
	}

	public synchronized Collection getRootNodesURIs(Configuration config) {
		Object o = config.getProperty("ontologyexplorer.roots.uri");// NOI18N
		if (o instanceof Collection) {
			return (Collection) o;
		} else {
			return null;
		}
	}

	public synchronized Hashtable getHierarchyDescription(Configuration config) {
		Hashtable hierarchyDescription = new Hashtable();
		Object ob = config
				.getProperty("ontologyexplorer.hierarchy.relationship.name");// NOI18N
		if (ob instanceof Collection) {
			List al = new ArrayList((Collection) ob);
			Object[] names = al.toArray();
			for (int counter = 0; counter < al.size(); counter++) {

				try {
					String name = (String) names[counter];
					List triple = new ArrayList();
					Resource property = resourceFactory
							.getResource((String) config
									.getProperty("ontologyexplorer.hierarchy.relationship("
											+ counter + ").predicate"));// NOI18N
					Criterion criterion = null;
					Object valueOf = config
							.getProperty("ontologyexplorer.hierarchy.relationship("
									+ counter + ").context.valueof");// NOI18N
					Object equals = config
							.getProperty("ontologyexplorer.hierarchy.relationship("
									+ counter + ").context.equals");// NOI18N
					if ((valueOf != null) && (equals != null)) {
						Resource valueOfResource = resourceFactory
								.getResource((String) valueOf);
						if (equals == null) {
							criterion = Expression.alwaysTrue();
						} else {
							Resource equalsResource = resourceFactory
									.getResource((String) equals);
							criterion = Expression.eq(valueOfResource,
									equalsResource);
						}
					}
					Object iconUrl = config
							.getProperty("ontologyexplorer.hierarchy.relationship("
									+ counter + ").icon");// NOI18N
					hierarchyDescription.put(name, new Object[] { property,
							criterion, (String) iconUrl });
				} catch (AllontoException ae) {
				}
			}
		}
		return hierarchyDescription;
	}

}
