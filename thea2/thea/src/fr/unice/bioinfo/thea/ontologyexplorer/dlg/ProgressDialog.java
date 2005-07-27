package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ProgressDialog extends JDialog implements PropertyChangeListener {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N

    private JLabel operationLbl;
    private JLabel textLbl;
    private JLabel statusLbl;
    private JProgressBar progressBar;

    /**
     * Creates a new ProgressDialog with the given frame as a parent.
     * @param parent parent the parent of this dialog. Typically this would be
     *        <code>WindowManager.getDefault().getMainWindow()</code>.
     */
    private ProgressDialog(Frame owner) {
        super(owner, false);
        init();
        setLocationRelativeTo(owner);
    }

    /**
     * Creates a new ProgressDialog with the given frame as a parent.
     * @param parent parent the parent of this dialog. Typically this would be
     *        another Dialog.
     */
    public ProgressDialog(Dialog owner) {
        super(owner);
        init();
    }

    public static ProgressDialog getProgressDialog() {
        return new ProgressDialog((Frame) WindowManager.getDefault()
                .getMainWindow());
    }

    private void init() {

        operationLbl = new JLabel();
        textLbl = new JLabel();
        statusLbl = new JLabel();
        progressBar = new JProgressBar();
        CellConstraints cc = new CellConstraints();

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setModal(false);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;300px):grow") }, new RowSpec[] {//NOI18N
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));

        //---- operationLbl ----
        operationLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        operationLbl.setText(bundle.getString("LBL_Operation"));//NOI18N
        contentPane.add(operationLbl, cc.xy(1, 1));

        //---- textLbl ----
        textLbl.setText(bundle.getString("LBL_Initializing"));//NOI18N
        contentPane.add(textLbl, cc.xy(3, 1));

        //---- statusLbl ----
        statusLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLbl.setText(bundle.getString("LBL_Status"));//NOI18N
        contentPane.add(statusLbl, cc.xy(1, 5));

        //---- progressBar ----
        progressBar.setIndeterminate(true);
        progressBar.setToolTipText(bundle.getString("LBL_ProgressBar"));//NO18N
        contentPane.add(progressBar, cc.xy(3, 5));

    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("initializing")) {
            textLbl.setText(bundle.getString("LBL_Initializing"));//NOI18N
        }
        if (e.getPropertyName().equals("processing")) {
            textLbl.setText(bundle.getString("LBL_Processing"));//NOI18N
        }
        if (e.getPropertyName().equals("endprocessing")) {
            textLbl.setText(bundle.getString("LBL_EndProcessing"));//NOI18N
            // close this dialog:
            close();
        }
    }

    /** Close this dialog. */
    public void close() {
        setVisible(false);
        dispose();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        ProgressDialog d = ProgressDialog.getProgressDialog();
        f.pack();
        f.show();
    }
}