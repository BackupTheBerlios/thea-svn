package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import fr.unice.bioinfo.thea.ontologyexplorer.ClDataObject;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.dlg.NewClPanel;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationsRootNode;

/**
 * A menu item that allow users to add a classification.
 * @author Saïd El Kasmi.
 */
public class NewClAction extends NodeAction {
    private Dialog dialog;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.actions.Bundle"); //NOI18N

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#performAction(org.openide.nodes.Node[])
     */
    protected void performAction(Node[] arg0) {
        //      Note: when adding nodes,test for redundance
        // Get the explorer manager from the ontology explorer
        OntologyExplorer e = OntologyExplorer.findDefault();

        // Extract the node
        final Node node = e.getExplorerManager().getSelectedNodes()[0];

        // Create the panel
        final NewClPanel panel = new NewClPanel();

        // Create the listener for buttons actions/ Ok/Cancel
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION) {
                    File cdf = panel.getClusteredDataFile();
                    File tdf = panel.getTabularDataFile();
                    String clName = panel.getClName();
                    String clHint = panel.getClDescription();
                    int format = NewClPanel.getSelectedFormat();
                    StringBuffer b = new StringBuffer();
                    // if no name is given
                    if (clName.equalsIgnoreCase("") || (clName == null)) {//NOI18N
                        b.append(bundle
                                .getString("NewClassificationDialog_NoName")); //NOI18N
                    }

                    // if no file selected
                    if ((cdf == null) && (tdf == null)) {
                        b.append(bundle
                                .getString("NewClassificationDialog_NoFile")); //NOI18N
                    }

                    // when the selected format is Eisen,the two files are
                    // required
                    if (format == NewClPanel.TYPE_EISEN) {
                        if ((cdf == null) || (tdf == null)) {
                            b
                                    .append(bundle
                                            .getString("NewClassificationDialog_EISEN")); //NOI18N
                        }
                    }

                    // Show a dialog with errors if any
                    if (b.length() > 0) {
                        String message = MessageFormat.format(bundle
                                .getString("NewClassificationDialog_ErrMsg"),//NOI18N
                                new String[] { b.toString() });
                        DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(message,
                                        NotifyDescriptor.INFORMATION_MESSAGE));

                        return;
                    }

                    // close dialog
                    closeDialog();

                    // If everything is ok: create the two nodes which
                    // represent the two data files
                    // and add them to a node of type ClassificationNode

                    // The node that represents the classification
                    ClassificationNode cn = new ClassificationNode(clName,
                            clHint);
                    // Create the ClassificationNodeInfo
                    ClassificationNodeInfo cni = ClassificationNodeInfo
                            .createClNodeInfo();
                    // Collect information about personnalization of the JTable
                    //cni.setSelectedFormat(NewClPanel.getSelectedFormat());
                    cni
                            .setIndexOfFirstIgnoredRow(NewClPanel
                                    .getMinIgnoredRow());
                    cni.setIndexOfLastIgnoredRow(NewClPanel.getMaxIgnoredRow());
                    cni.setIndexOfFirstIgnoredColumn(NewClPanel
                            .getMinIgnoredColumn());
                    cni.setIndexOfLastIgnoredColumn(NewClPanel
                            .getMaxIgnoredColumn());
                    cni.setIndexOfGeneColumn(NewClPanel.getGeneLabels());
                    cni.setIndexOfTitleRow(NewClPanel.getColumnLabels());
                    cni.setNbColumns(NewClPanel.getNbColumns());

                    cni.setName(clName);
                    cni.setCFile(cdf);
                    cni.setTFile(tdf);
                    cn.setInfo(cni);

                    // Create nodes using cdf and tdf:
                    FileObject foCDF = FileUtil.toFileObject(cdf);
                    FileObject foTDF = FileUtil.toFileObject(tdf);

                    DataObject doCDF = null;
                    DataObject doTDF = null;
                    Node cdfNode = null;
                    Node tdfNode = null;

                    try {
                        doCDF = ClDataObject.find(foCDF);
                        doTDF = ClDataObject.find(foTDF);
                        cdfNode = doCDF.getNodeDelegate();
                        tdfNode = doTDF.getNodeDelegate();
                    } catch (DataObjectNotFoundException exc) {
                        // TODO Auto-generated catch block
                        exc.printStackTrace();
                    }

                    // IF cdfNode and tdfNode are succefully created:
                    if ((cdfNode != null) && (tdfNode != null)) {
                        //cn.getChildren().add(new Node[] { cdfNode, tdfNode
                        // });
                    }

                    // Add the classification node to the ontology node
                    ((ClassificationsRootNode) node).getChildren().add(
                            new Node[] { cn });
                }
            }
        };

        // Use DialogDescriptor to show the panel
        DialogDescriptor descriptor = new DialogDescriptor(panel, bundle
                .getString("NewClassificationDialogTitle"), true, al); //NOI18N
        Object[] closingOptions = { DialogDescriptor.CANCEL_OPTION };
        descriptor.setClosingOptions(closingOptions);
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.show();
    }

    /*
     * (non-Javadoc)
     * @see org.openide.util.actions.NodeAction#enable(org.openide.nodes.Node[])
     */
    protected boolean enable(Node[] nodes) {
        //      Enable this action only for the OntologyNode
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
        return "fr/unice/bioinfo/thea/ontologyexplorer/resources/NewClassificationFileIcon.png"; //NOI18N
    }

    //  Closes dialog
    private void closeDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
}