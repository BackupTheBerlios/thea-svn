package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.configuration.Configuration;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
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
        Resource[] roots = null;
        try {

            Configuration con = TheaConfiguration.getDefault()
                    .getConfiguration();
            //if no configuration available,show an error dialog
            // and return immedialtely.
            if (con == null) {
                DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(bundle
                                .getString("ErrMsg_ExplorOntology"),//NOI18N
                                NotifyDescriptor.INFORMATION_MESSAGE));
                return;
            }

            // Create a Session using a Connection
            HibernateUtil.createSession(dbc.getConnection());
            Session sess = HibernateUtil.currentSession();

            ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                    .getResourceFactory();

            Object o = con.getProperty("ontologyexplorer.roots.uri");//NOI18N
            if (o instanceof Collection) {
                ArrayList al = new ArrayList((Collection) o);
                Object[] names = al.toArray();
                roots = new Resource[al.size()];
                for (int counter = 0; counter < al.size(); counter++) {
                    String name = (String) names[counter];
                    roots[counter] = resourceFactory.getResource(name);
                }
            }

        } catch (StackOverflowError s) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, s);
        } catch (HibernateException he) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, he);
        } catch (NullPointerException npe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, npe);
        }

        //Build root nodes and them the root context
        ResourceNode[] rootNodes = new ResourceNode[roots.length];
        ResourceNodeInfo[] rni = new ResourceNodeInfo[roots.length];
        for (int cnt = 0; cnt < roots.length; cnt++) {
            rootNodes[cnt] = new ResourceNode(roots[cnt]);
            rni[cnt] = new ResourceNodeInfo();
            rni[cnt].setConnection(dbc.getConnection());
            rootNodes[cnt].setInfo(rni[cnt]);

            node.getChildren().add(new Node[] { rootNodes[cnt] });
        }

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
        return bundle.getString("LBL_ExploreOntologyAction_Name");//NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}