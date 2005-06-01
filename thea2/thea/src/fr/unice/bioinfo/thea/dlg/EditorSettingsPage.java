package fr.unice.bioinfo.thea.dlg;

import java.awt.BorderLayout;

import fr.unice.bioinfo.thea.classification.editor.dlg.CanvasOptions;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class EditorSettingsPage extends AbstractSettingsPage {

    /**
     * @param container
     */
    public EditorSettingsPage(PreferencesContainer container) {
        super(container);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new CanvasOptions(),BorderLayout.CENTER);
    }
}