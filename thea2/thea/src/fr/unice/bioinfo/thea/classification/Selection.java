package fr.unice.bioinfo.thea.classification;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Selection {

    /** Color used to draw selection. */
    private Color color = null;

    /** Background color used to draw selection. */
    private Color backgroundColor = null;

    /** Selection's ID. */
    private String id = null;

    /** Semection's name. */
    private String name = null;

    /** Flag to register selection's frozen/unfrozen state. */
    private boolean frozen = false;

    /** The List of nodes */
    private List nodes;

    public Selection() {
        nodes = new ArrayList();
    }

    public Selection(List nodes) {
        this.nodes = nodes;
    }

    /**
     * Creates a selection using the given nodes list, color and id.
     * @param nodes Selected nodes.
     * @param id Selection's id.
     * @param color Selection's color.
     */
    public Selection(List nodes, String id, Color color) {
        this(nodes);
        this.color = color;
        this.id = id;
    }

    /** @return The List of nodes. */
    public List getNodes() {
        return nodes;
    }

    /**
     * @return Returns the backgroundColor.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor The backgroundColor to set.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return Returns the color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color The color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return Returns the frozen.
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * @param frozen The frozen to set.
     */
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}