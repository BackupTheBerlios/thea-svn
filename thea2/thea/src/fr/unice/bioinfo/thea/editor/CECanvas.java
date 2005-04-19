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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;

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
 * @author Saïd El kasmi.
 */
public class CECanvas extends JComponent implements PropertyChangeListener {

    //
    private FontRenderContext context;
    //

    /** the image used to show that a branch is collapsed */
    private Image expandImage = null;

    /** Tree's rootNode node. */
    private Node rootNode;

    /** contains the list of all root nodes in the classification. */
    private Stack roots;

    /** Size of a branch in percentage of the remaining width */
    private double branchSize = 0.1;

    private double baseBranchLength;

    /** The minimum amount of space between tho adjacent branchs */
    private double branchsMinimumSep = 1;

    /** The node currently highlighted */
    private Node hnode;

    /**
     * When moving the mouse over rootNode nodes, they are highlighted to make
     * the user have an idea about the node under mose selection. Three modes
     * are possible: <br>
     * <ul>
     * <li>hmode = 1 : Show a rectangle around the whole subtree of the node
     * under selection.
     * <li>hmode = 2 : Show a frame around the subtree.
     * <li>hmode = 3 : Show sub-tree's rootNode (parent).
     * </ul>
     */
    private int hmode;

    /** Popup menu. */
    private JPopupMenu popupMenu;

    /** Flag to indicate if branchs have to be drawn according to their length. */
    private boolean showBranchLength;

    /** A flag to indicate if expression values has to be shown. */
    private boolean showExpressionValues;

    /** Flag to indicate if terminal nodes should be aligned. */
    private boolean alignTerminalNodes;

    /** The maximum width on the screen used by terminal labels */
    private double labelMaxWidth;

    /** The standard height of a label compted from the layout of the letter 'X' */
    private double labelHeight;

    /** The size of the component with a zoom factor of (1,1) */
    private double currentZoomX;

    private double currentZoomY;

    /** True if terminal labels are surrounded by a frame */
    private boolean terminalBoxed;

    /** True if non terminal labels are surrounded by a frame */
    private boolean nonTerminalBoxed;

    /** The list of selected nodes */
    private Set selectedNodes = new HashSet();

    /** The set of collapsed nodes */
    private Set collapsedNodes;

    /** The list of successive selection lists */
    private List selectionsList = new Vector();

    /** The number of selections done */
    private int nbSel;

    /** The map between nodes and their positions (Point2D) on the screen */
    private Map nodeToPosition;

    /**
     * The map between nodes and the areas (a Rectangle2D) used to display them
     * on the screen
     */
    private Map nodeToArea;

    /** The map between nodes and their detailed states */
    private Map nodeToDetailedState;

    /** The map between nodes and their localisation or not into the windows area */
    private Map nodeToInClipState;

    /** The map between nodes and their layout */
    private Map nodeToLayout;

    /** The map between nodes and their number of displayable childs */
    private Map nodeToNbTerminals;

    /** The name of the selection */
    private String selectionName;

    /** data relative to expression values */
    private int expValNbMeasures;

    private int expValColumnWidth;

    private List expValUnderExpDeciles;

    private List expValOverExpDeciles;

    private double expValMinMeasure;

