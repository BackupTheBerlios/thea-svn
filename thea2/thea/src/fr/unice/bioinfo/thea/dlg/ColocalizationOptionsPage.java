package fr.unice.bioinfo.thea.dlg;

import java.awt.BorderLayout;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ColocalizationOptionsPage extends AbstractSettingsPage {

    /**
     * @param container
     */
    public ColocalizationOptionsPage(PreferencesContainer container) {
        super(container);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new ColocalizationOptionsPanel(), BorderLayout.CENTER);
    }
}