package fr.unice.bioinfo.thea.editor;

import java.util.EventListener;

/*
 * this interface should be renamed since the actual name give place to
 * confusion about what it is designed for, in fact: classes that implemnt this
 * interface are meant to listen to nodes selection in the classification viewer
 * Proposal for new name: NodeSelectionListener @author SAÏD, EL KASMI.
 */
public interface TreeSelectionListener extends EventListener {
    public void nodeSelected(TreeSelectionEvent e);

    public void nodeDeselected(TreeSelectionEvent e);

    public void nodeClicked(TreeSelectionEvent e);

    public void nodeSetSelected(TreeSelectionEvent e);
}