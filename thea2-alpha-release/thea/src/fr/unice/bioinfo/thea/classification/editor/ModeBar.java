package fr.unice.bioinfo.thea.classification.editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

public class ModeBar extends JToolBar {

    private JToggleButton mouseBtn;
    private JToggleButton zoomBtn;
    private ButtonGroup group;
    private Zoomable zoomable;

    public ModeBar(Zoomable zoomable) {
        super(null, JToolBar.HORIZONTAL);
        super.setRollover(true);
        this.zoomable = zoomable;
        ZoomManager zm = new ZoomManager(zoomable);
        zoomable.setZoomManager(zm);
        init();
    }

    private void init() {
        // set a nice border around this panel.
        //setBorder(new EtchedBorder());
        // make the background white
        setBackground(Color.WHITE);
        // create button
        group = new ButtonGroup();
        mouseBtn = new JToggleButton();
        group.add(mouseBtn);
        zoomBtn = new JToggleButton();
        group.add(zoomBtn);

        //---- mouseBtn ----
        mouseBtn.setBorder(new EtchedBorder());
        mouseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomable.setStandardMode();
            }
        });
        mouseBtn.setToolTipText("Standard mode");
        mouseBtn.setIcon(new ImageIcon(getClass().getResource(
                "resources/MouseCursorIcon.gif")));
        add(mouseBtn);

        //---- zoomBtn ----
        zoomBtn.setBorder(new EtchedBorder());
        zoomBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomable.setZoomingMode();
            }
        });
        zoomBtn.setIcon(new ImageIcon(getClass().getResource(
                "resources/ZoomModeIcon.gif")));
        zoomBtn.setToolTipText("Zooming mode");

        zoomBtn.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
            }

            public void mouseEntered(MouseEvent e) {
                zoomBtn.setIcon(new ImageIcon(getClass().getResource(
                        "resources/ZoomModeOnIcon.gif")));
            }

            public void mouseExited(MouseEvent e) {
                zoomBtn.setIcon(new ImageIcon(getClass().getResource(
                        "resources/ZoomModeIcon.gif")));
            }

            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub
            }

            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
            }
        });
        add(zoomBtn);
    }

}