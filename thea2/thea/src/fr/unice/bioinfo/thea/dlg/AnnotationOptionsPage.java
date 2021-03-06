package fr.unice.bioinfo.thea.dlg;

import java.awt.BorderLayout;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class AnnotationOptionsPage extends AbstractSettingsPage {

    /**
     * @param container
     */
    public AnnotationOptionsPage(PreferencesContainer container) {
        super(container);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new AnnotationOptionsPanel(), BorderLayout.CENTER);
    }
}