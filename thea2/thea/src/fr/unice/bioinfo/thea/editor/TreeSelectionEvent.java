package fr.unice.bioinfo.thea.editor;

import java.awt.event.MouseEvent;
import java.util.EventObject;

public class TreeSelectionEvent extends EventObject {
    Object selected;
    MouseEvent mouseEvent;

    public TreeSelectionEvent(Object obj, Object selected, MouseEvent mouseEvent) {
        super(obj);
        this.selected = selected;
        this.mouseEvent = mouseEvent;
    }

    public Object getSelected() {
        return selected;
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }
}