package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;

public class OntologiesListPanel extends JPanel {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N

    private JComponent ontologiesSeparator;
    private JLabel selectLabel;
    private JComboBox comboBox;
    private JComponent linkingSeparator;
    private JLabel statusLabel;
    private JProgressBar pb;

    private Node[] ontologies = null;
    private ClassificationNodeInfo cni;

    public OntologiesListPanel(Node[] ontologies, ClassificationNodeInfo cni) {
        this.ontologies = ontologies;
        this.cni = cni;
        init();
        //      Add a linking listener
        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (event.getPropertyName().equals("linking")) { //NOI18N
                            startProgress();
                        }
                        if (event.getPropertyName().equals("linked")) { //NOI18N
                            stopProgress(true);
                        }
                        if (event.getPropertyName().equals("failed")) { //NOI18N
                            stopProgress(false);
                        }
                    }
                });
            }
        };
        //cni.getClassification().addPropertyChangeListener(pcl);
    }

    private void init() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        ontologiesSeparator = compFactory.createSeparator(bundle
                .getString("LBL_OntologiesList"));//NOI18N);
        linkingSeparator = compFactory.createSeparator(bundle
                .getString("LBL_Linking"));//NOI18N
        selectLabel = new JLabel();
        // create the combo box
        // if no ontology, the combo box is empty
        if (this.ontologies == null) {
            comboBox = new JComboBox();
        } else {
            String[] s = new String[this.ontologies.length];
            for (int cnt = 0; cnt < s.length; cnt++) {
                s[cnt] = ontologies[cnt].getDisplayName();
            }
            comboBox = new JComboBox(s);
            //comboBox.setSelectedIndex(0);
        }

        statusLabel = new JLabel();
        pb = new JProgressBar();
        CellConstraints cc = new CellConstraints();
        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(ColumnSpec
                .decodeSpecs("max(default;100px), pref:grow"), new RowSpec[] {
                new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));
        add(ontologiesSeparator, cc.xywh(1, 1, 2, 1));

        //---- selectLabel ----
        selectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        selectLabel.setText(bundle.getString("LBL_SelectLabel"));
        add(selectLabel, cc.xy(1, 3));
        add(comboBox, cc.xywh(2, 3, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));
        add(linkingSeparator, cc.xywh(1, 5, 2, 1));

        //---- statusLabel ----
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setText(bundle.getString("LBL_StatusLabel"));
        add(statusLabel, cc.xy(1, 7));
        add(pb, cc.xy(2, 7));
    }

    /**
     * Return the selected ontology.
     * @return A Node that represents an ontology.
     */
    public Node getSelectedOntologyNode() {
        int index = comboBox.getSelectedIndex();
        return ontologies[index];
    }

    private void startProgress() {
        pb.setBorderPainted(true);
        pb.setIndeterminate(true);
        pb.setString(bundle.getString("LinkingProgress_Connecting")); //NOI18N
    }

    private void stopProgress(boolean connected) {
        pb.setIndeterminate(false);
        if (connected) {
            pb.setValue(pb.getMaximum());
            pb.setString(bundle.getString("LinkingProgress_Established")); //NOI18N
        } else {
            pb.setValue(pb.getMinimum());
            pb.setString(bundle.getString("LinkingProgress_Failed")); //NOI18N
        }
    }
}