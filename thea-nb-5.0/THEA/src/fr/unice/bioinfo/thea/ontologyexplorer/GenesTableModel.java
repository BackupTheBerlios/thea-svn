package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.configuration.Configuration;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GenesTableModel extends AbstractTableModel {
    static final long serialVersionUID = -4735352769266253105L;

    /** Columns names. */
    private String[] columnNames;

    /** Data */
    private Object[][] data;

//    private String[] properties;

//    private String[] evidences;

    public GenesTableModel(ResourceNode resourceNode, String[] evidences,
            String[] properties) {
        super();
//        this.evidences = evidences;
//        this.properties = properties;
        // init columns
        columnNames = new String[properties.length];
        for (int counter = 0; counter < properties.length; counter++) {
            String name = properties[counter];
            columnNames[counter] = name.substring(name.indexOf("#") + 1);// NOI18N;
        }

        // get the configuration
        Configuration con = TheaConfiguration.getDefault().getConfiguration();

        // Get the annotated genes list
        String annotatePropertyName;
        ResourceFactory resourceFactory = resourceNode.getResource().getResourceFactory();
        
        annotatePropertyName = (String) con
                .getProperty("ontologyexplorer.nodes.annotates");// NOI18N
        String hasEvidenceProperty = (String) con
                .getProperty("ontologyexplorer.nodes.hasevidence");// NOI18N
                System.out.println("resourceFactory="+resourceFactory);
                System.out.println("hasevidence="+hasEvidenceProperty);
        try {
            System.out.println("hasevidenceProp="+resourceFactory
                .getResource(hasEvidenceProperty));
            System.out.println("hasevidenceProp.rf="+resourceFactory
                .getResource(hasEvidenceProperty).getResourceFactory());
        } catch (AllontoException ex) {
            ex.printStackTrace();
        }
        LinkedList ll = new LinkedList();
        for (int cnt = 0; cnt < evidences.length; cnt++) {
            try {
                Resource evidence = resourceFactory.getResource(evidences[cnt]);
                System.out.println("evidence="+evidence);
                if (evidence != null) {
                    ll.add(evidence);
                }
            } catch (AllontoException ae) {
            }
        }
        Collection genes = null;
        try {
            Criterion criterion = Expression.in(resourceFactory
                    .getResource(hasEvidenceProperty), ll);

            Resource annotateProperty = resourceFactory
                    .getResource(annotatePropertyName);
            genes = resourceNode.getResource().getTargets(annotateProperty,
                    criterion);
        } catch (AllontoException ae) {
        }
        // if we find a list of genes:
        if (genes != null) {
            Object[] propertyPaths = new Object[properties.length];
            for (int cnt = 0; cnt < properties.length; cnt++) {
                List aPath = new ArrayList();
                try {
                    Resource prop = resourceFactory
                            .getResource("http://www.w3.org/2002/07/owl#sameAs");
//                    if (prop == null) continue;
                    aPath.add(prop);
                    prop=resourceFactory
                            .getResource("http://www.unice.fr/bioinfo/owl/biowl#translated_from");
//                    if (prop == null) continue;
                    aPath.add(prop);
                    prop=resourceFactory
                            .getResource(properties[cnt]);
//                    if (prop == null) continue;
                    aPath.add(prop);
                    propertyPaths[cnt] = aPath;
                } catch (AllontoException ex) {
                    ex.printStackTrace();
                }
            }
            
            data = new Object[genes.size()][columnNames.length];
            int counter = 0;
            Iterator genesIt = genes.iterator();
            Resource aResource;
            // iterate over the list of found genes
            while (genesIt.hasNext()) {
                aResource = (Resource) genesIt.next();
                System.out.println("gene="+aResource);
                // for each gene: get the list of properties
                for (int cnt = 0; cnt < properties.length; cnt++) {
//                    try {
//                        Resource accessedProperty = resourceFactory
//                                .getResource(properties[cnt]);
//                        if (accessedProperty != null) {
//                            System.out.println("pp="+propertyPaths[0]+","+propertyPaths[1]+","+propertyPaths[2]);
                            Resource res = aResource
                                    .getTarget((List)propertyPaths[cnt]);
                            if (res != null) {
                                data[counter][cnt] = res.getAcc();
                            } else if (res == null) {
                                data[counter][cnt] = "";// NOI18N
                            }
//                        }
//                    } catch (AllontoException ae) {
//                        data[counter][cnt] = "";// NOI18N
////                        resourceFactory.setMemoryCached(false);
//                    }
                }
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