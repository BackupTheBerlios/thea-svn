package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Iterator;
import java.util.Map;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.configuration.Configuration;
import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.Entity;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ResourceNodePropertiesTableModel extends AbstractTableModel {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); // NOI18N

    /** Columns names. */
    private String[] columnNames;

    /** Data */
    private Object[][] data;

    public ResourceNodePropertiesTableModel(Resource resource) {
        super();
        // init columns
        columnNames = new String[2];
        columnNames[0] = bundle.getString("LBL_PropertiesName");
        columnNames[1] = bundle.getString("LBL_PropertiesValue");

        // get the configuration
        Configuration con = TheaConfiguration.getDefault().getConfiguration();

        Iterator mapIt = resource.getArcs().entrySet().iterator();
        Map propname2value = new HashMap();
        while (mapIt.hasNext()) {
            Map.Entry entry = (Map.Entry) mapIt.next();
            Entity val = (Entity) entry.getValue();
            // Since values in the Map are not all instances of
            // StringValue,
            // do tests using the instanceof operator
            if (val instanceof StringValue) {
                Resource property = (Resource) entry.getKey();
                propname2value.put(property.getAcc(), ((StringValue) val)
                        .getValue());
            }
        }

        // if we find properties
        if (!propname2value.isEmpty()) {
            data = new Object[propname2value.size()][2];
            int counter = 0;
            Iterator propIt = propname2value.entrySet().iterator();
            while (propIt.hasNext()) {
                Map.Entry entry = (Map.Entry) propIt.next();
                String propName = (String) entry.getKey();
                String propValue = (String) entry.getValue();
                data[counter][0] = propName;
                data[counter][1] = propValue;
                counter++;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        if (columnNames == null) {
            return 0;
        }
        return columnNames.length;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }
}