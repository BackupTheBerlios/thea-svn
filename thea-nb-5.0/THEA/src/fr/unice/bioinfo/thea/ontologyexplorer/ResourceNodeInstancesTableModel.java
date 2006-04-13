package fr.unice.bioinfo.thea.ontologyexplorer;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.util.NbBundle;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:cpasquie@unice.fr"> Claude Pasquier </a>
 */
public class ResourceNodeInstancesTableModel extends AbstractTableModel {
    static final long serialVersionUID = -7006779448775667241L;
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); // NOI18N

    /** Columns names. */
    private String[] columnNames;

    /** Data */
    private Object[][] data;

    public ResourceNodeInstancesTableModel(ResourceNode node) {
        super();
        // init columns
        columnNames = new String[2];
        columnNames[0] = bundle.getString("LBL_InstancesName");
        columnNames[1] = bundle.getString("LBL_InstancesValue");

        try {
            HibernateUtil.createSession(node
                    .getConnection().getConnection());
            Session sess = HibernateUtil.currentSession();
            sess.update(node.getResource());
        } catch (HibernateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // get the configuration
//        Configuration con = node.getConfiguration();
//        Resource resource = node.getResource();
        
        // Get the annotated genes list
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        
        try {
            Set instances = resourceFactory.getResourcesOfType(node.getResource(), true);
            if (instances != null) {
                data = new Object[instances.size()][2];
                int counter = 0;
                Iterator instances_it = instances.iterator();
                while (instances_it.hasNext()) {
                    Resource instance = (Resource)instances_it.next();
                    data[counter][0] = instance.getAcc();
                    data[counter][1] = "";
                    counter++;
                    
                }
            }
        } catch (AllontoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            HibernateUtil.closeSession();
        } catch (HibernateException he) {
            he.printStackTrace();
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