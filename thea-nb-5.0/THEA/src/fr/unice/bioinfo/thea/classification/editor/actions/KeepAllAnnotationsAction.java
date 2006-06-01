package fr.unice.bioinfo.thea.classification.editor.actions;

import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class KeepAllAnnotationsAction extends GenericAction {

    private Node aNode;

    public KeepAllAnnotationsAction(String name, String accelerator,
            ImageIcon icon, String shortDescription,
            DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Collection collection = drawable.getClassificationRootNode()
                .getAllChildNodes();
        Iterator collectionIt = collection.iterator();
        while (collectionIt.hasNext()) {
            Node nextNode = (Node) collectionIt.next();
            if ((nextNode.getLabel() != null)
                    && (!nextNode.getLabel().equals("")) && !nextNode.isLeaf()) {
                List nodeAnnots = (List) nextNode
                        .getProperty(Node.USER_ANNOTATIONS);
                if (nodeAnnots == null) {
                    nodeAnnots = new Vector();
                }

                List param = new Vector();
                Resource r = (Resource) aNode.getProperty(Node.ASSOC_TERM);
                try {
                    param.add(r.getTarget(OWLProperties.getInstance()
                                .getNodeNameProperty()));
                } catch (AllontoException ex) {
                    ex.printStackTrace();
                }
//                param.add(r.getDisplayName()); replaced by the following line
                param.add(null);
                param.add(nextNode.getLayoutSupport());
                nodeAnnots.add(param);
                nextNode.addProperty(Node.USER_ANNOTATIONS, nodeAnnots);
                nextNode.setLabel("");// NOI18N
            }
        }
        drawable.updateGraphics();
    }

}