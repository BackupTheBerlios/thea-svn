package fr.unice.bioinfo.thea.classification.editor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import fr.unice.bioinfo.thea.classification.Node;

public class SotaUtil {
    public Node load(File sotFile) {
        try {
            return parse(new BufferedReader(new FileReader(sotFile)));
        } catch (FileNotFoundException fnfe) {
            System.err.println("Could not load the file " + fnfe);

            return null;
        }
    }

    public Node parse(BufferedReader sotaFile) {
        String sotaStmt = null;

        try {
            sotaStmt = sotaFile.readLine();

            while (!sotaStmt.startsWith("node ")) {
                sotaStmt = sotaFile.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Unable to read a line on 'sota' file");

            return null;
        }

        Map name2Node = new HashMap();

        while (sotaStmt != null) {
            StringTokenizer st = new StringTokenizer(sotaStmt, " ");

            try {
                String nodeString = st.nextToken();

                if (nodeString.equals("node")) {
                    String nodeId = st.nextToken();
                    Node fatherNode = (Node) name2Node.get(nodeId);

                    if (fatherNode == null) {
                        fatherNode = new Node();
                        fatherNode.setName("");
                        //                        fatherNode.setUserData("idInClassif", "");
                        fatherNode.addProperty(Node.ID_IN_CLASSIF, "");
                        name2Node.put(nodeId, fatherNode);
                    }

                    try {
                        sotaStmt = sotaFile.readLine();
                    } catch (IOException e) {
                        System.err
                                .println("Unable to read a line on 'sota' file");

                        return null;
                    }

                    Node child1Node = null;
                    StringTokenizer st2 = new StringTokenizer(sotaStmt, " ");

                    try {
                        String sep1 = st2.nextToken();
                        String sep2 = st2.nextToken();
                        String child1Name = st2.nextToken();
                        String sep3 = st2.nextToken();
                        String distance = st2.nextToken();

                        if (!sep1.equals("Dist.") || !sep2.equals("to")
                                || !sep3.equals("=")) {
                            System.err
                                    .println("Error while reading 'Dist.' line");

                            return null;
                        }

                        double branchLength = 0;

                        if (!distance.trim().equals("")) {
                            try {
                                branchLength = Double.parseDouble(distance);

                                if (branchLength <= 0) {
                                    branchLength = 0;
                                }
                            } catch (NumberFormatException nfe) {
                                branchLength = 0;
                            }
                        }

                        child1Node = (Node) name2Node.get(child1Name);

                        if (child1Node == null) {
                            child1Node = new Node();
                            child1Node.setName("");
                            //                            child1Node.setUserData("idInClassif", "");
                            child1Node.addProperty(Node.ID_IN_CLASSIF, "");
                            name2Node.put(child1Name, child1Node);
                        }

                        child1Node.setBranchLength(branchLength);
                    } catch (NoSuchElementException e) {
                        // unexpected end of line found => no more association
                        // to establish
                    }

                    try {
                        sotaStmt = sotaFile.readLine();
                    } catch (IOException e) {
                        System.err
                                .println("Unable to read a line on 'sota' file");

                        return null;
                    }

                    Node child2Node = null;
                    st2 = new StringTokenizer(sotaStmt, " ");

                    try {
                        String sep1 = st2.nextToken();
                        String sep2 = st2.nextToken();
                        String child2Name = st2.nextToken();
                        String sep3 = st2.nextToken();
                        String distance = st2.nextToken();

                        if (!sep1.equals("Dist.") || !sep2.equals("to")
                                || !sep3.equals("=")) {
                            System.err
                                    .println("Error while reading 'Dist.' line");

                            return null;
                        }

                        double branchLength = 0;

                        if (!distance.trim().equals("")) {
                            try {
                                branchLength = Double.parseDouble(distance);

                                if (branchLength <= 0) {
                                    branchLength = 0;
                                }
                            } catch (NumberFormatException nfe) {
                                branchLength = 0;
                            }
                        }

                        child2Node = (Node) name2Node.get(child2Name);

                        if (child2Node == null) {
                            child2Node = new Node();
                            child2Node.setName("");
                            //                            child2Node.setUserData("idInClassif", "");
                            child2Node.addProperty(Node.ID_IN_CLASSIF, "");
                            name2Node.put(child2Name, child2Node);
                        }

                        child2Node.setBranchLength(branchLength);
                    } catch (NoSuchElementException e) {
                        // unexpected end of line found => no more association
                        // to establish
                    }

                    List childs = new Vector();
                    childs.add(child1Node);
                    childs.add(child2Node);
                    fatherNode.setChildren(childs);
                } else {
                    String sep1 = st.nextToken();
                    String sep2 = st.nextToken();

                    if (sep1.equals("goes") && sep2.equals("to")) { // ok, this
                        // is a line
                        // describing
                        // a leaf

                        String nodeId = nodeString;
                        String fatherId = st.nextToken();
                        Node fatherNode = (Node) name2Node.get(fatherId);

                        if (fatherNode == null) {
                            System.err.println("Error. node '" + fatherId
                                    + "' not retrieved");

                            return null;
                        }

                        Node childNode = new Node();

                        //						String nodeName = (String)geneId2DbKey.get(nodeId);
                        childNode.setName(nodeId);
                        //                        childNode.setUserData("idInClassif", nodeId);
                        childNode.addProperty(Node.ID_IN_CLASSIF, nodeId);

                        //						childNode.setUserData("dbKey", nodeName);
                        name2Node.put(nodeId, childNode);

                        List childs = fatherNode.getChildren();

                        if (childs == null) {
                            childs = new Vector();
                        }

                        childs.add(childNode);
                        fatherNode.setChildren(childs);
                    }
                }
            } catch (NoSuchElementException e) {
            }

            try {
                sotaStmt = sotaFile.readLine();
            } catch (IOException e) {
                System.err.println("Unable to read a line on 'sota' file");

                return null;
            }
        }

        // identifies the root from a random node
        Node root = (Node) name2Node.values().toArray()[0];

        while (root.getParent() != null) {
            root = root.getParent();
        }

        return root;
    }
}