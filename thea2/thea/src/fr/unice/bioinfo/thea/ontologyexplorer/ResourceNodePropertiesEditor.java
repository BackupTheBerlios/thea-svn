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
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.api.components.PrintableJTable;
import fr.unice.bioinfo.thea.api.components.TableSorter;
import fr.unice.bioinfo.thea.ontologyexplorer.nodes.ResourceNode;

/**
 * @author <a href="mailto:claude.pasquier@unice.fr"> Claude Pasquier </a>
 */
public class ResourceNodePropertiesEditor extends TopComponent {

    // Serial Version UID
    static final long serialVersionUID = 463902162650994901L;

    /** The support for firing property changes */
    private PropertyChangeSupport propertySupport;

    /** preferred ID:geneeditor */
    private String PREFERRED_ID = "propertieseditor";// NOI18N

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.Bundle"); // NOI18N

    /** The Properties Editor displays properties associated with this node. */
    private ResourceNode node;

    /** Genes table. */
    // private JTable table;
    private PrintableJTable table;

    /** Button to print the genes table. */
    private JButton printBtn;

    private static final String MODE = "properties";

    /**
     * Creates a dockable windows to be used to hold displayable properties of a
     * resource represented by the given node.
     * 
     * @param node
     *            a node that represents a Resource in the ontology explorer.
     */
    public ResourceNodePropertiesEditor(Node node) {
        super();
        this.node = ((ResourceNode) node);

        propertySupport = new PropertyChangeSupport(this);

        propertySupport.firePropertyChange("initializing", null, null);// NOI18N

        // give a title to this window using the resource's name
        setName(node.getDisplayName());
        // icon
        setIcon(Utilities
                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/PropertiesEditorIcon16.gif")); // NOI18N

        // layout
        setLayout(new BorderLayout());
        propertySupport.firePropertyChange("processing", null, null);// NOI18N
        add(createMainPanel(), BorderLayout.CENTER);
        propertySupport.firePropertyChange("endprocessing", null, null);// NOI18N
    }

    private JPanel createMainPanel() {
        // create a panel where to place de genes' table
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EtchedBorder());
        // panel.setLayout(new BorderLayout());
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
        TableSorter sorter = new TableSorter(
                new ResourceNodePropertiesTableModel(this.node));
        table = new PrintableJTable(sorter);
        sorter.setTableHeader(table.getTableHeader());

        JScrollPane jsp = new JScrollPane(table);
        jsp.getViewport().setBackground(Color.WHITE);
        // add the table to the panel
        panel.add(jsp, cc.xywh(1, 1, 5, 1, CellConstraints.FILL,
                CellConstraints.FILL));
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
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/PrintIcon16.gif"))); // NOI18N
        printBtn.setToolTipText(bundle.getString("HINT_PrintPropertiesBtn"));// NOI18N
        printBtn.setBorder(new EtchedBorder());
        panel.add(printBtn, cc.xy(5, 3));
        // return the panel
        return panel;
    }

    public void open() {
        Mode m = WindowManager.getDefault().findMode(MODE);
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
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

    protected String getPreferredID() {
        return PREFERRED_ID;
    }

    /* (non-Javadoc)
     * @see org.openide.windows.TopComponent#getPersistenceType()
     */
    public int getPersistenceType() {
        // TODO Auto-generated method stub
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }
}