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

public class AnnotationSelectionPanel extends JPanel {

    private JComponent geneProductSelectionSeperator;
    private JCheckBox ignoreUnknownBtn;
    private JCheckBox ignoreNotAnnotatedBtn;
    private JCheckBox hideSimilarBtn;
    private JCheckBox showAdjacentsBtn;
    private JCheckBox showOverBtn;
    private JCheckBox showUnderBtn;
    private JComponent clusterSelectionSeparator;
    private JLabel minClusterSizeLbl;
    private JTextField minClusterSizeField;
    private JLabel minAssociatedLbl;
    private JTextField minAssociatedField;
    private JComponent lineSeparator;
    private JButton applyBtn;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); //NOI18N;

    public AnnotationSelectionPanel() {
        init();
    }

    private void init() {

        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        geneProductSelectionSeperator = compFactory.createSeparator(bundle
                .getString("LBL_GeneProductSelectionSeperator"));//NOI18N
        clusterSelectionSeparator = compFactory.createSeparator(bundle
                .getString("LBL_ClusterSelectionSeparator"));//NOI18N
        lineSeparator = compFactory.createSeparator("");//NOI18N

        ignoreUnknownBtn = new JCheckBox();
        ignoreNotAnnotatedBtn = new JCheckBox();
        hideSimilarBtn = new JCheckBox();
        showAdjacentsBtn = new JCheckBox();
        showOverBtn = new JCheckBox();
        showUnderBtn = new JCheckBox();

        minClusterSizeLbl = new JLabel();
        minClusterSizeField = new JTextField();
        minAssociatedLbl = new JLabel();
        minAssociatedField = new JTextField();

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
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));
        add(geneProductSelectionSeperator, cc.xywh(1, 1, 5, 1));

        //---- ignoreUnknownBtn ----
        ignoreUnknownBtn.setText(bundle.getString("TXT_IgnoreUnknownBtn"));//NOI18N
        ignoreUnknownBtn.setToolTipText(bundle
                .getString("TIP_IgnoreUnknownBtn"));//NOI18N
        ignoreUnknownBtn.setSelected(CESettings.getInstance()
                .isIgnoreUnknownSelected());
        ignoreUnknownBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setIgnoreUnknownSelected(
                        ignoreUnknownBtn.isSelected());
            }
        });
        add(ignoreUnknownBtn, cc.xywh(1, 3, 5, 1));

        //---- ignoreNotAnnotatedBtn ----
        ignoreNotAnnotatedBtn.setText(bundle
                .getString("TXT_IgnoreNotAnnotatedBtn"));//NOI18N
        ignoreNotAnnotatedBtn.setToolTipText(bundle
                .getString("TIP_IgnoreNotAnnotatedBtn"));//NOI18N
        ignoreNotAnnotatedBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setIgnoreNotAnnotatedSelected(
                        ignoreNotAnnotatedBtn.isSelected());
            }
        });
        ignoreNotAnnotatedBtn.setSelected(CESettings.getInstance()
                .isIgnoreNotAnnotatedSelected());
        add(ignoreNotAnnotatedBtn, cc.xywh(1, 5, 5, 1));

        //---- hideSimilarBtn ----
        hideSimilarBtn.setText(bundle.getString("TXT_HideSimilarBtn"));//NOI18N
        hideSimilarBtn.setToolTipText(bundle.getString("TIP_HideSimilarBtn"));//NOI18N
        hideSimilarBtn.setSelected(CESettings.getInstance()
                .isHideSimilarAnnotation());
        hideSimilarBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setHideSimilarAnnotation(
                        hideSimilarBtn.isSelected());
            }
        });
        add(hideSimilarBtn, cc.xywh(1, 7, 5, 1));

        //---- showAdjacentsBtn ----
        showAdjacentsBtn.setText(bundle.getString("TXT_ShowAdjacentsBtn"));//NOI18N
        showAdjacentsBtn.setToolTipText(bundle
                .getString("TIP_ShowAdjacentsBtn"));//NOI18N
        showAdjacentsBtn.setSelected(CESettings.getInstance()
                .isShowPhysicallyAdjacent());
        showAdjacentsBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setShowPhysicallyAdjacent(
                        showAdjacentsBtn.isSelected());
            }
        });
        add(showAdjacentsBtn, cc.xywh(1, 9, 5, 1));

        //---- showOverBtn ----
        showOverBtn.setText(bundle.getString("TXT_ShowOverBtn"));//NOI18N
        showOverBtn.setToolTipText(bundle.getString("TIP_ShowOverBtn"));//NOI18N
        showOverBtn.setSelected(CESettings.getInstance()
                .isShowOverRepresented());
        showOverBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setShowOverRepresented(
                        showOverBtn.isSelected());
            }
        });
        add(showOverBtn, cc.xywh(1, 11, 5, 1));

        //---- showUnderBtn ----
        showUnderBtn.setText(bundle.getString("TXT_ShowUnderBtn"));//NOI18N
        showUnderBtn.setToolTipText(bundle.getString("TIP_ShowUnderBtn"));//NOI18N
        showUnderBtn.setSelected(CESettings.getInstance()
                .isShowUnderRepresented());
        showUnderBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CESettings.getInstance().setShowUnderRepresented(
                        showUnderBtn.isSelected());
            }
        });
        add(showUnderBtn, cc.xywh(1, 13, 5, 1));
        add(clusterSelectionSeparator, cc.xywh(1, 15, 5, 1));

        //---- minClusterSizeLbl ----
        minClusterSizeLbl.setText(bundle.getString("TXT_MinClusterSizeLbl"));//NOI18N
        minClusterSizeLbl.setToolTipText(bundle
                .getString("TIP_MinClusterSizeLbl"));//NOI18N
        add(minClusterSizeLbl, cc.xywh(1, 17, 3, 1));
        minClusterSizeField.setText(""
                + CESettings.getInstance().getMinClusterSize());//NOI18N
        add(minClusterSizeField, cc.xy(5, 17));

        //---- minAssociatedLbl ----
        minAssociatedLbl.setText(bundle.getString("TXT_MinAssociatedLbl"));//NOI18N
        minAssociatedLbl.setToolTipText(bundle
                .getString("TIP_MinAssociatedLbl"));//NOI18N
        add(minAssociatedLbl, cc.xywh(1, 19, 3, 1));

        minAssociatedField.setText(""
                + CESettings.getInstance().getMinAssociatedGenes());//NOI18N
        add(minAssociatedField, cc.xy(5, 19));
        add(lineSeparator, cc.xywh(1, 21, 5, 1));

        //---- applyBtn ----
        applyBtn.setText(bundle.getString("TXT_ApplyBtn"));//NOI18N
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setMinClusterSize(
                        Integer.parseInt(minClusterSizeField.getText()));
                CESettings.getInstance().setMinAssociatedGenes(
                        Integer.parseInt(minAssociatedField.getText()));
            }
        });
        add(applyBtn, cc.xy(5, 23));
    }
}