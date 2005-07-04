package fr.unice.bioinfo.thea.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

import fr.unice.bioinfo.thea.classification.settings.CESettings;

public class ColocalizationOptionsPanel extends JPanel {

    private JComponent nbrSelectionSeparator;
    private JLabel maxDistanceLbl;
    private JTextField maxDistanceField;
    private JLabel maxClusterSizeLbl;
    private JTextField maxClusterSizeField;
    private JLabel pvalueLbl;
    private JTextField pvalueField;
    private JComponent groupsSeparator;
    private JCheckBox separateGroupsBtn;
    private JComponent lineSeparator;
    private JButton applyBtn;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); //NOI18N;

    public ColocalizationOptionsPanel() {
        init();
    }

    private void init() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        nbrSelectionSeparator = compFactory.createSeparator(bundle
                .getString("LBL_NbrSelectionSeparator"));//NOI18N
        groupsSeparator = compFactory.createSeparator(bundle
                .getString("LBL_GroupsSeparator"));//NOI18N
        separateGroupsBtn = new JCheckBox();
        lineSeparator = compFactory.createSeparator("");//NOI18N
        maxDistanceLbl = new JLabel();
        maxDistanceField = new JTextField();
        maxClusterSizeLbl = new JLabel();
        maxClusterSizeField = new JTextField();
        pvalueLbl = new JLabel();
        pvalueField = new JTextField();
        applyBtn = new JButton();
        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(min;50px)") }, new RowSpec[] {//NOI18N
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));
        add(nbrSelectionSeparator, cc.xywh(1, 1, 5, 1));

        //---- maxDistanceLbl ----
        maxDistanceLbl.setText(bundle.getString("TXT_MaxDistanceLbl"));//NOI18N
        maxDistanceLbl.setToolTipText(bundle.getString("TIP_MaxDistanceLbl"));//NOI18N
        add(maxDistanceLbl, cc.xywh(1, 3, 3, 1));
        maxDistanceField.setText(""
                + CESettings.getInstance().getNbrMaxDistance());
        add(maxDistanceField, cc.xy(5, 3));

        //---- maxClusterSizeLbl ----
        maxClusterSizeLbl.setText(bundle.getString("TXT_maxClusterSizeLbl"));//NOI18N
        maxClusterSizeLbl.setToolTipText(bundle
                .getString("TIP_maxClusterSizeLbl"));//NOI18N
        add(maxClusterSizeLbl, cc.xywh(1, 5, 4, 1));
        maxClusterSizeField.setText(""
                + CESettings.getInstance().getNbrMaxClusterSize());
        add(maxClusterSizeField, cc.xy(5, 5));

        //---- pvalueLbl ----
        pvalueLbl.setText(bundle.getString("TXT_PvalueLbl"));//NOI18N
        pvalueLbl.setToolTipText(bundle.getString("TIP_PvalueLbl"));//NOI18N
        add(pvalueLbl, cc.xywh(1, 7, 3, 1));
        pvalueField.setText("" + CESettings.getInstance().getNbrPValue());
        add(pvalueField, cc.xy(5, 7));
        add(groupsSeparator, cc.xywh(1, 9, 5, 1));

        //---- separateGroupsBtn ----
        separateGroupsBtn.setText(bundle.getString("TXT_SeparateGroupsBtn"));//NOI18N
        separateGroupsBtn.setToolTipText(bundle
                .getString("TIP_SeparateGroupsBtn"));//NOI18N
        separateGroupsBtn.setSelected(CESettings.getInstance()
                .isNbrSeparateStrandSelected());
        separateGroupsBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setNbrSeparateStrandSelected(
                        separateGroupsBtn.isSelected());
            }
        });
        add(separateGroupsBtn, cc.xywh(1, 11, 5, 1));
        add(lineSeparator, cc.xywh(1, 13, 5, 1));

        //---- applyBtn ----
        applyBtn.setText(bundle.getString("TXT_ApplyBtn"));//NOI18N
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setNbrMaxDistance(
                        Integer.parseInt(maxDistanceField.getText()));
                CESettings.getInstance().setNbrMaxClusterSize(
                        Integer.parseInt(maxClusterSizeField.getText()));
                CESettings.getInstance().setNbrPValue(
                        Double.parseDouble(pvalueField.getText()));
            }
        });
        add(applyBtn, cc.xy(5, 15));
    }
}