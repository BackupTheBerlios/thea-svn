package fr.unice.bioinfo.thea.dlg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import org.openide.util.NbBundle;

import com.jgoodies.forms.factories.Borders;

import fr.unice.bioinfo.thea.util.Helper;

public class PreferencesContainer extends JPanel {

    /** The used tree to select the different settings pages. */
    private JTree tree;

    /** Used to display the title of the current settings page. */
    private JLabel titleLabel;

    /** Panel to add the active settings page. */
    private JPanel settingsPanel;

    /** Holds the currently displayed settings page. */
    private JPanel currentSettingsPanel;

    /** Holds all property pages that have been displayed. */
    private Map panels = new HashMap(10);

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); //NOI18N;

    public PreferencesContainer() {
        init();
    }

    private void init() {
        setBorder(Borders.DIALOG_BORDER);
        // create the Panel that works as a spacer.
        JPanel spacer = new JPanel();
        Dimension space = new Dimension(5, 5);
        spacer.setMaximumSize(space);
        spacer.setMinimumSize(space);
        spacer.setPreferredSize(space);

        // Create the general settings panel:
        SettingsNodeInfo info = new SettingsNodeInfo(bundle
                .getString("GeneralSettingsPage_Name"), bundle
                .getString("GeneralSettingsPage_Title"), bundle
                .getString("GeneralSettingsPage_Class"));
        currentSettingsPanel = loadPanel(info);
        panels.put(info.key, currentSettingsPanel);

        // create Label and Panel for titles
        titleLabel = new JLabel();
        titleLabel.setText(info.title);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.white);
        titlePanel.setBorder(new LineBorder(Color.black));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(titleLabel);

        // Create the panel that holds the title panel and the
        // current settings page panel.
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout());
        settingsPanel.add(titlePanel, BorderLayout.NORTH);
        settingsPanel.add(currentSettingsPanel, BorderLayout.CENTER);
        settingsPanel.add(new JSeparator(), BorderLayout.SOUTH);

        // create the tree
        DefaultMutableTreeNode root = createNodes();
        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.putClientProperty("JTree.lineStyle", "Angled");//NOI18N
        tree.addTreeSelectionListener(new SettingsTreeSelectionHandler());
        DefaultTreeCellRenderer r = (DefaultTreeCellRenderer) tree
                .getCellRenderer();
        r.setClosedIcon(null);
        r.setOpenIcon(null);
        r.setLeafIcon(null);
        JScrollPane sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(200, 400));
        sp.getViewport().add(tree, null);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        //rightPanel.setPreferredSize(new Dimension(350, 400));
        rightPanel.add(spacer, BorderLayout.WEST);
        rightPanel.add(settingsPanel, BorderLayout.CENTER);

        // Set this panel's layout and all pieces:
        setLayout(new BorderLayout());
        add(sp, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private String getTitle(DefaultMutableTreeNode node) {
        TreeNode[] path = node.getPath();
        StringBuffer buf = new StringBuffer(30);

        for (int i = 0; i < path.length; i++) {
            if (path[i].getParent() != null) {
                buf.append(path[i]);
                buf.append(" \u00B7 " /* NOI18N */); // middle dot
            }
        }

        buf.setLength(buf.length() - 3);

        return buf.toString();
    }

    /**
     * Loads the panel defined by the given settings info.
     * @param info settings info.
     * @return the indicated settings panel.
     */
    private AbstractSettingsPage loadPanel(SettingsNodeInfo info) {
        try {
            Class[] params = { PreferencesContainer.class };
            Constructor c = info.getPanelClass().getDeclaredConstructor(params);
            Object[] args = { this };

            AbstractSettingsPage panel = (AbstractSettingsPage) c
                    .newInstance(args);

            panel.setTitle(info.title);

            return panel;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Creates the different nodes to choose the settings pages.
     * @return the root node of the created node tree.
     */
    private DefaultMutableTreeNode createNodes() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");

        DefaultMutableTreeNode general = new SettingsNode(new SettingsNodeInfo(
                bundle.getString("GeneralSettingsPage_Name"), bundle
                        .getString("GeneralSettingsPage_Title"), bundle
                        .getString("GeneralSettingsPage_Class")));

        DefaultMutableTreeNode classification = new SettingsNode(
                new SettingsNodeInfo(bundle
                        .getString("ClassificationSettingsPage_Name"), bundle
                        .getString("ClassificationSettingsPage_Title"), bundle
                        .getString("ClassificationSettingsPage_Class")));

        DefaultMutableTreeNode zooming = new SettingsNode(new SettingsNodeInfo(
                bundle.getString("ZoomingSettingsPage_Name"), bundle
                        .getString("ZoomingSettingsPage_Title"), bundle
                        .getString("ZoomingSettingsPage_Class")));

        DefaultMutableTreeNode ontology = new SettingsNode(
                new SettingsNodeInfo(bundle
                        .getString("OntologySettingsPage_Name"), bundle
                        .getString("OntologySettingsPage_Title"), bundle
                        .getString("OntologySettingsPage_Class")));
        
        DefaultMutableTreeNode annotation = new SettingsNode(
                new SettingsNodeInfo(bundle
                        .getString("AnnotationSettingsPage_Name"), bundle
                        .getString("AnnotationSettingsPage_Title"), bundle
                        .getString("AnnotationSettingsPage_Class")));

        root.add(general);
        classification.add(zooming);
        root.add(classification);
        root.add(ontology);
        root.add(annotation);
        return root;
    }

    /**
     * Helper class which provides the necessary information for every property
     * node. This is the user object for the JTree.
     */
    private static class SettingsNodeInfo implements Serializable {
        /** Use serialVersionUID for interoperability. */
        static final long serialVersionUID = 4496045479306791488L;
        transient Class panelClass;
        String className;
        String key;
        String title;
        int hashCode;

        public SettingsNodeInfo(String key, String title, String className) {
            this.key = key;
            this.className = className;
            this.title = title;
            this.hashCode = title.hashCode();
        }

        /**
         * Returns the class to load for the settings page.
         * @return the class to load for the settings page.
         */
        public Class getPanelClass() {
            if (this.panelClass == null) {
                try {
                    this.panelClass = Helper.loadClass(this.className, this);
                } catch (ClassNotFoundException ex) {
                    //this.panelClass = GeneralSettingsPage.class;
                }
            }

            return this.panelClass;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            // we only compare titles so that we can easily search the tree
            if (o instanceof String) {
                return this.title.equals(o);
            }

            return false;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            return this.title;
        }
    }

    /**
     * Our customized tree node.
     */
    private static class SettingsNode extends DefaultMutableTreeNode {
        /**
         * Creates a new SettingsNode object.
         * @param o the node information
         */
        public SettingsNode(Object o) {
            super(o);
        }

        /**
         * Returns the node information.
         * @return node information object.
         */
        public SettingsNodeInfo getInfo() {
            return (SettingsNodeInfo) getUserObject();
        }
    }

    /**
     * Listens to nodes selection in the tree. Whene a node is selected, the
     * correspending panel is shown on the right side of this panel.
     */
    private class SettingsTreeSelectionHandler implements TreeSelectionListener {

        /*
         * (non-Javadoc)
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e) {
            SettingsNode node = (SettingsNode) tree
                    .getLastSelectedPathComponent();

            // do nothing if no node is selected
            if (node == null) {
                return;
            }

            String title = getTitle(node);
            titleLabel.setText(title);
            settingsPanel.remove(currentSettingsPanel);

            SettingsNodeInfo info = node.getInfo();

            if (panels.containsKey(info.key)) {
                // load panel from cache
                currentSettingsPanel = (AbstractSettingsPage) panels
                        .get(info.key);
            } else {
                currentSettingsPanel = loadPanel(info);

                // update cache
                panels.put(info.key, currentSettingsPanel);
            }

            settingsPanel.add(currentSettingsPanel, BorderLayout.CENTER);
            settingsPanel.repaint();
        }
    }

    public static void main(String[] arg) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new PreferencesContainer());
        frame.pack();
        frame.setVisible(true);
    }
}