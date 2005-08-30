package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;
import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ImportOwlAction extends NodeAction {
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
        // Create a Session using a Connection
        DatabaseConnection dbc = ((OntologyNode) node).getConnection();
        try {
            HibernateUtil.createSession(dbc.getConnection());
        } catch (HibernateException he) {
            // TODO handle this exception
        }

        boolean useInference = useInferenceCheckBox.isSelected();
        File[] files = chooser.getSelectedFiles();
        for (int counter = 0; counter < files.length; counter++) {
            String filePath = files[counter].toURI().toString();
            // getAbsolutePath();
            fr.unice.bioinfo.batch.ImportOwlModel parser = new fr.unice.bioinfo.batch.ImportOwlModel();
            try {
                parser.parse(filePath, useInference);
            } catch (AllontoException ae) {
                // TODO handle this exception
            }
        }

        // Save the new used path
        // OESettings.getInstance().setLastBrowsedDirectory(file.getParent());

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