package fr.unice.bioinfo.thea.dlg;

import java.awt.BorderLayout;

import fr.unice.bioinfo.thea.classification.editor.dlg.ZoomingOptionsPanel;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ZoomingSettingsPage extends AbstractSettingsPage {

    /**
     * @param container
     */
    public ZoomingSettingsPage(PreferencesContainer container) {
        super(container);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new ZoomingOptionsPanel(), BorderLayout.CENTER);
    }
}