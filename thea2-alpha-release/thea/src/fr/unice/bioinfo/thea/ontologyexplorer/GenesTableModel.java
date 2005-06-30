package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.configuration.Configuration;

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
public class GenesTableModel extends AbstractTableModel {

    /** Columns names. */
    private String[] columnNames;
    /** Data */
    private Object[][] data;

    private String[] properties;
    private String[] evidences;

    public GenesTableModel(Resource resource, String[] evidences,
            String[] properties) {
        super();
        this.evidences = evidences;
        this.properties = properties;
        // init columns
        columnNames = new String[properties.length];
        for (int counter = 0; counter < properties.length; counter++) {
            String name = properties[counter];
            columnNames[counter] = name.substring(name.indexOf("#") + 1);//NOI18N;
        }

        // get the configuration
        Configuration con = TheaConfiguration.getDefault().getConfiguration();

        // Get the annotated genes list
        String annotatePropertyName;
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        annotatePropertyName = (String) con
                .getProperty("ontologyexplorer.nodes.annotates");//NOI18N
        String hasEvidenceProperty = (String) con
                .getProperty("ontologyexplorer.nodes.hasevidence");//NOI18N

        resourceFactory.setMemoryCached(true);
        LinkedList ll = new LinkedList();
        for (int cnt = 0; cnt < evidences.length; cnt++) {
            ll.add(resourceFactory.getResource(evidences[cnt]));
        }
        Criterion criterion = Expression.in(resourceFactory
                .getResource(hasEvidenceProperty), ll);

        Resource annotateProperty = resourceFactory
                .getProperty(annotatePropertyName);
        resourceFactory.setMemoryCached(false);

        Set genes = resource.getTargets(annotateProperty, criterion);

        // if we find a list of genes:
        if (genes != null) {
            data = new Object[genes.size()][columnNames.length];
            int counter = 0;
            Iterator genesIt = genes.iterator();
            Resource aResource;
            // iterate over the list of found genes
            while (genesIt.hasNext()) {
                aResource = (Resource) genesIt.next();
                // for each gene: get the list of properties
                for (int cnt = 0; cnt < properties.length; cnt++) {
                    resourceFactory.setMemoryCached(true);
                    Resource accessedProperty = resourceFactory
                            .getResource(properties[cnt]);
                    resourceFactory.setMemoryCached(false);

                    StringValue sv = (StringValue) aResource
                            .getTarget(accessedProperty);
                    if (sv != null) {
                        data[counter][cnt] = sv.getValue();
                    } else if (sv == null) {
                        data[counter][cnt] = "";//NOI18N
                    }
                }
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