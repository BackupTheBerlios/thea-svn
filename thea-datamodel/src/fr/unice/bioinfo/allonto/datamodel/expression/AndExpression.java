package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * A criterion performing the intersection (AND) of two criteria
 * @author Claude Pasquier
 */
public class AndExpression implements Criterion {
    
    private final Criterion first;
    private final Criterion second;
    
    /**
     * Constructor
     * @param first the first criterion
     * @param second the second criterion
     */
    public AndExpression(Criterion first, Criterion second) {
	this.first = first;
	this.second = second;
    }

    /**
     * Evaluate the criteria against a context
     * @param context the context to evaluate with the 2 criterion
     * @return true if the context satisfy both criteriq, false otherwise
     */
    public boolean eval(Resource context) {
	return ( first.eval(context) && second.eval(context));
    }
}
