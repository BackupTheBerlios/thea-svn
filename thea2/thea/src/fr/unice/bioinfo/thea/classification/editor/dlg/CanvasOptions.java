package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import fr.unice.bioinfo.thea.classification.editor.settings.CESettings;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class CanvasOptions extends JPanel {
    // separators
    private JComponent hmodeSeparator;
    private JComponent layoutSeparator;
    private JComponent othersSeparator;

    // highlighting mode variables
    private ButtonGroup group;
    private JRadioButton borderFrameBtn;
    private JRadioButton rootNodeBtn;
    private JRadioButton plainFrameBtn;
    private JRadioButton noHighBtn;

    private JCheckBox showBLCbx;
    private JCheckBox alignCbx;
    private JCheckBox terminalBoxingCbx;
    private JCheckBox showXPCbx;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.dlg.Bundle"); //NOI18N

    public CanvasOptions() {
        initComponents();
    }

    private void initComponents() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();

        hmodeSeparator = compFactory.createSeparator(bundle
                .getString("LBL_HModeSeparator"));//NOI18N
        othersSeparator = compFactory.createSeparator(bundle
                .getString("LBL_OthersSeparator"));//NOI18N
        layoutSeparator = compFactory.createSeparator(bundle
                .getString("LBL_LayoutSeparator"));//NOI18N

        borderFrameBtn = new JRadioButton();
        rootNodeBtn = new JRadioButton();
        plainFrameBtn = new JRadioButton();
        noHighBtn = new JRadioButton();
        group = new ButtonGroup();
        group.add(borderFrameBtn);
        group.add(rootNodeBtn);
        group.add(plainFrameBtn);
        group.add(noHighBtn);
        int hmode = CESettings.getInstance().getHighlightingMode();
        switch (hmode) {
        case 1: {
            plainFrameBtn.setSelected(true);
            break;
        }
        case 2: {
            borderFrameBtn.setSelected(true);
            break;
        }
        case 3: {
            rootNodeBtn.setSelected(true);
            break;
        }
        default: {
            noHighBtn.setSelected(true);
            break;
        }
        }

        showBLCbx = new JCheckBox();
        showBLCbx.setSelected(CESettings.getInstance().isShowBranchLength());

        alignCbx = new JCheckBox();
        alignCbx.setSelected(CESettings.getInstance().isAlignTerminalNodes());

        terminalBoxingCbx = new JCheckBox();
        terminalBoxingCbx.setSelected(CESettings.getInstance()
                .isTerminalsBoxed());

        showXPCbx = new JCheckBox();
        showXPCbx
                .setSelected(CESettings.getInstance().isShowExpressionValues());

        CellConstraints cc = new CellConstraints();

        // border and layout
        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(new ColumnSpec[] { new ColumnSpec("200px"),//NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("200px") }, new RowSpec[] {//NOI18N
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
        add(hmodeSeparator, cc.xywh(1, 1, 3, 1));

        //---- borderFrameBtn ----
        borderFrameBtn.setText(bundle.getString("LBL_BorderFrame"));//NOI18N
        borderFrameBtn.setToolTipText(bundle.getString("LBL_BorderFrame"));//NOI18N
        borderFrameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setHighlightingMode(2);
            }
        });
        add(borderFrameBtn, cc.xywh(1, 3, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- rootNodeBtn ----
        rootNodeBtn.setText(bundle.getString("LBL_RootNode"));//NOI18N
        rootNodeBtn.setToolTipText(bundle.getString("HINT_RootNode"));//NOI18N
        rootNodeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setHighlightingMode(3);
            }
        });
        add(rootNodeBtn, cc.xywh(3, 3, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- plainFrameBtn ----
        plainFrameBtn.setText(bundle.getString("LBL_PlainFrame"));//NOI18N
        plainFrameBtn.setToolTipText(bundle.getString("HINT_PlainFrame"));//NOI18N
        plainFrameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setHighlightingMode(1);
            }
        });
        add(plainFrameBtn, cc.xywh(1, 5, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- noHighBtn ----
        noHighBtn.setText(bundle.getString("LBL_NoHigh"));//NOI18N
        noHighBtn.setToolTipText(bundle.getString("HINT_NoHigh"));//NOI18N
        noHighBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setHighlightingMode(4);
            }
        });
        add(noHighBtn, cc.xywh(3, 5, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));
        add(layoutSeparator, cc.xywh(1, 7, 3, 1));

        //---- showBLCbx ----
        showBLCbx.setText(bundle.getString("LBL_ShowBL"));//NOI18N
        showBLCbx.setToolTipText(bundle.getString("HINT_ShowBL"));//NOI18N
        showBLCbx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setShowBranchLength(
                        showBLCbx.isSelected());
            }
        });
        add(showBLCbx, cc.xywh(1, 9, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- alignCbx ----
        alignCbx.setText(bundle.getString("LBL_AlignTerminalNodes"));//NOI18N
        alignCbx.setToolTipText(bundle.getString("HINT_AlignTerminalNodes"));//NOI18N
        alignCbx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setAlignTerminalNodes(
                        alignCbx.isSelected());
            }
        });
        add(alignCbx, cc.xywh(3, 9, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- terminalBoxingCbx ----
        terminalBoxingCbx.setText(bundle.getString("LBL_TerminalBoxed"));//NOI18N
        terminalBoxingCbx
                .setToolTipText(bundle.getString("HINT_TerminalBoxed"));//NOI18N
        terminalBoxingCbx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setTerminalsBoxed(
                        terminalBoxingCbx.isSelected());
            }
        });
        add(terminalBoxingCbx, cc.xywh(1, 11, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));
        add(othersSeparator, cc.xywh(1, 13, 3, 1));

        //---- showXPCbx ----
        showXPCbx.setText(bundle.getString("LBL_ShowXPV"));//NOI18N
        showXPCbx.setToolTipText(bundle.getString("HINT_ShowXPV"));//NOI18N
        showXPCbx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setShowExpressionValues(
                        showXPCbx.isSelected());
            }
        });
        add(showXPCbx, cc.xywh(1, 15, 1, 1, CellConstraints.FILL,
                CellConstraints.FILL));
    }
}