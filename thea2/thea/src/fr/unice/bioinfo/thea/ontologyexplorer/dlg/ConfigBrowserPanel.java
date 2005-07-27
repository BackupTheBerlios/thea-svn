package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

public class ConfigBrowserPanel extends JPanel {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N

    private JLabel msgLbl;
    private JLabel label;
    private JTextField pathField;
    private JButton browseBtn;

    public ConfigBrowserPanel() {
        initComponents();
    }

    private void initComponents() {

        msgLbl = new JLabel();
        label = new JLabel();
        pathField = new JTextField();
        browseBtn = new JButton();
        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec("max(default;70px)"),//NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;10px)") }, RowSpec //NOI18N
                .decodeSpecs("default, default")));//NOI18N

        //---- msgLbl ----
        msgLbl.setText(bundle.getString("LBL_BrowserMsg"));//NOI18N
        add(msgLbl, cc.xywh(1, 1, 5, 1));

        //---- label ----
        label.setText(bundle.getString("LBL_Path"));//NOI18N
        add(label, cc.xy(1, 2));
        add(pathField, cc.xy(3, 2));

        //---- browseBtn ----
        browseBtn.setBorder(new EtchedBorder());
        browseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performBroserBtnAction(e);
            }
        });
        browseBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif"))); //NOI18N
        add(browseBtn, cc.xy(5, 2));
    }

    private void performBroserBtnAction(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int r = chooser.showOpenDialog(WindowManager.getDefault()
                .getMainWindow());
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        pathField.setText(file.getAbsolutePath());
        OESettings.getInstance().setConfigFilePath(file.getAbsolutePath());
    }
}