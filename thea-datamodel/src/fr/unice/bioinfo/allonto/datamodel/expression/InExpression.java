package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.util.AllontoFactory;
import java.util.*;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * @author Claude Pasquier
 */
public class InExpression implements Criterion {
    protected static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();
   
    private final Resource property;
    private final Collection values;
    
    public InExpression(Resource property,
			Node[] values) {
	this.property = property;
	this.values = new HashSet();
	for (int i = 0 ; i < values.length; i++) {
	    this.values.add(values[i]);
	}
    }

    public InExpression(Resource property,
			Collection values) {
	this.property = property;
	this.values = values;
    }

    /*
      test for the value and all resources which is subsumed
    */
    public InExpression(Resource property,
			Node value) {
	this.property = property;
	values = new HashSet();
	values.add(value);
	if (value instanceof Resource) {
	    Set impliedValues = ((Resource)value).getReachableTargets((Resource)resourceFactory.getProperty("SUBSUMED_BY"));
	    if (impliedValues != null) {
		values.addAll(impliedValues);
	    }
	}
    }

    public boolean eval(Resource context) {
        if (context == resourceFactory.getResource("TRUE")) return true;
	Node target = context.getTarget(property);
        if ((target != null) && values.contains(target)) {
            return true;
        }
        else {
	    Set supercontexts = context.getTargets((Resource)resourceFactory.getProperty("SPECIALIZES"));
            if (supercontexts == null) return false;
            Iterator it = supercontexts.iterator();
            boolean success = false;
            while (!success && it.hasNext()) {
                Context supercontext = (Context)it.next();
                success = this.eval(supercontext);
            }
            return success;
        }
    }
}
