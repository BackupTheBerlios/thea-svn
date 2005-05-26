package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

public class OntologiesListPanel extends JPanel {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N

    private JLabel label;
    private JComboBox comboBox;
    private Node[] ontologies;

    public OntologiesListPanel(Node[] ontologies) {
        this.ontologies = ontologies;
        init();
    }

    private void init() {
        // create the label
        label = new JLabel();
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
            comboBox.setSelectedIndex(0);
        }
        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);

        setLayout(new FormLayout(ColumnSpec.decodeSpecs("pref:grow"),//NOI18N
                new RowSpec[] {
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT,
                                FormSpec.NO_GROW),
                        FormFactory.PARAGRAPH_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));

        //---- label ----
        label.setText(bundle.getString("LBL_OntologiesList"));//NOI18N
        add(label, cc.xywh(1, 1, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));
        add(comboBox, cc.xywh(1, 3, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));
    }
    /**
     * Return the selected ontology.
     * @return A Node that represents an ontology.
     */
    public Node getSelectedOntologyNode() {
        int index = comboBox.getSelectedIndex();
        return ontologies[index];
    }

}