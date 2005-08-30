package fr.unice.bioinfo.thea.ontologyexplorer.actions;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;

/**
 * The actions which shows the Ontology Explorer. It appears in Windows menu.
 * 
 * @author Saïd El Kasmi
 */
public class OntologyExplorerAction extends CallableSystemAction {
    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.CallableSystemAction#performAction()
     */
    public void performAction() {
        OntologyExplorer oe = OntologyExplorer.findDefault();
        Mode m = WindowManager.getDefault().findMode("explorer");

        if (m != null) {
            m.dockInto(oe);
        }

        oe.open();
        oe.requestActive();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getName()
     */
    public String getName() {
        return NbBundle.getBundle(OntologyExplorerAction.class).getString(
                "LBL_OntologyExplorerWindows_Name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#iconResource()
     */
    protected String iconResource() {
        return "fr/unice/bioinfo/thea/ontologyexplorer/resources/OntologyExplorer16.png";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.CallableSystemAction#asynchronous()
     */
    protected boolean asynchronous() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openide.util.actions.SystemAction#getHelpCtx()
     */
    public HelpCtx getHelpCtx() {
        // TODO Auto-generated method stub
        return HelpCtx.DEFAULT_HELP;
    }
}