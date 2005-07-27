package fr.unice.bioinfo.thea.classification.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.util.SotaUtil;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();

        Canvas canvas = new Canvas();
        Node rootNode = canvas.getClassificationRootNode();
        File cf = new File("C:/wspace/dagedit/samples/DeGregorio.sot");
        rootNode = new SotaUtil().load(cf);
        if (rootNode == null) {
            System.out.println("rootNode = null");
            System.exit(0);
        }
        rootNode.init();
        System.out.println("rootNode.init() successful ...");
        canvas.setCurrentRootNode(rootNode);
        System.out
                .println("canvas.setCurrentRootNode(rootNode) successful ...");

        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        frame.getContentPane().setLayout(new BorderLayout());

        ModeBar modeBar = new ModeBar((Zoomable) canvas);
        frame.getContentPane().add(modeBar, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }
}