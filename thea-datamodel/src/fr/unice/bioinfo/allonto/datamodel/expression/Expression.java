package fr.unice.bioinfo.allonto.datamodel.expression;

import java.util.*;

import fr.unice.bioinfo.allonto.datamodel.*;

public final class Expression {
	
    private Expression() {
	//cannot be instantiated
    }
    
    /**
     * Represents an "equal" constraint to the named property
     * @param property
     * @param value
     * @return Criterion
     */
    public static Criterion eq(Resource property, Node value) {
	return new EqExpression(property, value);
    }

    /**
     * Represents an "in" constraint to the named property
     * @param property
     * @param value
     * @return Criterion
     */
    public static Criterion inContext(Resource property, Node value) {
	return new InExpression(property, value);
    }

    /**
     * Represents an "in" constraint to the named property
     * @param property
     * @param values
     * @return Criterion
     */
    public static Criterion in(Resource property, Node[] values) {
	return new InExpression(property, values);
    }

    /**
     * Represents an "in" constraint to the named property
     * @param property
     * @param values
     * @return Criterion
     */
    public static Criterion in(Resource property, Collection values) {
	return new InExpression( property, values );
    }

    /**
     * Represents an "is null" constraint to the named property
     * @param property
     * @return Criterion
     */
    public static Criterion isNull(Resource property) {
	return new NullExpression(property);
    }

    /**
     * Represents an "is not null" constraint to the named property
     * @param property
     * @return Criterion
     */
    public static Criterion isNotNull(Resource property) {
	return new NotNullExpression(property);
    }

    /**
     * Represents the conjuction of two expressions
     * 
     * @param first
     * @param second
     * @return Criterion
     */
    public static Criterion and(Criterion first, Criterion second) {
	return new AndExpression(first, second);
    }
    
    /**
     * Represents the disjuction of two expressions
     * 
     * @param first
     * @param second
     * @return Criterion
     */
    public static Criterion or(Criterion first, Criterion second) {
	return new OrExpression(first, second);
    }

    /**
     * Represents the negation of an expression
     * 
     * @param expression
     * @return Criterion
     */
    public static Criterion not(Criterion expression) {
	return new NotExpression(expression);
    }
    
    /**
     * Represents the true (no context) expression
     */
    public static Criterion alwaysTrue() {
        return new TrueExpression();
    }
    
}
