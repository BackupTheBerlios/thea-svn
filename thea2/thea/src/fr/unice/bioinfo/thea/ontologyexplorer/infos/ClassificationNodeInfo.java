package fr.unice.bioinfo.thea.ontologyexplorer.infos;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Hashtable;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

import fr.unice.bioinfo.thea.classification.Classification;
import fr.unice.bioinfo.thea.classification.io.ClassificationFactory;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ClassificationNode;

/**
 * @author Saïd El Kasmi.
 */
public class ClassificationNodeInfo extends Hashtable implements Node.Cookie {

    public static final String NAME = "name"; //NOI18N
    // File format
    public static final String TYPE = "type"; //NOI18N
    // Index of First Ignored Row
    public static final String IFIR = "ifir"; //NOI18N
    // Index of Last Ignored Row
    public static final String ILIR = "ilir"; //NOI18N
    // Index of First Ignored Column
    public static final String IFIC = "ific"; //NOI18N
    //  Index of Last Ignored Column
    public static final String ILIC = "ilic"; //NOI18N
    // Index of Gene Column
    public static final String IGC = "igc"; //NOI18N
    // Index of Title Row
    public static final String ITR = "itr"; //NOI18N
    // number of columns
    public static final String NBC = "nbc"; //NOI18N
    // Clustered data file
    public static final String CDF = "cdf"; //NOI18N
    // Tabulat Data File
    public static final String TDF = "tdf"; //NOI18N

    /* Owning node */
    private WeakReference nodewr = null;

    private Classification classification = null;

    private AbstractNode linkedOntologyNode;

    public ClassificationNodeInfo() {
        super();
    }

    public static ClassificationNodeInfo createClNodeInfo() {
        ClassificationNodeInfo cni = null;
        cni = new ClassificationNodeInfo();
        return cni;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
     */
    public synchronized Object put(Object key, Object value) {
        //return super.put(key, value);
        Object old = get(key);

        if (key == null)
            throw new NullPointerException();

        if (value != null)
            super.put(key, value);
        else
            remove(key);
        return old;
    }

    /** Called by property editor */
    public Object getProperty(String key) {
        return get(key);
    }

    /** Called by property editor */
    public void setProperty(String key, Object value) {
        put(key, value);
    }

    /** Returns the name of the correspeinding node */
    public String getName() {
        return (String) get(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    /** Returns the {@link ClassificationNode}node */
    public ClassificationNode getNode() {
        if (nodewr != null)
            return (ClassificationNode) nodewr.get();
        return null;
    }

    /** Sets the {@link ClassificationNode}node */
    public void setNode(ClassificationNode node) {
        nodewr = new WeakReference(node);
    }

    /**
     * Returns clustered data file
     * @return Returns the cFile.
     */
    public File getCFile() {
        return (File) get(CDF);
    }

    /**
     * @param file The cFile to set.
     */
    public void setCFile(File file) {
        put(CDF, file);
    }

    /**
     * Returns tabular data file
     * @return Returns the tFile.
     */
    public File getTFile() {
        return (File) get(TDF);
    }

    /**
     * @param file The tFile to set.
     */
    public void setTFile(File file) {
        put(TDF, file);
    }

    /** Returns the index of the first ignored column */
    public int getIndexOfFirstIgnoredColumn() {
        return ((Integer) get(IFIC)).intValue();
    }

    /** Sets the index of the first ignored column */
    public void setIndexOfFirstIgnoredColumn(int value) {
        put(IFIC, new Integer(value));
    }

    /** Returns the index of the first ignored Row */
    public int getIndexOfFirstIgnoredRow() {
        return ((Integer) get(IFIR)).intValue();
    }

    /** Sets the index of the first ignored Row */
    public void setIndexOfFirstIgnoredRow(int value) {
        put(IFIR, new Integer(value));
    }

    /** Returns the index of the Genes column */
    public int getIndexOfGeneColumn() {
        return ((Integer) get(IGC)).intValue();
    }

    /** Sets the index of the Genes column */
    public void setIndexOfGeneColumn(int value) {
        put(IGC, new Integer(value));
    }

    /** Returns the index of the last ignored column */
    public int getIndexOfLastIgnoredColumn() {
        return ((Integer) get(ILIC)).intValue();
    }

    /** Sets the index of the last ignored column */
    public void setIndexOfLastIgnoredColumn(int value) {
        put(ILIC, new Integer(value));
    }

    /** Returns the index of the last ignored row */
    public int getIndexOfLastIgnoredRow() {
        return ((Integer) get(ILIR)).intValue();
    }

    /** Sets the index of the last ignored row */
    public void setIndexOfLastIgnoredRow(int value) {
        put(ILIR, new Integer(value));
    }

    /** Returns the index of the Tile Row */
    public int getIndexOfTitleRow() {
        return ((Integer) get(ITR)).intValue();
    }

    /** Sets the index of the Tile Row */
    public void setIndexOfTitleRow(int value) {
        put(ITR, new Integer(value));
    }

    /** Returns the number of columns */
    public int getNbColumns() {
        return ((Integer) get(NBC)).intValue();
    }

    /** Sets the number of columns */
    public void setNbColumns(int value) {
        put(NBC, new Integer(value));
    }

    /** Returns the selected data type */
    public String getSelectedFormat() {
        return (String) get(TYPE);
    }

    /** Sets the selected data type */
    public void setSelectedFormat(String value) {
        put(TYPE, value);
    }

    /**
     * @return Returns the classification.
     */
    public Classification getClassification() {
        if (classification == null) {
            classification = ClassificationFactory.getDefault()
                    .getClassification(this);
        }
        return classification;
    }

    /**
     * @param classification The classification to set.
     */
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public AbstractNode getLinkedOntologyNode() {
        return linkedOntologyNode;
    }

    public void setLinkedOntologyNode(AbstractNode node) {
        this.linkedOntologyNode = node;
    }
}