package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * A criterion used to filter contexts
 */
public interface Criterion {

    /**
     * Evaluate the criterion against a context
     * @param context the context to evaluate with the criterion
     * @return true if the context satisfy the criterion, false otherwise
     */
    public boolean eval(Resource context);
}
