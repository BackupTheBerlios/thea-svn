package fr.unice.bioinfo.thea.editor.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.openide.ErrorManager;

import fr.unice.bioinfo.thea.api.ClassificationParser;
import fr.unice.bioinfo.thea.editor.Node;

/**
 * An utility class that defines a parser for files in the <i>sota </i> format.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class SotaParser implements ClassificationParser {

    private BufferedReader buffer = null;

    private SotaParser() {
    }

    /** Returns a parser for files in the sota format. */
    public static ClassificationParser getDefault() {
        return (new SotaParser());
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.api.ClassificationParser#getRootNode()
     */
    public Object getRootNode(File file) throws FileNotFoundException {
        buffer = new BufferedReader(new FileReader(file));
        String line = null;
        // ignore lines that begins with "node"
        try {
            line = buffer.readLine();
            while (!line.startsWith("node ")) {//NO18N
                line = buffer.readLine();
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            return null;
        }
        // when the read line doesn't begins with "node":
        Map map = new HashMap();
        while (line != null) {
            StringTokenizer token = new StringTokenizer(line, " ");//NOI18N
            try {
                String string = token.nextToken();
                if (string.equals("node")) {//NOI18N
                    String nodeId = token.nextToken();
                    Node aNode = (Node) map.get(nodeId);
                    if (aNode == null) {
                        aNode = new Node();
                        //                        aNode.setName("");//NOI18N
                        //                        aNode.setUserData("idInClassif", "");
                        aNode.addProperty(Node.ID_IN_CLASSIF, "");
                        map.put(nodeId, aNode);
                    }
                    try {
                        line = buffer.readLine();
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, ioe);
                        return null;
                    }

                    Node aNodeChild1 = null;
                    StringTokenizer aNodeChild1Token = new StringTokenizer(
                            line, " ");//NOI18N

                    try {
                        String sep1 = aNodeChild1Token.nextToken();
                        String sep2 = aNodeChild1Token.nextToken();
                        String aNodeChild1Name = aNodeChild1Token.nextToken();
                        String sep3 = aNodeChild1Token.nextToken();
                        String distance = aNodeChild1Token.nextToken();

                        if (!sep1.equals("Dist.") || !sep2.equals("to")//NOI18N
                                || !sep3.equals("=")) {//NOI18N
                            return null;
                        }

                        double branchLength = 0;

                        if (!distance.trim().equals("")) {//NOI18N
                            try {
                                branchLength = Double.parseDouble(distance);
                                if (branchLength <= 0) {
                                    branchLength = 0;
                                }
                            } catch (NumberFormatException nfe) {
                                branchLength = 0;
                                ErrorManager.getDefault().notify(
                                        ErrorManager.INFORMATIONAL, nfe);
                            }
                        }

                        aNodeChild1 = (Node) map.get(aNodeChild1Name);
                        if (aNodeChild1 == null) {
                            aNodeChild1 = new Node();
                            //                            aNodeChild1.setName("");//NOI18N
                            //                            aNodeChild1.setUserData("idInClassif", "");
                            aNodeChild1.addProperty(Node.ID_IN_CLASSIF, "");
                            map.put(aNodeChild1Name, aNodeChild1);
                        }
                        aNodeChild1.setBranchLength(branchLength);
                    } catch (NoSuchElementException e) {
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, e);
                    }
                    try {
                        line = buffer.readLine();
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, e);
                        return null;
                    }

                    Node aNodeChild2 = null;
                    aNodeChild1Token = new StringTokenizer(line, " ");//NOI18N

                    try {
                        String sep1 = aNodeChild1Token.nextToken();
                        String sep2 = aNodeChild1Token.nextToken();
                        String aNodeChild2Name = aNodeChild1Token.nextToken();
                        String sep3 = aNodeChild1Token.nextToken();
                        String distance = aNodeChild1Token.nextToken();

                        if (!sep1.equals("Dist.") || !sep2.equals("to")//NOI18N
                                || !sep3.equals("=")) {//NOI18N
                            return null;
                        }
                        double branchLength = 0;
                        if (!distance.trim().equals("")) {//NOI18N
                            try {
                                branchLength = Double.parseDouble(distance);
                                if (branchLength <= 0) {
                                    branchLength = 0;
                                }
                            } catch (NumberFormatException nfe) {
                                ErrorManager.getDefault().notify(
                                        ErrorManager.INFORMATIONAL, nfe);
                                branchLength = 0;
                            }
                        }
                        aNodeChild2 = (Node) map.get(aNodeChild2Name);
                        if (aNodeChild2 == null) {
                            aNodeChild2 = new Node();
                            //                            aNodeChild2.setName("");//NOI18N
                            //                            aNodeChild2.setUserData("idInClassif", "");
                            aNodeChild2.addProperty(Node.ID_IN_CLASSIF, "");
                            map.put(aNodeChild2Name, aNodeChild2);
                        }
                        aNodeChild2.setBranchLength(branchLength);
                    } catch (NoSuchElementException e) {
                        ErrorManager.getDefault().notify(
                                ErrorManager.INFORMATIONAL, e);
                    }

                    List children = new LinkedList();
                    children.add(aNodeChild1);
                    children.add(aNodeChild2);
                    aNode.setChildren(children);
                } else {
                    String sep1 = token.nextToken();
                    String sep2 = token.nextToken();
                    if (sep1.equals("goes") && sep2.equals("to")) {//NOI18N
                        // ok, this
                        // is a line
                        // describing
                        // a leaf
                        String nodeId = string;
                        String fatherId = token.nextToken();
                        Node aNode = (Node) map.get(fatherId);
                        if (aNode == null) {
                            return null;
                        }
                        Node aChild = new Node();
                        //                        aChild.setName(nodeId);
                        //                        aChild.setUserData("idInClassif", nodeId);
                        aChild.addProperty(Node.ID_IN_CLASSIF, nodeId);
                        map.put(nodeId, aChild);
                        List children = aNode.getChildren();
                        if (children == null) {
                            children = new LinkedList();
                        }
                        children.add(aChild);
                        aNode.setChildren(children);
                    }
                }
            } catch (NoSuchElementException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            try {
                line = buffer.readLine();
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                        ioe);
                return null;
            }
        }
        // locates and returns the root node.
        Node rootNode = (Node) map.values().toArray()[0];
        while (rootNode.getParent() != null) {
            rootNode = rootNode.getParent();
        }
        return rootNode;
    }

}