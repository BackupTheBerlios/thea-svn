package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * @author Claude Pasquier
 */
public class NullExpression implements Criterion {
    
    private final Resource property;
    
    public NullExpression(Resource property) {
	this.property = property;
    }

    public boolean eval(Resource context) {
	return (context.getTarget(property) == null);
    }
}
