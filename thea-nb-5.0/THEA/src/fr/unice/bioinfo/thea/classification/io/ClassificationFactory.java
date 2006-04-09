package fr.unice.bioinfo.thea.classification.io;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import fr.unice.bioinfo.thea.classification.Classification;
import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.editor.util.EisenUtil;
import fr.unice.bioinfo.thea.classification.editor.util.MeasuresFileReader;
import fr.unice.bioinfo.thea.classification.editor.util.NewickUtil;
import fr.unice.bioinfo.thea.classification.editor.util.SotaUtil;
import fr.unice.bioinfo.thea.classification.io.wizard.SupportedFormat;
import fr.unice.bioinfo.thea.ontologyexplorer.infos.ClassificationNodeInfo;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ClassificationFactory {

    public static ClassificationFactory instance = null;

    private ClassificationFactory() {
    }

    public static ClassificationFactory getDefault() {
        if (instance == null) {
            instance = new ClassificationFactory();
        }
        return instance;
    }

    public Classification getClassification(ClassificationNodeInfo cni) {
        Node rootNode = null;
        File cf = cni.getCFile();
        File tf = cni.getTFile();
        String type = cni.getSelectedFormat();
        int indexOfFirstIgnoredRow = cni.getIndexOfFirstIgnoredRow();
        int indexOfLastIgnoredRow = cni.getIndexOfLastIgnoredRow();
        int indexOfFirstIgnoredColumn = cni.getIndexOfFirstIgnoredColumn();
        int indexOfLastIgnoredColumn = cni.getIndexOfLastIgnoredColumn();
        int indexOfGeneColumn = cni.getIndexOfGeneColumn();
        int indexOfTitleRow = cni.getIndexOfTitleRow();
        int nbColumns = cni.getNbColumns();

        long mem = 0;
        long t = 0;

        if (type.equalsIgnoreCase(SupportedFormat.NEWICK)) {
            // load new hampshire (newick) format
            Runtime.getRuntime().gc();
            mem = Runtime.getRuntime().totalMemory()
                    - Runtime.getRuntime().freeMemory();
            rootNode = new NewickUtil().load(cf);
            Runtime.getRuntime().gc();
            if (rootNode == null) {
                return null;
            }

            if (tf != null) {
                Map geneId2Measures = new MeasuresFileReader().load(tf,
                        indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
                        indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
                        indexOfGeneColumn, indexOfTitleRow, nbColumns);
                Iterator it = rootNode.getLeaves().iterator();

                while (it.hasNext()) {
                    Node leaf = (Node) it.next();
                    leaf.addProperty(Node.MEASURES, geneId2Measures.get(leaf
                            .getProperty(Node.ID_IN_CLASSIF)));
                }
            }

            rootNode.init();
        } else if (type.equalsIgnoreCase(SupportedFormat.EISEN)) {
            // cluster format
            rootNode = new EisenUtil().load(cf, tf);
            rootNode.init();
        } else if (type.equalsIgnoreCase(SupportedFormat.SOTA)) {
            // sota format
            rootNode = new SotaUtil().load(cf);
            if (rootNode == null) {
                return null;
            }
            if (tf != null) {
                Map geneId2Measures = new MeasuresFileReader().load(tf,
                        indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
                        indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
                        indexOfGeneColumn, indexOfTitleRow, nbColumns);
                Iterator it = rootNode.getLeaves().iterator();
                while (it.hasNext()) {
                    Node leaf = (Node) it.next();
                    // leaf.setUserData("measures", geneId2Measures.get(leaf
                    // .getUserData("idInClassif")));
                    leaf.addProperty(Node.MEASURES, geneId2Measures.get(leaf
                            .getProperty(Node.ID_IN_CLASSIF)));
                }
            }

            rootNode.init();
        } else if (type.equalsIgnoreCase(SupportedFormat.UNCLUSTERED)) {
            Map geneId2Measures = new MeasuresFileReader().load(tf,
                    indexOfFirstIgnoredRow, indexOfLastIgnoredRow,
                    indexOfFirstIgnoredColumn, indexOfLastIgnoredColumn,
                    indexOfGeneColumn, indexOfTitleRow, nbColumns);
            List nodes = new Vector();
            Iterator it = geneId2Measures.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String idInClassif = (String) entry.getKey();

                if ("*TITLE*".equals(idInClassif)) {
                    continue;
                }

                Node node = new Node();
                node.setName(idInClassif);
                // node.setUserData("idInClassif", idInClassif);
                node.addProperty(Node.ID_IN_CLASSIF, idInClassif);
                // node.setUserData("measures", entry.getValue());
                node.addProperty(Node.MEASURES, entry.getValue());
                node.setBranchLength(0);
                nodes.add(node);
            }

            rootNode = new Node();
            rootNode.setName("");
            // rootNode.setUserData("idInClassif", "");
            rootNode.addProperty(Node.ID_IN_CLASSIF, "");
            rootNode.setChildren(nodes);
            rootNode.init();
        }
        Runtime.getRuntime().gc();
        return new Classification(rootNode);
    }
}