package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import java.awt.event.ActionEvent;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;


/**
 * @author SAÏD, EL KASMI.
 */
public class OntologyExplorerAction extends SystemAction {
    
    public OntologyExplorerAction(){
        super();
        setEnabled(false);
    }
    
    /* (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return NbBundle.getMessage(OntologyExplorerAction.class,
                "LBL_OntologyExplorerAction_Name"); // NOI18N;
    }
    
    /*
     * (non-Javadoc)
     * @see org.openide.util.HelpCtx.Provider#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        TopComponent tc = OntologyExplorer.getDefault();
        if (!tc.isOpened())
            tc.open();
        tc.requestActive();
    }
    
    /* (non-Javadoc)
     * @see org.openide.util.actions.SystemAction#iconResource()
     */
    protected String iconResource() {
        return super.iconResource();
    }
}