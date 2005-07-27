package fr.unice.bioinfo.thea.classification.editor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import fr.unice.bioinfo.thea.classification.Node;

public class EisenUtil {
    public Node load(File gtrFile, File cdtFile) {
        try {
            return parse(new BufferedReader(new FileReader(cdtFile)),
                    new BufferedReader(new FileReader(gtrFile)));
        } catch (FileNotFoundException fnfe) {
            System.err.println("Could not load the file " + fnfe);
            System.exit(1);
        }

        return null;
    }

    public Node parse(BufferedReader cdtFile, BufferedReader gtrFile) {
        String cdtStmt = null;

        try {
            cdtStmt = cdtFile.readLine(); // ignore one line of header
            cdtStmt = cdtFile.readLine(); // ignore one line of header
            cdtStmt = cdtFile.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Unable to read a line on '.ctr' file");

            return null;
        }

        String gtrStmt = null;

        try {
            gtrStmt = gtrFile.readLine();
        } catch (IOException e) {
            System.err.println("Unable to read a line on '.gtr' file");

            return null;
        }

        Map geneId2GeneName = new HashMap();
        Map geneId2DbKey = new HashMap();
        Map geneId2Measures = new HashMap();

        while (cdtStmt != null) {
            while (cdtStmt.indexOf("\t\t") != -1) {
                cdtStmt = cdtStmt.replaceAll("\t\t", "\tNA\t");
            }

            if (cdtStmt.endsWith("\t")) {
                cdtStmt += "NA";
            }

            StringTokenizer st = new StringTokenizer(cdtStmt, "\t");

            try {
                String geneId = st.nextToken();
                String dbKey = st.nextToken();
                String geneName = st.nextToken();
                String weight = st.nextToken();
                Vector measures = new Vector();

                while (st.hasMoreTokens()) {
                    String measure = st.nextToken();

                    try {
                        Double val = new Double(measure);
                        measures.add(val);
                    } catch (NumberFormatException nfe) {
                        try {
                            NumberFormat nf = NumberFormat.getInstance();
                            Double val = (Double) nf.parse(measure);
                            measures.add(val);
                        } catch (ParseException pe) {
                            measures.add(new Double(0));
                        }
                    }
                }

                geneId2Measures.put(geneId, measures);

                geneId2GeneName.put(geneId, geneName);
                geneId2DbKey.put(geneId, dbKey);
            } catch (NoSuchElementException e) {
                // unexpected end of line found => no processing needed
            }

            try {
                cdtStmt = cdtFile.readLine();
            } catch (IOException e) {
                System.err.println("Unable to read a line on '.cdt' file");

                return null;
            }
        }

        Map name2Node = new HashMap();

        while (gtrStmt != null) {
            StringTokenizer st = new StringTokenizer(gtrStmt, "\t");

            try {
                String fatherName = st.nextToken();
                String child1Name = st.nextToken();
                String child2Name = st.nextToken();
                String simil = st.nextToken();
                double branchLength = 0;

                if (!simil.trim().equals("")) {
                    try {
                        branchLength = Double.parseDouble(simil);

                        if (branchLength <= 0) {
                            branchLength = 0;
                        }
                    } catch (NumberFormatException nfe) {
                        branchLength = 0;
                    }
                }

                Node fatherNode = (Node) name2Node.get(fatherName);

                if (fatherNode == null) {
                    fatherNode = new Node();

                    String name = (String) geneId2DbKey.get(fatherName);

                    if (name == null) {
                        name = "";
                    }

                    //                    fatherNode.setUserData("idInClassif", name);
                    fatherNode.addProperty(Node.ID_IN_CLASSIF, name);
                    fatherNode.setName(name);

                    String dbKey = (String) geneId2DbKey.get(fatherName);

                    if (dbKey != null) {
                        //                        fatherNode.setUserData("dbKey", dbKey);
                        fatherNode.addProperty(Node.DB_KEY, dbKey);
                        //                        fatherNode.setUserData("idInClassif", dbKey);
                        fatherNode.addProperty(Node.ID_IN_CLASSIF, dbKey);
                    }

                    //                    fatherNode.setUserData("measures", geneId2Measures
                    //                            .get(fatherName));
                    fatherNode.addProperty(Node.MEASURES, geneId2Measures
                            .get(fatherName));
                    name2Node.put(fatherName, fatherNode);
                }

                Node child1Node = (Node) name2Node.get(child1Name);

                if (child1Node == null) {
                    child1Node = new Node();

                    String name = (String) geneId2DbKey.get(child1Name);

                    if (name == null) {
                        name = "";
                    }

                    //                    child1Node.setUserData("idInClassif", name);
                    child1Node.addProperty(Node.ID_IN_CLASSIF, name);
                    child1Node.setName(name);

                    String dbKey = (String) geneId2DbKey.get(child1Name);

                    if (dbKey != null) {
                        //                        child1Node.setUserData("dbKey", dbKey);
                        child1Node.addProperty(Node.DB_KEY, dbKey);
                        //                        child1Node.setUserData("idInClassif", dbKey);
                        child1Node.addProperty(Node.ID_IN_CLASSIF, dbKey);
                    }

                    //                    child1Node.setUserData("measures", geneId2Measures
                    //                            .get(child1Name));
                    child1Node.addProperty(Node.MEASURES, geneId2Measures
                            .get(child1Name));
                    name2Node.put(child1Name, child1Node);
                }

                child1Node.setBranchLength(branchLength);

                Node child2Node = (Node) name2Node.get(child2Name);

                if (child2Node == null) {
                    child2Node = new Node();

                    String name = (String) geneId2DbKey.get(child2Name);

                    if (name == null) {
                        name = "";
                    }

                    //                    child2Node.setUserData("idInClassif", name);
                    child2Node.addProperty(Node.ID_IN_CLASSIF, name);
                    child2Node.setName(name);

                    String dbKey = (String) geneId2DbKey.get(child2Name);

                    if (dbKey != null) {
                        //                        child2Node.setUserData("dbKey", dbKey);
                        child2Node.addProperty(Node.DB_KEY, dbKey);
                        //                        child2Node.setUserData("idInClassif", dbKey);
                        child2Node.addProperty(Node.ID_IN_CLASSIF, dbKey);
                    }

                    //                    child2Node.setUserData("measures", geneId2Measures
                    //                            .get(child2Name));
                    child2Node.addProperty(Node.MEASURES, geneId2Measures
                            .get(child2Name));
                    name2Node.put(child2Name, child2Node);
                }

                child2Node.setBranchLength(branchLength);

                List childs = new Vector();
                childs.add(child1Node);
                childs.add(child2Node);
                fatherNode.setChildren(childs);
            } catch (NoSuchElementException e) {
                // unexpected end of line found => no more association to
                // establish
            }

            try {
                gtrStmt = gtrFile.readLine();
            } catch (IOException e) {
                System.err.println("Unable to read a line on '.gtr' file");

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