package fr.unice.bioinfo.thea.editor.util;

import java.awt.Color;
import java.awt.Font;

/**
 * @author SAÏD, EL KASMI.
 */
public class Consts {
    public static int TYPE_UNDEFINED = 0;
    public static int TYPE_EISEN = 1;
    public static int TYPE_NEWICK = 2;
    public static int TYPE_SOTA = 3;
    public static int TYPE_XML = 4;
    public static int TYPE_UNCLUSTERED = 5;
    public static int SEL_NO_SELECTION = 0;
    public static final int SEL_IGNORED_COLUMNS = 1;
    public static final int SEL_IGNORED_ROWS = 2;
    public static final int SEL_GENE_LABELS = 3;
    public static final int SEL_COLUMNS_LABELS = 4;
    public static String[] format = { "Undefined", "Eisen (gtr & cdt files)",
            "New Hampshire (newick)", "Sota", "xml", "Unclustered data" };

    /** The colors used to display selections */
    public static Color[] SELECTIONS_COLOR = new Color[] {
            new Color(255, 128, 0), new Color(0, 128, 0), new Color(0, 0, 255),
            new Color(128, 0, 128), new Color(0, 0, 160), new Color(128, 0, 0),
            new Color(128, 0, 255), new Color(255, 0, 128),
            new Color(128, 128, 255), new Color(128, 128, 0),
            new Color(0, 255, 0), new Color(0, 255, 255),
            new Color(255, 255, 0), new Color(0, 255, 128),
            new Color(255, 128, 255), new Color(64, 128, 128),
            new Color(192, 192, 192), new Color(64, 0, 0) };

    /** The font used to draw labels of terminal nodes */
    public static Font TERMINAL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The font used to draw labels of non terminal nodes */
    public static Font NON_TERMINAL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The font used to draw info strings */
    public static Font INFO_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The font used to draw labels of selected nodes */
    public static Font selectedFont;

    /** The color used to draw labels of terminal nodes */
    public static Color TERMINAL_COLOR = Color.black;

    /** The color used to draw labels of non terminal nodes */
    public static Color NON_TERMINAL_COLOR = Color.black;

    /** The background color used to draw labels of selected nodes */
    public static Color SELECTED_BACKGROUND = new Color(192, 0, 0);

    /** The background color used to draw labels of terminal nodes */
    public static Color TERMINAL_BACKGROUND = null;

    /** The background color used to draw labels of terminal nodes */
    public static Color NON_TERMINAL_BACKGROUND = null;

    /** The color used to draw labels of selected nodes */
    public static Color SELECTED_COLOR = Color.white;
}