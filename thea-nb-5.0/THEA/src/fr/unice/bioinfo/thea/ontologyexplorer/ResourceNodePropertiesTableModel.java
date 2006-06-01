package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ResourceNodePropertiesTableModel extends AbstractTableModel {
    static final long serialVersionUID = -1189981033800112308L;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); // NOI18N

    /** Columns names. */
    private String[] columnNames;

    /** Data */
    private Object[][] data;

    public ResourceNodePropertiesTableModel(ResourceNode node) {
        super();
        // init columns
        columnNames = new String[2];
        columnNames[0] = bundle.getString("LBL_PropertiesName");
        columnNames[1] = bundle.getString("LBL_PropertiesValue");

        // get the configuration
        // Configuration con = node.getConfiguration();
        Resource resource = node.getResource();

        Iterator propIt = resource.getProperties().iterator();
        Map propname2value = new HashMap();
        while (propIt.hasNext()) {
            Resource property = (Resource)propIt.next();
            Collection targets = resource.getTargets(property);
            if (targets.size() == 1) {
                Resource res = (Resource)targets.iterator().next();
            // Since values in the Map are not all instances of
            // StringValue,
            // do tests using the instanceof operator
                propname2value
                        .put(property.getAcc(), res.getAcc());

            }
        }

        // if we find properties
        if (!propname2value.isEmpty()) {
            data = new Object[propname2value.size()][2];
            int counter = 0;
            Iterator propnameIt = propname2value.entrySet().iterator();
            while (propnameIt.hasNext()) {
                Map.Entry entry = (Map.Entry) propnameIt.next();
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
     * 
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
     * 
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
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }
}