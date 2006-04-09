package fr.unice.bioinfo.thea.classification.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.openide.ErrorManager;

import fr.unice.bioinfo.thea.api.ClassificationParser;
import fr.unice.bioinfo.thea.classification.Node;

/**
 * An utility class that provides a parser for files in the newick format.
 * 
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class NewickParser implements ClassificationParser {

    private int c;

    private BufferedReader buffer = null;

    private NewickParser() {
    }

    /** Returns a parser for files in the sota format. */
    public static ClassificationParser getDefault() {
        return (new NewickParser());
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.unice.bioinfo.thea.api.ClassificationParser#getRootNode(java.io.File)
     */
    public Object getRootNode(File file) throws FileNotFoundException {
        buffer = new BufferedReader(new FileReader(file));
        try {
            c = buffer.read();
            skipExtraChar(buffer);
            if (c != '(') {// NO18N
                throw new IOException();
            }
            Node aNode = new Node();
            List children = newick(buffer);
            if (children.size() == 1) {
                return (Node) children.get(0);
            }
            aNode.setChildren(newick(buffer));
            return aNode;
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
        }
        return null;
    }

    private List newick(BufferedReader buffer) throws IOException {
        List children = new LinkedList();
        String label = null;
        double branchLength = 0;
        Node aNode = null;
        while ((c != -1) && (c != ';')) {// NO18N
            skipExtraChar(buffer);
            if (c == '(') {// NO18N
                aNode = new Node();
                c = buffer.read();
                aNode.setChildren(newick(buffer));
            } else if (c == ')') {// NO18N
                children.add(initNodeOrNodeGroup(aNode, label, branchLength));
                c = buffer.read();
                return children;
            } else if (c == ',') {// NO18N
                children.add(initNodeOrNodeGroup(aNode, label, branchLength));
                aNode = new Node();
                c = buffer.read();
            } else {
                label = readLabel(buffer);
                String bl = readBranchLength(buffer).trim();
                if (!bl.equals("")) {// NOI18N
                    try {
                        branchLength = Double.parseDouble(bl);
                        if (branchLength <= 0) {
                            branchLength = 0;
                        }
                    } catch (NumberFormatException nfe) {
                        branchLength = 0;
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, nfe);
                    }
                }
            }
        }
        children.add(aNode);
        return children;
    }

    private Node initNodeOrNodeGroup(Node aNode, String label,
            double branchLength) {
        if (aNode == null) {
            aNode = new Node();
        }
        if (label == null) {
            aNode.setBranchLength(branchLength);
            return aNode;
        }
        if (label.indexOf('|') == -1) {// NOI18N
            // there is only one label in the string
            // aNode.setName(label);
            // aNode.setUserData("idInClassif", label);
            aNode.addProperty(Node.ID_IN_CLASSIF, label);
            aNode.setBranchLength(branchLength);
            return aNode;
        }
        List children = new LinkedList();
        // There is several labels in the string. decompose it
        StringTokenizer token = new StringTokenizer(label, "|");// NOI18N
        while (token.hasMoreTokens()) {
            String nodeIdent = token.nextToken();
            int index = nodeIdent.indexOf('=');// NOI18N
            if (index != -1) {
                // there is a branch length specified
                String nodeLabel = nodeIdent.substring(0, index).trim();
                String bl = nodeIdent.substring(index).trim();
                double bLength = 0;
                if (!bl.equals("")) {// NOI18N
                    try {
                        bLength = Double.parseDouble(bl);
                        if (bLength <= 0) {
                            bLength = 0;
                        }
                    } catch (NumberFormatException nfe) {
                        bLength = 0;
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, nfe);
                    }
                }
                Node aChild = new Node();
                // aChild.setName(nodeLabel);
                // aChild.setUserData("idInClassif", nodeLabel);
                aChild.addProperty(Node.ID_IN_CLASSIF, nodeLabel);
                aChild.setBranchLength(bLength);
                children.add(aChild);
            } else {
                Node aChild = new Node();
                // aChild.setName(nodeIdent);
                // aChild.setUserData("idInClassif", nodeIdent);
                aChild.addProperty(Node.ID_IN_CLASSIF, nodeIdent);
                children.add(aChild);
            }
        }
        aNode.setChildren(children);
        return aNode;
    }

    private String readLabel(BufferedReader in) throws IOException {
        String label = "";// NOI18N
        skipExtraChar(in);
        boolean quoted = false;
        boolean doubleQuoted = false;
        if (c == '\'') { // quoted label //NOI18N
            quoted = true;
            c = in.read();
        } else if (c == '"') { // double quoted label //NOI18N
            doubleQuoted = true;
            c = in.read();
        }

        while (c != -1) {
            if (!quoted && !doubleQuoted && ("\n\r\f()[]:;,".indexOf(c) != -1)) {// NOI18N
                break;
            }
            if ((c == '\'') && quoted) {// NO18N
                c = in.read();
                if (c != '\'') {// NO18N
                    break;
                }
                // two single quote is converted to single quote
            }
            if ((c == '"') && doubleQuoted) {// NO18N
                c = in.read();
                if (c != '"') {// NO18N
                    break;
                }
                // two double quotes is converted into one
            }
            if (" \t".indexOf(c) != -1) { // Blanks or tabs may appear
                // anywhere
                // //NO18N
            }
            label += (char) c;
            c = in.read();
        }
        return label;
    }

    private String readBranchLength(BufferedReader buffer) throws IOException {
        String branchLength = "";// NO18N
        skipExtraChar(buffer);
        if (c == -1) {
            return branchLength;
        }
        if (c != ':') {// NOI18N
            return branchLength;
        }
        c = buffer.read();
        skipExtraChar(buffer);
        while (c != -1) {
            if ("0123456789.e-".indexOf(c) == -1) {// NO18N
                break;
            }
            branchLength += (char) c;
            c = buffer.read();
        }
        return branchLength;
    }

    private void skipExtraChar(BufferedReader buffer) throws IOException {
        while (c != -1) {
            if (c == '[') {// NO18N
                c = buffer.read();
                while ((c != -1) && (c != ']'))
                    // NO18N
                    c = buffer.read();
                c = buffer.read();
            }
            if (" \t\n\r\f".indexOf(c) == -1) {// NO18N
                break;
            }
            c = buffer.read();
        }
        return;
    }

}