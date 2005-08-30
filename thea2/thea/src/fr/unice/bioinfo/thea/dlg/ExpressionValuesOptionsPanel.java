package fr.unice.bioinfo.thea.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

public class ExpressionValuesOptionsPanel extends JPanel {

    private JComponent expressionValuesSeparator;

    private JLabel slotsLbl;

    private JTextField slotsField;

    private JComponent lineSeparator;

    private JButton applyBtn;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); // NOI18N;

    public ExpressionValuesOptionsPanel() {
        init();
    }

    private void init() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        expressionValuesSeparator = compFactory.createSeparator(bundle
                .getString("LBL_ExpressionValuesSeparator"));// NOI18N
        lineSeparator = compFactory.createSeparator("");

        slotsLbl = new JLabel();
        slotsField = new JTextField();
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
                new ColumnSpec("max(min;50px)") }, new RowSpec[] {// NOI18N
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));
        add(expressionValuesSeparator, cc.xywh(1, 1, 5, 1));

        // ---- slotsLbl ----
        slotsLbl.setText(bundle.getString("TXT_SlotsLbl"));// NOI18N
        slotsLbl.setToolTipText(bundle.getString("TIP_SlotsLbl"));// NOI18N
        add(slotsLbl, cc.xywh(1, 3, 3, 1));

        slotsField.setText("" + CESettings.getInstance().getSlots());
        add(slotsField, cc.xy(5, 3));
        add(lineSeparator, cc.xywh(1, 5, 5, 1));

        // ---- applyBtn ----
        applyBtn.setText(bundle.getString("TXT_ApplyBtn"));// NOI18N
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setSlots(
                        Integer.parseInt(slotsField.getText()));
            }
        });
        add(applyBtn, cc.xy(5, 7));
    }
}