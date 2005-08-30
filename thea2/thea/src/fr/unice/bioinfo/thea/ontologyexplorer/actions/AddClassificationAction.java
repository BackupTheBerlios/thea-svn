package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.classification.io.wizard.ClassificationInfo;
import fr.unice.bioinfo.thea.classification.io.wizard.OpenClassificationWizardDescriptor;
import fr.unice.bioinfo.thea.classification.io.wizard.SupportedFormat;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationsRootNode;

/**
 * A menu item that allow users to add a classification.
 * @author Saïd El Kasmi.
 */
public class AddClassificationAction extends NodeAction {
    private Dialog dialog;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); // NOI18N

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        // Note: when adding nodes,test for redundance
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];
        OpenClassificationWizardDescriptor wd = new OpenClassificationWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.show();
        if (wd.getValue() == WizardDescriptor.FINISH_OPTION) {
            File cdf = ClassificationInfo.getInstance().getClusteredDataFile();
            File tdf = ClassificationInfo.getInstance().getTabularDataFile();
            String name = ClassificationInfo.getInstance().getName();
            if (name.equalsIgnoreCase("")) {
                name = "Classification - "
                        + (ClassificationInfo.getInstance().getCounter() + 1);// NOI18N
            }
            String description = ClassificationInfo.getInstance().getHint();
            String format = ClassificationInfo.getInstance().getFileFormat();
            StringBuffer b = new StringBuffer();
            // if no file selected
            if ((cdf == null) && (tdf == null)) {
                b.append(bundle.getString("NewClassificationDialog_NoFile")); // NOI18N
            }

            // when the selected format is Eisen,the two files are
            // required
            if (format.equalsIgnoreCase(SupportedFormat.EISEN)) {
                if ((cdf == null) || (tdf == null)) {
                    b.append(bundle.getString("NewClassificationDialog_EISEN")); // NOI18N
                }
            }

            // Show a dialog with errors if any
            if (b.length() > 0) {
                String message = MessageFormat.format(bundle
                        .getString("NewClassificationDialog_ErrMsg"),// NOI18N
                        new String[] { b.toString() });
                DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(message,
                                NotifyDescriptor.INFORMATION_MESSAGE));
                return;
            }
            // If everything is ok: create the two nodes which
            // represent the two data files
            // and add them to a node of type ClassificationNode

            // The node that represents the classification
            ClassificationNode cn = new ClassificationNode(name, description);
            // Create the ClassificationNodeInfo
            ClassificationNodeInfo cni = ClassificationNodeInfo
                    .createClNodeInfo();
            // Collect information about personnalization of the JTable
            cni.setSelectedFormat(ClassificationInfo.getInstance()
                    .getFileFormat());
            cni.setIndexOfFirstIgnoredRow(ClassificationInfo.getInstance()
                    .getMinIgnoredRow());
            cni.setIndexOfLastIgnoredRow(ClassificationInfo.getInstance()
                    .getMaxIgnoredRow());
            cni.setIndexOfFirstIgnoredColumn(ClassificationInfo.getInstance()
                    .getMinIgnoredColumn());
            cni.setIndexOfLastIgnoredColumn(ClassificationInfo.getInstance()
                    .getMaxIgnoredColumn());
            cni.setIndexOfGeneColumn(ClassificationInfo.getInstance()
                    .getGeneLabels());
            cni.setIndexOfTitleRow(ClassificationInfo.getInstance()
                    .getColumnLabels());
            cni.setNbColumns(ClassificationInfo.getInstance().getNbColumns());

            cni.setName(name);
            cni.setCFile(cdf);
            cni.setTFile(tdf);
            cn.setInfo(cni);
            // Add the classification node to the ontology node
            ((ClassificationsRootNode) node).getChildren().add(
                    new Node[] { cn });
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
        if (node instanceof ClassificationsRootNode) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return bundle.getString("LBL_NewClassificationFileAction_Name");
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
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#iconResource()
     */
    protected String iconResource() {
        return "fr/unice/bioinfo/thea/ontologyexplorer/resources/NewClassificationFileIcon.png"; // NOI18N
    }

    // Closes dialog
    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
}