package fr.unice.bioinfo.allonto.datamodel.expression;

import fr.unice.bioinfo.allonto.util.AllontoFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.unice.bioinfo.allonto.datamodel.*;

/**
 * @author Claude Pasquier
 */
public class TrueExpression implements Criterion {
    private static Log log = LogFactory.getLog(Resource.class);
    
    public TrueExpression() {
    }

    public boolean eval(Resource context) {
        return (context.equals(((ResourceFactory)AllontoFactory.getResourceFactory()).getResource("TRUE")));
    }
}
