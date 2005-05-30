package fr.unice.bioinfo.thea.classification.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
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

import fr.unice.bioinfo.thea.classification.Node;
import fr.unice.bioinfo.thea.classification.NodeLayoutSupport;
import fr.unice.bioinfo.thea.classification.Selection;
import fr.unice.bioinfo.thea.classification.editor.util.Discretization;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class Canvas extends JComponent implements DrawableClassification,
        Zoomable {

    private SelectionManager selectionManager = null;

    /** The image used to show that a branch is collapsed */
    private Image expandImage = null;

    /** The currentRootNode node of the currently displayed tree. */
    private Node currentRootNode;

    /** contains the list of all currentRootNode nodes in the classification. */
    private Stack roots = new Stack();

    /** The list of successive selection lists */
    private List selectionsList = new Vector();

    /** The maximum width on the screen used by terminal labels */
    private double labelMaxWidth;

    /** The standard height of a label compted from the layout of the letter 'X' */
    private double labelHeight = -1;

    /** Size of a branch in percentage of the remaining width */
    private double branchSize = 0.1;

    /** The minimum amount of space between tho adjacent branchs */
    private double branchsMinimumSep = 1;

    private double baseBranchLength = -1;

    /** A flag to indicate if expression values has to be shown. */
    private boolean showExpressionValues = true;

    /** Flag to indicate if branchs have to be drawn according to their length. */
    private boolean showBranchLength;

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
    private int hmode = 2;

    /** The size of the component with a zoom factor of (1,1) */
    private double currentZoomX = 1;
    private double currentZoomY = 1;

    /** The map between nodes and their detailed states */
    private Map detailedNodes = new HashMap();

    /** The map between nodes and their localisation or not into the windows area */
    private Map clippedNodes = new HashMap();

    /** The set of collapsed nodes */
    private Set collapsedNodes = new HashSet();

    /** The node currently highlighted */
    private Node hnode;

    /** True if terminal labels are surrounded by a frame */
    private boolean terminalBoxed = false;

    /** Flag to indicate if terminal nodes should be aligned. */
    private boolean alignTerminalNodes;

    /** The font used to draw labels of terminal nodes */
    private Font terminalFont = new Font("monospaced", Font.PLAIN, 12);;

    /** The font used to draw labels of non terminal nodes */
    private Font nonTerminalFont = new Font("monospaced", Font.PLAIN, 12);;

    /** The background color used to draw labels of terminal nodes */
    private Color terminalBackground;

    /** The background color used to draw labels of selected nodes */
    private Color selectedBackground = new Color(192, 0, 0);

    /** The color used to draw labels of terminal nodes */
    private Color terminalColor = Color.black;

    /** The color used to draw labels of non terminal nodes */
    private Color nonTerminalColor = Color.black;

    /** The background color used to draw labels of terminal nodes */
    private Color nonTerminalBackground;

    /** True if non terminal labels are surrounded by a frame */
    private boolean nonTerminalBoxed = true;

    /** data relative to expression values */
    private int expValNbMeasures = 0;
    private int expValColumnWidth = 0;
    private List expValUnderExpDeciles;
    private List expValOverExpDeciles;
    private double expValMinMeasure = 0;
    private double expValMaxMeasure = 0;

    /** Popup menu. */
    private JPopupMenu popupMenu = null;

    /** A mouse standardListener */
    private CanvasMouseManager standardListener = null;
    private ZoomManager zoomListener = null;

    /** The image used to create the cursor for the zooming mode. */
    private Image zoomImage = null;

    public Canvas() {
        // set a nice border around this panel.
        // setBorder(new EtchedBorder());
        // make the background white
        setBackground(Color.WHITE);
        // Load the image used for collapsed trees:
        URL url = this.getClass().getResource("resources/expand.gif");
        expandImage = Toolkit.getDefaultToolkit().getImage(url);
        url = this.getClass().getResource("resources/zoomImage.gif");
        zoomImage = Toolkit.getDefaultToolkit().getImage(url);
        // mouse settings:
        standardListener = new CanvasMouseManager(this);
        addMouseListener(standardListener);
        addMouseMotionListener(standardListener);
        // create a selection manager
        selectionManager = new SelectionManagerImpl(this);
        PropertyChangeListener selectionListener = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals("selectionsList")) { //NOI18N
                    selectionsList = (List) event.getNewValue();
                    baseBranchLength = -1;
                    updateGraphics();
                }
            }
        };
        this.selectionManager.addPropertyChangeListener(selectionListener);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        draw(g, true);
        super.paint(g);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Drawable#draw(java.awt.Graphics)
     */
    public void draw(Graphics graphics, boolean doClipping) {
        if (currentRootNode == null) {
            return;
        }
        FontRenderContext context = ((Graphics2D) graphics)
                .getFontRenderContext();

        labelMaxWidth = computeLabelMaxWidth(getCurrentRootNode(),
                (Graphics2D) graphics, terminalFont, context);
        TextLayout tl = new TextLayout("X", terminalFont, context);
        labelHeight = tl.getBounds().getHeight();
        double treeWidth = getWidth();
        double selWidth = getWidth();
        double height = getHeight();
        if (selectionsList != null) {
            treeWidth -= (selectionsList.size() * 10);
        }
        if (showExpressionValues) {
            Integer nbMeasuresI = (Integer) getClassificationRootNode()
                    .getProperty(Node.NB_MEASURES);
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
                    treeWidth - 25, (Graphics2D) graphics, terminalFont,
                    context);
        }
        detailedNodes.clear();
        clippedNodes.clear();
        drawNode(getCurrentRootNode(), (Graphics2D) graphics, context, 15, 5,
                treeWidth - 30, height - 10, doClipping);
        getCurrentRootNode().setArea(
                new Rectangle2D.Double(0, 5, treeWidth - 15, height - 10));
        if (selectionsList != null) {
            Iterator iterator = selectionsList.iterator();
            int ctr = 0;
            while (iterator.hasNext()) {
                Selection selection = (Selection) iterator.next();
                displaySelected((Graphics2D) graphics, selection, selWidth - 15
                        - (ctr++ * 10), 8);
            }
        }
        if (showExpressionValues && (expValNbMeasures > 0)) {
            displayExpressionValues((Graphics2D) graphics, getCurrentRootNode()
                    .getVisibleLeaves(), selWidth);
        }
    }

    /**
     * Draw the node n and all its subnodes in the rectangular area defined by
     * (x, y, width, height)
     * @param aNode The node to be displayed
     * @param graphics The graphics context
     * @param context The font render context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     * @return the vertical position of the horizontal bar
     */
    private double drawNode(Node aNode, Graphics2D graphics,
            FontRenderContext context, double x, double y, double width,
            double height, boolean doClipping) {
        Color c = graphics.getColor();
        if (aNode == hnode) {
            highlightNode(aNode, graphics);
        }
        aNode.setArea(new Rectangle2D.Double(x, y, width, height));
        if (doClipping
                && !graphics.hitClip((int) x, (int) y, (int) width + 1,
                        (int) height + 1)) {
            detailNode(aNode, true);
            clipNode(aNode, false);
            return y + (height / 2);
        }
        if (aNode.isTerminal()) {
            drawTerminalNode(aNode, graphics, context, x, y, width, height);
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
        Iterator iterator = children.iterator();
        int leaves = aNode.getTerminals();
        double posy = y;
        double miny = Double.MAX_VALUE;
        double maxy = Double.MIN_VALUE;
        boolean noDetail = false;
        while (iterator.hasNext()) {
            Node aChild = (Node) iterator.next();
            if (noDetail == true) {
                detailNode(aChild, true);
                continue;
            }
            int childLeaves = aChild.getTerminals();
            double childHeight = (double) childLeaves / (double) leaves;
            double newy = height * childHeight;
            double centery = drawNode(aChild, graphics, context, x
                    + branchLength, posy, width - branchLength, newy,
                    doClipping);
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
        if (this.getSelectionManager().isNodeSelected(aNode.getParent())) {
            graphics.setColor(selectedBackground);
        } else {
            graphics.setColor(Color.black);
        }
        graphics.draw(new Line2D.Double(Math.rint(x), Math
                .rint((miny + maxy) / 2), Math.rint(x + branchLength), Math
                .rint((miny + maxy) / 2)));
        if (this.getSelectionManager().isNodeSelected(aNode)) {
            graphics.setColor(selectedBackground);
        } else {
            graphics.setColor(Color.black);
        }
        graphics.draw(new Line2D.Double(Math.rint(x + branchLength), Math
                .rint(miny), Math.rint(x + branchLength), Math.rint(maxy)));
        aNode.setPosition(new Point2D.Double(x + branchLength,
                (miny + maxy) / 2));
        //the maximum height available to write the label
        double maxLabelHeight = Math.min((y + (height / 2)) - miny, maxy - y
                - (height / 2));
        //the maximum width available to write the label
        drawNonTerminalNode(aNode, graphics, context, x + (branchLength * 0.2),
                (Math.rint((miny + maxy) / 2)) - (maxLabelHeight / 2), /* maxLabelWidth */
                width, maxLabelHeight);
        graphics.setColor(c);
        return (miny + maxy) / 2;
    }

    /**
     * Displays the label of a terminal node
     * @param aNode The node to be displayed
     * @param graphics The graphics context
     * @param context The font render context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     */
    private void drawTerminalNode(Node aNode, Graphics2D graphics,
            FontRenderContext context, double x, double y, double width,
            double height) {
        boolean boxed = terminalBoxed;
        List annotations = (List) aNode.getProperty(Node.USER_ANNOTATIONS);
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
            param.add(aNode.getLayoutSupport());
            annotations.add(param);
        }
        if (annotations.isEmpty() && aNode.isCollapsed()) {
            List param = new Vector();
            param.add("[" + aNode.getLeaves().size() + "]");
            param.add(aNode.getLayoutSupport());
            annotations.add(param);
        }
        if (annotations.isEmpty()) {
            return; // nothing to display
        }
        Color color = graphics.getColor();
        List layouts = new Vector();
        List layoutsAttr = new Vector();
        double layHeight = 0;
        double maxWidth = 0;
        Iterator iterator = annotations.iterator();
        while (iterator.hasNext()) {
            List param = (List) iterator.next();
            String annotation = (String) param.get(0);
            NodeLayoutSupport nls = (NodeLayoutSupport) param.get(1);
            Font font = null;
            if (nls != null) {
                font = nls.getFont();
            }
            if (font == null) {
                font = terminalFont;
            }
            TextLayout tl = new TextLayout(annotation, font, context);
            double labelNormalWidth = tl.getBounds().getWidth();
            while ((labelNormalWidth > width) && (annotation.length() > 0)) {
                annotation = annotation.substring(0, annotation.length() - 1);
                tl = new TextLayout(annotation, font, context);
                labelNormalWidth = tl.getBounds().getWidth();
            }
            layouts.add(tl);
            layoutsAttr.add(nls);
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
            TextLayout tl = (TextLayout) layouts.get(i);
            NodeLayoutSupport layoutAttr = (NodeLayoutSupport) layoutsAttr
                    .get(i);
            Color backColor = null;
            if (layoutAttr != null) {
                backColor = layoutAttr.getBgColor();
            }
            if (backColor == null) {
                backColor = terminalBackground;
            }
            Color fontColor = null;
            if (layoutAttr != null) {
                fontColor = layoutAttr.getColor();
            }
            if (fontColor == null) {
                fontColor = terminalColor;
            }
            graphics.setColor((backColor == null) ? graphics.getBackground()
                    : backColor);
            Rectangle2D lineArea = null;
            if (alignTerminalNodes) {
                lineArea = new Rectangle2D.Double(Math
                        .rint(((x + width) - maxWidth)), Math.rint(currentY),
                        Math.rint(maxWidth), Math.rint(tl.getAscent()
                                + tl.getDescent() + tl.getLeading()));
            } else {
                lineArea = new Rectangle2D.Double(Math.rint(x
                        + (baseBranchLength * (double) branchLengthFactor)),
                        Math.rint(currentY), Math.rint(maxWidth), Math.rint(tl
                                .getAscent()
                                + tl.getDescent() + tl.getLeading()));
            }
            if (backColor != null) {
                graphics.setColor(backColor);
                graphics.fill(lineArea);
            }
            if ((i == (nbLines - 1)) && lastAnnotIsCurrentLabel) {
                graphics.setColor(Color.black);
                graphics.draw(lineArea);
            }
            graphics.setColor(fontColor);
            currentY += (tl.getAscent());
            if (alignTerminalNodes) {
                tl.draw(graphics, (float) (Math.rint((x + width) - maxWidth)),
                        (float) (float) Math.rint(currentY));
            } else {
                tl.draw(graphics, (float) (Math.rint(x
                        + (baseBranchLength * (double) branchLengthFactor))),
                        (float) (float) Math.rint(currentY));
            }
            currentY += (tl.getDescent() + tl.getLeading());
        }
        if (boxed) {
            graphics.setColor(Color.black);
            graphics.draw(area);
        }
        if (this.getSelectionManager().isNodeSelected(aNode)) {
            graphics.setColor(selectedBackground);
            graphics.draw(new Line2D.Double(area.getMaxX()
                    - expandImage.getWidth(null), area.getMaxY(), area
                    .getMaxX()
                    - expandImage.getWidth(null), area.getMinY()));
        } else {
            graphics.setColor(Color.black);
        }
        if (alignTerminalNodes) {
            graphics.draw(new Line2D.Double(Math.rint(x), Math.rint(y
                    + (height / 2)), Math.rint((x + width) - area.getWidth()),
                    Math.rint(y + (height / 2))));
            aNode.setPosition(new Point2D.Double(Math.rint((x + width)
                    - area.getWidth()), Math.rint(y + (height / 2))));
        } else {
            graphics.draw(new Line2D.Double(Math.rint(x), Math.rint(y
                    + (height / 2)), Math.rint(x
                    + (baseBranchLength * (double) branchLengthFactor)), Math
                    .rint(y + (height / 2))));
            aNode.setPosition(new Point2D.Double(Math.rint(x
                    + (baseBranchLength * (double) branchLengthFactor)), Math
                    .rint(y + (height / 2))));
        }
        if (aNode.isCollapsed()) {
            graphics.drawImage(expandImage, (int) (area.getMaxX() - expandImage
                    .getWidth(null)), (int) ((area.getMinY() + (area
                    .getHeight() / 2)) - (expandImage.getHeight(null) / 2)),
                    null);
        }
        graphics.setColor(color);
    }

    /**
     * Displays the label of a non terminal node
     * @param aNode The node to be displayed
     * @param graphics The graphics context
     * @param context The font render context
     * @param x The horizontal position of the node
     * @param y The vertical position of the node
     * @param width The width of the area used to display the node
     * @param height The height of the area used to display the node
     */
    private void drawNonTerminalNode(Node aNode, Graphics2D graphics,
            FontRenderContext context, double x, double y, double width,
            double height) {
        List annotations = (List) aNode.getProperty(Node.USER_ANNOTATIONS);
        if (annotations == null) {
            annotations = new Vector();
        } else {
            annotations = new Vector(annotations);
        }
        boolean lastAnnotIsCurrentLabel = false;
        String label = aNode.getLabel();
        if (!label.equals("")) {
            List param = new Vector();
            param.add(label);
            param.add(aNode.getLayoutSupport());
            annotations.add(param);
            lastAnnotIsCurrentLabel = true;
        }
        if (annotations.isEmpty()) {
            return; // nothing to display
        }
        Color c = graphics.getColor();
        List layouts = new Vector();
        List layoutsAttr = new Vector();
        double layHeight = 0;
        double maxWidth = 0;
        Iterator iterator = annotations.iterator();
        while (iterator.hasNext()) {
            List param = (List) iterator.next();
            String annot = (String) param.get(0);
            NodeLayoutSupport nls = (NodeLayoutSupport) param.get(1);
            Font font = null;
            if (nls != null) {
                font = nls.getFont();
            }
            if (font == null) {
                font = nonTerminalFont;
            }
            TextLayout tl = new TextLayout(annot, font, context);
            double labelNormalWidth = tl.getBounds().getWidth();

            while ((labelNormalWidth > width) && (annot.length() > 0)) {
                annot = annot.substring(0, annot.length() - 1);
                tl = new TextLayout(annot, font, context);
                labelNormalWidth = tl.getBounds().getWidth();
            }
            layouts.add(tl);
            layoutsAttr.add(nls);
            layHeight += (tl.getAscent() + tl.getDescent() + tl.getLeading());
            maxWidth = Math.max(maxWidth, tl.getAdvance());
        }

        int nbLines = layouts.size();
        double currentY = (y + (height / 2)) - ((layHeight) / 2);
        Rectangle2D area = new Rectangle2D.Double(Math.rint(x), Math
                .rint(currentY), Math.rint(maxWidth), Math.rint(layHeight));

        for (int i = 0; i < nbLines; i++) {
            TextLayout tl = (TextLayout) layouts.get(i);
            NodeLayoutSupport nls = (NodeLayoutSupport) layoutsAttr.get(i);
            Color backColor = null;
            if (nls != null) {
                backColor = nls.getBgColor();
            }
            if (backColor == null) {
                backColor = nonTerminalBackground;
            }
            Color fontColor = null;
            if (nls != null) {
                fontColor = nls.getColor();
            }
            if (fontColor == null) {
                fontColor = nonTerminalColor;
            }
            graphics.setColor((backColor == null) ? graphics.getBackground()
                    : backColor);

            Rectangle2D lineArea = new Rectangle2D.Double(Math.rint(x), Math
                    .rint(currentY), Math.rint(maxWidth), Math.rint(tl
                    .getAscent()
                    + tl.getDescent() + tl.getLeading()));
            graphics.fill(lineArea);
            if ((i == (nbLines - 1)) && lastAnnotIsCurrentLabel) {
                graphics.setColor(Color.black);
                graphics.draw(lineArea);
            }
            graphics.setColor(fontColor);
            currentY += (tl.getAscent());
            tl.draw(graphics, (float) (Math.rint(x)), (float) Math
                    .rint(currentY));
            currentY += (tl.getDescent() + tl.getLeading());
        }

        if (nonTerminalBoxed) {
            graphics.setColor(Color.black);
            graphics.draw(area);
        }
        graphics.setColor(c);
    }

    /**
     * Draw a mark relative of the tree to show the position of selected nodes
     * @param graphics The graphics context
     * @param selection The list of selected nodes
     * @param color The color to use to fill the area
     * @param x The horizontal position where to draw selected marks
     * @param width The width of the marks
     */
    private void displaySelected(Graphics2D graphics, Selection selection,
            double x, double width) {
        if (selection == null) {
            return;
        }
        if (selection.getNodes() == null) {
            return;
        }
        if (selection.getNodes().isEmpty()) {
            return;
        }
        Color color = selection.getColor();
        Color bgc = selection.getBackgroundColor();
        Color c = getBackground();
        if (bgc != null) {
            graphics.setColor(bgc);
            Rectangle2D.Double rootArea = (Rectangle2D.Double) getClassificationRootNode()
                    .getArea();
            if (rootArea != null) {
                graphics.fill(new Rectangle2D.Double(Math.rint(x), Math
                        .rint(rootArea.y), Math.rint(width), Math
                        .rint(rootArea.height)));
            }
        }
        graphics.setColor(color);
        Iterator it = selection.getNodes().iterator();
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
                    graphics.fill(new Rectangle2D.Double(Math.rint(x), Math
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
                    Polygon polygon = new Polygon();
                    polygon.addPoint((int) x,
                            (int) (aNodeArea.y + (aNodeArea.height / 2)));
                    polygon
                            .addPoint(
                                    (int) (x + width),
                                    (int) ((aNodeArea.y + (aNodeArea.height / 2)) - (width / 2)));
                    polygon.addPoint((int) (x + width), (int) (aNodeArea.y
                            + (aNodeArea.height / 2) + (width / 2)));
                    graphics.fill(polygon);
                }
            } else {
                graphics.fill(new Rectangle2D.Double(Math.rint(x), Math
                        .rint(aNodeArea.y), Math.rint(width), Math.rint(Math
                        .max(aNodeArea.height, 1))));
            }
        }
        graphics.setColor(c);
    }

    /**
     * Draw a colored representation of expression values
     * @param g The graphics context
     * @param leaves The list of leaves in the displayed tree
     * @param x The horizontal position where to display expression values
     */
    private void displayExpressionValues(Graphics2D g, List leaves, double x) {
        if (leaves == null) {
            return;
        }
        if (leaves.isEmpty()) {
            return;
        }
        Iterator iterator = leaves.iterator();
        Color c = getBackground();
        double posy = 0;
        while (iterator.hasNext()) {
            Node aNode = (Node) iterator.next();
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
            measures = (List) knownNode.getProperty(Node.MEASURES);

            if (measures == null) {
                measures = new Vector();
                List listSub = knownNode.getLeaves();
                measures.addAll((Collection) ((Node) listSub.get(0))
                        .getProperty(Node.MEASURES));
                for (int leafCtr = 1; leafCtr < listSub.size(); leafCtr++) {
                    Node leaf = (Node) listSub.get(leafCtr);
                    List leafMeasure = (List) leaf.getProperty(Node.MEASURES);

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
                knownNode.addProperty(Node.MEASURES, measures);
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
     * Compute the maximum label width of subnodes of this node.
     * @param font The font
     * @param aNode The currentRootNode of the subtree
     * @param graphics The graphics context
     * @param context The font render context
     * @return The maximum width occupied on the screen by the label of this
     *         node or all its subnodes
     */
    private double computeLabelMaxWidth(Node aNode, Graphics2D graphics,
            Font font, FontRenderContext context) {
        String label = aNode.getLabel();

        if (label.equals("")) {
            return 0;
        }
        if (aNode.isTerminal()) {
            TextLayout tl = new TextLayout(label, font, context);
            Rectangle2D bounds = tl.getBounds();
            return bounds.getWidth();
        }
        double maxWidth = 0;
        List children = aNode.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Node aChild = (Node) it.next();
            maxWidth = Math.max(computeLabelMaxWidth(aChild, graphics, font,
                    context), maxWidth);
        }
        return maxWidth;
    }

    private double computeBaseBranchLength(Node aNode, double width,
            Graphics2D graphics, Font font, FontRenderContext context) {
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
                    graphics, font, context), baseBranchLength);
        }
        return baseBranchLength;
    }

    private void highlightNode(Node aNode, Graphics2D graphics) {
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
            graphics.setColor(new Color(255, 255, 223));
            if (aNode != null) {
                ((Graphics2D) graphics).fill(aNodeRectangle);
            }
            break;
        case 2:
            graphics.setColor(new Color(0, 0, 255));
            if (aNode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(aNodeRectangle);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) graphics).draw(rect);
            }
            break;
        case 3:
            graphics.setColor(new Color(0, 0, 255));
            if (aNode != null) {
                ((Graphics2D) graphics)
                        .fill(new Ellipse2D.Double(aNodePosition.x
                                - labelHeight, aNodePosition.y - labelHeight,
                                labelHeight * 2, labelHeight * 2));
            }
            break;
        }
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

    /**
     * Sets the given node to be used as a currentRootNode node for the
     * currently displayed tree.
     */
    public void setCurrentRootNode(Node aNode) {
        currentRootNode = aNode;
        expValMinMeasure = 0;
        expValMaxMeasure = 0;
        expValUnderExpDeciles = null;
        expValOverExpDeciles = null;
        int nbMeasures = ((Integer) getClassificationRootNode().getProperty(
                Node.NB_MEASURES)).intValue();
        if (nbMeasures > 0) {
            expValMinMeasure = ((Double) getClassificationRootNode()
                    .getProperty(Node.MIN_MEASURE)).doubleValue();
            expValMaxMeasure = ((Double) getClassificationRootNode()
                    .getProperty(Node.MAX_MEASURE)).doubleValue();
            expValUnderExpDeciles = (List) getClassificationRootNode()
                    .getProperty(Node.UNDER_EXP_DECILES);
            expValOverExpDeciles = (List) getClassificationRootNode()
                    .getProperty(Node.OVER_EXP_DECILES);
        }
        if (selectionsList != null) {
            Iterator it = selectionsList.iterator();
            while (it.hasNext()) {
                int countSelected = 0;
                Selection selection = (Selection) it.next();
                Iterator it2 = selection.getNodes().iterator();
                while (it2.hasNext()) {
                    Node node = (Node) it2.next();
                    if (currentRootNode.isAncestorOf(node)) {
                        countSelected++;
                    }
                }
                String localHits = countSelected + "/"
                        + currentRootNode.getNumberOfLeaves();
                String selectionID = selection.getId();
            }
        }
        if (!this.getSelectionManager().getSelectedNodes().isEmpty()) {
            List selectedLeaves = this.getSelectionManager()
                    .getSelectedLeaves();
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
            String selectionID = String.valueOf(this.getSelectionManager()
                    .getNumberOfSelections());
        }
        baseBranchLength = -1; // force the recalculation
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#zoom(double,
     *      double)
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
        Dimension maximum = getMaximumSize();
        Dimension dimension = new Dimension(
                (int) ((width * zoomx) / currentZoomX),
                (int) ((height * zoomy) / currentZoomY));
        if (dimension.width > maximum.width) {
            dimension.width = maximum.width;
        }
        if (dimension.height > maximum.height) {
            dimension.height = maximum.height;
        }
        setPreferredSize(dimension);
        if (currentZoomX != zoomx) {
            baseBranchLength = -1; // to force the recalculation
        }
        currentZoomX = zoomx;
        currentZoomY = zoomy;
        revalidate();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#getCurrentZoomX()
     */
    public double getCurrentZoomX() {
        return this.currentZoomX;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#getCurrentZoomY()
     */
    public double getCurrentZoomY() {
        return this.currentZoomY;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#setZoomManager(fr.unice.bioinfo.thea.classification.editor.ZoomManager)
     */
    public void setZoomManager(ZoomManager zm) {
        this.zoomListener = zm;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#getZoomManager()
     */
    public ZoomManager getZoomManager() {
        return this.zoomListener;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#setZoomingMode()
     */
    public void setZoomingMode() {
        if (zoomListener == null)
            return;
        this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                zoomImage, new Point(0, 0), "zooming"));
        this.removeMouseListener(standardListener);
        this.removeMouseMotionListener(standardListener);
        this.addMouseListener(zoomListener);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Zoomable#setStandardMode()
     */
    public void setStandardMode() {
        this.setCursor(Cursor.getDefaultCursor());
        this.removeMouseListener(zoomListener);
        this.addMouseListener(standardListener);
        this.addMouseMotionListener(standardListener);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#showWholeTree()
     */
    public void showWholeTree() {
        Node aNode = null;
        while (!roots.empty()) {
            aNode = (Node) roots.pop();
        }
        setCurrentRootNode(aNode);
        zoom(1, 1);
        updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#showPreviousTree()
     */
    public void showPreviousTree() {
        Node aNode = (Node) roots.pop();
        setCurrentRootNode(aNode);
        zoom(1, 1);
        updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#showOnLevelUp()
     */
    public void showOnLevelUp() {
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
        updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#showSubTree(fr.unice.bioinfo.thea.classification.Node)
     */
    public void showSubTree(Node aNode) {
        roots.push(getCurrentRootNode());
        setCurrentRootNode(aNode);
        zoom(1, 1);
        updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#getCurrentRootNode()
     */
    public Node getCurrentRootNode() {
        return currentRootNode;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#getClassificationRootNode()
     */
    public Node getClassificationRootNode() {
        if (roots.isEmpty()) {
            return currentRootNode;
        } else {
            return (Node) roots.elementAt(0);
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#collapseNode(fr.unice.bioinfo.thea.classification.Node)
     */
    public void collapseNode(Node aNode) {
        collapsedNodes.add(aNode);
        zoom(1, 1);
        updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#uncollapseNode(fr.unice.bioinfo.thea.classification.Node)
     */
    public void uncollapseNode(Node aNode) {
        collapsedNodes.remove(aNode);
        updateGraphics();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#locateNode(fr.unice.bioinfo.thea.classification.Node,
     *      java.awt.geom.Point2D)
     */
    public Node locateNode(Node aNode, Point2D position) {
        if (aNode == null) {
            return null;
        }
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
            Node aChild = (Node) iterator.next();
            Node node = locateNode(aChild, position);
            if (node != null) {
                return node;
            }
        }
        return aNode;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#highlightSurroundingArea(fr.unice.bioinfo.thea.classification.Node)
     */
    public void highlightSurroundingArea(Node aNode) {
        if (hnode == aNode) {
            return;
        }
        //        List selectionsList;
        Graphics graphics = getGraphics();
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
            graphics.setXORMode(new Color(0, 0, 32));
            if (hnode != null) {
                ((Graphics2D) graphics).fill(hnodeRectangle);
            }
            if (aNode != null) {
                ((Graphics2D) graphics).fill(aNodeRectangle);
            }
            break;
        case 2:
            graphics.setXORMode(new Color(255, 255, 0));
            if (hnode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(hnodeRectangle);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) graphics).draw(rect);
            }
            if (aNode != null) {
                Rectangle2D.Double rect = new Rectangle2D.Double();
                rect.setRect(aNodeRectangle);
                rect.x += labelHeight;
                rect.width -= labelHeight;
                ((Graphics2D) graphics).draw(rect);
            }
            break;
        case 3:
            graphics.setXORMode(new Color(255, 255, 0));
            if (hnode != null) {
                ((Graphics2D) graphics)
                        .fill(new Ellipse2D.Double(hnodePosition.x
                                - labelHeight, hnodePosition.y - labelHeight,
                                labelHeight * 2, labelHeight * 2));
            }
            if (aNode != null) {
                ((Graphics2D) graphics)
                        .fill(new Ellipse2D.Double(aNodePosition.x
                                - labelHeight, aNodePosition.y - labelHeight,
                                labelHeight * 2, labelHeight * 2));
            }
            break;
        }
        hnode = aNode;
        graphics.setPaintMode();
        //        repaint();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#setPopupMenuVisible(boolean)
     */
    public void setPopupMenuVisible(boolean b) {
        if (this.popupMenu == null)
            return;
        this.popupMenu.setVisible(b);
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#isShowingPopupMenu()
     */
    public boolean isShowingPopupMenu() {
        if (this.popupMenu == null)
            return false;
        return this.popupMenu.isShowing();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#getSelectionManager()
     */
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#setPopupMenu(javax.swing.JPopupMenu)
     */
    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.DrawableClassification#getExpressionColumnIndex(java.awt.geom.Point2D)
     */
    public int getExpressionColumnIndex(Point2D point2D) {
        int width = getWidth() - 15;
        if (showExpressionValues) {
            width -= (expValNbMeasures * expValColumnWidth);
        }
        int index = (int) Math.ceil((width - point2D.getX()) / 10);
        return index;
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Drawable#updateGraphics()
     */
    public void updateGraphics() {
        invalidate();
        repaint();
    }

    /*
     * (non-Javadoc)
     * @see fr.unice.bioinfo.thea.classification.editor.Drawable#repaintRectangle(int,
     *      int, int, int)
     */
    public void repaintRectangle(int x, int y, int width, int height) {
        repaint(x, y, width, height);
    }
}