package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

/**
 * @author SAÏD, EL KASMI.
 */
public class ZoomingPanel extends JPanel {
    /** JSlider to get the zooming factor vertically. */
    private static JSlider verticalZoomingSlider;

    /** JSlider to get the zooming factor horizontally. */
    private static JSlider horizontalZoomingSlider;

    /**
     * Create a JPanel containing Sliders.
     */
    public ZoomingPanel() {
        setName("zoomingPanel");

        //setBackground(Color.WHITE);
        setOpaque(true);

        // set Layout
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        GridBagConstraints constraints = new GridBagConstraints();
        Insets defaultInsets = new Insets(5, 5, 5, 5);

        // Adds Children
        constraints.insets = defaultInsets;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;

        // verticalZoomingSlider
        verticalZoomingSlider = new JSlider(JSlider.HORIZONTAL);
        verticalZoomingSlider.setName("verticalZoomingSlider");
        verticalZoomingSlider.setMaximum(50);
        verticalZoomingSlider.setMajorTickSpacing(5);
        verticalZoomingSlider.setMinorTickSpacing(1);

        //verticalZoomingSlider.setBackground(Color.WHITE);
        verticalZoomingSlider.setValue(0);
        verticalZoomingSlider.setPaintLabels(true);
        verticalZoomingSlider.setPaintTicks(true);
        verticalZoomingSlider.setBorder(new TitledBorder("Vertical zoom"));
        gbl.setConstraints(verticalZoomingSlider, constraints);
        add(verticalZoomingSlider);

        // horizontalZoomingSlider
        horizontalZoomingSlider = new JSlider(JSlider.HORIZONTAL);
        horizontalZoomingSlider.setName("horizontalZoomingSlider");
        horizontalZoomingSlider.setMaximum(50);
        horizontalZoomingSlider.setMajorTickSpacing(5);
        horizontalZoomingSlider.setMinorTickSpacing(1);

        //horizontalZoomingSlider.setBackground(Color.WHITE);
        horizontalZoomingSlider.setValue(0);
        horizontalZoomingSlider.setPaintLabels(true);
        horizontalZoomingSlider.setPaintTicks(true);
        horizontalZoomingSlider.setBorder(new TitledBorder("Horizontal zoom"));
        constraints.gridx = 0;
        gbl.setConstraints(horizontalZoomingSlider, constraints);
        add(horizontalZoomingSlider);
    }

    /**
     * By delegation, attachs a <i>ChangeListenr </i> to sliders.
     */
    public void setChangeListenerForSliders(ChangeListener cl) {
        horizontalZoomingSlider.addChangeListener(cl);
        verticalZoomingSlider.addChangeListener(cl);
    }

    /**
     * Getter for horizontalZoomingSlider
     * @return Returns the horizontalZoomingSlider.
     */
    public static JSlider getHorizontalZoomingSlider() {
        return horizontalZoomingSlider;
    }

    /**
     * Getter for verticalZoomingSlider
     * @return Returns the verticalZoomingSlider.
     */
    public static JSlider getVerticalZoomingSlider() {
        return verticalZoomingSlider;
    }
}