package fr.unice.bioinfo.thea.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

public class MessagePopup extends JDialog {

    private JLabel msgLbl;
    private JLabel statusLbl;
    private JProgressBar bar;
    private boolean beep = false;

    /**
     * Creates new form MessageDialog
     * @param parent The parent component
     * @param title DOCUMENT ME!
     * @param message The message that will be showed
     * @param beep If true, beeps when clicked on
     */
    public MessagePopup(java.awt.Component parent, String title,
            String message, boolean beep) {
        super(JOptionPane.getFrameForComponent(parent), false);

        this.beep = beep;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle(title);

        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setBorder(Borders.DIALOG_BORDER);
        p.setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec("max(min;75px)"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW) }, new RowSpec[] {
                new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));
        p.setPreferredSize(new Dimension(300, 100));

        //---- msgLbl ----
        msgLbl = new JLabel();
        msgLbl.setText(message);
        msgLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (MessagePopup.this.beep) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        p.add(msgLbl, cc.xywh(1, 1, 3, 1));

        //---- statusLbl ----
        statusLbl = new JLabel();
        statusLbl.setText("Running:");
        p.add(statusLbl, cc.xy(1, 3));

        //---- bar ----
        bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        bar.setForeground(Color.blue);

        p.add(bar, cc.xy(3, 3));

        contentPane.add(p, BorderLayout.CENTER);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        pack();
        setLocationRelativeTo(JOptionPane.getFrameForComponent(parent));
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}