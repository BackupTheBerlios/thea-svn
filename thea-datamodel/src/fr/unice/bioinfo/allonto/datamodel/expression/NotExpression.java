package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * @author Claude Pasquier
 */
public class NotExpression implements Criterion {
    
    private final Criterion criterion;
    
    public NotExpression(Criterion criterion) {
	this.criterion = criterion;
    }

    public boolean eval(Resource context) {
	return ( !criterion.eval(context));
    }
}
