package fr.unice.bioinfo.thea.editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.editor.dlg.ZoomingPanel;
import fr.unice.bioinfo.thea.editor.selection.SelectionEditor;

/**
 * A Factory that builds popup menus corresponding to actions on nodes. An
 * action is mainly a selction, a click or a deselction.
 * @author SAÏD, EL KASMI.
 */
public class PopupFactory {
    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.editor.Bundle"); //NOI18N;
    private static JPopupMenu popup;
    private JMenuItem menuItem;

    public static JPopupMenu getNodeSelectedPopup(TreeSelectionEvent e,
            final CEditor editor) {
        final CECanvas tc = editor.getCanvas();
        final Node n = (Node) e.getSelected();
        popup = new JPopupMenu();

        JMenuItem menuItem;

        // Add zoom menu:
        menuItem = new JMenuItem(bundle.getString("LBL_ZoomingAction_Name"));
        menuItem
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/editor/resources/Zoom16.gif")));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZoomingPanel zoomingPanel = new ZoomingPanel();
                zoomingPanel.setChangeListenerForSliders(editor);

                //create the Cancel button
                JButton hideBtn = new JButton(bundle
                        .getString("LBL_HideButton_Name"));
                hideBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                Object[] options = { hideBtn };
                DialogDescriptor dd = new DialogDescriptor(zoomingPanel, bundle
                        .getString("LBL_ZoomingPanel_Name"), false, options,
                        null, DialogDescriptor.DEFAULT_ALIGN,
                        HelpCtx.DEFAULT_HELP, null);
                dd.setClosingOptions(new Object[] { hideBtn });
                DialogDisplayer.getDefault().createDialog(dd).show();
            }
        });
        popup.add(menuItem);
        
        // Add a menu to show the Selection Editor
        menuItem = new JMenuItem(bundle.getString("LBL_SelectionWindowAction"));
        menuItem
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/editor/resources/SelectionEditor16.gif")));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectionEditor se = editor.getSelectionEditor();
                Mode m = WindowManager.getDefault().findMode("explorer");//NOI18N
                if (m != null) {
                    m.dockInto(se);
                }
                se.open();
                se.requestActive();
            }
        });
        popup.add(menuItem);
        popup.addSeparator();

        // Add ClassificationViewerSettings menu
        //        menuItem = new JMenuItem(bundle.getString(
        //                    "LBL_ClassificationViewerSettingsAction_Name"));
        //        menuItem.setIcon(new ImageIcon(Utilities.loadImage(
        //                    "fr/unice/bioinfo/thea/CEditor/resources/Preferences16.gif")));
        //        menuItem.addActionListener(new ActionListener() {
        //                public void actionPerformed(ActionEvent e) {
        //                    // create the main panel for Classification Viewer Settings
        //                    ClassificationViewerSettingsPanel p = new
        // ClassificationViewerSettingsPanel();
        //
        //                    // create the hide button
        //                    JButton hideBtn = new JButton(NbBundle.getMessage(
        //                                ClassificationViewerSettingsAction.class,
        //                                "LBL_HideButton_Name"));
        //                    hideBtn.addActionListener(new ActionListener() {
        //                            public void actionPerformed(ActionEvent e) {
        //                            }
        //                        });
        //
        //                    Object[] options = { hideBtn };
        //                    DialogDescriptor dd = new DialogDescriptor(p,
        //                            NbBundle.getMessage(
        //                                ClassificationViewerSettingsAction.class,
        //                                "LBL_ClassificationViewerSettingsDialog_Name"),
        //                            false, options, null,
        //                            DialogDescriptor.DEFAULT_ALIGN,
        //                            HelpCtx.DEFAULT_HELP, null);
        //                    dd.setClosingOptions(new Object[] { hideBtn });
        //                    DialogDisplayer.getDefault().createDialog(dd).show();
        //                }
        //            });
        //        popup.add(menuItem);
        //        popup.addSeparator();

        // copy to profile classifier
        if ((n.isLeaf()) && (n.getUserData("measures") != null)) {
            menuItem = new JMenuItem(bundle
                    .getString("LBL_CopyToProfileClassifierAction_Name"));
            menuItem
                    .setIcon(new ImageIcon(
                            Utilities
                                    .loadImage("fr/unice/bioinfo/thea/editor/resources/Copy16.gif")));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });

            /*
             * Profile Classifier doesn't exists yet
             */

            //popup.add(menuItem);
        }

        // collapse and Uncollapse
        if (!n.isLeaf()) {
//            menuItem = new JMenuItem(tc.getCollapsed(n) ? bundle
//                    .getString("LBL_UncollapseAction_Name") : bundle
//                    .getString("LBL_CollapseAction_Name"));
            menuItem = new JMenuItem(n.isCollapsed() ? bundle
                    .getString("LBL_UncollapseAction_Name") : bundle
                    .getString("LBL_CollapseAction_Name"));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
//                    tc.addCollapsedNode(n, !tc.getCollapsed(n));
                    tc.addCollapsedNode(n, !n.isCollapsed());
                }
            });
            popup.add(menuItem);
        }

        // dispplay whole tree
        if (tc.getTreeRoot() != tc.getRootNode()) {
            menuItem = new JMenuItem(bundle
                    .getString("LBL_PreviousTreeAction_Name"));
            menuItem
                    .setIcon(new ImageIcon(
                            Utilities
                                    .loadImage("fr/unice/bioinfo/thea/editor/resources/left.gif")));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tc.popRoot();
                }
            });
            popup.add(menuItem);
        }

        // One level up
        if (tc.getTreeRoot() != tc.getRootNode()) {
            menuItem = new JMenuItem(bundle
                    .getString("LBL_OneLevelUpAction_Name"));
            menuItem
                    .setIcon(new ImageIcon(
                            Utilities
                                    .loadImage("fr/unice/bioinfo/thea/editor/resources/up.gif")));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tc.levelUp();
                }
            });
            popup.add(menuItem);
        }

        // sub-tree
        if (tc.getRootNode() != n) {
            menuItem = new JMenuItem(bundle.getString("LBL_SubTreeAction_Name"));
            menuItem
                    .setIcon(new ImageIcon(
                            Utilities
                                    .loadImage("fr/unice/bioinfo/thea/editor/resources/down.gif")));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tc.pushRoot(n);
                }
            });
            popup.add(menuItem);
        }

        // keep selection
        if (!tc.getSelected().isEmpty()) {
            menuItem = new JMenuItem(bundle
                    .getString("LBL_KeepSelectionAction_Name"));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tc.keepSelection();
                }
            });
            popup.add(menuItem);
        }

        return popup;
    }

    /*
     * Create the popup menu correspending the node selction in the CECanvas
     * widget. CEditor uses this method from the nodeSelected(...) method.
     */
    public static JPopupMenu getNodeSetSelectedPopup(TreeSelectionEvent e,
            final CECanvas tc) {
        final NodeSet ns = (NodeSet) e.getSelected();
        popup = null;
        popup = new JPopupMenu();

        JMenuItem menuItem;

        // Delete Selection
        menuItem = new JMenuItem(bundle
                .getString("LBL_DeleteSelectionAction_Name"));
        menuItem
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/editor/resources/Delete16.gif")));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tc.removeSelection(ns);
            }
        });
        popup.add(menuItem);

        // Move to current
        menuItem = new JMenuItem(bundle
                .getString("LBL_MoveToCurrentAction_Name"));
        menuItem
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/editor/resources/Remove16.gif")));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tc.moveSelectionToCurrent(ns);
            }
        });
        popup.add(menuItem);

        // copy to current
        menuItem = new JMenuItem(bundle
                .getString("LBL_CopyToCurrentAction_Name"));
        menuItem
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/editor/resources/Copy16.gif")));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tc.copySelectionToCurrent(ns);
            }
        });
        popup.add(menuItem);

        // union with current
        menuItem = new JMenuItem(bundle
                .getString("LBL_UnionWithCurrentAction_Name"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tc.unionSelectionWithCurrent(ns);
            }
        });
        popup.add(menuItem);

        // Intersect with current
        menuItem = new JMenuItem(bundle
                .getString("LBL_IntersectWithCurrentAction_Name"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tc.intersectSelectionWithCurrent(ns);
            }
        });
        popup.add(menuItem);

        // group selection
        menuItem = new JMenuItem(bundle
                .getString("LBL_GroupSelectionAction_Name"));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tc.groupSelection(ns);
            }
        });
        popup.add(menuItem);

        // Freeze, Unfreeze selection
        Boolean frozenState = (Boolean) ns.getUserData("frozen");
        final Boolean frozen = (frozenState == null) ? Boolean.FALSE
                : frozenState;

        if (frozen.equals(Boolean.FALSE)) {
            menuItem = new JMenuItem(bundle
                    .getString("LBL_FreezeSelctionAction_Name"));
        } else {
            menuItem = new JMenuItem(bundle
                    .getString("LBL_UnfreezeSelctionAction_Name"));
        }

        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection nodes = ns.getNodes();
                Iterator it = nodes.iterator();

                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    node.setUserData("frozen",
                            frozen.equals(Boolean.TRUE) ? Boolean.FALSE
                                    : Boolean.TRUE);
                }

                if (frozen.equals(Boolean.TRUE)) {
                    ns.setUserData("frozen", Boolean.FALSE);
                    ns.setUserData("bgColor", null);
                } else {
                    ns.setUserData("frozen", Boolean.TRUE);
                    ns.setUserData("bgColor", new Color(192, 255, 255));
                }
            }
        });
        popup.add(menuItem);

        // Separator
        popup.addSeparator();

        // Save as
        menuItem = new JMenuItem(bundle
                .getString("LBL_SaveSelectionAction_Name"));
        menuItem
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/editor/resources/SaveAs16.gif")));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showSaveDialog(WindowManager
                        .getDefault().getMainWindow());

                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File file = chooser.getSelectedFile();

                if (file == null) {
                    return;
                }

                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new FileWriter(file)));

                    Collection selectedNodes = ns.getNodes();
                    Iterator it = selectedNodes.iterator();
                    Node n = null;

                    while (it.hasNext()) {
                        n = (Node) it.next();
                        out.println(n.getName());
                    }
                } catch (java.io.IOException ioe) {
                    ErrorManager.getDefault().log(
                            "problem writing newick format");
                }
            }
        });
        popup.add(menuItem);

        return popup;
    }
}