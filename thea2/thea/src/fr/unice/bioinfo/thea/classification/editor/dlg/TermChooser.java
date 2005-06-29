package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.NodeLayoutSupport;
import fr.unice.bioinfo.thea.classification.Score;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;
import fr.unice.bioinfo.thea.classification.settings.CESettings;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * A Swing GUI that allows users to choose an annotation from the list of all
 * available annotation for a given cluster in the classification. <br>
 * The list of available annotations is shown on two modes: as a table and in a
 * tree mode. <br>
 * This class uses two other classes to to build those two modes.
 * After the user select an annotation, he can make it used as a prefereed
 * annotation using a <i>Select </i> button.
 * @see {@link TermChooserTreeView}
 * @see {@link TermChooserTableView}
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class TermChooser extends JPanel {

    private JTabbedPane tp;
    private TermChooserTreeView treeViewPanel;
    private TermChooserTableView tableViewPanel;

    private List scores;
    private Resource selectedTerm;
    private List ln;/* leave nodes */
    private String nodeName;
    private Node aNode;
    private String ontologyBranchName;

    private DrawableClassification drawable;

    private JButton selectBtn;
    private JButton exportBtn;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.dlg.Bundle"); //NOI18N

    public TermChooser(Node aNode, String ontologyBranchName,
            DrawableClassification drawable) {
        this.aNode = aNode;
        this.scores = (List) aNode.getProperty(Node.TERM_AND_SCORE);
        this.selectedTerm = (Resource) aNode.getProperty(Node.ASSOC_TERM);
        this.ontologyBranchName = ontologyBranchName;
        this.drawable = drawable;
        ln = (List) aNode.getLeaves();
        nodeName = aNode.getName();
        init();
    }

    private void init() {
        tp = new JTabbedPane();
        treeViewPanel = new TermChooserTreeView(null);
        tableViewPanel = new TermChooserTableView(scores);
        selectBtn = new JButton();
        exportBtn = new JButton();
        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(ColumnSpec
                .decodeSpecs("default:grow, default, default, default:grow"),//NOI18N
                new RowSpec[] {
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                                FormSpec.DEFAULT_GROW),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));

        //---- tp ----
        tp.setTabPlacement(JTabbedPane.TOP);
        ImageIcon tableViewIcon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/TableViewIcon.gif"));
        ImageIcon treeViewIcon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/TreeViewIcon.gif"));
        tp.addTab(bundle.getString("LBL_TermChooserTreeView"), treeViewIcon,
                treeViewPanel);//NOI18N
        tp.addTab(bundle.getString("LBL_TermChooserTableView"), tableViewIcon,
                tableViewPanel);//NOI18N
        add(tp, cc.xywh(1, 1, 4, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- selectBtn ----
        selectBtn.setText(bundle.getString("TXT_SelectBtn"));//NOI18N
        selectBtn.setToolTipText(bundle.getString("TIP_SelectBtn"));//NOI18N
        selectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSelectBtnAction(e);
            }
        });
        add(selectBtn, cc.xy(2, 3));

        //---- exportBtn ----
        exportBtn.setText(bundle.getString("TXT_ExportBtn"));//NOI18N
        exportBtn.setToolTipText(bundle.getString("TIP_ExportBtn"));//NOI18N
        add(exportBtn, cc.xy(3, 3));
    }

    private void performSelectBtnAction(ActionEvent e) {
        Resource selectedTerm = tableViewPanel.getSelectedTerm();
        setPreferredAssociatedTerm(aNode, selectedTerm, true);
        drawable.updateGraphics();
    }

    private void setPreferredAssociatedTerm(Node aNode, Resource selectedTerm,
            boolean updateLabel) {
        if (aNode.isLeaf()) {
            return;
        }
        List scores = (List) (aNode.getProperty(Node.TERM_AND_SCORE));
        if ((scores != null) && (scores.size() > 1)) {
            Iterator scoresIt = scores.iterator();
            while (scoresIt.hasNext()) {
                final Score score = (Score) scoresIt.next();
                ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                        .getResourceFactory();
                Set set = createWholeBranchTermsList(resourceFactory, score
                        .getTerm());
                set.add(score.getTerm());
                if (set.contains(score.getTerm())) {
                    String label = "";
                    if (CESettings.getInstance().isShowTermID()) {
                        label += score.getTerm().getId();
                    }

                    if (CESettings.getInstance().isShowTermName()) {
                        if (!"".equals(label)) {
                            label += ":";
                        }
                        //label += (termAndScore.getTerm().getTerm() + " ");
                        StringValue sv = (StringValue) score
                                .getTerm()
                                .getTarget(
                                        resourceFactory
                                                .getProperty(OWLProperties
                                                        .getInstance()
                                                        .getNodeNameProperty()));
                        if (sv != null) {
                            label += (sv.getValue() + " ");
                        }
                    }
                    label += "(+)";
                    if (updateLabel) {
                        aNode.setLabel(label);
                    }
                    aNode.addProperty(Node.ASSOC_TERM, score.getTerm());

                    if (score.isOverexpressed()) {
                        aNode.setLayoutSupport(new NodeLayoutSupport(null,
                                null, new Color(255, 200, 200),
                                NodeLayoutSupport.RECTANGLE, null));
                    } else {
                        aNode.setLayoutSupport(new NodeLayoutSupport(null,
                                null, new Color(200, 255, 200),
                                NodeLayoutSupport.RECTANGLE, null));
                    }
                    if (selectedTerm == score.getTerm()) {
                        break;
                    }
                }
            }
        }
        Iterator childrenIt = aNode.getChildren().iterator();
        while (childrenIt.hasNext()) {
            Node childNode = (Node) childrenIt.next();
            setPreferredAssociatedTerm(childNode, selectedTerm, false);
        }
    }

    /**
     * Retreives and returns resource under the branch which the parent node
     * correspends to the given resource, <i>aResource </i>.
     * @param resourceFactory A ResourceFactory.
     * @param aResource The resource represented by the root node of the
     *        selected branch.
     * @return Children of the term correspending to the resource <i>aResource
     *         </i>.
     */
    private Set createWholeBranchTermsList(ResourceFactory resourceFactory,
            Resource aResource) {
        Set descendants = new HashSet();
        Set targets = aResource.getTargets(OWLProperties.getInstance()
                .getHierarchyProperties());
        if (targets != null) {
            Iterator targetsIt = targets.iterator();
            while (targetsIt.hasNext()) {
                Resource target = (Resource) targetsIt.next();
                descendants.addAll(createWholeBranchTermsList(resourceFactory,
                        target));
            }
            descendants.addAll(targets);
        }
        return descendants;
    }

}