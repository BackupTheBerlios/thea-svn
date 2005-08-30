package fr.unice.bioinfo.thea.classification.editor.util;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import fr.unice.bioinfo.thea.classification.settings.CESettings;

/**
 * A class to discretize data
 */
public class Discretization implements PropertyChangeListener {
    public static int PALETTE_GREEN_BLACK_RED = 1;

    public static int PALETTE_BLUE_WHITE_YELLOW = 2;

    public static int PALETTE_BLUE_WHITE_PINK = 3;

    /** the list of upper slice values */
    static List upperLimits;

    /** the list of associated colors */
    static List colors;

    private static int nbSlices = 1;

    private static float medianPos = 0;

    private static Double medianValue;

    private static List sortedMeasures;

    /** The color palette used to display expression values: <br> */
    private static int colorPalette = PALETTE_GREEN_BLACK_RED;

    private static float underExpDecileValue;

    private static float overExpDecileValue;

    private static boolean isInitialized = false;

    Double minValue;

    Double maxValue;

    boolean isLogValues;

    private Discretization() {
        // Register itself as a listener to changes in global settings:
        CESettings.getInstance().addPropertyChangeListener(this);
    }

    public Discretization(List measures, List lowerLimits, List cols, int slices) {
        this();
        nbSlices = slices;
        sortedMeasures = new Vector(measures);
        colors = new Vector(cols);
        upperLimits = new Vector(lowerLimits);
        upperLimits.add(new Double(Double.MAX_VALUE));
        upperLimits.remove(0);
        isInitialized = true;
    }

    public Discretization(List measures, boolean logValues, int slices) {
        this();
        nbSlices = slices;
        sortedMeasures = new Vector(measures);
        medianValue = (logValues ? new Double(0) : new Double(1));
        sortedMeasures.add(medianValue);
        Collections.sort(sortedMeasures);
        medianPos = sortedMeasures.indexOf(medianValue);
        underExpDecileValue = medianPos / (nbSlices / 2);
        overExpDecileValue = (float) ((float) sortedMeasures.size() - medianPos)
                / (nbSlices / 2);
        initColor();
        isInitialized = true;
    }

    public static void initColor() {
        colors = new Vector();
        upperLimits = new Vector();

        float red = (float) 0.0;
        float green = (float) 1.0;
        float blue = (float) 0.0;

        if ((colorPalette == PALETTE_BLUE_WHITE_YELLOW)
                || (colorPalette == PALETTE_BLUE_WHITE_PINK)) {
            red = (float) 0.0;
            green = (float) 0.0;
            blue = (float) 1.0;
        }

        for (int i = (nbSlices / 2) - 1; i > 0; i--) {
            upperLimits.add(sortedMeasures.get(Math.round(medianPos
                    - (underExpDecileValue * i))));
            colors.add(new Color(red, green, blue));

            if (colorPalette == PALETTE_GREEN_BLACK_RED) {
                green -= ((float) 1.0 / (float) (nbSlices / 2));
            } else {
                green += ((float) 1.0 / (float) (nbSlices / 2));
                red += ((float) 1.0 / (float) (nbSlices / 2));
            }
        }

        upperLimits.add(medianValue);
        colors.add(new Color(red, green, blue));
        red = (float) 0.0;
        green = (float) 0.0;
        blue = (float) 0.0;

        if ((colorPalette == PALETTE_BLUE_WHITE_YELLOW)
                || (colorPalette == PALETTE_BLUE_WHITE_PINK)) {
            red = (float) 1.0;
            green = (float) 1.0;
            blue = (float) 1.0;
        }

        for (int i = 1; i < (nbSlices / 2); i++) {
            upperLimits.add(sortedMeasures.get(Math.round(medianPos
                    + (overExpDecileValue * i))));

            if (colorPalette == PALETTE_GREEN_BLACK_RED) {
                red += ((float) 1.0 / (float) (nbSlices / 2));
            } else if (colorPalette == PALETTE_BLUE_WHITE_YELLOW) {
                blue -= ((float) 1.0 / (float) (nbSlices / 2));
            } else if (colorPalette == PALETTE_BLUE_WHITE_PINK) {
                green -= ((float) 1.0 / (float) (nbSlices / 2));
            }

            colors.add(new Color(red, green, blue));
        }

        upperLimits.add(new Double(Double.MAX_VALUE));
        red = (float) 1.0;
        green = (float) 0.0;
        blue = (float) 0.0;

        if (colorPalette == PALETTE_BLUE_WHITE_YELLOW) {
            red = (float) 1.0;
            green = (float) 1.0;
            blue = (float) 0.0;
        } else if (colorPalette == PALETTE_BLUE_WHITE_YELLOW) {
            red = (float) 1.0;
            green = (float) 0.0;
            blue = (float) 1.0;
        }

        colors.add(new Color(red, green, blue));
    }

    static public Color getColor(double value) {
        Color colorValue = null;

        for (int i = 0; i < upperLimits.size(); i++) {
            double d = ((Double) upperLimits.get(i)).doubleValue();

            if (value < d) {
                colorValue = (Color) colors.get(i);

                break;
            }
        }

        return colorValue;
    }

    static public Color getColor(Double value) {
        Color colorValue = null;

        for (int i = 0; i < upperLimits.size(); i++) {
            if (value.compareTo((Double) upperLimits.get(i)) < 0) {
                colorValue = (Color) colors.get(i);

                break;
            }
        }

        return colorValue;
    }

    static public void setColorPalette(int cPalette) {
        colorPalette = cPalette;
    }

    static public void setColorPalette(List lowerLimits, List cols, int slices) {
        nbSlices = slices;
        colors = new Vector(cols);
        upperLimits = new Vector(lowerLimits);
        upperLimits.add(new Double(Double.MAX_VALUE));
        upperLimits.remove(0);
    }

    static public void setNbSlices(int slices) {
        nbSlices = slices;
        underExpDecileValue = medianPos / (nbSlices / 2);
        overExpDecileValue = (float) ((float) sortedMeasures.size() - medianPos)
                / (nbSlices / 2);
    }

    static public List getLowerBounds() {
        List lowerBounds = new Vector(upperLimits);
        lowerBounds.add(0, new Double(Double.NEGATIVE_INFINITY));
        lowerBounds.remove(new Double(Double.MAX_VALUE));

        return lowerBounds;
    }

    static public List getColors() {
        return (List) new Vector(colors);
    }

    static public boolean isInitialized() {
        return isInitialized;
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equalsIgnoreCase(CESettings.PROP_BWP_SELECT)) {
            setColorPalette(Discretization.PALETTE_BLUE_WHITE_PINK);
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_GBR_SELECT)) {
            setColorPalette(Discretization.PALETTE_GREEN_BLACK_RED);
        } else if (e.getPropertyName().equalsIgnoreCase(
                CESettings.PROP_BWY_SELECT)) {
            setColorPalette(Discretization.PALETTE_BLUE_WHITE_YELLOW);
        }
    }
}