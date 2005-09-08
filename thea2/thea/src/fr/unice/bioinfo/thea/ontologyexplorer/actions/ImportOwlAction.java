package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Frame;
import java.io.File;
import java.sql.Connection;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;
import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;
import fr.unice.bioinfo.thea.util.BlockingSwingWorker;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ImportOwlAction extends NodeAction {
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    private Connection connection = null;

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
        connection = ((OntologyNode) node).getConnection().getConnection();

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(OESettings.getInstance()
                .getLastBrowsedDirectory()));
        chooser.setMultiSelectionEnabled(true);
        JCheckBox useInferenceCheckBox = new JCheckBox(bundle
                .getString("LBL_ImportOntologyAction_UseInference"));// NOI18N);
        chooser.setAccessory(useInferenceCheckBox);
        int r = chooser.showOpenDialog(WindowManager.getDefault()
                .getMainWindow());
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // creates the configuration by merging default and local config files

        CompositeConfiguration cc = new CompositeConfiguration();
        Configuration defaultConfiguration = TheaConfiguration.getDefault()
                .getConfiguration();
        if (defaultConfiguration != null) {
            cc.addConfiguration(defaultConfiguration);
        }
        Configuration localConfiguration = ((OntologyNode) node)
                .getConfiguration();
        if (localConfiguration != null) {
            cc.addConfiguration(localConfiguration);
        }

        boolean useInference = useInferenceCheckBox.isSelected();
        File[] files = chooser.getSelectedFiles();
        importTask(files, cc, useInference);

        // Save the new used path
        // OESettings.getInstance().setLastBrowsedDirectory(file.getParent());

    }

    public void importTask(final File[] files,
            final Configuration configuration, final boolean useInference) {
        BlockingSwingWorker worker = new BlockingSwingWorker(
                (Frame) WindowManager.getDefault().getMainWindow(),
                "Importing files...", "Import in progress, please wait...",
                true) {
            protected void doNonUILogic() throws RuntimeException {
                for (int counter = 0; counter < files.length; counter++) {
                    String filePath = files[counter].toURI().toString();
                    try {
                        fr.unice.bioinfo.batch.ImportOwlModel parser = new fr.unice.bioinfo.batch.ImportOwlModel();
                        try {
                            HibernateUtil.createSession(connection);
                        } catch (HibernateException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        parser.load(filePath, configuration, useInference);
                        try {
                            HibernateUtil.closeSession();
                        } catch (HibernateException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        System.out.println("processing of file: " + filePath
                                + " successfull");
                    } catch (AllontoException ae) {
                        // ae.printStackTrace();
                        System.out.println("error in the processing of file: "
                                + filePath);
                        // TODO handle this exception
                    }
                }
            }
        };
        worker.start();
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
            if (((OntologyNode) node).isConnected()
                    && ((OntologyNode) node).isCompatibleKB()) {
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
        return bundle.getString("LBL_ImportOntologyAction_Name");// NOI18N
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