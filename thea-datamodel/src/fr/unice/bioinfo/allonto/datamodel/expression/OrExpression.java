package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * @author Claude Pasquier
 */
public class OrExpression implements Criterion {
    
    private final Criterion first;
    private final Criterion second;
    
    public OrExpression(Criterion first, Criterion second) {
	this.first = first;
	this.second = second;
    }

    public boolean eval(Resource context) {
	return ( first.eval(context) || second.eval(context));
    }
}
