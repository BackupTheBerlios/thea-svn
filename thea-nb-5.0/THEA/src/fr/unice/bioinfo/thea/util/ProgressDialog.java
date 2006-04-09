/*
 * LOG:
 * ====
 * 1- Making this dialog modal, ie: this.setModal(true); blocks the
 *    calling thread. In fact, this dialog is mainly ti be used
 *    wthin the main netbeans IDE Frame as parent.If we call the
 *    super constructor in this way: super(parent,true); the window
 *    that makes the progress dialog visible is blocked.
 */
package fr.unice.bioinfo.thea.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * Non-modal dialog box displaying the progress of a given operation and
 * allowing the user to cancel it.
 * 
 * @author SAÏD, EL KASMI.
 */
public class ProgressDialog extends JDialog {
    /**
     * A default ProgressDialog that uses
     * <code>WindowManager.getDefault().getMainWindow()</code> as a parent
     * frame.
     */
    private static ProgressDialog DEFAULT = null;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.core.util.Bundle"); // NOI18N;

    /** The JProgressBar used in this progression indicator dialog. */
    private JProgressBar progressBar;

    /** A button that allows to cancel the running operation. */
    private JButton cancelButton;

    /** A place where to print the current state of the running operation */
    private JLabel textLabel;

    /** A place where to print the state of the running operation */
    private JLabel percentLabel;

    /** Tracks whether or not the runing operation has been cancelled. */
    private boolean isCancelled = false;

    /**
     * Creates a new ProgressDialog with the given frame as a parent.
     * 
     * @param parent
     *            parent the parent of this dialog. Typically this would be
     *            <code>WindowManager.getDefault().getMainWindow()</code>.
     */
    public ProgressDialog(Frame parent) {
        super(parent, false);
        init();
        setLocationRelativeTo(parent);
    }

    /**
     * This method is called from within the constructor to initialize the
     * dialog.
     */
    private void init() {
        GridBagLayout gbl = new GridBagLayout();

        // Set a layout
        getContentPane().setLayout(gbl);

        // give it a title
        setTitle(bundle.getString("LBL_ProgressionDialog_Title"));

        // Fix a dimension
        setSize(new Dimension(400, 140));

        GridBagConstraints constraints = new GridBagConstraints();
        Insets defaultInsets = new Insets(5, 5, 5, 5);

        // Adds Children
        constraints.insets = defaultInsets;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;

        // textLabel
        textLabel = new JLabel();
        textLabel.setPreferredSize(new Dimension(300, 22));
        textLabel.setMinimumSize(textLabel.getPreferredSize());
        textLabel.setText(bundle.getString("LBL_TextLabel"));
        gbl.setConstraints(textLabel, constraints);
        getContentPane().add(textLabel);

        // progressBar
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(300, 22));
        progressBar.setMinimumSize(progressBar.getPreferredSize());
        progressBar.setMinimum(1);
        constraints.gridy = 1;
        gbl.setConstraints(progressBar, constraints);
        getContentPane().add(progressBar);

        // percentLabel
        percentLabel = new JLabel();
        percentLabel.setPreferredSize(new Dimension(75, 22));
        percentLabel.setMinimumSize(percentLabel.getPreferredSize());
        constraints.gridx = 1;
        gbl.setConstraints(percentLabel, constraints);
        getContentPane().add(percentLabel);

        // cancelButton
        cancelButton = new JButton();
        cancelButton.setPreferredSize(new Dimension(75, 22));
        cancelButton.setMinimumSize(cancelButton.getPreferredSize());
        cancelButton
                .setMnemonic(bundle.getString("MNM_CancelButton").charAt(0));
        cancelButton.setText(bundle.getString("BTN_CancelButton"));
        cancelButton.setToolTipText(bundle.getString("HINT_CancelButton"));
        constraints.gridy = 2;
        gbl.setConstraints(cancelButton, constraints);
        getContentPane().add(cancelButton);
    }

    /**
     * Creates a progress dialog using the netbeans IDE main window as its
     * parent.
     * 
     * @return ProgressDialog.
     */
    public static ProgressDialog getDefault() {
        // final Frame[] frameReceiver = new Frame[1];
        // try {
        // SwingUtilities.invokeAndWait(new Runnable() {
        // public void run() {
        // frameReceiver[0] = WindowManager.getDefault()
        // .getMainWindow();
        // }
        // });
        // } catch (Exception e) {
        // ErrorManager.getDefault().notify(e);
        // }
        // DEFAULT = new ProgressDialog(frameReceiver[0]);
        DEFAULT = new ProgressDialog((Frame) WindowManager.getDefault()
                .getMainWindow());

        return DEFAULT;
    }

    /**
     * Yields whether the cancel button has been pressed.
     * 
     * @return Returns true if the cancel button has been pressed.
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * This implementation sets the progress bar maximum, and opens the dialog
     * by calling <code>show</code>.
     * 
     * @param value
     *            the maximum.
     */
    public void start(int value) {
        progressBar.setMaximum(value);
        setVisible(true);
    }

    /**
     * Updates the progress bar and returns true unless {@link #isCancelled}is
     * true.
     * 
     * @param index
     *            the progression value
     * @return true,or false if {@link #isCancelled}has been called.
     */
    public boolean updateState(int index, String taskname) {
        progressBar.setValue(index);
        textLabel.setText(bundle.getString("LBL_TextLabel") + " " + taskname);

        // progressBar.setString(Integer.toString(index) + '/'
        // + progressBar.getMaximum());
        return !isCancelled();
    }

    /**
     * Closes the dialog and resets its state.
     */
    public void hideDialog() {
        closeDialog();
    }

    /**
     * Closes the dialog, resetting its state.
     */
    private void closeDialog() {
        setVisible(false);
        isCancelled = false;
        updateCancelButton();
        dispose();
    }

    /**
     * Updates the state of the Cancel button according to whether the operation
     * is cancelled or not.
     */
    private void updateCancelButton() {
        if (isCancelled()) {
            cancelButton.setEnabled(false);
            cancelButton.setText(bundle
                    .getString("BTN_CancelButton_Cancelling"));
            cancelButton.setToolTipText(bundle
                    .getString("HINT_CancelButton_Cancelling"));
        } else {
            cancelButton.setEnabled(true);
            cancelButton.setText(bundle.getString("BTN_CancelButton"));
            cancelButton.setToolTipText(bundle.getString("HINT_CancelButton"));
        }
    }

    /**
     * @param isCancelled
     *            The isCancelled to set.
     */
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}