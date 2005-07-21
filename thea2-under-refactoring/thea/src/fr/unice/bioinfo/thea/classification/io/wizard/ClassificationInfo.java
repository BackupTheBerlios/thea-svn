package fr.unice.bioinfo.thea.classification.io.wizard;

import java.io.File;

/**
 * A java object to store into nevessart informations to locate a classification
 * and its properties.
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class ClassificationInfo {

    private static ClassificationInfo instance = null;

    /** The selected format. */
    private String fileFormat = SupportedFormat.UNCLUSTERED;

    /** Clustered data's file. */
    private File clusteredDataFile;

    /** Tabular data's file . */
    private File tabularDataFile;

    private String name = "";//NOI18N
    private String hint;
    private static int counter = 0;

    private int minIgnoredRow = -1;
    private int maxIgnoredRow = -1;
    private int minIgnoredColumn = -1;
    private int maxIgnoredColumn = -1;
    private int geneLabels = -1;
    private int columnLabels = -1;

    private int nbColumns;

    private ClassificationInfo() {
    }

    public static ClassificationInfo getInstance() {
        if (instance == null) {
            instance = new ClassificationInfo();
        }
        return instance;
    }

    public File getClusteredDataFile() {
        return clusteredDataFile;
    }

    public void setClusteredDataFile(File clusteredDataFile) {
        this.clusteredDataFile = clusteredDataFile;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public File getTabularDataFile() {
        return tabularDataFile;
    }

    public void setTabularDataFile(File tabularDataFile) {
        this.tabularDataFile = tabularDataFile;
    }

    public int getGeneLabels() {
        return geneLabels;
    }

    public void setGeneLabels(int geneLabels) {
        this.geneLabels = geneLabels;
    }

    public int getMaxIgnoredColumn() {
        return maxIgnoredColumn;
    }

    public void setMaxIgnoredColumn(int maxIgnoredColumn) {
        this.maxIgnoredColumn = maxIgnoredColumn;
    }

    public int getMaxIgnoredRow() {
        return maxIgnoredRow;
    }

    public void setMaxIgnoredRow(int maxIgnoredRow) {
        this.maxIgnoredRow = maxIgnoredRow;
    }

    public int getMinIgnoredColumn() {
        return minIgnoredColumn;
    }

    public void setMinIgnoredColumn(int minIgnoredColumn) {
        this.minIgnoredColumn = minIgnoredColumn;
    }

    public int getMinIgnoredRow() {
        return minIgnoredRow;
    }

    public void setMinIgnoredRow(int minIgnoredRow) {
        this.minIgnoredRow = minIgnoredRow;
    }

    public int getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(int columnLabels) {
        this.columnLabels = columnLabels;
    }

    public int getNbColumns() {
        return nbColumns;
    }

    public void setNbColumns(int nbColumns) {
        this.nbColumns = nbColumns;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCounter() {
        return counter;
    }
}