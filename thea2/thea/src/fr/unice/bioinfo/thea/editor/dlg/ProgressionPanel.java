package fr.unice.bioinfo.thea.editor.dlg;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ProgressionPanel extends JPanel {

    private JLabel statusLbl;
    private JProgressBar statusProgBar;
    private TitledBorder border;

    public ProgressionPanel() {
        initComponents();
    }

    private void initComponents() {
        statusLbl = new JLabel();
        statusProgBar = new JProgressBar();
        border = new TitledBorder("Progression");//NOI18N
        CellConstraints cc = new CellConstraints();

        setBorder(border);

        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec("max(default;50dlu):grow"),//NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;300px):grow") }, RowSpec//NOI18N
                .decodeSpecs("fill:default")));//NOI18N

        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statusLbl.setText("Status");//NOI18N
        add(statusLbl, cc.xywh(1, 1, 1, 1, CellConstraints.DEFAULT,
                CellConstraints.FILL));
        add(statusProgBar, cc.xywh(3, 1, 1, 1, CellConstraints.DEFAULT,
                CellConstraints.FILL));
    }

    /** Updates the title of the line border */
    public void setBorderText(String text) {
        border.setTitle(text);
    }

    /** Updates the text of the status label */
    public void setStatusText(String text) {
        statusLbl.setText(text);
    }

}