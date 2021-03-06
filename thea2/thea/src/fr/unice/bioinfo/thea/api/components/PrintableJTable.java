package fr.unice.bioinfo.thea.api.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JTable;
import javax.swing.RepaintManager;
import javax.swing.table.AbstractTableModel;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Sa�d El Kasmi </a>
 */
public class PrintableJTable extends JTable implements Printable {

    public PrintableJTable() {
        super();
    }

    public PrintableJTable(AbstractTableModel model) {
        super(model);
    }

    /**
     * The method
     * 
     * @print@ must be implemented for
     * @Printable@ interface. Parameters are supplied by system.
     */
    public int print(Graphics g, PageFormat pf, int pageIndex)
            throws PrinterException {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setColor(Color.black); // set default foreground color to black

        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        Dimension d = this.getSize(); // get size of document
        double panelWidth = d.width; // width in pixels
        double panelHeight = d.height; // height in pixels
        double pageHeight = pf.getImageableHeight(); // height of printer
        // page
        double pageWidth = pf.getImageableWidth(); // width of printer page
        double scale = pageWidth / panelWidth;
        int totalNumPages = (int) Math.ceil(scale * panelHeight / pageHeight);
        // Make sure not print empty pages
        if (pageIndex >= totalNumPages)
            return Printable.NO_SUCH_PAGE;

        // Shift Graphic to line up with beginning of print-imageable region
        graphics.translate(pf.getImageableX(), pf.getImageableY());
        // Shift Graphic to line up with beginning of next page to print
        graphics.translate(0f, -pageIndex * pageHeight);
        // Scale the page so the width fits...
        graphics.scale(scale, scale);
        this.paint(graphics); // repaint the page for printing
        return Printable.PAGE_EXISTS;
    }

    public void doPrintActions() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(this);
        pj.printDialog();
        try {
            pj.print();
        } catch (Exception printException) {
            printException.printStackTrace();
        }
    }

}
