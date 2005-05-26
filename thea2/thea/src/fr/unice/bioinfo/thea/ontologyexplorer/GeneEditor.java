package fr.unice.bioinfo.thea.ontologyexplorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.thea.api.components.PrintableJTable;
import fr.unice.bioinfo.thea.api.components.TableSorter;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:elkasmi@unice.fr"> Saïd El Kasmi </a>
 */
public class GeneEditor extends TopComponent {

    //  Serial Version UID
    static final long serialVersionUID = 6857108441469780252L;
    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport;
    /** preferred ID:geneeditor */
    private String PREFERRED_ID = "geneeditor";//NOI18N
    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); //NOI18N
    /** The Gene Editor loks for genes annotated by this resource. */
    private Resource resource;
    /** Genes table. */
    //private JTable table;
    private PrintableJTable table;
    /** Button to print the genes table. */
    private JButton printBtn;
    /** Save button. */
    private JButton saveBtn;
    private String[] evidences;
    private String[] properties;

    /**
     * Creates a dockable windows to be used to hold the table of genes
     * annotated by the resource represented by the given node.
     * @param node a node that represents a Resource in the ontology explorer.
     * @param evidences Evidences codes used as creterions to find annotated
     *        genes.
     * @param properties Array of strings that defines properties of genes.
     */
    public GeneEditor(Node node, String[] evidences, String[] properties) {
        super();
        this.evidences = evidences;
        this.properties = properties;
        this.resource = ((ResourceNode) node).getResource();

        propertySupport = new PropertyChangeSupport(this);

        propertySupport.firePropertyChange("initializing", null, null);//NOI18N

        // give a title to this window using the resource's name
        setName(node.getDisplayName());
        // icon
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/GeneEditorIcon16.gif")); // NOI18N

        //      layout
        setLayout(new BorderLayout());
        propertySupport.firePropertyChange("processing", null, null);//NOI18N
        add(createMainPanel(), BorderLayout.CENTER);
        propertySupport.firePropertyChange("endprocessing", null, null);//NOI18N
    }

    private JPanel createMainPanel() {
        // create a panel where to place de genes' table
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EtchedBorder());
        //panel.setLayout(new BorderLayout());
        panel.setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.PREF_COLSPEC }, new RowSpec[] {
                new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW), FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC }));

        CellConstraints cc = new CellConstraints();

        // create the table
        TableSorter sorter = new TableSorter(new GenesTableModel(this.resource,
                this.evidences, this.properties));
        table = new PrintableJTable(sorter);
        sorter.setTableHeader(table.getTableHeader());

        JScrollPane jsp = new JScrollPane(table);
        jsp.getViewport().setBackground(Color.WHITE);
        // add the table to the panel
        panel.add(jsp, cc.xywh(1, 1, 5, 1, CellConstraints.FILL,
                CellConstraints.FILL));
        // careate and add the save button
        saveBtn = new JButton();
        saveBtn.setToolTipText(bundle.getString("HINT_SaveBtn"));//NOI18N
        saveBtn.setBorder(new EtchedBorder());
        saveBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/SaveIcon16.gif"))); //NOI18N
        panel.add(saveBtn, cc.xy(3, 3));
        // careet and add the print button
        printBtn = new JButton();
        printBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performPrintBtnAction(e);
            }
        });
        printBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/PrintIcon16.gif"))); //NOI18N
        printBtn.setToolTipText(bundle.getString("HINT_PrintBtn"));//NOI18N
        printBtn.setBorder(new EtchedBorder());
        panel.add(printBtn, cc.xy(5, 3));
        // return the panel
        return panel;
    }

    /**
     * Add property change listener Registers a listener for the PropertyChange
     * event.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    /**
     * Remove property change listener Remove a listener for the PropertyChange
     * event.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }

    private void performPrintBtnAction(ActionEvent e) {
        table.doPrintActions();
    }
}