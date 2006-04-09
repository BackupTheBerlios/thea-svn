package fr.unice.bioinfo.thea.classification.editor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import fr.unice.bioinfo.thea.classification.Node;

public class NewickUtil {
    static int c;

    public Node load(File f) {
        try {
            Node root = parse(new BufferedReader(new FileReader(f)));

            return root;
        } catch (FileNotFoundException fnfe) {
            System.err.println("Could not load the file " + fnfe);
            System.exit(1);
        }

        return null;
    }

    public Node parse(BufferedReader in) {
        try {
            c = in.read();
            skipExtraChar(in);

            if (c != '(') {
                throw new IOException();
            }

            Node n = new Node();
            List childs = newick(in);

            if (childs.size() == 1) {
                return (Node) childs.get(0);
            }

            n.setChildren(newick(in));

            // System.err.print("final : "); n.toStringg();
            return n;
        } catch (IOException e) {
        }

        return null;
    }

    private List newick(BufferedReader in) throws IOException {
        List childs = new Vector();
        String label = null;
        double branchLength = 0;
        Node n = null;

        while ((c != -1) && (c != ';')) {
            skipExtraChar(in);

            if (c == '(') {
                n = new Node();
                c = in.read();
                n.setChildren(newick(in));

                // System.err.print("built : ");n.toStringg();
                // n.setName(readLabel(in));
                // readBranchLength(in);
                // childs.add(n);
                // n = new Node();
            } else if (c == ')') {
                childs.add(initNodeOrNodeGroup(n, label, branchLength));
                c = in.read();

                return childs;
            } else if (c == ',') {
                childs.add(initNodeOrNodeGroup(n, label, branchLength));
                n = new Node();
                c = in.read();
            } else {
                label = readLabel(in);

                String bl = readBranchLength(in).trim();

                if (!bl.equals("")) {
                    try {
                        branchLength = Double.parseDouble(bl);

                        if (branchLength <= 0) {
                            branchLength = 0;
                        }
                    } catch (NumberFormatException nfe) {
                        branchLength = 0;
                    }
                }
            }
        }

        childs.add(n);

        // System.err.println("childs="+childs);
        return childs;
    }

    private Node initNodeOrNodeGroup(Node n, String label, double branchLength) {
        if (n == null) {
            n = new Node();
        }

        if (label == null) {
            n.setBranchLength(branchLength);

            return n;
        }

        if (label.indexOf('|') == -1) {
            // there is only one label in the string
            n.setName(label);
            // n.setUserData("idInClassif", label);
            n.addProperty(Node.ID_IN_CLASSIF, label);
            n.setBranchLength(branchLength);

            return n;
        }

        List childs = new Vector();

        // There is several labels in the string. decompose it
        StringTokenizer st = new StringTokenizer(label, "|");

        while (st.hasMoreTokens()) {
            String nodeIdent = st.nextToken();
            int index = nodeIdent.indexOf('=');

            if (index != -1) {
                // there is a branch length specified
                String nodeLabel = nodeIdent.substring(0, index).trim();
                String bl = nodeIdent.substring(index).trim();
                double bLength = 0;

                if (!bl.equals("")) {
                    try {
                        bLength = Double.parseDouble(bl);

                        if (bLength <= 0) {
                            bLength = 0;
                        }
                    } catch (NumberFormatException nfe) {
                        bLength = 0;
                    }
                }

                Node childNode = new Node();
                childNode.setName(nodeLabel);
                // childNode.setUserData("idInClassif", nodeLabel);
                childNode.addProperty(Node.ID_IN_CLASSIF, nodeLabel);
                childNode.setBranchLength(bLength);
                childs.add(childNode);
            } else {
                Node childNode = new Node();
                childNode.setName(nodeIdent);
                // childNode.setUserData("idInClassif", nodeIdent);
                childNode.addProperty(Node.ID_IN_CLASSIF, nodeIdent);
                childs.add(childNode);
            }
        }

        n.setChildren(childs);

        return n;
    }

    private String readLabel(BufferedReader in) throws IOException {
        String label = "";
        skipExtraChar(in);

        boolean quoted = false;
        boolean doubleQuoted = false;

        if (c == '\'') { // quoted label
            quoted = true;
            c = in.read();
        } else if (c == '"') { // double quoted label
            doubleQuoted = true;
            c = in.read();
        }

        while (c != -1) {
            if (!quoted && !doubleQuoted && ("\n\r\f()[]:;,".indexOf(c) != -1)) {
                break;
            }

            if ((c == '\'') && quoted) {
                c = in.read();

                if (c != '\'') {
                    break;
                }

                // two single quote is converted to single quote
            }

            if ((c == '"') && doubleQuoted) {
                c = in.read();

                if (c != '"') {
                    break;
                }

                // two double quotes is converted into one
            }

            // if ((c == '_') && !quoted) { // Underscore characters in unquoted
            // labels are converted to blanks
            // c = ' ';
            // }
            if (" \t".indexOf(c) != -1) { // Blanks or tabs may appear
                // anywhere

                // in quoted labels
                // if (!quoted) break;
            }

            label += (char) c;
            c = in.read();
        }

        // System.err.println("Label read="+label);
        return label;
    }

    private String readBranchLength(BufferedReader in) throws IOException {
        String branchLength = "";
        skipExtraChar(in);

        if (c == -1) {
            return branchLength;
        }

        if (c != ':') {
            return branchLength;
        }

        c = in.read();
        skipExtraChar(in);

        while (c != -1) {
            if ("0123456789.e-".indexOf(c) == -1) {
                break;
            }

            branchLength += (char) c;
            c = in.read();
        }

        ;

        return branchLength;
    }

    private void skipExtraChar(BufferedReader in) throws IOException {
        while (c != -1) {
            if (c == '[') {
                c = in.read();

                while ((c != -1) && (c != ']'))
                    c = in.read();

                c = in.read();
            }

            if (" \t\n\r\f".indexOf(c) == -1) {
                break;
            }

            c = in.read();
        }

        ;

        return;
    }
}