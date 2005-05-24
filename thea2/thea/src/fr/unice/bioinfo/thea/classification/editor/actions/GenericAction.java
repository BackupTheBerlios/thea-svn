package fr.unice.bioinfo.thea.classification.editor.actions;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public abstract class GenericAction extends AbstractAction {

    protected DrawableClassification drawable;
    
    public GenericAction(String name, String accelerator, ImageIcon icon,
            String shortDescription,DrawableClassification drawable) {
        putValue(NAME, name);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
        putValue(SMALL_ICON, icon);
        putValue(SHORT_DESCRIPTION, shortDescription);
        this.drawable = drawable;
    }

    //    public GenericAction() {
    //        ClassLoader classLoader = getClass().getClassLoader();
    //        putValue(NAME, "Cut");
    //        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
    //        putValue(SMALL_ICON, new ImageIcon(classLoader
    //                .getResource("com/fortraneditor/images/Cut16.gif")));
    //        //putValue( LARGE_ICON, new ImageIcon( classLoader.getResource(
    // "com/fortraneditor/images/close24.gif" ) ) );
    //        putValue(SHORT_DESCRIPTION, "Cut selected text");
    //        putValue(LONG_DESCRIPTION, "");
    //    }

}