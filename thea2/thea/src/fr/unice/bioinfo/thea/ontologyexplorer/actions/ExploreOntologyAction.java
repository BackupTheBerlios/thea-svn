package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyProperties;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.ConfigBrowserPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ResourceNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;
import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class ExploreOntologyAction extends NodeAction {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
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

        Configuration localConfiguration = ((OntologyNode) node)
                .getConfiguration();
        Configuration defaultConfiguration = TheaConfiguration.getDefault()
                .getConfiguration();

        if (localConfiguration == null) {
            if (defaultConfiguration == null) {
                // No configuration available
                // Select a default configuration in Prefences
                // or choose a local one
                // Create the panel
                final ConfigBrowserPanel panel = new ConfigBrowserPanel();
                // Create the listener for buttons actions/ Ok/Cancel
                ActionListener al = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == DialogDescriptor.OK_OPTION) {

                            String filePath = panel.getFilePath();

                            if (filePath == null) {
                                ((OntologyNode) node).setConfiguration(null);
                                return;
                            } else {
                                try {
                                    XMLConfiguration conf = new XMLConfiguration(
                                            filePath);
                                    ((OntologyNode) node)
                                            .setConfiguration(conf);
                                } catch (ConfigurationException ce) {
                                    ((OntologyNode) node)
                                            .setConfiguration(null);
                                    ErrorManager.getDefault().notify(
                                            ErrorManager.INFORMATIONAL, ce);
                                    String message = bundle
                                            .getString("ErrMsg_InvalidConfigurationFile"); // NOI18N
                                    NotifyDescriptor d = new NotifyDescriptor.Message(
                                            message,
                                            NotifyDescriptor.INFORMATION_MESSAGE);
                                    DialogDisplayer.getDefault().notify(d);
                                }
                            }
                        }
                    }
                };
                // Use DialogDescriptor to show the panel
                DialogDescriptor descriptor = new DialogDescriptor(panel,
                        bundle.getString("LBL_BrowseDialogTitle"), true, al); // NOI18N
                Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
                // descriptor.setClosingOptions(closingOptions);
                final Dialog dialog = DialogDisplayer.getDefault()
                        .createDialog(descriptor);
                dialog.show();
            } else {
                // Only a default configuration file is found
                // ask user if only default configuration should be used
                String message = bundle
                        .getString("LBL_ExploreOntologyAction_UseDefaultConfiguration_Confirmation"); // NOI18N
                if (DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Confirmation(message,
                                NotifyDescriptor.OK_CANCEL_OPTION)) != NotifyDescriptor.OK_OPTION) {
                    return;
                }
            }
        }
        localConfiguration = ((OntologyNode) node).getConfiguration();
        if ((localConfiguration == null) && (defaultConfiguration == null)) {
            return;
        }
        CompositeConfiguration cc = new CompositeConfiguration();
        if (defaultConfiguration != null) {
            cc.addConfiguration(defaultConfiguration);
        }
        if (localConfiguration != null) {
            cc.addConfiguration(localConfiguration);
        }

        DatabaseConnection dbc = ((OntologyNode) node).getConnection();

        List result = null;
        Resource[] roots = null;
        try {

            // if no configuration available,show an error dialog
            // and return immedialtely.

            // TODO (a supprimer)
            if (cc == null) {
                DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(bundle
                                .getString("ErrMsg_ExplorOntology"),// NOI18N
                                NotifyDescriptor.INFORMATION_MESSAGE));
                return;
            }

            // Create a Session using a Connection
            HibernateUtil.createSession(dbc.getConnection());
            Session sess = HibernateUtil.currentSession();

            ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                    .getResourceFactory();
            ArrayList rootsNotFound = new ArrayList();
            // Object o = cc.getProperty("ontologyexplorer.roots.uri");// NOI18N
            Object o = OntologyProperties.getInstance().getRootNodesURIs(cc);

            if (o instanceof Collection) {
                ArrayList rootList = new ArrayList();
                Iterator rootIt = ((Collection) o).iterator();
                while (rootIt.hasNext()) {
                    String name = (String) rootIt.next();
                    try {
                        Resource root = resourceFactory.getResource(name);
                        if (root != null) {
                            rootList.add(root);
                        } else {
                            rootsNotFound.add(name);
                        }
                    } catch (AllontoException ae) {
                        rootsNotFound.add(name);
                    }

                }
                roots = new Resource[rootList.size()];
                rootIt = rootList.iterator();
                int counter = 0;
                while (rootIt.hasNext()) {
                    roots[counter] = (Resource) rootIt.next();
                    counter += 1;
                }
                // display error message if needed
                if (!rootsNotFound.isEmpty()) {
                    String message = bundle
                            .getString("ErrMsg_OntologyRootNotFound"); // NOI18N

                    rootIt = rootsNotFound.iterator();
                    while (rootIt.hasNext()) {
                        message += "\n" + (String) rootIt.next();
                    }

                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(message,
                                    NotifyDescriptor.INFORMATION_MESSAGE));

                }

            }

        } catch (StackOverflowError s) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, s);
        } catch (HibernateException he) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, he);
        } catch (NullPointerException npe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, npe);
        }

        if (roots == null) {
            String message = bundle
                    .getString("ErrMsg_ExploreOntologyAction_noRootToDisplay"); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message,
                    NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return;
        }
        // Build root nodes and then the root context
        ResourceNode[] rootNodes = new ResourceNode[roots.length];
        ResourceNodeInfo[] rni = new ResourceNodeInfo[roots.length];
        for (int cnt = 0; cnt < roots.length; cnt++) {
            rootNodes[cnt] = new ResourceNode(roots[cnt]);
            rni[cnt] = new ResourceNodeInfo();
            rni[cnt].setResource(roots[cnt]);
            // rni[cnt].setOntologyNode((OntologyNode)node);
            rootNodes[cnt].setInfo(rni[cnt]);
            rootNodes[cnt]
                    .setIconBase("fr/unice/bioinfo/thea/ontologyexplorer/resources/RootResourceIcon16");
            node.getChildren().add(new Node[] { rootNodes[cnt] });
        }
    }

    /*
     * (non-Javadoc)
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
            if (((OntologyNode) node).isConnected()
                    && ((OntologyNode) node).isCompatibleKB()
                    && (((OntologyNode) node).getNbResources() > 0)) {
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
        return bundle.getString("LBL_ExploreOntologyAction_Name");// NOI18N
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}