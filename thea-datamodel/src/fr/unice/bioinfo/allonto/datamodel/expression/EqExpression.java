package fr.unice.bioinfo.allonto.datamodel.expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import fr.unice.bioinfo.allonto.util.AllontoFactory;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * @author Claude Pasquier
 */
public class EqExpression implements Criterion {
    private static Log log = LogFactory.getLog(Resource.class);
    protected static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();
    
    private final Resource property;
    private final Node value;
    
    public EqExpression(Resource property,
			Node     value) {
	this.property = property;
	this.value = value;
    }

    public boolean eval(Resource context) {
	Node target = context.getTarget(property);
        if ((target != null) && target.equals(value)) {
            System.err.println("context="+context+", target="+target+", value="+value);
            return true;
        }
        else {
            Context resourceClass = (Context)context.getResourceClass();
	    if (resourceClass == null) return false;
            return this.eval(resourceClass);
        }

//        Node target = context.getTarget(property);
//	if (target == null) {
//	    return false;
//	}
//	return (target.equals(value));
    }
}
