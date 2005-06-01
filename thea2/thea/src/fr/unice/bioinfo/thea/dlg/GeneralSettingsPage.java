package fr.unice.bioinfo.thea.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.settings.OESettings;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneralSettingsPage extends AbstractSettingsPage {

    private JComponent configurationSeparator;
    private JLabel cfgFileLbl;
    private JTextField cfgFileField;
    private JButton browseBtn;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); //NOI18N;

    /**
     * @param container
     */
    public GeneralSettingsPage(PreferencesContainer container) {
        super(container);
        init();
    }

    private void init() {
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        configurationSeparator = compFactory.createSeparator(bundle
                .getString("LBL_ConfigurationSeparator"));

        CellConstraints cc = new CellConstraints();

        //======== this ========
        setBorder(Borders.DIALOG_BORDER);
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec("max(min;70px)"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;10px)") }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC }));
        add(configurationSeparator, cc.xywh(1, 1, 5, 1));

        //---- cfgFileLbl ----
        cfgFileLbl = new JLabel();
        cfgFileLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        cfgFileLbl.setText(bundle.getString("LBL_ConfigurationFile"));
        add(cfgFileLbl, cc.xy(1, 3));

        cfgFileField = new JTextField();
        cfgFileField.setText(OESettings.getInstance().getConfigFilePath());
        add(cfgFileField, cc.xy(3, 3));

        //---- browseBtn ----
        browseBtn = new JButton();
        browseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performBroserBtnAction(e);
            }
        });
        browseBtn.setBorder(new EtchedBorder());
        browseBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif")));
        add(browseBtn, cc.xy(5, 3));
    }

    private void performBroserBtnAction(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int r = chooser.showOpenDialog(WindowManager.getDefault()
                .getMainWindow());
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        cfgFileField.setText(file.getAbsolutePath());
        OESettings.getInstance().setConfigFilePath(file.getAbsolutePath());
    }
}