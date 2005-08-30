package fr.unice.bioinfo.thea.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

public class OntologyOptionsPanel extends JPanel {

    private JComponent ontologySeparator;

    private JCheckBox showIDBtn;

    private JCheckBox showNameBtn;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); // NOI18N

    public OntologyOptionsPanel() {
        init();
    }

    private void init() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        ontologySeparator = compFactory.createSeparator(bundle
                .getString("LBL_OntologySeparator"));
        showIDBtn = new JCheckBox();
        showNameBtn = new JCheckBox();
        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC }));
        add(ontologySeparator, cc.xywh(1, 1, 5, 1));

        // ---- showIDBtn ----
        showIDBtn.setText(bundle.getString("TXT_ShowIDBtn"));
        showIDBtn.setToolTipText(bundle.getString("TIP_ShowIDBtn"));
        showIDBtn.setSelected(CESettings.getInstance().isShowTermID());
        showIDBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setShowTermID(showIDBtn.isSelected());
            }
        });
        add(showIDBtn, cc.xywh(1, 3, 5, 1));

        // ---- showNameBtn ----
        showNameBtn.setText(bundle.getString("TXT_ShowNameBtn"));
        showNameBtn.setToolTipText(bundle.getString("TIP_ShowNameBtn"));
        showNameBtn.setSelected(CESettings.getInstance().isShowTermName());
        showNameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setShowTermName(
                        showNameBtn.isSelected());
            }
        });
        add(showNameBtn, cc.xywh(1, 5, 5, 1));
    }

    public static void main(String[] arg) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new OntologyOptionsPanel());
        frame.pack();
        frame.setVisible(true);
    }
}