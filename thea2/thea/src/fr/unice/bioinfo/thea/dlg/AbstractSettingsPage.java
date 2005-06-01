package fr.unice.bioinfo.thea.dlg;

import javax.swing.JPanel;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi</a>
 */
public class AbstractSettingsPage extends JPanel {
    
    /** The title to display in title bar. */
    private String title;
    
    private PreferencesContainer container;
    
    public AbstractSettingsPage(PreferencesContainer container){
        super();
        this.container = container;
        }
    
    public PreferencesContainer getContainer() {
        return container;
    }
    public void setContainer(PreferencesContainer container) {
        this.container = container;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
