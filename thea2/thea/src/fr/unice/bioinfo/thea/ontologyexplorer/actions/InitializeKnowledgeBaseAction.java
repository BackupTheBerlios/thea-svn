package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Properties;
import net.sf.hibernate.HibernateException;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class InitializeKnowledgeBaseAction extends NodeAction {
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

        try {
            Properties prop = new Properties();
            prop.setProperty("hibernate.connection.driver_class", dbc
                    .getDriver());
            prop.setProperty("hibernate.connection.url", dbc.getDatabase());
            prop.setProperty("hibernate.connection.username", dbc.getUser());
            prop
                    .setProperty("hibernate.connectino.password", dbc
                            .getPassword());
            net.sf.hibernate.tool.hbm2ddl.SchemaExport se = new net.sf.hibernate.tool.hbm2ddl.SchemaExport(
                    HibernateUtil.getConfiguration(), prop);
            se.drop(false, true);
            se.create(false, true);
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        ((OntologyNode) node).getChildren().remove(
                ((OntologyNode) node).getChildren().getNodes());
        dbc.connect();
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