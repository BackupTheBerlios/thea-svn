package fr.unice.bioinfo.thea.classification.editor.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.configuration.Configuration;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.TheaConfiguration;
import fr.unice.bioinfo.thea.classification.settings.CESettings;

public class AnnotationEvidencesPanel extends JPanel {

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N
    //separators
    private JComponent evidencesSeparator;

    //  labels
    private JLabel allEvidencesLbl;
    private JLabel selectedEvidencesLbl;

    //  lists
    private JList allEvidencesList;
    private JList selectedEvidencesList;

    //  scroll panes
    private JScrollPane allEvidencesJsp;
    private JScrollPane selectedEvidencesJsp;

    // buttons
    private JButton addEvidencesBtn;
    private JButton removeEvidencesBtn;

    //  lists' models
    private DefaultListModel allEvidencesModel;
    private DefaultListModel selectedEvidencesModel;

    private String[] evidences;

    public AnnotationEvidencesPanel() {
        createModels();
        init();
    }

    private void init() {
        //      create separators
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        evidencesSeparator = compFactory.createSeparator("Evidences");//NOI18N

        //      labels
        allEvidencesLbl = new JLabel();
        selectedEvidencesLbl = new JLabel();

        //      lists
        allEvidencesList = new JList(allEvidencesModel);
        allEvidencesList
                .addListSelectionListener(new AllEvidencesListSelectionListener());
        allEvidencesJsp = new JScrollPane(allEvidencesList);

        selectedEvidencesList = new JList(selectedEvidencesModel);
        selectedEvidencesList
                .addListSelectionListener(new SelectedEvidencesListSelectionListener());
        selectedEvidencesJsp = new JScrollPane(selectedEvidencesList);

        //      selection mode
        allEvidencesList
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedEvidencesList
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //      buttons
        addEvidencesBtn = new JButton();
        addEvidencesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performAddEvidencesBtnAction(e);
            }
        });
        removeEvidencesBtn = new JButton();
        removeEvidencesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performRemoveEvidencesBtnAction(e);
            }
        });

        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED,
                        FormSpec.DEFAULT_GROW) }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC,
                new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC,
                new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW) }));

        CellConstraints cc = new CellConstraints();
        add(evidencesSeparator, cc.xywh(1, 1, 5, 1));

        //---- allEvidencesLbl ----
        allEvidencesLbl.setText(bundle.getString("LBL_AllEvidencesList"));//NOI18N
        allEvidencesLbl.setToolTipText(bundle
                .getString("HINT_AllEvidencesList"));//NOI18N
        add(allEvidencesLbl, cc.xy(1, 3));

        //---- selectedEvidencesLbl ----
        selectedEvidencesLbl.setText(bundle
                .getString("LBL_SelectedEvidencesList"));//NOI18N
        selectedEvidencesLbl.setToolTipText(bundle
                .getString("HINT_SelectedEvidencesList"));//NOI18N
        add(selectedEvidencesLbl, cc.xy(5, 3));
        add(allEvidencesJsp, cc.xywh(1, 4, 1, 8, CellConstraints.FILL,
                CellConstraints.FILL));
        add(selectedEvidencesJsp, cc.xywh(5, 4, 1, 8, CellConstraints.FILL,
                CellConstraints.FILL));

        //---- addEvidenceBtn ----
        addEvidencesBtn.setBorder(new EtchedBorder());
        addEvidencesBtn.setToolTipText(bundle.getString("HINT_AddEvidence"));//NOI18N
        addEvidencesBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/AddIcon16.gif"))); //NOI18N
        add(addEvidencesBtn, cc.xy(3, 7));

        //---- removeEvidenceBtn ----
        removeEvidencesBtn.setBorder(new EtchedBorder());
        removeEvidencesBtn.setToolTipText(bundle
                .getString("HINT_RemoveEvidence"));//NOI18N
        removeEvidencesBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/RemoveIcon16.gif"))); //NOI18N
        add(removeEvidencesBtn, cc.xy(3, 9));
    }

    /**
     * Create models ( instances of DefaultListModel ) for JList components. For
     * selected evidences and properties, we try to make selected data that the
     * user would have selected in the last run.
     */
    private void createModels() {
        //      create the list's allEvidencesModel
        allEvidencesModel = new DefaultListModel();
        // get evidences list form the configuration file.
        //      get the configuration
        Configuration con = TheaConfiguration.getDefault().getConfiguration();
        // get list of columns (properties)
        Object o = con.getProperty("evidences.uri");//NOI18N
        if (o instanceof Collection) {
            ArrayList al = new ArrayList((Collection) o);
            Object[] names = al.toArray();
            evidences = new String[al.size()];
            for (int counter = 0; counter < al.size(); counter++) {
                String name = (String) names[counter];
                evidences[counter] = name;//.substring(name.indexOf("#") +
                // 1);//NOI18N
                allEvidencesModel.addElement(evidences[counter]);
            }
        }
        // crate the model for the selected evidences list.
        selectedEvidencesModel = new DefaultListModel();
        // add the last selected evidences to this list:
        String[] l = CESettings.getInstance().getLastSelectedEvidences();
        if (l != null) {
            for (int cnt = 0; cnt < l.length; cnt++) {
                selectedEvidencesModel.addElement(l[cnt]);
            }
        }
    }

    private class AllEvidencesListSelectionListener implements
            ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                removeEvidencesBtn.setEnabled(false);
                addEvidencesBtn.setEnabled(true);
            }
        }
    }

    private class SelectedEvidencesListSelectionListener implements
            ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                addEvidencesBtn.setEnabled(false);
                removeEvidencesBtn.setEnabled(true);
            }
        }
    }

    /** returns selected evidences. */
    public String[] getSelectedEvidences() {
        int size = selectedEvidencesModel.getSize();
        if (size == 0) {
            return null;
        }
        String[] s = new String[size];
        for (int cnt = 0; cnt < size; cnt++) {
            s[cnt] = (String) selectedEvidencesModel.get(cnt);
        }
        return s;
    }

    private void performAddEvidencesBtnAction(ActionEvent e) {
        //      Get the index of all the selected items
        int[] selectedIx = allEvidencesList.getSelectedIndices();
        // Get all the selected items using the indices
        for (int i = 0; i < selectedIx.length; i++) {
            Object o = allEvidencesModel.getElementAt(selectedIx[i]);
            if (!selectedEvidencesModel.contains(o)) {
                selectedEvidencesModel.addElement(o);
            }
        }
    }

    private void performRemoveEvidencesBtnAction(ActionEvent e) {
        int index = selectedEvidencesList.getSelectedIndex();
        selectedEvidencesModel.remove(index);
        int size = selectedEvidencesModel.getSize();
        if (size == 0) {
            removeEvidencesBtn.setEnabled(false);
        } else { //Select an index.
            if (index == selectedEvidencesModel.getSize()) {
                //removed item in last position
                index--;
            }
            selectedEvidencesList.setSelectedIndex(index);
            selectedEvidencesList.ensureIndexIsVisible(index);
        }
    }

    public static void main(String[] arg) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new AnnotationEvidencesPanel());
        frame.pack();
        frame.setVisible(true);
    }
}