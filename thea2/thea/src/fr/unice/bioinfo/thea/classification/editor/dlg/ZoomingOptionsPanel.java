package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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

import fr.unice.bioinfo.thea.classification.editor.settings.CESettings;

public class ZoomingOptionsPanel extends JPanel {

    private JPanel btnPanel;
    private JButton applyBtn;
    private JPanel widgetsPanel;
    private JComponent factorsSeparator;
    private JLabel xFactorLbl;
    private JTextField xFactorField;
    private JLabel yFactorLbl;
    private JTextField yFactorField;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.dlg.Bundle"); //NOI18N

    public ZoomingOptionsPanel() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();

        factorsSeparator = compFactory.createSeparator(bundle
                .getString("LBL_ZoomingSeparator"));

        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);

        //======== btnPanel ========
        {
            applyBtn = new JButton();
            applyBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    double xFactor = Double.parseDouble(xFactorField.getText());
                    double yFactor = Double.parseDouble(yFactorField.getText());
                    CESettings.getInstance().setZoomFactorX(xFactor);
                    CESettings.getInstance().setZoomFactorY(yFactor);
                }
            });
            btnPanel = new JPanel();
            btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            //---- applyBtn ----
            applyBtn.setText(bundle.getString("LBL_ApplyBtn"));
            btnPanel.add(applyBtn);
        }
        add(btnPanel, BorderLayout.SOUTH);

        //======== widgetsPanel ========
        {
            widgetsPanel = new JPanel();
            widgetsPanel.setLayout(new FormLayout(new ColumnSpec[] {
                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                            FormSpec.DEFAULT_GROW),
                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                    new ColumnSpec("max(min;50px)") }, new RowSpec[] {
                    FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                    FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                    FormFactory.DEFAULT_ROWSPEC }));
            widgetsPanel.add(factorsSeparator, cc.xywh(1, 1, 3, 1));

            //---- xFactorLbl ----
            xFactorLbl = new JLabel();
            xFactorLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            xFactorLbl.setText(bundle.getString("LBL_HorizontalZoomingFactor"));
            widgetsPanel.add(xFactorLbl, cc.xy(1, 3));

            xFactorField = new JTextField();
            xFactorField
                    .setText("" + CESettings.getInstance().getZoomFactorX());
            widgetsPanel.add(xFactorField, cc.xy(3, 3));

            //---- yFactorLbl ----
            yFactorLbl = new JLabel();
            yFactorLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            yFactorLbl.setText(bundle.getString("LBL_VerticalZoomingFactor"));
            widgetsPanel.add(yFactorLbl, cc.xy(1, 5));
            yFactorField = new JTextField();
            yFactorField
                    .setText("" + CESettings.getInstance().getZoomFactorY());
            widgetsPanel.add(yFactorField, cc.xy(3, 5));
        }
        add(widgetsPanel, BorderLayout.CENTER);
    }

}