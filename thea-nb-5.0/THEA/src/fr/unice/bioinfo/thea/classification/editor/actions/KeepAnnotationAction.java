package fr.unice.bioinfo.thea.classification.editor.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import fr.unice.bioinfo.allonto.datamodel.AllontoException;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.DrawableClassification;
import fr.unice.bioinfo.thea.util.OWLProperties;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class KeepAnnotationAction extends GenericAction {

    private Node aNode;

    public KeepAnnotationAction(String name, String accelerator,
            ImageIcon icon, String shortDescription,
            DrawableClassification drawable, Node aNode) {
        // call super constructor
        super(name, accelerator, icon, shortDescription, drawable);
        this.aNode = aNode;

        if ((aNode.getLabel() != null) && (!aNode.getLabel().equals(""))// NOI18N
                && (aNode.getProperty(Node.ASSOC_TERM) != null)) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        List l = (List) aNode.getProperty(Node.USER_ANNOTATIONS);
        if (l == null) {
            l = new Vector();
        }
        List param = new Vector();
        Resource r = (Resource) aNode.getProperty(Node.ASSOC_TERM);
        ResourceFactory resourceFactory = (ResourceFactory) AllontoFactory
                .getResourceFactory();
        try {
            StringValue sv = (StringValue) r.getTarget(resourceFactory
                    .getResource(OWLProperties.getInstance()
                            .getNodeNameProperty()));
            if (sv != null) {
                param.add(sv.getValue());
            }
        } catch (AllontoException ae) {
        }
        param.add(aNode.getLayoutSupport());
        l.add(param);
        aNode.addProperty(Node.USER_ANNOTATIONS, l);
        aNode.setLabel("");// NOI18N
        drawable.updateGraphics();
    }

}