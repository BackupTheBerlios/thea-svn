package fr.unice.bioinfo.thea.classification.editor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * read a tabular data file containing measures
 */
public class MeasuresFileReader {
    /**
     * read the file and return a mapping from node name to a vector of measures
     */
    public Map load(File measuresFile, int indexOfFirstIgnoredRow,
            int indexOfLastIgnoredRow, int indexOfFirstIgnoredColumn,
            int indexOfLastIgnoredColumn, int indexOfGeneColumn,
            int indexOfTitleRow, int nbColumns) {
        int nbColumnsValues = nbColumns - 1
                - (indexOfLastIgnoredColumn - indexOfFirstIgnoredColumn);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(measuresFile));
        } catch (FileNotFoundException fnfe) {
            System.err.println("Could not load the file " + fnfe);

            return null;
        }

        Map geneId2Measures = new HashMap();
        String line = null;
        int currentRow = -1;

        try {
            while ((line = reader.readLine()) != null) {
                // insert "NA" between two consecutive tabs
                while (line.indexOf("\t\t") != -1) {
                    line = line.replaceAll("\t\t", "\tNA\t");
                }

                if (line.endsWith("\t")) {
                    line += "NA";
                }

                currentRow += 1;

                if ((currentRow >= indexOfFirstIgnoredRow)
                        && (currentRow <= indexOfLastIgnoredRow)
                        && (currentRow != indexOfTitleRow)) {
                    continue;
                }

                StringTokenizer st = new StringTokenizer(line, "\t");
                List mapValues = new Vector();
                String mapKey = null;

                int currentColumn = -1;

                while (st.hasMoreTokens()) {
                    try {
                        String cellData = st.nextToken();
                        currentColumn += 1;

                        if ((currentColumn >= indexOfFirstIgnoredColumn)
                                && (currentColumn <= indexOfLastIgnoredColumn)
                                && (currentColumn != indexOfGeneColumn)) {
                            continue;
                        }

                        if (currentRow == indexOfTitleRow) {
                            if (currentColumn == indexOfGeneColumn) {
                                mapKey = "*TITLE*";
                            } else {
                                mapValues.add(cellData);
                            }
                        } else {
                            if (currentColumn == indexOfGeneColumn) {
                                mapKey = cellData;
                            } else {
                                try {
                                    Double val = new Double(cellData);
                                    mapValues.add(val);
                                } catch (NumberFormatException nfe) {
                                    try {
                                        NumberFormat nf = NumberFormat
                                                .getInstance();
                                        Double val = (Double) nf
                                                .parse(cellData);
                                        mapValues.add(val);
                                    } catch (ParseException pe) {
                                        mapValues.add(new Double(0));
                                    }
                                }
                            }
                        }
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                    }
                }

                while (mapValues.size() < nbColumnsValues) {
                    mapValues.add(new Double(0));
                }

                geneId2Measures.put(mapKey, mapValues);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return geneId2Measures;
    }
}