    private double expValMaxMeasure;

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
        // Collapse Icon
        expandImage = Utilities
                .loadImage("fr/unice/bioinfo/thea/editor/resources/expand.gif");
        // Register this as a listener to changes in that instance
        settings.addPropertyChangeListener(this);
        // Initialize the list that contains selections' listeners
        svl = new ArrayList();
        init();
    }

    public void init() {
        rootNode = null;
        roots = new Stack();
        hnode = null;
        //showAnnotation = false;
        //hmode = 1;
        popupMenu = null;
        //showBranchLength = false;
        //alignTerminalNodes = true;
        labelMaxWidth = -1;
        labelHeight = -1;
        currentZoomX = 1;
        currentZoomY = 1;
        terminalBoxed = false;
        nonTerminalBoxed = true;
        baseBranchLength = -1;
        collapsedNodes = new HashSet();
        if (!selectionsList.isEmpty()) {
            List selectionsClone = new Vector(selectionsList);
            Iterator it = selectionsClone.iterator();
            while (it.hasNext()) {
                NodeSet selectionList = (NodeSet) it.next();
                removeSelection(selectionList);
            }
            selectionsList.clear();
        }
        clearSelected();
        nbSel = 0;
        nodeToPosition = new HashMap();
        nodeToArea = new HashMap();
        nodeToDetailedState = new HashMap();
        nodeToInClipState = new HashMap();
        nodeToLayout = new HashMap();
        nodeToNbTerminals = new HashMap();
        expValNbMeasures = 0;
        expValColumnWidth = 0;
        expValMinMeasure = 0;
        expValMaxMeasure = 0;
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

    /**
     * Paints the component
     * @param g The graphics context
     */
    public void paint(Graphics g) {
        this.context = ((Graphics2D) g).getFontRenderContext();
        draw(g);
        super.paint(g);
    }

    private void draw(Graphics g) {
        if (rootNode == null) {
            return;
        }
        labelMaxWidth = computeLabelMaxWidth(getRootNode(), (Graphics2D) g,
                Consts.TERMINAL_FONT);
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
            baseBranchLength = computeBaseBranchLength(getRootNode(),
                    treeWidth - 25, (Graphics2D) g, Consts.TERMINAL_FONT);
        }
        nodeToInClipState.clear();
        nodeToDetailedState.clear();
        drawNode(getRootNode(), (Graphics2D) g, 15, 5, treeWidth - 30,
                height - 10);
        nodeToArea.put(getRootNode(), new Rectangle2D.Double(0, 5,
                treeWidth - 15, height - 10));
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
            drawExpressions((Graphics2D) g, getAllVisibleLeaves(getRootNode()),
                    selWidth);
        }
    }

    /**
     * Display various data about the displayed tree
     * @param g The graphics context
     */
    private void displayInfo(Graphics2D g) {
        TextLayout tl = new TextLayout(getRootNode().getNumberOfLeaves()
                + " leaves", Consts.INFO_FONT, context);
        tl.draw(g, 0, 20);
        tl = new TextLayout("depth=" + getRootNode().getDepth(),
                Consts.INFO_FONT, context);
        tl.draw(g, 0, 40);
    }

    /**
     * Draw a mark relative of the tree to show the position of selected nodes
     * @param g The graphics context
     * @param selected The list of selected nodes
     * @param color The color to use to fill the area
     * @param x The horizontal position where to draw selected marks
     * @param width The width of the marks
     */
    private void displaySelected(Graphics2D g, NodeSet selected, double x,
            double width) {
        if (selected == null) {
            return;
        }
        if (selected.getNodes() == null) {
            return;
        }
        if (selected.getNodes().isEmpty()) {
            return;
        }
        Color col = (Color) selected.getUserData("color");
        Color bgCol = (Color) selected.getUserData("bgColor");
        Color c = getBackground();
        if (bgCol != null) {
            g.setColor(bgCol);
            Rectangle2D.Double rootArea = (Rectangle2D.Double) nodeToArea
                    .get(getTreeRoot());
            if (rootArea != null) {
                g.fill(new Rectangle2D.Double(Math.rint(x), Math
                        .rint(rootArea.y), Math.rint(width), Math
                        .rint(rootArea.height)));
            }
        }
        g.setColor(col);
        Iterator it = selected.getNodes().iterator();
        while (it.hasNext()) {
            Node n = (Node) it.next();
            if (!getRootNode().isAncestorOf(n)) {
                continue;
            }
            if (!isInClip(n)) {
                continue;
            }
            Rectangle2D.Double area = (Rectangle2D.Double) nodeToArea.get(n);
            if (isNotDetailed(n)) {
                do {
                    n = n.getParent();
                } while ((n != null) && isNotDetailed(n));

                if (n != null) {
                    area = (Rectangle2D.Double) nodeToArea.get(n);
                }
                if (area != null) {
                    g.fill(new Rectangle2D.Double(Math.rint(x), Math
                            .rint(area.y), Math.rint(width), 1));
                }
//            } else if (isHidden(n)) {
            } else if (n.isHidden()) {
                do {
                    n = n.getParent();
//                } while ((n != null) && !getCollapsed(n) && isHidden(n));
//                } while ((n != null) && !n.isCollapsed() && isHidden(n));
                } while ((n != null) && !n.isCollapsed() && n.isHidden());

                if (n != null) {
                    area = (Rectangle2D.Double) nodeToArea.get(n);
                }
                if (area != null) {
                    Polygon poly = new Polygon();
                    poly.addPoint((int) x, (int) (area.y + (area.height / 2)));
                    poly.addPoint((int) (x + width),
                            (int) ((area.y + (area.height / 2)) - (width / 2)));
                    poly.addPoint((int) (x + width), (int) (area.y
                            + (area.height / 2) + (width / 2)));
                    g.fill(poly);
                }
            } else {
                g.fill(new Rectangle2D.Double(Math.rint(x), Math.rint(area.y),
                        Math.rint(width), Math.rint(Math.max(area.height, 1))));
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
            Node n = (Node) it.next();
            if (!getRootNode().isAncestorOf(n) && !(getRootNode() == n)) {
                continue;
            }
            if (!isInClip(n)) {
                continue;
            }
            Rectangle2D.Double area = (Rectangle2D.Double) nodeToArea.get(n);
            Node knownNode = n;
            while (isNotDetailed(knownNode)) {
                knownNode = knownNode.getParent();
                area = (Rectangle2D.Double) nodeToArea.get(knownNode);
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
     * Draw the node n and all its subnodes in the rectangular area defined by
     * (x, y, width, height)
     * @param n The node to be displayed
     * @param g The graphics context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     * @return the vertical position of the horizontal bar
     */
    private double drawNode(Node n, Graphics2D g, double x, double y,
            double width, double height) {
        Color c = g.getColor();
        if (n == hnode) {
            highlightNode(n, g);
        }
        nodeToArea.put(n, new Rectangle2D.Double(x, y, width, height));
        if (!g.hitClip((int) x, (int) y, (int) width + 1, (int) height + 1)) {
            setNotDetailed(n, true);
            setInClip(n, false);
            return y + (height / 2);
        }
        if (isTerminal(n)) {
            drawTerminalNode(n, g, x, y, width, height);

            return y + (height / 2);
        }
        double branchLength = baseBranchLength;
        if (showBranchLength) {
            branchLength *= n.getBranchLength();
        }
        if (n == rootNode) {
            branchLength = 0;
        }
        List childs = n.getChildren();
        Iterator it = childs.iterator();
//        int leaves = countTerminals(n);
        int leaves = n.getTerminals();
        double posy = y;
        double miny = Double.MAX_VALUE;
        double maxy = Double.MIN_VALUE;
        boolean noDetail = false;
        while (it.hasNext()) {
            Node child = (Node) it.next();
            if (noDetail == true) {
                setNotDetailed(child, true);
                continue;
            }
//            int childLeaves = countTerminals(child);
            int childLeaves = child.getTerminals();
            double childHeight = (double) childLeaves / (double) leaves;
            double newy = height * childHeight;
            double centery = drawNode(child, g, x + branchLength, posy, width
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
        Node parentNode = n.getParent();
        if (isSelected(n.getParent())) {
            g.setColor(Consts.SELECTED_BACKGROUND);
        } else {
            g.setColor(Color.black);
        }
        g.draw(new Line2D.Double(Math.rint(x), Math.rint((miny + maxy) / 2),
                Math.rint(x + branchLength), Math.rint((miny + maxy) / 2)));
        if (isSelected(n)) {
            g.setColor(Consts.SELECTED_BACKGROUND);
        } else {
            g.setColor(Color.black);
        }
        g.draw(new Line2D.Double(Math.rint(x + branchLength), Math.rint(miny),
                Math.rint(x + branchLength), Math.rint(maxy)));
        nodeToPosition.put(n, new Point2D.Double(x + branchLength,
                (miny + maxy) / 2));

        // the maximum height available to write the label
        double maxLabelHeight = Math.min((y + (height / 2)) - miny, maxy - y
                - (height / 2));
        // the maximum width available to write the label
        drawNonTerminalNode(n, g, x + (branchLength * 0.2), (Math
                .rint((miny + maxy) / 2))
                - (maxLabelHeight / 2), width, /* maxLabelWidth */
        maxLabelHeight);
        g.setColor(c);
        return (miny + maxy) / 2;
    }

    private void highlightNode(Node n, Graphics2D g) {
        Point2D.Double posN = (Point2D.Double) nodeToPosition.get(n);
        Rectangle2D.Double rectN = (Rectangle2D.Double) nodeToArea.get(n);
        Rectangle2D.Double rectNDisp = new Rectangle2D.Double();
        if (rectN != null) {
            rectNDisp.setRect(rectN);
        }
        if (n != null) {
            rectNDisp.width += 10;
            if (selectionsList != null) {
                rectNDisp.width += (selectionsList.size() * 10);
            }
        }
        switch (hmode) {
        case 1:
            g.setColor(new Color(255, 255, 223));
            if (n != null) {
                ((Graphics2D) g).fill(rectNDisp);
            }
            break;
        case 2:
            g.setColor(new Color(0, 0, 255));
            if (n != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(rectNDisp);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) g).draw(rect);
            }
            break;
        case 3:
            g.setColor(new Color(0, 0, 255));
            if (n != null) {
                ((Graphics2D) g).fill(new Ellipse2D.Double(
                        posN.x - labelHeight, posN.y - labelHeight,
                        labelHeight * 2, labelHeight * 2));
            }
            break;
        }
    }

    /**
     * Displays the label of a non terminal node
     * @param n The node to be displayed
     * @param g The graphics context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     */
    private void drawNonTerminalNode(Node n, Graphics2D g, double x, double y,
            double width, double height) {
        List annotations = (List) n.getUserData("userAnnotations");
        if (annotations == null) {
            annotations = new Vector();
        } else {
            annotations = new Vector(annotations);
        }
        boolean lastAnnotIsCurrentLabel = false;
        String label = getNodeLabel(n);
        if (!label.equals("")) {
            List param = new Vector();
            param.add(label);
            param.add(getLayout(n));
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
            TextLayout layout = new TextLayout(annot, font, context);
            double labelNormalWidth = layout.getBounds().getWidth();
            while ((labelNormalWidth > width) && (annot.length() > 0)) {
                annot = annot.substring(0, annot.length() - 1);
                layout = new TextLayout(annot, font, context);
                labelNormalWidth = layout.getBounds().getWidth();
            }
            layouts.add(layout);
            layoutsAttr.add(layoutAttr);
            layHeight += (layout.getAscent() + layout.getDescent() + layout
                    .getLeading());
            maxWidth = Math.max(maxWidth, layout.getAdvance());
        }
        int nbLines = layouts.size();
        double currentY = (y + (height / 2)) - ((layHeight) / 2);
        Rectangle2D area = new Rectangle2D.Double(Math.rint(x), Math
                .rint(currentY), Math.rint(maxWidth), Math.rint(layHeight));
        for (int i = 0; i < nbLines; i++) {
            TextLayout layout = (TextLayout) layouts.get(i);
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
                    .rint(currentY), Math.rint(maxWidth), Math.rint(layout
                    .getAscent()
                    + layout.getDescent() + layout.getLeading()));
            g.fill(lineArea);
            if ((i == (nbLines - 1)) && lastAnnotIsCurrentLabel) {
                g.setColor(Color.black);
                g.draw(lineArea);
            }
            g.setColor(fontColor);
            currentY += (layout.getAscent());
            layout.draw(g, (float) (Math.rint(x)), (float) Math.rint(currentY));
            currentY += (layout.getDescent() + layout.getLeading());
        }
        if (nonTerminalBoxed) {
            g.setColor(Color.black);
            g.draw(area);
        }
        g.setColor(c);
    }

    /**
     * Displays the label of a terminal node
     * @param n The node to be displayed
     * @param g The graphics context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     */
    private void drawTerminalNode(Node n, Graphics2D g, double x, double y,
            double width, double height) {
        boolean boxed = terminalBoxed;
        List annotations = (List) n.getUserData("userAnnotations");
        if (annotations == null) {
            annotations = new Vector();
        } else {
            annotations = new Vector(annotations);
            boxed = true;
        }
        boolean lastAnnotIsCurrentLabel = false;
        String label = getNodeLabel(n);
        if (!label.equals("")) {
            if (annotations.size() > 0) {
                lastAnnotIsCurrentLabel = true;
            }
            List param = new Vector();
            param.add(label);
            param.add(getLayout(n));
            annotations.add(param);
        }
//        if (annotations.isEmpty() && getCollapsed(n)) {
        if (annotations.isEmpty() && n.isCollapsed()) {
            List param = new Vector();
            param.add("[" + n.getLeaves().size() + "]");

            //boxed = true;
            param.add(getLayout(n));
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
                font = Consts.TERMINAL_FONT;
            }
            TextLayout layout = new TextLayout(annot, font, context);
            double labelNormalWidth = layout.getBounds().getWidth();
            while ((labelNormalWidth > width) && (annot.length() > 0)) {
                annot = annot.substring(0, annot.length() - 1);
                layout = new TextLayout(annot, font, context);
                labelNormalWidth = layout.getBounds().getWidth();
            }
            layouts.add(layout);
            layoutsAttr.add(layoutAttr);
            layHeight += (layout.getAscent() + layout.getDescent() + layout
                    .getLeading());
            maxWidth = Math.max(maxWidth, layout.getAdvance());
        }
        int nbLines = layouts.size();
        double currentY = (y + (height / 2)) - ((layHeight) / 2);
        double branchLengthFactor = 1;
        if (showBranchLength) {
            branchLengthFactor = n.getBranchLength();
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
        if (isSelected(n)) {
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
            nodeToPosition.put(n, new Point2D.Double(Math.rint((x + width)
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
            nodeToPosition.put(n, new Point2D.Double(Math.rint(x
                    + (baseBranchLength * (double) branchLengthFactor)), Math
                    .rint(y + (height / 2))));
        }
//        if (getCollapsed(n)) {
        if (n.isCollapsed()) {
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

    /**
     * Compute the maximum label width of subnodes of this node
     * @param font The font
     * @param node The rootNode of the subtree
     * @param g The graphics context
     * @param frc The font render context
     * @return The maximum width occupied on the screen by the label of this
     *         node or all its subnodes
     */
    public double computeLabelMaxWidth(Node node, Graphics2D g, Font font) {
        String label = getNodeLabel(node);
        if (label.equals("")) {
            return 0;
        }
        if (isTerminal(node)) {
            TextLayout tl = new TextLayout(label, font, context);
            Rectangle2D bounds = tl.getBounds();

            return bounds.getWidth();
        }
        double maxWidth = 0;

        List children = node.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Node child = (Node) it.next();
            maxWidth = Math.max(computeLabelMaxWidth(child, g, font), maxWidth);
        }
        return maxWidth;
    }

    public double computeBaseBranchLength(Node n, double width, Graphics2D g,
            Font f) {
        if (isTerminal(n)) {
            double depth = n.getDistanceToAncestor(rootNode, showBranchLength);
            if (depth == 0) {
                return Double.MAX_VALUE;
            }
            String label = getNodeLabel(n);
            if (label.equals("")) {
                return width / depth;
            }
            TextLayout layout = new TextLayout(label, f, context);
            Rectangle2D bounds = layout.getBounds();
            return (width - bounds.getWidth()) / depth;
        }
        double baseBranchLength = Double.MAX_VALUE;
        List childs = n.getChildren();
        Iterator it = childs.iterator();
        while (it.hasNext()) {
            Node child = (Node) it.next();
            baseBranchLength = Math.min(computeBaseBranchLength(child, width,
                    g, f), baseBranchLength);
        }
        return baseBranchLength;
    }

    private String getNodeLabel(Node n) {
        String label = n.getName();
        if (label == null) {
            label = "";
        }
        if (CESettings.getInstance().isShowAnnotation()) {
            String annotation = (String) n.getUserData("annotation");

            if (annotation != null) {
                label += (" " + annotation);
            }
        }
        return label;
    }

    /**
     * Setter for rootNode
     * @param n The rootNode of the tree
     */
    public void setRootNode(Node n) {
        rootNode = n;
        expValMinMeasure = 0;
        expValMaxMeasure = 0;
        expValUnderExpDeciles = null;
        expValOverExpDeciles = null;
        Object o = getTreeRoot().getUserData("nbMeasures");
        if(o == null){
            System.out.println("o est null");
            }
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
                    if (rootNode.isAncestorOf(node)) {
                        countSelected++;
                    }
                }
                String localHits = countSelected + "/"
                        + rootNode.getNumberOfLeaves();
                String selId = (String) ns.getUserData("selId");
                SelectionEvent e = new SelectionEvent(this, selId, null, null,
                        null, localHits, null);
                fireSelectionDone(e);
                //                ClassifSelectionEvent event = new ClassifSelectionEvent(this,
                //                        selId, null, null, null, localHits, null);
                //                org.bdgp.apps.dagedit.gui.Controller.getController()
                //                        .fireSelectionDoneInClassif(event);
            }
        }
        if (!selectedNodes.isEmpty()) {
            List selectedLeaves = getSelectedLeaves();
            int countSelected = 0;
            Iterator it = selectedLeaves.iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();

                if (rootNode.isAncestorOf(node)) {
                    countSelected++;
                }
            }
            String localHits = countSelected + "/"
                    + rootNode.getNumberOfLeaves();
            String selId = String.valueOf(nbSel);
            SelectionEvent e = new SelectionEvent(this, selId, null, null,
                    null, localHits, null);
            fireSelectionDone(e);
            //            ClassifSelectionEvent event = new ClassifSelectionEvent(this,
            //                    selId, null, null, null, localHits, null);
            //            org.bdgp.apps.dagedit.gui.Controller.getController()
            //                    .fireSelectionDoneInClassif(event);
        }
        baseBranchLength = -1; // force the recalculation
    }

    /**
     * Getter for rootNode
     * @return The rootNode of the displayed tree
     */
    public Node getRootNode() {
        return rootNode;
    }

    public void setRoots(Stack roots) {
        this.roots = roots;
    }

    public Stack getRoots() {
        return roots;
    }

    /** Push a new rootNode */
    public void pushRoot(Node n) {
        roots.push(getRootNode());
        setRootNode(n);
        zoom(1, 1);
        invalidate();
        repaint();
    }

    /** Pop a rootNode */
    public void popRoot() {
        Node n = (Node) roots.pop();
        setRootNode(n);
        zoom(1, 1);
        invalidate();
        repaint();
    }

    /** Pop all roots */
    public void popAllRoot() {
        Node n = null;
        while (!roots.empty()) {
            n = (Node) roots.pop();
        }
        setRootNode(n);
        zoom(1, 1);
        invalidate();
        repaint();
    }

    /**
     * Setter for the current popupMenu
     * @param n The current popupMenu
     */
    public void setPopupMenu(JPopupMenu pop) {
        popupMenu = pop;
    }

    /**
     * Get the rootNode of the whole tree
     * @return The rootNode of the whole tree
     */
    public Node getTreeRoot() {
        if (roots.isEmpty()) {
            return rootNode;
        } else {
            return (Node) roots.elementAt(0);
        }
    }

    /** Adds the given node to the list of collapsed nodes.*/
    public void addCollapsedNode(Node aNode, boolean b) {
        aNode.setCollapsed(b);
        if (b) {
            collapsedNodes.add(aNode);
        } else {
            collapsedNodes.remove(aNode);
        }
        Node nodeToUpdate = aNode;

        while (nodeToUpdate != null) {
            nodeToNbTerminals.remove(nodeToUpdate);
            nodeToUpdate = nodeToUpdate.getParent();
        }
        zoom(1, 1);
        invalidate();
        repaint();
    }
    /** Removes the given node from the list of collapsed nodes.*/
    public void removeCollapsedNode(Node aNode){
        collapsedNodes.remove(aNode);
        }

//    /**
//     * Get the collapsed state of a node
//     * @param n The node to get the value
//     * @return The collapsed state of the parameter node
//     */
//    public boolean getCollapsed(Node n) {
//        return collapsedNodes.contains(n);
//    }

    public void setCollapsedNodes(Set s) {
        collapsedNodes = s;
    }

    //    public Set getCollapsedNodes() {
    //        return collapsedNodes;
    //    }

    /**
     * Sets all nodes as non selected
     */
    public void clearSelected() {
        String selId = String.valueOf(nbSel);
        //        ClassifSelectionEvent event = new ClassifSelectionEvent(this, selId);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionClearedInClassif(event);

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
    public void setSelected(Collection nodes, int state, String selName) {
        setSelectionName(selName);
        setSelected(nodes, state);
    }

    /**
     * Sets the selection state of the nodes in the list to b
     * @param nodes the new list of selected nodes
     * @param state Indicates the new selected state of the node (if state
     *        <0:unselect, if state=0:invert, is state>0:select)
     */
    public void setSelected(Collection nodes, int state) {
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
    public void setSelected(Node node, int state) {
        setSelectedNodeAndChilds(node, state);
        // modify the selected state of the parent if necessary
        updateSelectedState(node.getParent());
        sendSelectionEvent();
        // repaint the area
        Rectangle2D.Double rectInNode = (Rectangle2D.Double) nodeToArea
                .get(node);
        if (rectInNode != null) {
            repaint((int) rectInNode.x, (int) rectInNode.y,
                    (int) rectInNode.width, (int) rectInNode.height);
        }
    }

    /**
     * Send a selection event
     */
    private void sendSelectionEvent() {
        Node localRoot = getRootNode();
        String selId = String.valueOf(nbSel);
        String globalHits = getSelectedLeaves(true).size() + "/"
                + getTreeRoot().getNumberOfLeaves();
        String localHits = getSelectedLeaves(false).size() + "/"
                + localRoot.getNumberOfLeaves();
        SelectionEvent e = new SelectionEvent(this, selId, selectionName,
                "Current", Consts.SELECTED_COLOR, Consts.SELECTED_BACKGROUND,
                globalHits, localHits, getSelectedLeaves(true));
        fireSelectionDone(e);
        //        ClassifSelectionEvent event = new ClassifSelectionEvent(this, selId,
        //                selectionName, "Current", selectedColor, selectedBackground,
        //                globalHits, localHits, getSelectedLeaves(true));
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionDoneInClassif(event);
    }

    /**
     * Set the selected state of a node ant its child nodes
     * @param n The node to update
     * @param state Indicates the new selected state of the node (if state
     *        <0:unselect, if state=0:invert, is state>0:select)
     */
    private void setSelectedNodeAndChilds(Node n, int state) {
        if (state < 0) {
            selectedNodes.remove(n);
        } else if (state > 0) {
            selectedNodes.add(n);
        } else { // state == 0
            if (isSelected(n)) {
                selectedNodes.remove(n);
            } else {
                selectedNodes.add(n);
            }
        }
        if (n.isLeaf()) {
            return;
        }
        Iterator iterator = n.getChildren().iterator();
        while (iterator.hasNext()) {
            Node childNode = (Node) iterator.next();
            setSelectedNodeAndChilds(childNode, state);
        }
    }

    /**
     * Update the selected state of the node
     */
    private void updateSelectedState(Node n) {
        if (n == null) {
            return;
        }
        if (n.isLeaf()) {
            return; // to be sure
        }
        boolean nodeIsSelected = isSelected(n);
        boolean allChildsInSelectedState = true;
        Iterator iterator = n.getChildren().iterator();
        while (iterator.hasNext()) {
            Node childNode = (Node) iterator.next();

            if (!isSelected(childNode)) {
                allChildsInSelectedState = false;
                break;
            }
        }
        if (nodeIsSelected != allChildsInSelectedState) {
            // update needed
            if (allChildsInSelectedState) {
                selectedNodes.add(n);
            } else {
                selectedNodes.remove(n);
            }
            // repaint the area
            Rectangle2D.Double rectInNode = (Rectangle2D.Double) nodeToArea
                    .get(n);
            if (rectInNode != null) {
                repaint((int) rectInNode.x, (int) rectInNode.y,
                        (int) rectInNode.width, (int) rectInNode.height);
            }
            // modify the selected state of the parent if necessary
            updateSelectedState(n.getParent());
        }
    }

    /**
     * Get the selected state of a node
     * @param n The node to get the value
     * @return The selected state of the parameter node
     */
    public boolean isSelected(Node n) {
        return selectedNodes.contains(n);
    }

    public void moveSelectionToCurrent(NodeSet sel) {
        clearSelected();
        setSelected(sel.getNodes(), 1, (String) sel.getUserData("selName"));
        removeSelection(sel);
        invalidate();
        repaint();
    }

    public void copySelectionToCurrent(NodeSet sel) {
        clearSelected();
        setSelected(sel.getNodes(), 1, (String) sel.getUserData("selName"));
        invalidate();
        repaint();
    }

    public void unionSelectionWithCurrent(NodeSet sel) {
        String newSelectionName = (String) sel.getUserData("selName");
        if (selectionName.startsWith("Manual")) {
            // use selName for the name of the union
        } else {
            if (newSelectionName.startsWith("Manual")) {
                newSelectionName = selectionName;
            } else {
                newSelectionName = selectionName + " OR " + newSelectionName;
            }
        }
        setSelected(sel.getNodes(), 1, newSelectionName);
        invalidate();
        repaint();
    }

    public void intersectSelectionWithCurrent(NodeSet sel) {
        String newSelectionName = (String) sel.getUserData("selName");
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
        currentSel.retainAll(sel.getNodes());
        clearSelected();
        setSelected(currentSel, 1, newSelectionName);
        invalidate();
        repaint();
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
    public List getSelectedLeaves(boolean wholeTreeSearch) {
        Node searchSubtree = (wholeTreeSearch ? getTreeRoot() : getRootNode());
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
     * @param selList The list of selections
     */
    public void setSelectionsList(Collection selList) {
        if (selList == null) {
            selectionsList = null;
        } else {
            selectionsList = new java.util.Vector(selList);
        }
    }

    /**
     * Setter for the list of selections
     * @return the list of selections
     */
    public List getSelectionsList() {
        return selectionsList;
    }

    //    public void informListenersOfCurrentSelections() {
    //        Node localRoot = getRootNode();
    //        if (selectionsList != null) {
    //            Iterator it = selectionsList.iterator();
    //            Collection selectedLeaves = new HashSet();
    //            while (it.hasNext()) {
    //                int countSelectedLocal = 0;
    //                int countSelectedGlobal = 0;
    //                NodeSet ns = (NodeSet) it.next();
    //                Iterator it2 = ns.getNodes().iterator();
    //                while (it2.hasNext()) {
    //                    Node node = (Node) it2.next();
    //                    if (node.isLeaf()) {
    //                        if (localRoot.isAncestor(node)) {
    //                            countSelectedLocal++;
    //                        }
    //                        countSelectedGlobal++;
    //                    }
    //                }
    //                String localHits = countSelectedLocal + "/"
    //                        + rootNode.countLeaves();
    //                String selId = (String) ns.getUserData("selId");
    //                String selName = (String) ns.getUserData("selName");
    //                Color selColor = (Color) ns.getUserData("color");
    //                String globalHits = countSelectedGlobal + "/"
    //                        + getTreeRoot().countLeaves();
    //              SelectionEvent e = new SelectionEvent(
    //                                        this, selId, selName, selColor, globalHits, localHits,
    //                                        ns.getNodes());
    //             fireSelectionDone(e);
    //                // ClassifSelectionEvent event = new ClassifSelectionEvent(
    //                // this, selId, selName, selColor, globalHits, localHits,
    //                // ns.getNodes());
    //                // org.bdgp.apps.dagedit.gui.Controller.getController()
    //                // .fireSelectionDoneInClassif(event);
    //            }
    //        }
    //        if (selectedNodes != null) {
    //            List selectedLeaves = getSelectedLeaves();
    //            int countSelectedLocal = 0;
    //            int countSelectedGlobal = 0;
    //            Iterator it = selectedLeaves.iterator();
    //            while (it.hasNext()) {
    //                Node node = (Node) it.next();
    //                if (node.isLeaf()) {
    //                    if (localRoot.isAncestor(node)) {
    //                        countSelectedLocal++;
    //                    }
    //                    countSelectedGlobal++;
    //                }
    //            }
    //            String localHits = countSelectedLocal + "/"
    //                    + rootNode.countLeaves();
    //            String selId = String.valueOf(nbSel);
    //            String globalHits = countSelectedGlobal + "/"
    //                    + getTreeRoot().countLeaves();
    //         SelectionEvent e = new SelectionEvent(this,
    //                                selId, selectionName, "current", Consts.SELECTED_COLOR,
    //                                Consts.SELECTED_BACKGROUND, globalHits, localHits,
    //                                getSelectedLeaves(true));
    //            fireSelectionDone(e);
    //            // ClassifSelectionEvent event = new ClassifSelectionEvent(this,
    //            // selId, selectionName, "current", selectedColor,
    //            // selectedBackground, globalHits, localHits,
    //            // getSelectedLeaves(true));
    //            // org.bdgp.apps.dagedit.gui.Controller.getController()
    //            // .fireSelectionDoneInClassif(event);
    //        }
    //    }

//    /**
//     * Indicates if the node is collapsed, or a descendant of a collapsed node
//     * @param n The node to check
//     * @return The hidden state of the parameter node
//     */
//    private boolean isHidden(Node n) {
////        if (getCollapsed(n)) {
//        if (n.isCollapsed()) {
//            return true;
//        }
//        if (n.getParent() == null) {
//            return false;
//        }
//        return isHidden(n.getParent());
//    }

    /**
     * Find a node from a position
     * @param node The node to search
     * @param pos A position from which a corresponding node is to be searched
     * @return The corresponding node
     */
    public Node findNode(Node node, Point2D pos) {
        Point2D position = (Point2D) nodeToPosition.get(node);
        if (position == null) {
            return null;
        }
        if (position.distance(pos) < 5) {
            return node;
        }
//        if (getCollapsed(node)) {
        if (node.isCollapsed()) {
            return null;
        }
        if (node.isLeaf()) {
            return null;
        }
        Iterator iterator = node.getChildren().iterator();
        while (iterator.hasNext()) {
            Node n = findNode((Node) iterator.next(), pos);
            if (n != null) {
                return n;
            }
        }
        return null;
    }

    /**
     * Find a node from a position using rectangular areas
     * @param node The node to search
     * @param position A position from which a corresponding node is to be
     *        searched
     * @return The corresponding node
     */
    private Node locateNode(Node node, Point2D position) {
        Rectangle2D area = (Rectangle2D) nodeToArea.get(node);
        if (area == null) {
            return null;
        }
        if (!area.contains(position)) {
            return null;
        }
//        if (getCollapsed(node)) {
        if (node.isCollapsed()) {
            return node;
        }
        List childNodes = node.getChildren();
        if (childNodes == null) {
            return node;
        }
        Iterator iterator = childNodes.iterator();
        while (iterator.hasNext()) {
            Node n = locateNode((Node) iterator.next(), position);
            if (n != null) {
                return n;
            }
        }
        return node;
    }

    /**
     * Indicate if the node has no child or is collapsed
     * @param node The node to check
     * @return A flag indicating if the parameter node is terminal
     */
    private boolean isTerminal(Node node) {
//        return ((node.isLeaf()) || getCollapsed(node));
        return ((node.isLeaf()) || node.isCollapsed());
    }

//    /**
//     * Count terminals (non hidden nodes) that are descendant of that node
//     * @param node The node to check
//     * @return The number of terminals that are childs of the parameter node
//     */
//    private int countTerminals(Node node) {
//        int t = node.getTerminals();
//        
//        Integer count = (Integer) nodeToNbTerminals.get(node);
//        if (count != null) {
//            return count.intValue();
//        }
//        if (isTerminal(node)) {
//            nodeToNbTerminals.put(node, new Integer(1));
//            return 1;
//        }
//        int terminals = 0;
//        Iterator iterator = node.getChildren().iterator();
//        while (iterator.hasNext()) {
//            terminals += countTerminals((Node) iterator.next());
//        }
//        nodeToNbTerminals.put(node, new Integer(terminals));
//        System.out.println("node.getTerminals() = "+t+".terminals = "+terminals);
//        return terminals;
//    }

    //    /**
    //     * Resets the position of the node and all its childs to null
    //     * @param node The node to reset
    //     */
    //    public void clearPosition(Node node) {
    //        nodeToPosition.put(node, null);
    //        nodeToArea.put(node, null);
    //        if (node.isLeaf()) {
    //            return;
    //        }
    //        Iterator iterator = node.getChildren().iterator();
    //        while (iterator.hasNext()) {
    //            clearPosition((Node) iterator.next());
    //        }
    //    }

    /**
     * Sets the detailed state of a node and all its childs
     * @param node The node which has to be changed
     * @param detail The detailed state
     */
    public void setNotDetailed(Node node, boolean detail) {
        nodeToDetailedState.put(node, new Boolean(detail));
        if (node.isLeaf()) {
            return;
        }
        Iterator iterator = node.getChildren().iterator();
        while (iterator.hasNext()) {
            setNotDetailed((Node) iterator.next(), detail);
        }
    }

    /**
     * Sets the flag indicating if the node is in the clipping area
     * @param node The node which has to be changed
     * @param inClip The inClip state
     */
    public void setInClip(Node node, boolean inClip) {
        nodeToInClipState.put(node, new Boolean(inClip));
        if (node.isLeaf()) {
            return;
        }
        Iterator iterator = node.getChildren().iterator();
        while (iterator.hasNext()) {
            setInClip((Node) iterator.next(), inClip);
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
     * Indicates if the node is not paint du to a lack of space on the display
     * area
     * @param n The node to check
     * @return A flag indicating if the parameter node is not painted
     */
    private boolean isNotDetailed(Node n) {
        Object notDetailed = nodeToDetailedState.get(n);
        if (notDetailed == null) {
            return false;
        }
        if (notDetailed.equals(Boolean.TRUE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the list of all visible leaves that are descendant of the
     * parameter node
     * @return The list of leaves
     */
    public List getAllVisibleLeaves(Node n) {
        List vnodes /* visible nodes */= new LinkedList();
        List children = n.getChildren();
        if (isTerminal(n)) {
            vnodes.add(n);
        } else {
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                vnodes.addAll(getAllVisibleLeaves((Node) iterator.next()));
            }
        }
        return vnodes;
    }

    /**
     * Indicates if the node is in the clipping area
     * @param n The node to check
     * @return A flag indicating if the parameter node is in the clipping area
     */
    public boolean isInClip(Node n) {
        Object inClip = nodeToInClipState.get(n);
        if (inClip == null) {
            return true;
        }
        if (inClip.equals(Boolean.TRUE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Highlight the area surrounding the node
     * @param n The node which has to be highlighted
     */
    private void highlightSurroundingArea(Node n) {
        if (hnode == n) {
            return;
        }
        Graphics g = getGraphics();
        Point2D.Double posInNode = (Point2D.Double) nodeToPosition.get(hnode);
        Rectangle2D.Double rectInNode = (Rectangle2D.Double) nodeToArea
                .get(hnode);
        Rectangle2D.Double rectInNodeDisp = new Rectangle2D.Double();
        if (rectInNode != null) {
            rectInNodeDisp.setRect(rectInNode);
        }
        Point2D.Double posN = (Point2D.Double) nodeToPosition.get(n);
        Rectangle2D.Double rectN = (Rectangle2D.Double) nodeToArea.get(n);
        Rectangle2D.Double rectNDisp = new Rectangle2D.Double();
        if (rectN != null) {
            rectNDisp.setRect(rectN);
        }
        if (hnode != null) {
            rectInNodeDisp.width += 10;
            if (selectionsList != null) {
                rectInNodeDisp.width += (selectionsList.size() * 10);
            }
        }
        if (n != null) {
            rectNDisp.width += 10;

            if (selectionsList != null) {
                rectNDisp.width += (selectionsList.size() * 10);
            }
        }
        switch (hmode) {
        case 1:
            g.setXORMode(new Color(0, 0, 32));
            if (hnode != null) {
                ((Graphics2D) g).fill(rectInNodeDisp);
            }
            if (n != null) {
                ((Graphics2D) g).fill(rectNDisp);
            }
            break;
        case 2:
            g.setXORMode(new Color(255, 255, 0));
            if (hnode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(rectInNodeDisp);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) g).draw(rect);
            }
            if (n != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(rectNDisp);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) g).draw(rect);
            }
            break;
        case 3:
            g.setXORMode(new Color(255, 255, 0));

            if (hnode != null) {
                ((Graphics2D) g).fill(new Ellipse2D.Double(posInNode.x
                        - labelHeight, posInNode.y - labelHeight,
                        labelHeight * 2, labelHeight * 2));
            }
            if (n != null) {
                ((Graphics2D) g).fill(new Ellipse2D.Double(
                        posN.x - labelHeight, posN.y - labelHeight,
                        labelHeight * 2, labelHeight * 2));
            }
            break;
        }
        hnode = n;
        g.setPaintMode();
    }

    /**
     * Sets the rootNode node to be the parent of the current rootNode
     */
    public void levelUp() {
        if (roots.empty()) {
            return;
        }
        Node parentNode = rootNode.getParent();
        if (parentNode == null) {
            return;
        }
        if (parentNode == roots.peek()) {
            roots.pop();
        }
        setRootNode(parentNode);
        zoom(1, 1);
        invalidate();
        repaint();
    }

    /**
     * Memorize the last selection
     */
    public void keepSelection() {
        if (selectedNodes == null) {
            return;
        }
        NodeSet selectionToKeep = new NodeSet(getSelectedLeaves(true));
        String selID = String.valueOf(nbSel);
        selectionToKeep.setUserData("color",
                Consts.SELECTIONS_COLOR[nbSel % 18]);
        selectionToKeep.setUserData("selId", selID);
        selectionToKeep.setUserData("selName", selectionName);
        SelectionEvent e = new SelectionEvent(this, selID, null,
                (Color) selectionToKeep.getUserData("color"), null, null, null);
        // fire event
        fireSelectionDone(e);

        //        ClassifSelectionEvent event = new ClassifSelectionEvent(this, selID,
        //                null, (Color) selectionToKeep.getUserData("color"), null, null,
        //                null);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionDoneInClassif(event);
        selectionsList.add(selectionToKeep);
        nbSel++;
        clearSelected();
        invalidate();
        repaint();
    }

    public void removeSelection(NodeSet sel) {
        String selId = (String) sel.getUserData("selId");
        SelectionEvent e = new SelectionEvent(this, selId);
        fireSelectionCleared(e);
        //        ClassifSelectionEvent event = new ClassifSelectionEvent(this, selId);
        //        org.bdgp.apps.dagedit.gui.Controller.getController()
        //                .fireSelectionClearedInClassif(event);
        if (selectionsList.contains(sel)) {
            selectionsList.remove(sel);
        }
        invalidate();
        repaint();
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
        invalidate();
        repaint();
    }

    /**
     * Perform a zoom of the display area
     * @param zoomx The hirozontal zoom factor
     * @param zoomy The vertical zoom factor
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
     * Search the nodes in the tree with a label equals to a string
     * @param s The string to search
     */
    public void searchString(String s) {
        clearSelected();
        setSelected(getTreeRoot().findMatchingNodes(s, true), 1, s);
        invalidate();
        repaint();
    }

    /**
     * Search the nodes in the tree with a label equals (case sensitive) to a
     * string
     * @param s The string to search
     */
    public void searchStringCaseSensitive(String s) {
        clearSelected();
        setSelected(getTreeRoot().findMatchingNodes(s, false), 1, s);
        invalidate();
        repaint();
    }

    /**
     * Search the nodes in the tree with a label corresponding to a pattern
     * @param p The pattern to search
     */
    public void searchPattern(Pattern p) {
        clearSelected();
        setSelected(getTreeRoot().findMatchingNodes(p), 1, p.toString());
        invalidate();
        repaint();
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
            Node n = locateNode(getRootNode(), p);
            if (n == null) {
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
            TreeSelectionEvent tse = new TreeSelectionEvent(this, n, e);
            Iterator it = treeSelectionListeners.iterator();
            while (it.hasNext()) {
                ((TreeSelectionListener) it.next()).nodeSelected(tse);
            }
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            Point2D p = (Point2D) e.getPoint();
            Node n = locateNode(getRootNode(), p);
            if (n == null) {
                return;
            }
            TreeSelectionEvent tse = new TreeSelectionEvent(this, n, e);
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
            Node n = locateNode(getRootNode(), p);
            highlightSurroundingArea(n);
        }

        /**
         * mouse lisener
         * @param e The received mouse event
         */
        public void mouseExited(MouseEvent e) {
            Point2D p = (Point2D) e.getPoint();
            Node n = locateNode(getRootNode(), p);
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
            alignTerminalNodes = (showBranchLength & ((Boolean) e.getNewValue())
                    .booleanValue());
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_HIDE_SIMILAR_ANNOTATION)) {
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_SHOW_EXP_VALUES_SELECT)) {
            showExpressionValues = CESettings.getInstance()
                    .isShowExpressionValues();
        }
        repaint();
    }
}