package fr.unice.bioinfo.thea.classification;

import java.awt.Color;
import java.awt.Font;

/**
 * A class representing the layout attributes of a node.
 */
public class NodeLayoutSupport {
    public static int NO_FRAME = 0;

    public static int RECTANGLE = 1;

    public static int ROUND_RECTANGLE = 2;

    public static int ELLIPSE = 3;

    /** The font used to write the label of the node */
    private Font font;

    /** The color used to write the label of the node */
    private Color color;

    /** The color used as a background for the node */
    private Color bgColor;

    /**
     * The shape used to frame the node's label. One of RECTANGLE,
     * ROUND_RECTANGLE, ELLIPSE
     */
    private int framingShape;

    /** The color used to draw the node's frame */
    private Color framingColor;

    /**
     * Default constructor
     */
    public NodeLayoutSupport() {
        init();
    }

    /**
     * Constructor used to set all attributes
     */
    public NodeLayoutSupport(Font font, Color color, Color bgColor,
            int framingShape, Color framingColor) {
        this.font = font;
        this.color = color;
        this.bgColor = bgColor;
        this.framingShape = framingShape;
        this.framingColor = framingColor;
    }

    /**
     * Initialize node
     */
    private void init() {
        font = null;
        color = null;
        bgColor = null;
        framingShape = NO_FRAME;
        framingColor = null;
    }

    /**
     * Setter for font
     * 
     * @param s
     *            The value for font
     */
    public void setFont(Font f) {
        font = f;
    }

    /**
     * Getter for font
     * 
     * @return The font used to display the node
     */
    public Font getFont() {
        return font;
    }

    /**
     * Setter for color
     * 
     * @param s
     *            The value for color
     */
    public void setColor(Color c) {
        color = c;
    }

    /**
     * Getter for color
     * 
     * @return The color used to display the node
     */
    public Color getColor() {
        return color;
    }

    /**
     * Setter for bgColor
     * 
     * @param s
     *            The value for bgColor
     */
    public void setBgColor(Color c) {
        bgColor = c;
    }

    /**
     * Getter for bgColor
     * 
     * @return The color used to display the background of node
     */
    public Color getBgColor() {
        return bgColor;
    }

    /**
     * Setter for framingShape
     * 
     * @param s
     *            The value for framingShape
     */
    public void setFramingShape(int s) {
        framingShape = s;
    }

    /**
     * Getter for framingShape
     * 
     * @return The shape of the node's frame
     */
    public int getFramingShape() {
        return framingShape;
    }

    /**
     * Setter for framingColor
     * 
     * @param s
     *            The value for framingColor
     */
    public void setFramingColor(Color c) {
        framingColor = c;
    }

    /**
     * Getter for framingColor
     * 
     * @return The color used to display the node's frame
     */
    public Color getFramingColor() {
        return framingColor;
    }
}