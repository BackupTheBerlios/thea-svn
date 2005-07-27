package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerManager.Provider;
import org.openide.explorer.view.BeanTreeView;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.RootNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class TermChooserTreeView extends JPanel implements Provider {

    private JTree tree;
    private JScrollPane jsp;

    private List scores;
    private Resource selectedTerm;

    /**
     * The widget that allow this component visualize the ontology as a tree.
     * @see org.openide.explorer.view.BeanTreeView
     */
    private BeanTreeView beanTreeView;

    /** An expolorer manager to associate the bean tree view */
    private ExplorerManager explorerManager;

    public TermChooserTreeView(List scores) {
        this.scores = scores;
        init();
    }

    private void init() {
        //      Instanciate an explorer manager
        explorerManager = new ExplorerManager();

        // Create the root node
        RootNode rn = new RootNode();

        //        // Add the root node for all ontologies
        //        rn.getChildren().add(new Node[] { new OntologiesRootNode() });
        //
        //        // Add the root node for all classifications
        //        rn.getChildren().add(new Node[] { new ClassificationsRootNode() });

        explorerManager.setRootContext(rn);

        // Build and add th BeanTreeView widget
        beanTreeView = new BeanTreeView();

        // Make the root node hidden
        beanTreeView.setRootVisible(true);
        setLayout(new BorderLayout());
        add(beanTreeView, BorderLayout.CENTER);
    }

    /*
     * (non-Javadoc)
     * @see org.openide.explorer.ExplorerManager.Provider#getExplorerManager()
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    /** Returns the selected term. */
    public Resource getSelectedTerm() {
        return selectedTerm;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new TermChooserTreeView(null));
        f.pack();
        f.setVisible(true);
    }
}