package fr.unice.bioinfo.thea.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import org.openide.util.Utilities;

import fr.unice.bioinfo.thea.editor.selection.SelectionEvent;
import fr.unice.bioinfo.thea.editor.selection.SelectionListener;
import fr.unice.bioinfo.thea.editor.settings.CESettings;
import fr.unice.bioinfo.thea.editor.util.Consts;
import fr.unice.bioinfo.thea.editor.util.Discretization;

/**
 * A Swing GUI component for displaying a {@link Node}object and its sub-nodes
 * in a tree layout.
 * <p>
 * The component is used by {@link CEditor}component. It first looks for an
 * instance of {@link CESettings}to initialize some attributes from stored
 * settings and then registers itself as a <i>PropertyChangeListener </i> to
 * listen to users preferences.
 * @author Claude Pasquier.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class CECanvas extends JComponent implements PropertyChangeListener {

    private FontRenderContext context;

    /** the image used to show that a branch is collapsed */
    private Image expandImage = null;

    /** The root node of the currently displayed tree. */
    private Node currentRootNode = null;

    /** contains the list of all root nodes in the classification. */
    private Stack roots = new Stack();

    /** Size of a branch in percentage of the remaining width */
    private double branchSize = 0.1;

    private double baseBranchLength = -1;

    /** The minimum amount of space between tho adjacent branchs */
    private double branchsMinimumSep = 1;

    /** The node currently highlighted */
    private Node hnode = null;

    /**
     * When moving the mouse over currentRootNode nodes, they are highlighted to
     * make the user have an idea about the node under mose selection. Three
     * modes are possible: <br>
     * <ul>
     * <li>hmode = 1 : Show a rectangle around the whole subtree of the node
     * under selection.
     * <li>hmode = 2 : Show a frame around the subtree.
     * <li>hmode = 3 : Show sub-tree's currentRootNode (parent).
     * </ul>
     */
    private int hmode;

    /** Popup menu. */
    private JPopupMenu popupMenu = null;

    /** Flag to indicate if branchs have to be drawn according to their length. */
    private boolean showBranchLength;

    /** A flag to indicate if expression values has to be shown. */
    private boolean showExpressionValues;

    /** Flag to indicate if terminal nodes should be aligned. */
    private boolean alignTerminalNodes;

    /** The maximum width on the screen used by terminal labels */
    //private double labelMaxWidth;
    /** The standard height of a label compted from the layout of the letter 'X' */
    private double labelHeight = -1;

    /** The size of the component with a zoom factor of (1,1) */
    private double currentZoomX = 1;

    private double currentZoomY = 1;

    /** True if terminal labels are surrounded by a frame */
    private boolean terminalBoxed;

    /** True if non terminal labels are surrounded by a frame */
    private boolean nonTerminalBoxed;

    /** The list of selected nodes */
    private Set selectedNodes = new HashSet();

    /** The set of collapsed nodes */
    private Set collapsedNodes = new HashSet();

    /** The list of successive selection lists */
    private List selectionsList = new Vector();

    /** The map between nodes and their detailed states */
    private Map detailedNodes = new HashMap();

    /** The map between nodes and their localisation or not into the windows area */
    private Map clippedNodes = new HashMap();

    /** The map between nodes and their layout */
    private Map nodeToLayout = new HashMap();

    /** The number of selections done */
    private int selectionsCounter = 0;

    /** The name of the selection */
    private String selectionName;

    /** data relative to expression values */
    private int expValNbMeasures = 0;

    private int expValColumnWidth = 0;

    private List expValUnderExpDeciles;

    private List expValOverExpDeciles = null;

    private double expValMinMeasure = 0;

    private double expValMaxMeasure = 0;

    /** the list of selection s */
    protected Set treeSelectionListeners;

    /** The mouse adapter */
    private TreeViewMouseAdapter tvma;

    /** A list to contain references on widgets that listen to selections. */
    /* It contains objects that implement SelectionListener interface */
    private ArrayList svl;

    /** Creates a JComponent to draw into it. */
    public CECanvas() {
        super();
        setBackground(Color.WHITE);
        treeSelectionListeners = new HashSet();
        // Get the CESettings instance
        CESettings settings = CESettings.getInstance();
        // Initialize from settings:
        hmode = settings.getHighlightingMode();
        showBranchLength = settings.isShowBranchLength();
        alignTerminalNodes = settings.isAlignTerminalNodes();
        showExpressionValues = settings.isShowExpressionValues();
        terminalBoxed = settings.isTerminalsBoxed();
        nonTerminalBoxed = settings.isNonTerminalsBoxed();
        // Collapse Icon
        expandImage = Utilities
                .loadImage("fr/unice/bioinfo/thea/editor/resources/expand.gif");//NOI18N
        // Register this as a listener to changes in that instance
        settings.addPropertyChangeListener(this);
        // Initialize the list that contains selections' listeners
        svl = new ArrayList();
    }

    /** Adds a new {@link SelectionListener}object. */
    public void addSelectionListener(SelectionListener sl) {
        svl.add(sl);
    }

    /** Removes a {@link SelectionListener}object. */
    public void removeSelectionListener(SelectionListener sl) {
        svl.remove(sl);
    }

    /**
     * Fire a {@link SelectionEvent}to make listeners know that a selection is
     * done.
     */
    private void fireSelectionDone(SelectionEvent e) {
        Iterator r = svl.iterator();
        while (r.hasNext()) {
            ((SelectionListener) r.next()).selectionDone(e);
        }
    }

    /**
     * Fire a {@link SelectionEvent}to make listeners know that a selection is
     * deleted.
     */
    private void fireSelectionCleared(SelectionEvent e) {
        Iterator r = svl.iterator();
        while (r.hasNext()) {
            ((SelectionListener) r.next()).selectionCleared(e);
        }
    }

    /** Adds a listener for TreeSelection events. */
    public void addTreeSelectionListener(TreeSelectionListener l) {
        treeSelectionListeners.add(l);
    }

    /** Removes a TreeSelection listener */
    public void removeTreeSelectionListener(TreeSelectionListener l) {
        treeSelectionListeners.remove(l);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        this.context = ((Graphics2D) g).getFontRenderContext();
        draw(g);
        super.paint(g);
    }

    private void draw(Graphics g) {
        if (currentRootNode == null) {
            return;
        }
        TextLayout tl = new TextLayout("X", Consts.TERMINAL_FONT, context);
        labelHeight = tl.getBounds().getHeight();
        double treeWidth = getWidth();
        double selWidth = getWidth();
        double height = getHeight();
        if (selectionsList != null) {
            treeWidth -= (selectionsList.size() * 10);
        }
        if (showExpressionValues) {
            Integer nbMeasuresI = (Integer) getTreeRoot().getUserData(
                    "nbMeasures");
            if ((nbMeasuresI != null) && (nbMeasuresI.intValue() > 0)) {
                expValNbMeasures = nbMeasuresI.intValue();
                expValColumnWidth = (int) treeWidth / 5 / expValNbMeasures;
                if (expValColumnWidth < 1) {
                    expValColumnWidth = 1;
                }
                if (expValColumnWidth > 20) {
                    expValColumnWidth = 20;
                }
                treeWidth -= (expValNbMeasures * expValColumnWidth);
                selWidth -= (expValNbMeasures * expValColumnWidth);
            }
        }
        if (baseBranchLength == -1) {
            baseBranchLength = computeBaseBranchLength(getCurrentRootNode(),
                    treeWidth - 25, (Graphics2D) g, Consts.TERMINAL_FONT);
        }
        detailedNodes.clear();
        clippedNodes.clear();
        drawNode(getCurrentRootNode(), (Graphics2D) g, 15, 5, treeWidth - 30,
                height - 10);
        getCurrentRootNode().setArea(
                new Rectangle2D.Double(0, 5, treeWidth - 15, height - 10));
        //displayInfo((Graphics2D) g, frc);
        if (selectionsList != null) {
            Iterator it = selectionsList.iterator();
            int ctr = 0;
            while (it.hasNext()) {
                NodeSet selectionList = (NodeSet) it.next();
                displaySelected((Graphics2D) g, selectionList, selWidth - 15
                        - (ctr++ * 10), 8);
            }
        }
        if (showExpressionValues && (expValNbMeasures > 0)) {
            drawExpressions((Graphics2D) g, getCurrentRootNode()
                    .getVisibleLeaves(), selWidth);
        }
    }

    /**
     * Display various data about the displayed tree
     * @param g The graphics context
     */
    private void displayInfo(Graphics2D g) {
        TextLayout tl = new TextLayout(getCurrentRootNode().getNumberOfLeaves()
                + " leaves", Consts.INFO_FONT, context);
        tl.draw(g, 0, 20);
        tl = new TextLayout("depth=" + getCurrentRootNode().getDepth(),
                Consts.INFO_FONT, context);
        tl.draw(g, 0, 40);
    }

    /**
     * Draw a mark relative of the tree to show the position of selected nodes
     * @param g The graphics context
     * @param ns The list of selected nodes
     * @param color The color to use to fill the area
     * @param x The horizontal position where to draw selected marks
     * @param width The width of the marks
     */
    private void displaySelected(Graphics2D g, NodeSet ns, double x,
            double width) {
        if (ns == null) {
            return;
        }
        if (ns.getNodes() == null) {
            return;
        }
        if (ns.getNodes().isEmpty()) {
            return;
        }
        Color color = (Color) ns.getUserData("color");
        Color backgroundColor = (Color) ns.getUserData("bgColor");
        Color c = getBackground();
        if (backgroundColor != null) {
            g.setColor(backgroundColor);
            Rectangle2D.Double rootArea = (Rectangle2D.Double) getTreeRoot()
                    .getArea();
            if (rootArea != null) {
                g.fill(new Rectangle2D.Double(Math.rint(x), Math
                        .rint(rootArea.y), Math.rint(width), Math
                        .rint(rootArea.height)));
            }
        }
        g.setColor(color);
        Iterator it = ns.getNodes().iterator();
        while (it.hasNext()) {
            Node aNode = (Node) it.next();
            if (!getCurrentRootNode().isAncestorOf(aNode)) {
                continue;
            }
            if (!isNodeClipped(aNode)) {
                continue;
            }
            Rectangle2D.Double aNodeArea = (Rectangle2D.Double) aNode.getArea();
            if (isNodeDetailed(aNode)) {
                do {
                    aNode = aNode.getParent();
                } while ((aNode != null) && isNodeDetailed(aNode));
                if (aNode != null) {
                    aNodeArea = (Rectangle2D.Double) aNode.getArea();
                }
                if (aNodeArea != null) {
                    g.fill(new Rectangle2D.Double(Math.rint(x), Math
                            .rint(aNodeArea.y), Math.rint(width), 1));
                }
            } else if (aNode.isHidden()) {
                do {
                    aNode = aNode.getParent();
                } while ((aNode != null) && !aNode.isCollapsed()
                        && aNode.isHidden());

                if (aNode != null) {
                    aNodeArea = (Rectangle2D.Double) aNode.getArea();
                }
                if (aNodeArea != null) {
                    Polygon poly = new Polygon();
                    poly.addPoint((int) x,
                            (int) (aNodeArea.y + (aNodeArea.height / 2)));
                    poly
                            .addPoint(
                                    (int) (x + width),
                                    (int) ((aNodeArea.y + (aNodeArea.height / 2)) - (width / 2)));
                    poly.addPoint((int) (x + width), (int) (aNodeArea.y
                            + (aNodeArea.height / 2) + (width / 2)));
                    g.fill(poly);
                }
            } else {
                g.fill(new Rectangle2D.Double(Math.rint(x), Math
                        .rint(aNodeArea.y), Math.rint(width), Math.rint(Math
                        .max(aNodeArea.height, 1))));
            }
        }
        g.setColor(c);
    }

    /**
     * Draw a colored representation of expression values
     * @param g The graphics context
     * @param leaves The list of leaves in the displayed tree
     * @param x The horizontal position where to display expression values
     */
    private void drawExpressions(Graphics2D g, List leaves, double x) {
        if (leaves == null) {
            return;
        }
        if (leaves.isEmpty()) {
            return;
        }
        Iterator it = leaves.iterator();
        Color c = getBackground();
        double posy = 0;
        while (it.hasNext()) {
            Node aNode = (Node) it.next();
            if (!getCurrentRootNode().isAncestorOf(aNode)
                    && !(getCurrentRootNode() == aNode)) {
                continue;
            }
            if (!isNodeClipped(aNode)) {
                continue;
            }
            Rectangle2D.Double area = (Rectangle2D.Double) aNode.getArea();
            Node knownNode = aNode;
            while (isNodeDetailed(knownNode)) {
                knownNode = knownNode.getParent();
                area = (Rectangle2D.Double) knownNode.getArea();
            }
            posy = area.y;
            List measures = null;
            measures = (List) knownNode.getUserData("measures");
            if (measures == null) {
                measures = new Vector();
                List listSub = knownNode.getLeaves();
                measures.addAll((Collection) ((Node) listSub.get(0))
                        .getUserData("measures"));
                for (int leafCtr = 1; leafCtr < listSub.size(); leafCtr++) {
                    Node leaf = (Node) listSub.get(leafCtr);
                    List leafMeasure = (List) leaf.getUserData("measures");
                    for (int i = 0; i < leafMeasure.size(); i++) {
                        double measure = ((Double) leafMeasure.get(i))
                                .doubleValue();
                        double sum = ((Double) measures.get(i)).doubleValue();
                        measures.set(i, new Double(measure + sum));
                    }
                }
                for (int i = 0; i < measures.size(); i++) {
                    double sum = ((Double) measures.get(i)).doubleValue();
                    measures.set(i, new Double(sum / listSub.size()));
                }
                System.err.println();
                knownNode.setUserData("measures", measures);
            }
            Iterator iter = measures.iterator();
            int ctr = 0;
            while (iter.hasNext()) {
                double measure = ((Double) iter.next()).doubleValue();
                Color col = Discretization.getColor(measure);
                g.setColor(col);
                g.fill(new Rectangle2D.Double(x + (ctr * expValColumnWidth),
                        area.y, expValColumnWidth, Math.max(area.height, 1)));
                ctr++;
            }
        }
        g.setColor(c);
    }

    /**
     * Draw the node aNode and all its subnodes in the rectangular area defined
     * by (x, y, width, height)
     * @param aNode The node to be displayed
     * @param g The graphics context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     * @return the vertical position of the horizontal bar
     */
    private double drawNode(Node aNode, Graphics2D g, double x, double y,
            double width, double height) {
        Color c = g.getColor();
        if (aNode == hnode) {
            highlightNode(aNode, g);
        }
        aNode.setArea(new Rectangle2D.Double(x, y, width, height));
        if (!g.hitClip((int) x, (int) y, (int) width + 1, (int) height + 1)) {
            detailNode(aNode, true);
            clipNode(aNode, false);
            return y + (height / 2);
        }
        if (aNode.isTerminal()) {
            drawTerminalNode(aNode, g, x, y, width, height);

            return y + (height / 2);
        }
        double branchLength = baseBranchLength;
        if (showBranchLength) {
            branchLength *= aNode.getBranchLength();
        }
        if (aNode == currentRootNode) {
            branchLength = 0;
        }
        List children = aNode.getChildren();
        Iterator it = children.iterator();
        int leaves = aNode.getTerminals();
        double posy = y;
        double miny = Double.MAX_VALUE;
        double maxy = Double.MIN_VALUE;
        boolean noDetail = false;
        while (it.hasNext()) {
            Node aChild = (Node) it.next();
            if (noDetail == true) {
                detailNode(aChild, true);
                continue;
            }
            int childLeaves = aChild.getTerminals();
            double childHeight = (double) childLeaves / (double) leaves;
            double newy = height * childHeight;
            double centery = drawNode(aChild, g, x + branchLength, posy, width
                    - branchLength, newy);
            if (centery < miny) {
                miny = centery;
            }
            if (centery > maxy) {
                maxy = centery;
            }
            posy += newy;
            if (height < branchsMinimumSep) {
                noDetail = true; // no need to draw other branches
            }
        }
        Node aNodeParent = aNode.getParent();
        if (isSelected(aNode.getParent())) {
            g.setColor(Consts.SELECTED_BACKGROUND);
        } else {
            g.setColor(Color.black);
        }
        g.draw(new Line2D.Double(Math.rint(x), Math.rint((miny + maxy) / 2),
                Math.rint(x + branchLength), Math.rint((miny + maxy) / 2)));
        if (isSelected(aNode)) {
            g.setColor(Consts.SELECTED_BACKGROUND);
        } else {
            g.setColor(Color.black);
        }
        g.draw(new Line2D.Double(Math.rint(x + branchLength), Math.rint(miny),
                Math.rint(x + branchLength), Math.rint(maxy)));
        aNode.setPosition(new Point2D.Double(x + branchLength,
                (miny + maxy) / 2));

        // the maximum height available to write the label
        double maxLabelHeight = Math.min((y + (height / 2)) - miny, maxy - y
                - (height / 2));
        // the maximum width available to write the label
        drawNonTerminalNode(aNode, g, x + (branchLength * 0.2), (Math
                .rint((miny + maxy) / 2))
                - (maxLabelHeight / 2), width, /* maxLabelWidth */
        maxLabelHeight);
        g.setColor(c);
        return (miny + maxy) / 2;
    }

    private void highlightNode(Node aNode, Graphics2D g) {
        Point2D.Double aNodePosition = (Point2D.Double) aNode.getPosition();
        Rectangle2D.Double aNodeArea = (Rectangle2D.Double) aNode.getArea();
        Rectangle2D.Double aNodeRectangle = new Rectangle2D.Double();
        if (aNodeArea != null) {
            aNodeRectangle.setRect(aNodeArea);
        }
        if (aNode != null) {
            aNodeRectangle.width += 10;
            if (selectionsList != null) {
                aNodeRectangle.width += (selectionsList.size() * 10);
            }
        }
        switch (hmode) {
        case 1:
            g.setColor(new Color(255, 255, 223));
            if (aNode != null) {
                ((Graphics2D) g).fill(aNodeRectangle);
            }
            break;
        case 2:
            g.setColor(new Color(0, 0, 255));
            if (aNode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(aNodeRectangle);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) g).draw(rect);
            }
            break;
        case 3:
            g.setColor(new Color(0, 0, 255));
            if (aNode != null) {
                ((Graphics2D) g).fill(new Ellipse2D.Double(aNodePosition.x
                        - labelHeight, aNodePosition.y - labelHeight,
                        labelHeight * 2, labelHeight * 2));
            }
            break;
        }
    }

    /**
     * draws the label of a non terminal node.
     * @param aNode The node to be displayed
     * @param g The graphics context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     */
    private void drawNonTerminalNode(Node aNode, Graphics2D g, double x,
            double y, double width, double height) {
        List annotations = (List) aNode.getUserData("userAnnotations");
        if (annotations == null) {
            annotations = new Vector();
        } else {
            annotations = new Vector(annotations);
        }
        boolean lastAnnotIsCurrentLabel = false;
        String label = aNode.getLabel();
        if (!label.equals("")) {//NOI18N
            List param = new Vector();
            param.add(label);
            param.add(getLayout(aNode));
            annotations.add(param);
            lastAnnotIsCurrentLabel = true;
        }
        if (annotations.isEmpty()) {
            return; // nothing to display
        }
        Color c = g.getColor();
        List layouts = new Vector();
        List layoutsAttr = new Vector();
        double layHeight = 0;
        double maxWidth = 0;
        Iterator iter = annotations.iterator();
        while (iter.hasNext()) {
            List param = (List) iter.next();
            String annot = (String) param.get(0);
            NodeLayoutSupport layoutAttr = (NodeLayoutSupport) param.get(1);
            Font font = null;
            if (layoutAttr != null) {
                font = layoutAttr.getFont();
            }
            if (font == null) {
                font = Consts.NON_TERMINAL_FONT;
            }
            TextLayout tl = new TextLayout(annot, font, context);
            double labelNormalWidth = tl.getBounds().getWidth();
            while ((labelNormalWidth > width) && (annot.length() > 0)) {
                annot = annot.substring(0, annot.length() - 1);
                tl = new TextLayout(annot, font, context);
                labelNormalWidth = tl.getBounds().getWidth();
            }
            layouts.add(tl);
            layoutsAttr.add(layoutAttr);
            layHeight += (tl.getAscent() + tl.getDescent() + tl.getLeading());
            maxWidth = Math.max(maxWidth, tl.getAdvance());
        }
        int nbLines = layouts.size();
        double currentY = (y + (height / 2)) - ((layHeight) / 2);
        Rectangle2D area = new Rectangle2D.Double(Math.rint(x), Math
                .rint(currentY), Math.rint(maxWidth), Math.rint(layHeight));
        for (int i = 0; i < nbLines; i++) {
            TextLayout tl = (TextLayout) layouts.get(i);
            NodeLayoutSupport layoutAttr = (NodeLayoutSupport) layoutsAttr
                    .get(i);
            Color backColor = null;
            if (layoutAttr != null) {
                backColor = layoutAttr.getBgColor();
            }
            if (backColor == null) {
                backColor = Consts.NON_TERMINAL_BACKGROUND;
            }
            Color fontColor = null;

            if (layoutAttr != null) {
                fontColor = layoutAttr.getColor();
            }
            if (fontColor == null) {
                fontColor = Consts.NON_TERMINAL_COLOR;
            }
            g.setColor((backColor == null) ? g.getBackground() : backColor);
            Rectangle2D lineArea = new Rectangle2D.Double(Math.rint(x), Math
                    .rint(currentY), Math.rint(maxWidth), Math.rint(tl
                    .getAscent()
                    + tl.getDescent() + tl.getLeading()));
            g.fill(lineArea);
            if ((i == (nbLines - 1)) && lastAnnotIsCurrentLabel) {
                g.setColor(Color.black);
                g.draw(lineArea);
            }
            g.setColor(fontColor);
            currentY += (tl.getAscent());
            tl.draw(g, (float) (Math.rint(x)), (float) Math.rint(currentY));
            currentY += (tl.getDescent() + tl.getLeading());
        }
        if (nonTerminalBoxed) {
            g.setColor(Color.black);
            g.draw(area);
        }
        g.setColor(c);
    }

    /**
     * draws the label of a terminal node.
     * @param aNode The node to be displayed
     * @param g The graphics context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     */
    private void drawTerminalNode(Node aNode, Graphics2D g, double x, double y,
            double width, double height) {
        boolean boxed = terminalBoxed;
        List annotations = (List) aNode.getUserData("userAnnotations");
        if (annotations == null) {
            annotations = new Vector();
        } else {
            annotations = new Vector(annotations);
            boxed = true;
        }
        boolean lastAnnotIsCurrentLabel = false;
        String label = aNode.getLabel();
        if (!label.equals("")) {
            if (annotations.size() > 0) {
                lastAnnotIsCurrentLabel = true;
            }
            List param = new Vector();
            param.add(label);
            param.add(getLayout(aNode));
            annotations.add(param);
        }
        if (annotations.isEmpty() && aNode.isCollapsed()) {
            List param = new Vector();
            param.add("[" + aNode.getLeaves().size() + "]");

            //boxed = true;
            param.add(getLayout(aNode));
            annotations.add(param);
        }
        if (annotations.isEmpty()) {
            return; // nothing to display
        }
        Color c = g.getColor();
        List layouts = new Vector();
        List layoutsAttr = new Vector();
        double layHeight = 0;
        double maxWidth = 0;
        Iterator iterator = annotations.iterator();
        while (iterator.hasNext()) {
            List param = (List) iterator.next();
            String annot = (String) param.get(0);
            NodeLayoutSupport layoutAttr = (NodeLayoutSupport) param.get(1);
            Font font = null;
            if (layoutAttr != null) {
                font = layoutAttr.getFont();
            }
            if (font == null) {
                font = Consts.TERMINAL_FONT;
            }
            TextLayout tl = new TextLayout(annot, font, context);
            double labelNormalWidth = tl.getBounds().getWidth();
            while ((labelNormalWidth > width) && (annot.length() > 0)) {
                annot = annot.substring(0, annot.length() - 1);
                tl = new TextLayout(annot, font, context);
                labelNormalWidth = tl.getBounds().getWidth();
            }
            layouts.add(tl);
            layoutsAttr.add(layoutAttr);
            layHeight += (tl.getAscent() + tl.getDescent() + tl.getLeading());
            maxWidth = Math.max(maxWidth, tl.getAdvance());
        }
        int nbLines = layouts.size();
        double currentY = (y + (height / 2)) - ((layHeight) / 2);
        double branchLengthFactor = 1;
        if (showBranchLength) {
            branchLengthFactor = aNode.getBranchLength();
        }
        Rectangle2D area = null;
        maxWidth += expandImage.getWidth(null);
        if (alignTerminalNodes) {
            area = new Rectangle2D.Double(Math.rint(((x + width) - maxWidth)),
                    Math.rint(currentY), Math.rint(maxWidth), Math
                            .rint(layHeight));
        } else {
            area = new Rectangle2D.Double(Math.rint(x
                    + (baseBranchLength * (double) branchLengthFactor)), Math
                    .rint(currentY), Math.rint(maxWidth), Math.rint(layHeight));
        }
        if (nbLines > 1) {
            boxed = true;
        }
        for (int i = 0; i < nbLines; i++) {
            TextLayout layout = (TextLayout) layouts.get(i);
            NodeLayoutSupport layoutAttr = (NodeLayoutSupport) layoutsAttr
                    .get(i);
            Color backColor = null;
            if (layoutAttr != null) {
                backColor = layoutAttr.getBgColor();
            }
            if (backColor == null) {
                backColor = Consts.TERMINAL_BACKGROUND;
            }
            Color fontColor = null;

            if (layoutAttr != null) {
                fontColor = layoutAttr.getColor();
            }
            if (fontColor == null) {
                fontColor = Consts.TERMINAL_COLOR;
            }
            g.setColor((backColor == null) ? g.getBackground() : backColor);

            Rectangle2D lineArea = null;

            if (alignTerminalNodes) {
                lineArea = new Rectangle2D.Double(Math
                        .rint(((x + width) - maxWidth)), Math.rint(currentY),
                        Math.rint(maxWidth), Math.rint(layout.getAscent()
                                + layout.getDescent() + layout.getLeading()));
            } else {
                lineArea = new Rectangle2D.Double(Math.rint(x
                        + (baseBranchLength * (double) branchLengthFactor)),
                        Math.rint(currentY), Math.rint(maxWidth), Math
                                .rint(layout.getAscent() + layout.getDescent()
                                        + layout.getLeading()));
            }
            if (backColor != null) {
                g.setColor(backColor);
                g.fill(lineArea);
            }

            if ((i == (nbLines - 1)) && lastAnnotIsCurrentLabel) {
                g.setColor(Color.black);
                g.draw(lineArea);
            }
            g.setColor(fontColor);
            currentY += (layout.getAscent());
            if (alignTerminalNodes) {
                layout.draw(g, (float) (Math.rint((x + width) - maxWidth)),
                        (float) (float) Math.rint(currentY));
            } else {
                layout.draw(g, (float) (Math.rint(x
                        + (baseBranchLength * (double) branchLengthFactor))),
                        (float) (float) Math.rint(currentY));
            }
            currentY += (layout.getDescent() + layout.getLeading());
        }
        if (boxed) {
            g.setColor(Color.black);
            g.draw(area);
        }
        if (isSelected(aNode)) {
            g.setColor(Consts.SELECTED_BACKGROUND);
            g.draw(new Line2D.Double(area.getMaxX()
                    - expandImage.getWidth(null), area.getMaxY(), area
                    .getMaxX()
                    - expandImage.getWidth(null), area.getMinY()));
        } else {
            g.setColor(Color.black);
        }
        if (alignTerminalNodes) {
            g.draw(new Line2D.Double(Math.rint(x), Math.rint(y + (height / 2)),
                    Math.rint((x + width) - area.getWidth()), Math.rint(y
                            + (height / 2))));
            aNode.setPosition(new Point2D.Double(Math.rint((x + width)
                    - area.getWidth()), Math.rint(y + (height / 2))));
        } else {
            g
                    .draw(new Line2D.Double(
                            Math.rint(x),
                            Math.rint(y + (height / 2)),
                            Math
                                    .rint(x
                                            + (baseBranchLength * (double) branchLengthFactor)),
                            Math.rint(y + (height / 2))));
            aNode.setPosition(new Point2D.Double(Math.rint(x
                    + (baseBranchLength * (double) branchLengthFactor)), Math
                    .rint(y + (height / 2))));
        }
        if (aNode.isCollapsed()) {
            g.drawImage(expandImage, (int) (area.getMaxX() - expandImage
                    .getWidth(null)), (int) ((area.getMinY() + (area
                    .getHeight() / 2)) - (expandImage.getHeight(null) / 2)),
                    null);
        }
        g.setColor(c);
    }

    public void activate(boolean b) {
        if (b) {
            tvma = new TreeViewMouseAdapter();
            this.addMouseListener(tvma);
            this.addMouseMotionListener(tvma);
        } else {
            this.removeMouseListener(tvma);
            this.removeMouseMotionListener(tvma);
        }
    }

    public double computeBaseBranchLength(Node aNode, double width,
            Graphics2D g, Font font) {
        if (aNode.isTerminal()) {
            double depth = aNode.getDistanceToAncestor(currentRootNode,
                    showBranchLength);
            if (depth == 0) {
                return Double.MAX_VALUE;
            }
            String label = aNode.getLabel();
            if (label.equals("")) {
                return width / depth;
            }
            TextLayout tl = new TextLayout(label, font, context);
            Rectangle2D bounds = tl.getBounds();
            return (width - bounds.getWidth()) / depth;
        }
        double baseBranchLength = Double.MAX_VALUE;
        List children = aNode.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Node aChild = (Node) it.next();
            baseBranchLength = Math.min(computeBaseBranchLength(aChild, width,
                    g, font), baseBranchLength);
        }
        return baseBranchLength;
    }

    /**
     * Sets the given node to be used as a root node for the currently displayed
     * tree.
     */
    public void setCurrentRootNode(Node aNode) {
        currentRootNode = aNode;
        expValMinMeasure = 0;
        expValMaxMeasure = 0;
        expValUnderExpDeciles = null;
        expValOverExpDeciles = null;
        Object o = getTreeRoot().getUserData("nbMeasures");
        int nbMeasures = ((Integer) o).intValue();
        if (nbMeasures > 0) {
            expValMinMeasure = ((Double) getTreeRoot()
                    .getUserData("minMeasure")).doubleValue();
            expValMaxMeasure = ((Double) getTreeRoot()
                    .getUserData("maxMeasure")).doubleValue();
            expValUnderExpDeciles = (List) getTreeRoot().getUserData(
                    "underExpDeciles");
            expValOverExpDeciles = (List) getTreeRoot().getUserData(
                    "overExpDeciles");
        }
        if (selectionsList != null) {
            Iterator it = selectionsList.iterator();
            while (it.hasNext()) {
                int countSelected = 0;
                NodeSet ns = (NodeSet) it.next();
                Iterator it2 = ns.getNodes().iterator();
                while (it2.hasNext()) {
                    Node node = (Node) it2.next();
                    if (currentRootNode.isAncestorOf(node)) {
                        countSelected++;
                    }
                }
                String localHits = countSelected + "/"
                        + currentRootNode.getNumberOfLeaves();
                String selId = (String) ns.getUserData("selId");
                SelectionEvent e = new SelectionEvent(this, selId, null, null,
                        null, localHits, null);
                fireSelectionDone(e);
            }
        }
        if (!selectedNodes.isEmpty()) {
            List selectedLeaves = getSelectedLeaves();
            int countSelected = 0;
            Iterator it = selectedLeaves.iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();
                if (currentRootNode.isAncestorOf(node)) {
                    countSelected++;
                }
            }
            String localHits = countSelected + "/"
                    + currentRootNode.getNumberOfLeaves();
            String selId = String.valueOf(selectionsCounter);
            SelectionEvent e = new SelectionEvent(this, selId, null, null,
                    null, localHits, null);
            fireSelectionDone(e);
        }
        baseBranchLength = -1; // force the recalculation
    }

    /** Returns the root node of the currently displayed tree. */
    public Node getCurrentRootNode() {
        return currentRootNode;
    }

    /** Sets the list of root nodes of the used classification. */
    public void setRoots(Stack roots) {
        this.roots = roots;
    }

    /** Returns all root nodes used in the classification. */
    public Stack getRoots() {
        return roots;
    }

    /** Push a new currentRootNode */
    public void pushRoot(Node aNode) {
        roots.push(getCurrentRootNode());
        setCurrentRootNode(aNode);
        zoom(1, 1);
        update();
    }

    /** Pop a currentRootNode */
    public void popRoot() {
        Node aNode = (Node) roots.pop();
        setCurrentRootNode(aNode);
        zoom(1, 1);
        update();
    }

    /** Pop all roots */
    public void popAllRoot() {
        Node aNode = null;
        while (!roots.empty()) {
            aNode = (Node) roots.pop();
        }
        setCurrentRootNode(aNode);
        zoom(1, 1);
        update();
    }

    /** Sets a pop-up menu for user's intercations. */
    public void setPopupMenu(JPopupMenu pop) {
        popupMenu = pop;
    }

    /** Returns the root node of the whole tree. */
    public Node getTreeRoot() {
        if (roots.isEmpty()) {
            return currentRootNode;
        } else {
            return (Node) roots.elementAt(0);
        }
    }

    /** Adds the given node to the list of collapsed nodes. */
    public void collapseNode(Node aNode) {
        collapsedNodes.add(aNode);
        zoom(1, 1);
        update();
    }

    /** Removes the given node from the list of collapsed nodes. */
    public void uncollapseNode(Node aNode) {
        collapsedNodes.remove(aNode);
        zoom(1, 1);
        update();
    }

    public void setCollapsedNodes(Set s) {
        collapsedNodes = s;
    }

    /**
     * Sets all nodes as non selected
     */
    private void clearSelected() {
        String selId = String.valueOf(selectionsCounter);
        selectedNodes.clear();
        selectionName = null;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selName) {
        selectionName = selName;
    }

    /**
     * Sets the selection state of the nodes in the list to b
     * @param nodes the new list of selected nodes
     * @param state Indicates the new selected state of the node (if state
     *        <0:unselect, if state=0:invert, is state>0:select)
     * @param selName The name of the selection
     */
    private void setSelected(Collection nodes, int state, String selName) {
        setSelectionName(selName);
        setSelected(nodes, state);
    }

    /**
     * Sets the selection state of the nodes in the list to b
     * @param nodes the new list of selected nodes
     * @param state Indicates the new selected state of the node (if state
     *        <0:unselect, if state=0:invert, is state>0:select)
     */
    private void setSelected(Collection nodes, int state) {
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node n = (Node) it.next();
            setSelectedNodeAndChilds(n, state);
            // modify the selected state of the parent if necessary
            updateSelectedState(n.getParent());
        }
        sendSelectionEvent();
        // repaint the whole panel
        repaint();
    }

    /**
     * Set the selected state of a node ant its child nodes
     * @param n The node to update
     * @param state Indicates the new selected state of the node (if state
     *        <0:unselect, if state=0:invert, is state>0:select)
     */
    public void setSelected(Node aNode, int state) {
        setSelectedNodeAndChilds(aNode, state);
        // modify the selected state of the parent if necessary
        updateSelectedState(aNode.getParent());
        sendSelectionEvent();
        // repaint the area
        Rectangle2D.Double rectInNode = (Rectangle2D.Double) aNode.getArea();
        if (rectInNode != null) {
            repaint((int) rectInNode.x, (int) rectInNode.y,
                    (int) rectInNode.width, (int) rectInNode.height);
        }
    }

    /**
     * Send a selection event
     */
    private void sendSelectionEvent() {
        Node localRoot = getCurrentRootNode();
        String selId = String.valueOf(selectionsCounter);
        String globalHits = getSelectedLeaves(true).size() + "/"
                + getTreeRoot().getNumberOfLeaves();
        String localHits = getSelectedLeaves(false).size() + "/"
                + localRoot.getNumberOfLeaves();
        SelectionEvent e = new SelectionEvent(this, selId, selectionName,
                "Current", Consts.SELECTED_COLOR, Consts.SELECTED_BACKGROUND,
                globalHits, localHits, getSelectedLeaves(true));
        fireSelectionDone(e);
    }

    /**
     * Set the selected state of a node ant its child nodes
     * @param aNode The node to update
     * @param state Indicates the new selected state of the node (if state
     *        <0:unselect, if state=0:invert, is state>0:select)
     */
    private void setSelectedNodeAndChilds(Node aNode, int state) {
        if (state < 0) {
            selectedNodes.remove(aNode);
        } else if (state > 0) {
            selectedNodes.add(aNode);
        } else { // state == 0
            if (isSelected(aNode)) {
                selectedNodes.remove(aNode);
            } else {
                selectedNodes.add(aNode);
            }
        }
        if (aNode.isLeaf()) {
            return;
        }
        Iterator iterator = aNode.getChildren().iterator();
        while (iterator.hasNext()) {
            Node aChild = (Node) iterator.next();
            setSelectedNodeAndChilds(aChild, state);
        }
    }

    /**
     * Update the selected state of the node
     */
    private void updateSelectedState(Node aNode) {
        if (aNode == null) {
            return;
        }
        if (aNode.isLeaf()) {
            return; // to be sure
        }
        boolean nodeIsSelected = isSelected(aNode);
        boolean allChildsInSelectedState = true;
        Iterator iterator = aNode.getChildren().iterator();
        while (iterator.hasNext()) {
            Node aChild = (Node) iterator.next();
            if (!isSelected(aChild)) {
                allChildsInSelectedState = false;
                break;
            }
        }
        if (nodeIsSelected != allChildsInSelectedState) {
            // update needed
            if (allChildsInSelectedState) {
                selectedNodes.add(aNode);
            } else {
                selectedNodes.remove(aNode);
            }
            // repaint the area
            Rectangle2D.Double aNodeArea = (Rectangle2D.Double) aNode.getArea();
            if (aNodeArea != null) {
                repaint((int) aNodeArea.x, (int) aNodeArea.y,
                        (int) aNodeArea.width, (int) aNodeArea.height);
            }
            // modify the selected state of the parent if necessary
            updateSelectedState(aNode.getParent());
        }
    }

    /**
     * Get the selected state of a node
     * @param n The node to get the value
     * @return The selected state of the parameter node
     */
    private boolean isSelected(Node n) {
        return selectedNodes.contains(n);
    }

    public void moveSelectionToCurrent(NodeSet ns) {
        clearSelected();
        setSelected(ns.getNodes(), 1, (String) ns.getUserData("selName"));
        removeSelection(ns);
        update();
    }

    public void copySelectionToCurrent(NodeSet ns) {
        clearSelected();
        setSelected(ns.getNodes(), 1, (String) ns.getUserData("selName"));
        update();
    }

    public void unionSelectionWithCurrent(NodeSet ns) {
        String newSelectionName = (String) ns.getUserData("selName");
        if (selectionName.startsWith("Manual")) {
            // use selName for the name of the union
        } else {
            if (newSelectionName.startsWith("Manual")) {
                newSelectionName = selectionName;
            } else {
                newSelectionName = selectionName + " OR " + newSelectionName;
            }
        }
        setSelected(ns.getNodes(), 1, newSelectionName);
        update();
    }

    public void intersectSelectionWithCurrent(NodeSet ns) {
        String newSelectionName = (String) ns.getUserData("selName");
        if (selectionName.startsWith("Manual")) {
            // use selName for the name of the union
        } else {
            if (newSelectionName.startsWith("Manual")) {
                newSelectionName = selectionName;
            } else {
                newSelectionName = selectionName + " AND " + newSelectionName;
            }
        }
        Collection currentSel = getSelectedLeaves(true);
        currentSel.retainAll(ns.getNodes());
        clearSelected();
        setSelected(currentSel, 1, newSelectionName);
        update();
    }

    /**
     * Returns a collection of all selected nodes
     * @return the collection of selected nodes
     */
    public Collection getSelected() {
        return selectedNodes;
    }

    /**
     * Returns a list of selected leaves in the displayed tree
     * @return the list of selected leaves
     */
    public List getSelectedLeaves() {
        return getSelectedLeaves(false);
    }

    /**
     * Returns a list of selected leaves
     * @param wholeTreeSeach indicates if the function must return the list of
     *        selected leaves in the whole tree
     * @return the list of selected leaves
     */
    private List getSelectedLeaves(boolean wholeTreeSearch) {
        Node searchSubtree = (wholeTreeSearch ? getTreeRoot()
                : getCurrentRootNode());
        List selectedLeaves = new Vector();
        Iterator iterator = searchSubtree.getLeaves().iterator();
        while (iterator.hasNext()) {
            Node n = (Node) iterator.next();
            if (isSelected(n)) {
                selectedLeaves.add(n);
            }
        }
        return selectedLeaves;
    }

    /**
     * Setter for the list of selections
     * @param collection The list of selections
     */
    public void setSelectionsList(Collection collection) {
        if (collection == null) {
            selectionsList = null;
        } else {
            selectionsList = new Vector(collection);
        }
    }

    /**
     * Setter for the list of selections
     * @return the list of selections
     */
    public List getSelectionsList() {
        return selectionsList;
    }

    /**
     * Find a node from a position
     * @param aNode The node to search
     * @param point2D A position from which a corresponding node is to be
     *        searched
     * @return The corresponding node
     */
    public Node findNode(Node aNode, Point2D point2D) {
        Point2D position = (Point2D) aNode.getPosition();
        if (position == null) {
            return null;
        }
        if (position.distance(point2D) < 5) {
            return aNode;
        }
        if (aNode.isCollapsed()) {
            return null;
        }
        if (aNode.isLeaf()) {
            return null;
        }
        Iterator iterator = aNode.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = findNode((Node) iterator.next(), point2D);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    /**
     * Find a node from a position using rectangular areas
     * @param aNode The node to locate.
     * @param position Starting point.
     * @return The corresponding node.
     */
    private Node locateNode(Node aNode, Point2D position) {
        Rectangle2D aNodeArea = (Rectangle2D) aNode.getArea();
        if (aNodeArea == null) {
            return null;
        }
        if (!aNodeArea.contains(position)) {
            return null;
        }
        if (aNode.isCollapsed()) {
            return aNode;
        }
        List children = aNode.getChildren();
        if (children == null) {
            return aNode;
        }
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            Node node = locateNode((Node) iterator.next(), position);
            if (node != null) {
                return node;
            }
        }
        return aNode;
    }

    /** Sets the detailed state of the given node and its children. */
    public void detailNode(Node aNode, boolean detail) {
        detailedNodes.put(aNode, new Boolean(detail));
        if (aNode.isLeaf()) {
            return;
        }
        Iterator iterator = aNode.getChildren().iterator();
        while (iterator.hasNext()) {
            detailNode((Node) iterator.next(), detail);
        }
    }

    /**
     * Gets the layout attributes of a node
     * @param node The node for which attributes are requested
     */
    public NodeLayoutSupport getLayout(Node node) {
        return (NodeLayoutSupport) nodeToLayout.get(node);
    }

    public void setNodeToLayout(Map m) {
        nodeToLayout = m;
    }

    /**
     * Indicates wether the given node isn't paint due to a lack of space on the
     * display area.
     */
    private boolean isNodeDetailed(Node aNode) {
        Object o = detailedNodes.get(aNode);
        if (o == null) {
            return false;
        }
        if (o.equals(Boolean.TRUE)) {
            return true;
        } else {
            return false;
        }
    }

    /** Highlights the area surrounding the given node. */
    private void highlightSurroundingArea(Node aNode) {
        if (hnode == aNode) {
            return;
        }
        Graphics g = getGraphics();
        Rectangle2D.Double hnodeArea = null;
        Point2D.Double hnodePosition = null;

        if (hnode != null) {
            hnodeArea = (Rectangle2D.Double) hnode.getArea();
            hnodePosition = (Point2D.Double) hnode.getPosition();
        }

        Rectangle2D.Double hnodeRectangle = new Rectangle2D.Double();
        if (hnodeArea != null) {
            hnodeRectangle.setRect(hnodeArea);
        }
        Rectangle2D.Double aNodeArea = null;
        Point2D.Double aNodePosition = null;
        if (aNode != null) {
            aNodeArea = (Rectangle2D.Double) aNode.getArea();
            aNodePosition = (Point2D.Double) aNode.getPosition();
        }

        Rectangle2D.Double aNodeRectangle = new Rectangle2D.Double();
        if (aNodeArea != null) {
            aNodeRectangle.setRect(aNodeArea);
        }
        if (hnode != null) {
            hnodeRectangle.width += 10;
            if (selectionsList != null) {
                hnodeRectangle.width += (selectionsList.size() * 10);
            }
        }
        if (aNode != null) {
            aNodeRectangle.width += 10;

            if (selectionsList != null) {
                aNodeRectangle.width += (selectionsList.size() * 10);
            }
        }
        switch (hmode) {
        case 1:
            g.setXORMode(new Color(0, 0, 32));
            if (hnode != null) {
                ((Graphics2D) g).fill(hnodeRectangle);
            }
            if (aNode != null) {
                ((Graphics2D) g).fill(aNodeRectangle);
            }
            break;
        case 2:
            g.setXORMode(new Color(255, 255, 0));
            if (hnode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(hnodeRectangle);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) g).draw(rect);
            }
            if (aNode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(aNodeRectangle);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) g).draw(rect);
            }
            break;
        case 3:
            g.setXORMode(new Color(255, 255, 0));

            if (hnode != null) {
                ((Graphics2D) g).fill(new Ellipse2D.Double(hnodePosition.x
                        - labelHeight, hnodePosition.y - labelHeight,
                        labelHeight * 2, labelHeight * 2));
            }
            if (aNode != null) {
                ((Graphics2D) g).fill(new Ellipse2D.Double(aNodePosition.x
                        - labelHeight, aNodePosition.y - labelHeight,
                        labelHeight * 2, labelHeight * 2));
            }
            break;
        }
        hnode = aNode;
        g.setPaintMode();
    }

    /** Indicates wether the given node is in the clipping area. */
    private boolean isNodeClipped(Node aNode) {
        Object o = clippedNodes.get(aNode);
        if (o == null) {
            return true;
        }
        if (o.equals(Boolean.TRUE)) {
            return true;
        } else {
            return false;
        }
    }

    /** Adds the given node to the list of clipped nodes. */
    private void clipNode(Node aNode, boolean b) {
        clippedNodes.put(aNode, new Boolean(b));
        if (aNode.isLeaf()) {
            return;
        }
        Iterator iterator = aNode.getChildren().iterator();
        while (iterator.hasNext()) {
            clipNode((Node) iterator.next(), b);
        }
    }

    /**
     * Sets the currentRootNode node to be the parent of the current
     * currentRootNode
     */
    public void levelUp() {
        if (roots.empty()) {
            return;
        }
        Node parentNode = currentRootNode.getParent();
        if (parentNode == null) {
            return;
        }
        if (parentNode == roots.peek()) {
            roots.pop();
        }
        setCurrentRootNode(parentNode);
        zoom(1, 1);
        update();
    }

    /**
     * Memorize the last selection
     */
    public void keepSelection() {
        if (selectedNodes == null) {
            return;
        }
        NodeSet selectionToKeep = new NodeSet(getSelectedLeaves(true));
        String selID = String.valueOf(selectionsCounter);
        selectionToKeep.setUserData("color",
                Consts.SELECTIONS_COLOR[selectionsCounter % 18]);
        selectionToKeep.setUserData("selId", selID);
        selectionToKeep.setUserData("selName", selectionName);
        SelectionEvent e = new SelectionEvent(this, selID, null,
                (Color) selectionToKeep.getUserData("color"), null, null, null);
        // fire event
        fireSelectionDone(e);
        selectionsList.add(selectionToKeep);
        selectionsCounter++;
        clearSelected();
        update();
    }

    public void removeSelection(NodeSet sel) {
        String selId = (String) sel.getUserData("selId");
        SelectionEvent e = new SelectionEvent(this, selId);
        fireSelectionCleared(e);
        if (selectionsList.contains(sel)) {
            selectionsList.remove(sel);
        }
        update();
    }

    public void groupSelection(NodeSet sel) {
        List nodesToGroup = new Vector(sel.getNodes());
        while (!nodesToGroup.isEmpty()) {
            Node node = (Node) nodesToGroup.get(0);
            Boolean frozen = (Boolean) node.getUserData("frozen");
            if ((frozen == null) || frozen.equals(Boolean.FALSE)) {
                Node parentNode = node.getParent();
                List parentChilds = parentNode.getChildren();
                for (int i = 0; i < parentChilds.size(); i++) {
                    frozen = (Boolean) ((Node) parentChilds.get(i))
                            .getUserData("frozen");
                    if ((frozen == null) || frozen.equals(Boolean.FALSE)) {
                        parentNode.moveChild(node, i);
                        break;
                    }
                }
            }
            nodesToGroup.remove(node);
        }
        update();
    }

    /**
     * Zomm over the displat area.
     * @param zoomx The hirozontal zoom factor.
     * @param zoomy The vertical zoom factor.
     */
    public void zoom(double zoomx, double zoomy) {
        highlightSurroundingArea(null);
        Dimension currentSize = getSize();
        int width = (int) currentSize.getWidth();
        int height = (int) currentSize.getHeight();
        if ((zoomx == 1) && (getVisibleRect().width < width)) {
            width = getVisibleRect().width;
        }
        if ((zoomy == 1) && (getVisibleRect().height < height)) {
            height = getVisibleRect().height;
        }
        Dimension maxSize = getMaximumSize();
        double zoomXFactor = 1;
        double zoomYFactor = 1;
        Dimension newSize = new Dimension(
                (int) (((double) width * zoomx * zoomXFactor) / currentZoomX),
                (int) ((height * zoomy * zoomYFactor) / currentZoomY));
        if (newSize.width > maxSize.width) {
            newSize.width = maxSize.width;
        }
        if (newSize.height > maxSize.height) {
            newSize.height = maxSize.height;
        }
        setPreferredSize(newSize);
        if (currentZoomX != zoomx) {
            baseBranchLength = -1; // to force the recalculation
        }
        currentZoomX = zoomx;
        currentZoomY = zoomy;
        revalidate();
    }

    /**
     * A class dealing with mouse inputs
     */
    class TreeViewMouseAdapter extends MouseInputAdapter {
        /**
         * mouse lisener
         * @param e The received mouse event
         */
        public void mousePressed(MouseEvent e) {
            Point2D p = (Point2D) e.getPoint();
            Node aNode = locateNode(getCurrentRootNode(), p);
            if (aNode == null) {
                // tries to find if the mouse is on the selection area
                int nbSelections = ((selectionsList == null) ? 0
                        : selectionsList.size());
                int width = getWidth() - 15;
                if (showExpressionValues) {
                    width -= (expValNbMeasures * expValColumnWidth);
                }
                int selnb = (int) Math.ceil((width - p.getX()) / 10);
                if ((selnb >= nbSelections) || (selnb < 0)) {
                    return;
                }
                NodeSet selected = (NodeSet) selectionsList.get(selnb);
                TreeSelectionEvent tse = new TreeSelectionEvent(this, selected,
                        e);
                Iterator it = treeSelectionListeners.iterator();
                while (it.hasNext()) {
                    ((TreeSelectionListener) it.next()).nodeSetSelected(tse);
                }
                return;
            }
            TreeSelectionEvent tse = new TreeSelectionEvent(this, aNode, e);
            Iterator it = treeSelectionListeners.iterator();
            while (it.hasNext()) {
                ((TreeSelectionListener) it.next()).nodeSelected(tse);
            }
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            Point2D p = (Point2D) e.getPoint();
            Node aNode = locateNode(getCurrentRootNode(), p);
            if (aNode == null) {
                return;
            }
            TreeSelectionEvent tse = new TreeSelectionEvent(this, aNode, e);
            Iterator it = treeSelectionListeners.iterator();

            while (it.hasNext()) {
                ((TreeSelectionListener) it.next()).nodeClicked(tse);
            }
        }

        /**
         * mouse lisener
         * @param e The received mouse event
         */
        public void mouseMoved(MouseEvent e) {
            if ((popupMenu != null) && popupMenu.isShowing()) {
                return;
            }
            Point2D p = (Point2D) e.getPoint();
            Node n = locateNode(getCurrentRootNode(), p);
            highlightSurroundingArea(n);
        }

        /**
         * mouse lisener
         * @param e The received mouse event
         */
        public void mouseExited(MouseEvent e) {
            Point2D p = (Point2D) e.getPoint();
            Node n = locateNode(getCurrentRootNode(), p);
            if (n != null) {
                return;
            }
            highlightSurroundingArea(null);
            if (popupMenu != null) {
                popupMenu.setVisible(false);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_HIGHLIGHTING_MODE)) {
            hmode = ((Integer) e.getNewValue()).intValue();
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_SHOW_BRANCH_LENGTH)) {
            showBranchLength = ((Boolean) e.getNewValue()).booleanValue();
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_ALIGN_TERMINAL_NODES)) {
            alignTerminalNodes = CESettings.getInstance()
                    .isAlignTerminalNodes();
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_HIDE_SIMILAR_ANNOTATION)) {
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_SHOW_EXP_VALUES_SELECT)) {
            showExpressionValues = CESettings.getInstance()
                    .isShowExpressionValues();
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_TERMINALS_BOXED)) {
            terminalBoxed = CESettings.getInstance().isTerminalsBoxed();
        }
        repaint();
    }

    /** Repaints graphics. */
    private void update() {
        invalidate();
        repaint();
    }
}