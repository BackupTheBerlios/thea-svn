package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Frame;
import java.io.File;
import java.sql.Connection;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.sf.hibernate.HibernateException;

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
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;
import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;
import fr.unice.bioinfo.thea.util.BlockingSwingWorker;
import fr.unice.bioinfo.thea.util.OwlImportProgressDialog;

/**
 * @author <a href="mailto:claude.pasquier@unice.fr"> Claude Pasquier </a>
 */
public class ImportOwlAction extends NodeAction {
    static final long serialVersionUID = -8263503394950983654L;

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
                .getLastBrowsedOwlDirectory()));
        chooser.setMultiSelectionEnabled(true);
        JCheckBox useInferenceCheckBox = new JCheckBox(bundle
                .getString("LBL_ImportOntologyAction_UseInference"));// NOI18N);
        //chooser.setAccessory(useInferenceCheckBox);
        JCheckBox processImportsCheckBox = new JCheckBox(bundle
                .getString("LBL_ImportOntologyAction_ProcessImports"));// NOI18N);
        //chooser.setAccessory(processImportsCheckBox);
        JComponent accessory = new JPanel();
        accessory.setLayout(new BoxLayout(accessory, BoxLayout.Y_AXIS));
        accessory.add(useInferenceCheckBox);
        accessory.add(processImportsCheckBox);
        chooser.setAccessory(accessory);
        int r = chooser.showOpenDialog(WindowManager.getDefault()
                .getMainWindow());
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }

        boolean useInference = useInferenceCheckBox.isSelected();
        boolean processImports = processImportsCheckBox.isSelected();
        File[] files = chooser.getSelectedFiles();
        if (files.length == 0)
            return;
        // Save the new used path
        OESettings.getInstance().setLastBrowsedOwlDirectory(
                files[0].getParent());

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

        // Imports the list of files

        importTask(files, cc, useInference, processImports);

    }

    public void importTask(final File[] files,
            final Configuration configuration, final boolean useInference,
            final boolean processImports) {
        final OwlImportProgressDialog importProgressDialog = new OwlImportProgressDialog(
                WindowManager.getDefault().getMainWindow());
        BlockingSwingWorker worker = new BlockingSwingWorker(
                (Frame) WindowManager.getDefault().getMainWindow(),
                importProgressDialog) {
            protected void doNonUILogic() throws RuntimeException {
                for (int counter = 0; counter < files.length; counter++) {
                    String filePath = files[counter].toURI().toString();
                    try {
                        fr.unice.bioinfo.batch.OwlReader parser = new fr.unice.bioinfo.batch.OwlReader();
                        parser
                                .addOwlParsingListener((fr.unice.bioinfo.batch.OwlParsingListener) importProgressDialog);
                        try {
                            HibernateUtil.createSession(connection);
                        } catch (HibernateException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        parser.load(filePath, configuration, useInference, processImports);

                        try {
                            HibernateUtil.closeSession();
                        } catch (HibernateException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        parser
                                .removeOwlParsingListener((fr.unice.bioinfo.batch.OwlParsingListener) importProgressDialog);
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