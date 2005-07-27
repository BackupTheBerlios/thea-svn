package fr.unice.bioinfo.thea.dlg;

import java.awt.BorderLayout;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ExpressionValuesSettingsPage extends AbstractSettingsPage {

    /**
     * @param container
     */
    public ExpressionValuesSettingsPage(PreferencesContainer container) {
        super(container);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new ExpressionValuesOptionsPanel(), BorderLayout.CENTER);
    }
}