package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.Consts;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author Saïd El Kasmi
 */
public class ExploreOntologyAction extends NodeAction {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); //NOI18N

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        //Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        if (!(node instanceof OntologyNode)) {
            return;
        }

        //        RequestProcessor.getDefault().post(new Runnable() {
        //            public void run() {
        DatabaseConnection dbc = ((OntologyNode) node).getConnection();
        List result = null;
        Resource root = null;
        try {

            ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                    .getResourceFactory();
            // Create a Session using a Connection
            HibernateUtil.createSession(dbc.getConnection());
            Session sess = HibernateUtil.currentSession();

            Query q = sess
                    .createQuery("select res from Resource res, StringValue sv where res.arcs[:name] = sv and sv.value = :nm");

            q.setString("nm", "top");
            q.setEntity("name", resourceFactory.getProperty("NAME"));

            result = q.list();
            root = (Resource) result.iterator().next();

        } catch (StackOverflowError s) {
            s.printStackTrace(System.err);
        } catch (HibernateException he) {
            he.printStackTrace(System.err);
        }
        Set childs = ((Resource) root).getTargets(Consts.getListOfProperties());

        //Build a root node and set the root context
        ResourceNode rootNode = new ResourceNode(root, childs);
        ResourceNodeInfo rni = new ResourceNodeInfo();
        rni.setConnection(dbc.getConnection());
        rootNode.setInfo(rni);

        node.getChildren().add(new Node[] { rootNode });
        //            }
        //        }, 0);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
        //Enable this action only for the OntologyNode
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
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_ExploreOntologyAction_Name");
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}