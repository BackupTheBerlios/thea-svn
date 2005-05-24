package fr.unice.bioinfo.thea;

import java.util.Iterator;

import org.openide.modules.ModuleInstall;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

import fr.unice.bioinfo.thea.classification.editor.CEditor;
import fr.unice.bioinfo.thea.editor.selection.SelectionEditor;
import fr.unice.bioinfo.thea.ontologyexplorer.OntologyExplorer;
import fr.unice.bioinfo.thea.ontologyexplorer.db.DatabaseConnection;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologiesRootNode;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.OntologyNode;

/**
 * @author Saïd El Kasmi
 */
public class TheaModule extends ModuleInstall {
    // Serial Version UID for serialization
    static final long serialVersionUID = 5426465356344170725L;

    /** Called when all modules agreed with closing and the IDE will be closed */
    public void close() {
        final Node n = OntologyExplorer.findDefault().getExplorerManager()
                .getRootContext();
        //      close all opened connection
        Children.MUTEX.writeAccess(new Runnable() {
            public void run() {
                Node[] nodes = n.getChildren().getNodes();
                for (int cnt = 0; cnt < nodes.length; cnt++) {
                    // Get nodes that represents ontologies and close
                    // connections
                    if (nodes[cnt] instanceof OntologiesRootNode) {
                        Node[] ontologies = (nodes[cnt]).getChildren()
                                .getNodes();
                        for (int counter = 0; counter < ontologies.length; counter++) {
                            final OntologyNode on = (OntologyNode) ontologies[counter];
                            //                          If a connection to a database opened, close it.
                            if (on.isConnected()) {
                                RequestProcessor.getDefault().post(
                                        new Runnable() {
                                            public void run() {
                                                DatabaseConnection dbc = on
                                                        .getConnection();
                                                dbc.disconnect();
                                            }
                                        }, 0);
                            }

                        }
                    }
                }
            }

        });
        // Close opend TopComponents:
        Iterator opened = TopComponent.getRegistry().getOpened().iterator();
        while (opened.hasNext()) {
            Object tc = opened.next();
            if (tc instanceof CEditor) {
                CEditor e = (CEditor) tc;
                e.close();
            }
            if (tc instanceof SelectionEditor) {
                SelectionEditor se = (SelectionEditor) tc;
                se.close();
            }
        }

    }
}