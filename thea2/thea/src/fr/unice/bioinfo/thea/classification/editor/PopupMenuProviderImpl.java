package fr.unice.bioinfo.thea.classification.editor;

import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.actions.BackAction;
import fr.unice.bioinfo.thea.classification.editor.actions.ChooseAnnotationAction;
import fr.unice.bioinfo.thea.classification.editor.actions.ClassifierAction;
import fr.unice.bioinfo.thea.classification.editor.actions.CollapseAction;
import fr.unice.bioinfo.thea.classification.editor.actions.ColorAction;
import fr.unice.bioinfo.thea.classification.editor.actions.CopyAction;
import fr.unice.bioinfo.thea.classification.editor.actions.DownAction;
import fr.unice.bioinfo.thea.classification.editor.actions.FreezeAction;
import fr.unice.bioinfo.thea.classification.editor.actions.GroupAction;
import fr.unice.bioinfo.thea.classification.editor.actions.IntersectAction;
import fr.unice.bioinfo.thea.classification.editor.actions.KeepAllAnnotationsAction;
import fr.unice.bioinfo.thea.classification.editor.actions.KeepAnnotationAction;
import fr.unice.bioinfo.thea.classification.editor.actions.MoveAction;
import fr.unice.bioinfo.thea.classification.editor.actions.NewAction;
import fr.unice.bioinfo.thea.classification.editor.actions.RemoveAction;
import fr.unice.bioinfo.thea.classification.editor.actions.RemoveAnnotationAction;
import fr.unice.bioinfo.thea.classification.editor.actions.SaveAction;
import fr.unice.bioinfo.thea.classification.editor.actions.SelectAction;
import fr.unice.bioinfo.thea.classification.editor.actions.UncollapseAction;
import fr.unice.bioinfo.thea.classification.editor.actions.UnfreezeAction;
import fr.unice.bioinfo.thea.classification.editor.actions.UnionAction;
import fr.unice.bioinfo.thea.classification.editor.actions.UpAction;
import fr.unice.bioinfo.thea.classification.editor.actions.WholeAction;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class PopupMenuProviderImpl implements PopupMenuProvider {

    private JPopupMenu popup = null;
    private JMenuItem menuItem = null;
    private ImageIcon icon = null;
    private String name = "";
    private String shortDescription = "";
    private String accelerator = "";
    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.classification.editor.Bundle"); //NOI18N;

    private JPopupMenu getNodePopupMenu(DrawableClassification drawable,
            Node aNode) {
        popup = new JPopupMenu();
        // 1 - Collapse:
        popup.add(createCollapseMenuItem(drawable, aNode));
        // 2 - Uncollapse:
        popup.add(createUncollapseMenuItem(drawable, aNode));
        // add a separator here
        popup.addSeparator();
        // 3 - Keep annotation
        popup.add(createKeepAnnotationMenuItem(drawable, aNode));
        // 4 - Keep all annotations
        popup.add(createKeepAllAnnotationsMenuItem(drawable, aNode));
        // 5 - Choose annotation
        popup.add(createChooseAnnotationMenuItem(drawable, aNode));
        // 6 - Remove annotation
        popup.add(createRemoveAnnotationsMenuItem(drawable, aNode));
        // add a separator here
        popup.addSeparator();
        // 7 - Back:
        popup.add(createBackMenuItem(drawable, aNode));
        // 8 - Up:
        popup.add(createUpMenuItem(drawable, aNode));
        // 9 - Down:
        popup.add(createDownMenuItem(drawable, aNode));
        // 10 - Whole Tree:
        // It causes a NullPointerException sometimes,
        // commenting it until this bug is fixed.
        //popup.add(createWholeMenuItem(drawable, aNode));
        // add a separator here
        popup.addSeparator();
        // 11 - Classifier:
        popup.add(createClassifierMenuItem(drawable, aNode));
        // 12 - New:
        popup.add(createNewMenuItem(drawable, aNode));
        // add a separator here
        popup.addSeparator();
        // 13 - Select:
        popup.add(createSelectMenuItem(drawable, aNode));
        return popup;
    }

    private JMenuItem createCollapseMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_CollapseAction");
        shortDescription = bundle.getString("HINT_CollapseAction");
        accelerator = bundle.getString("ACC_CollapseAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action collapse = new CollapseAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(collapse);
        return menuItem;
    }

    private JMenuItem createUncollapseMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_UncollapseAction");
        shortDescription = bundle.getString("HINT_UncollapseAction");
        accelerator = bundle.getString("ACC_UncollapseAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action uncollapse = new UncollapseAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(uncollapse);
        return menuItem;
    }

    private JMenuItem createKeepAnnotationMenuItem(
            DrawableClassification drawable, Node aNode) {
        name = bundle.getString("LBL_KeepAnnotationAction");
        shortDescription = bundle.getString("HINT_KeepAnnotationAction");
        accelerator = bundle.getString("ACC_KeepAnnotationAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action keep = new KeepAnnotationAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(keep);
        return menuItem;
    }

    private JMenuItem createKeepAllAnnotationsMenuItem(
            DrawableClassification drawable, Node aNode) {
        name = bundle.getString("LBL_KeepAllAnnotationsAction");
        shortDescription = bundle.getString("HINT_KeepAllAnnotationsAction");
        accelerator = bundle.getString("ACC_KeepAllAnnotationsAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action keepAll = new KeepAllAnnotationsAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(keepAll);
        return menuItem;
    }

    private JMenuItem createChooseAnnotationMenuItem(
            DrawableClassification drawable, Node aNode) {
        name = bundle.getString("LBL_ChooseAnnotationAction");
        shortDescription = bundle.getString("HINT_ChooseAnnotationAction");
        accelerator = bundle.getString("ACC_ChooseAnnotationAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action choose = new ChooseAnnotationAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(choose);
        return menuItem;
    }

    private JMenuItem createRemoveAnnotationsMenuItem(
            DrawableClassification drawable, Node aNode) {
        name = bundle.getString("LBL_RemoveAnnotationAction");
        shortDescription = bundle.getString("HINT_RemoveAnnotationAction");
        accelerator = bundle.getString("ACC_RemoveAnnotationAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action remove = new RemoveAnnotationAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(remove);
        return menuItem;
    }

    private JMenuItem createBackMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_PreviousTreeAction");
        shortDescription = bundle.getString("HINT_PreviousTreeAction");
        accelerator = bundle.getString("ACC_PreviousTreeAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/PreviousTreeIcon16.gif"));
        Action back = new BackAction(name, accelerator, icon, shortDescription,
                drawable, aNode);
        menuItem = new JMenuItem(back);
        return menuItem;
    }

    private JMenuItem createUpMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_OneLevelUpAction");
        shortDescription = bundle.getString("HINT_OneLevelUpAction");
        accelerator = bundle.getString("ACC_OneLevelUpAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/ShowParentTreeIcon16.gif"));
        Action up = new UpAction(name, accelerator, icon, shortDescription,
                drawable, aNode);
        menuItem = new JMenuItem(up);
        return menuItem;
    }

    private JMenuItem createDownMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_SubTreeAction");
        shortDescription = bundle.getString("HINT_SubTreeAction");
        accelerator = bundle.getString("ACC_SubTreeAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/SubTreeIcon16.gif"));
        Action down = new DownAction(name, accelerator, icon, shortDescription,
                drawable, aNode);
        menuItem = new JMenuItem(down);
        return menuItem;
    }

    private JMenuItem createWholeMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_WholeTreeAction");
        shortDescription = bundle.getString("HINT_WholeTreeAction");
        accelerator = bundle.getString("ACC_WholeTreeAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action whole = new WholeAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(whole);
        return menuItem;
    }

    private JMenuItem createClassifierMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_CopyToProfileClassifierAction");
        shortDescription = bundle
                .getString("HINT_CopyToProfileClassifierAction");
        accelerator = bundle.getString("ACC_CopyToProfileClassifierAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action cl = new ClassifierAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(cl);
        return menuItem;
    }

    private JMenuItem createNewMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_OpenInNewWindowAction");
        shortDescription = bundle.getString("HINT_OpenInNewWindowAction");
        accelerator = bundle.getString("ACC_OpenInNewWindowAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action n = new NewAction(name, accelerator, icon, shortDescription,
                drawable, aNode);
        menuItem = new JMenuItem(n);
        return menuItem;
    }

    private JMenuItem createSelectMenuItem(DrawableClassification drawable,
            Node aNode) {
        name = bundle.getString("LBL_KeepSelectionAction");
        shortDescription = bundle.getString("HINT_KeepSelectionAction");
        accelerator = bundle.getString("ACC_KeepSelectionAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action select = new SelectAction(name, accelerator, icon,
                shortDescription, drawable, aNode);
        menuItem = new JMenuItem(select);
        return menuItem;
    }

    private JPopupMenu getSelectionPopupMenu(DrawableClassification drawable,
            Selection selection) {
        popup = new JPopupMenu();
        // 1 - Remove:
        popup.add(createRemoveMenuItem(drawable, selection));
        // add a seperator:
        popup.addSeparator();
        // 2 - Move:
        popup.add(createMoveMenuItem(drawable, selection));
        // 3 - Copy:
        popup.add(createCopyMenuItem(drawable, selection));
        // 4 - Union:
        popup.add(createUnionMenuItem(drawable, selection));
        // 5 - Intersect:
        popup.add(createIntersectMenuItem(drawable, selection));
        // 6 - Group:
        popup.add(createGroupMenuItem(drawable, selection));
        // add a seperator:
        popup.addSeparator();
        // 7 - Freeze
        popup.add(createFreezeMenuItem(drawable, selection));
        // 8 - Freeze
        popup.add(createUnfreezeMenuItem(drawable, selection));
        // add a seperator:
        popup.addSeparator();
        // 9 - Save
        popup.add(createSaveMenuItem(drawable, selection));
        // add separator
        popup.addSeparator();
        // 10 - Color
        popup.add(createColorMenuItem(drawable, selection));
        // the popup menu ie ready,return it.
        return popup;
    }

    private JMenuItem createRemoveMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_DeleteSelectionAction");
        shortDescription = bundle.getString("HINT_DeleteSelectionAction");
        accelerator = bundle.getString("ACC_DeleteSelectionAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/RemoveIcon16.gif"));
        Action remove = new RemoveAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(remove);
        return menuItem;
    }

    private JMenuItem createMoveMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_MoveToCurrentAction");
        shortDescription = bundle.getString("HINT_MoveToCurrentAction");
        accelerator = bundle.getString("ACC_MoveToCurrentAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action move = new MoveAction(name, accelerator, icon, shortDescription,
                drawable, selection);

        menuItem = new JMenuItem(move);
        return menuItem;
    }

    private JMenuItem createCopyMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_CopyToCurrentAction");
        shortDescription = bundle.getString("HINT_CopyToCurrentAction");
        accelerator = bundle.getString("ACC_CopyToCurrentAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action copy = new CopyAction(name, accelerator, icon, shortDescription,
                drawable, selection);
        menuItem = new JMenuItem(copy);
        return menuItem;
    }

    private JMenuItem createUnionMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_UnionWithCurrentAction");
        shortDescription = bundle.getString("HINT_UnionWithCurrentAction");
        accelerator = bundle.getString("ACC_UnionWithCurrentAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action union = new UnionAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(union);
        return menuItem;
    }

    private JMenuItem createIntersectMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_IntersectWithCurrentAction");
        shortDescription = bundle.getString("HINT_IntersectWithCurrentAction");
        accelerator = bundle.getString("ACC_IntersectWithCurrentAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action intersect = new IntersectAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(intersect);
        return menuItem;
    }

    private JMenuItem createGroupMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_GroupSelectionAction");
        shortDescription = bundle.getString("HINT_GroupSelectionAction");
        accelerator = bundle.getString("ACC_GroupSelectionAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action group = new GroupAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(group);
        return menuItem;
    }

    private JMenuItem createSaveMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_SaveSelectionAction");
        shortDescription = bundle.getString("HINT_SaveSelectionAction");
        accelerator = bundle.getString("ACC_SaveSelectionAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/FileSaveIcon16.gif"));
        Action save = new SaveAction(name, accelerator, icon, shortDescription,
                drawable, selection);
        menuItem = new JMenuItem(save);
        return menuItem;
    }

    private JMenuItem createFreezeMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_FreezeSelctionAction");
        shortDescription = bundle.getString("HINT_FreezeSelctionAction");
        accelerator = bundle.getString("ACC_FreezeSelctionAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action freeze = new FreezeAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(freeze);
        return menuItem;
    }

    private JMenuItem createUnfreezeMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_UnfreezeSelctionAction");
        shortDescription = bundle.getString("HINT_UnfreezeSelctionAction");
        accelerator = bundle.getString("ACC_UnfreezeSelctionAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action unfreeze = new UnfreezeAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(unfreeze);
        return menuItem;
    }

    private JMenuItem createColorMenuItem(DrawableClassification drawable,
            Selection selection) {
        name = bundle.getString("LBL_ColorAction");
        shortDescription = bundle.getString("HINT_ColorAction");
        accelerator = bundle.getString("ACC_ColorAction");
        icon = new ImageIcon(
                Utilities
                        .loadImage("fr/unice/bioinfo/thea/classification/editor/resources/EmptyIcon.gif"));
        Action color = new ColorAction(name, accelerator, icon,
                shortDescription, drawable, selection);
        menuItem = new JMenuItem(color);
        return menuItem;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.PopupMenuProvider#showNodePopupMenu(java.awt.event.MouseEvent,
     *      fr.unice.bioinfo.thea.classification.editor.DrawableClassification,
     *      fr.unice.bioinfo.thea.classification.Node)
     */
    public void showNodePopupMenu(MouseEvent e,
            DrawableClassification drawable, Node aNode) {
        this.getNodePopupMenu(drawable, aNode).show(e.getComponent(), e.getX(),
                e.getY());
        //        JPopupMenu pm = this.getNodePopupMenu(drawable, aNode);
        //        drawable.setPopupMenu(pm);
        //        pm.show(e.getComponent(), e.getX(), e.getY());
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.PopupMenuProvider#showSelectionPopupMenu(java.awt.event.MouseEvent,
     *      fr.unice.bioinfo.thea.classification.editor.DrawableClassification,
     *      fr.unice.bioinfo.thea.classification.Selection)
     */
    public void showSelectionPopupMenu(MouseEvent e,
            DrawableClassification drawable, Selection selection) {
        this.getSelectionPopupMenu(drawable, selection).show(e.getComponent(),
                e.getX(), e.getY());
        //        JPopupMenu pm = this.getSelectionPopupMenu(drawable, selection);
        //        drawable.setPopupMenu(pm);
        //        pm.show(e.getComponent(), e.getX(), e.getY());
    }

}