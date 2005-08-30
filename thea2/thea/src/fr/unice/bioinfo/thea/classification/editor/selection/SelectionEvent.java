package fr.unice.bioinfo.thea.classification.editor.selection;

import java.awt.Color;
import java.util.EventObject;
import java.util.List;

/**
 * This is an event class that holds all of information necessary for a {@link
 * SelectionListener} to figure out what is selected.
 * @author Claude Pasquier.
 * @author Saïd El Kasmi.
 */
public class SelectionEvent extends EventObject {
    /** An ID to identife the selection with. */
    private String selectionId;

    /** A name to attribute to the selection */
    private String selectionName;

    private String selectionLabel;

    private Color selectionColor;

    /** The color used to colorize the bar that materialize the selection */
    private Color selectionBgColor;

    private String nbGlobalHits;

    private String nbLocalHits;

    /** List of nodes correspending to the selection. */
    private List selectedNodes;

    /**
     * Creates a selection event.
     * @param source The object upon which the event in question occured on
     * @param id The slection's identifier (ID).
     */
    public SelectionEvent(Object source, String id) {
        super(source);
        this.selectionId = id;
        this.selectionName = null;
        this.selectionLabel = null;
        this.selectionColor = null;
        this.selectionBgColor = null;
        this.nbGlobalHits = null;
        this.nbLocalHits = null;
        this.selectedNodes = null;
    }

    /**
     * Creates a selection event.
     * @param source The object upon which the event in question occured on
     * @param id The slection's identifier (ID).
     * @param name The selection's name
     * @param color The selection's color.
     * @param nbGlobalHits
     * @param nbLocalHits
     * @param nodes Nodes correspending to the selection.
     */
    public SelectionEvent(Object source, String id, String name, Color color,
            String nbGlobalHits, String nbLocalHits, List nodes) {
        super(source);
        this.selectionId = id;
        this.selectionName = name;
        this.selectionColor = color;
        this.nbGlobalHits = nbGlobalHits;
        this.nbLocalHits = nbLocalHits;
        this.selectedNodes = nodes;
    }

    /**
     * Creates a selection event.
     * @param source The object upon which the event in question occured on
     * @param id The slection's identifier (ID).
     * @param name The selection's name
     * @param color The selection's color.
     * @param bgColor The background's color.
     * @param nbGlobalHits
     * @param nbLocalHits
     * @param nodes Nodes correspending to the selection.
     */
    public SelectionEvent(Object source, String id, String name,
            String selectionLabel, Color color, Color bgColor,
            String nbGlobalHits, String nbLocalHits, List nodes) {
        super(source);
        this.selectionId = id;
        this.selectionName = name;
        this.selectionLabel = selectionLabel;
        this.selectionColor = color;
        this.selectionBgColor = bgColor;
        this.nbGlobalHits = nbGlobalHits;
        this.nbLocalHits = nbLocalHits;
        this.selectedNodes = nodes;
    }

    /**
     * @return Returns the nbGlobalHits.
     */
    public String getNbGlobalHits() {
        return nbGlobalHits;
    }

    /**
     * @return Returns the nbLocalHits.
     */
    public String getNbLocalHits() {
        return nbLocalHits;
    }

    /**
     * @return Returns the selectedNodes.
     */
    public List getSelectedNodes() {
        return selectedNodes;
    }

    /**
     * @return Returns the selectionColor.
     */
    public Color getSelectionColor() {
        return selectionColor;
    }

    /**
     * @return Returns the selectionId.
     */
    public String getSelectionId() {
        return selectionId;
    }

    /**
     * @return Returns the selectionLabel.
     */
    public String getSelectionLabel() {
        return selectionLabel;
    }

    /**
     * @return Returns the selectionName.
     */
    public String getSelectionName() {
        return selectionName;
    }

    /**
     * @return Returns the selectionBgColor.
     */
    public Color getSelectionBgColor() {
        return selectionBgColor;
    }
}