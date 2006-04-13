package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import fr.unice.bioinfo.util.ImportTask;
import fr.unice.bioinfo.util.OwlFastReader;
import fr.unice.bioinfo.util.SWReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import net.sf.hibernate.HibernateException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author <a href="mailto:claude.pasquier@unice.fr"> Claude Pasquier </a>
 */
public class InitializeKnowledgeBaseAction extends NodeAction {
    static final long serialVersionUID = 456213572242749827L;
    
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {

        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        if (!(node instanceof OntologyNode)) {
            return;
        }

        String message = bundle
                .getString("LBL_InitializeKnowledgeBaseAction_Confirmation"); // NOI18N
        if (DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Confirmation(message,
                        NotifyDescriptor.OK_CANCEL_OPTION)) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        DatabaseConnection dbc = ((OntologyNode) node).getConnection();

            Properties prop = new Properties();
            prop.setProperty("hibernate.connection.driver_class", dbc
                    .getDriver());
            prop.setProperty("hibernate.connection.url", dbc.getDatabase());
            prop.setProperty("hibernate.connection.username", dbc.getUser());
            prop
                    .setProperty("hibernate.connectino.password", dbc
                            .getPassword());
            net.sf.hibernate.tool.hbm2ddl.SchemaExport se = null;
            try {
                se = new net.sf.hibernate.tool.hbm2ddl.SchemaExport(HibernateUtil.getConfiguration(), prop);
            } catch (HibernateException ex) {
                ex.printStackTrace();
            }
            se.drop(false, true);
            se.create(false, true);
            // Load standard RDF, RDFS and OWL descriptions
            String rdfFile = this.getClass().getResource("/fr/unice/bioinfo/thea/resources/rdf.rdf").toString();
            String rdfsFile = this.getClass().getResource("/fr/unice/bioinfo/thea/resources/rdfs.rdf").toString();
            String owlFile = this.getClass().getResource("/fr/unice/bioinfo/thea/resources/owl.rdf").toString();
            try {
//                fr.unice.bioinfo.util.ImportTask parser = new fr.unice.bioinfo.util.ImportTask();
                fr.unice.bioinfo.util.OwlReader parser = new fr.unice.bioinfo.util.OwlReader();
                HibernateUtil.createSession(dbc.getConnection());
                net.sf.hibernate.Session sess = HibernateUtil.currentSession();
                ImportTask.updateSchema();
                parser.parse(rdfFile, new CompositeConfiguration(), true, false);
                parser.parse(rdfsFile, new CompositeConfiguration(),true, false);
                parser.parse(owlFile, new CompositeConfiguration(),true, false);
                sess.flush();
            } catch (AllontoException ae) {
                ae.printStackTrace();
                // TODO handle this exception
            } catch (HibernateException he) {
                he.printStackTrace();
            }
            finally {
                try {
                    HibernateUtil.closeSession();
                } catch (HibernateException he) {
                    he.printStackTrace(System.out);
                }
            }
            

        ((OntologyNode) node).getChildren().remove(
                ((OntologyNode) node).getChildren().getNodes());
//        dbc.connect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
        // Enable this action only for the OntologyNode
        nodes = OntologyExplorer.findDefault().getExplorerManager()
                .getSelectedNodes();
        Node node;
        if ((nodes != null) && (nodes.length == 1)) {
            node = nodes[0];
        } else {
            return false;
        }
        if (node instanceof OntologyNode) {
            if (((OntologyNode) node).isConnected()) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_InitializeKnowledgeBaseAction_Name");// NOI18N
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}