package fr.unice.bioinfo.thea.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import fr.unice.bioinfo.thea.classification.settings.CESettings;

public class AnnotationOptionsPanel extends JPanel {

    private JComponent scoreSeparator;
    private JRadioButton termCntBtn;
    private JTextField termCntTfd;
    private JRadioButton densityInClusterBtn;
    private JTextField densityInClusterTfd;
    private JRadioButton densityInPopulationBtn;
    private JTextField densityInPopulationTfd;
    private JRadioButton relativeDensityBtn;
    private JTextField relativeDensityTfd;
    private JRadioButton statCalculationBtn;
    private JTextField statCalculationTfd;
    private JComponent statSeparator;
    private JRadioButton m1Btn;
    private JRadioButton m2Btn;
    private JComponent pvalueSeparator;
    private JRadioButton binLawBtn;
    private JRadioButton hyperLawBtn;
    private JComponent lawSeparator;
    private JComponent correctionSeparator;
    private JCheckBox bonCorrectionBtn;
    private JCheckBox dunCorrectionBtn;

    private JComponent baseCalculationSeparator;
    private JRadioButton inClassificationBtn;
    private JRadioButton inOntologyBtn;
    private JRadioButton userSpecifiedBtn;
    private JTextField browseTfd;
    private JButton browseBtn;
    private JComponent endSeparator;
    private JButton applyBtn;

    /** Resource Bundle */
    private static ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.dlg.Bundle"); //NOI18N;

    public AnnotationOptionsPanel() {
        init();
    }

    private void init() {

        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        scoreSeparator = compFactory.createSeparator(bundle
                .getString("LBL_ScoreSeparator"));//NOI18N
        statSeparator = compFactory.createSeparator(bundle
                .getString("LBL_StatSeparator"));//NOI18N
        pvalueSeparator = compFactory.createSeparator(bundle
                .getString("LBL_PValueSeparator"));//NOI18N
        lawSeparator = compFactory.createSeparator(bundle
                .getString("LBL_LawSeparator"));//NOI18N
        correctionSeparator = compFactory.createSeparator(bundle
                .getString("LBL_CorrectionSeparator"));//NOI18N
        baseCalculationSeparator = compFactory.createSeparator(bundle
                .getString("LBL_BaseCalculationSeparator"));//NOI18N

        //Create a group for score
        ButtonGroup scoreGroup = new ButtonGroup();
        //Create "Methods" group:
        ButtonGroup mGroup = new ButtonGroup();
        //Create laws group:
        ButtonGroup lGroup = new ButtonGroup();
        //Create group for base of calculation options:
        ButtonGroup buttonGroup = new ButtonGroup();

        termCntBtn = new JRadioButton();
        scoreGroup.add(termCntBtn);
        termCntTfd = new JTextField();
        densityInClusterBtn = new JRadioButton();
        scoreGroup.add(densityInClusterBtn);
        densityInClusterTfd = new JTextField();
        densityInPopulationBtn = new JRadioButton();
        scoreGroup.add(densityInPopulationBtn);
        densityInPopulationTfd = new JTextField();
        relativeDensityBtn = new JRadioButton();
        scoreGroup.add(relativeDensityBtn);
        relativeDensityTfd = new JTextField();
        statCalculationBtn = new JRadioButton();
        scoreGroup.add(statCalculationBtn);
        statCalculationTfd = new JTextField();
        m1Btn = new JRadioButton();
        mGroup.add(m1Btn);
        m2Btn = new JRadioButton();
        mGroup.add(m2Btn);

        binLawBtn = new JRadioButton();
        lGroup.add(binLawBtn);
        hyperLawBtn = new JRadioButton();
        lGroup.add(hyperLawBtn);

        bonCorrectionBtn = new JCheckBox();
        dunCorrectionBtn = new JCheckBox();

        inClassificationBtn = new JRadioButton();
        buttonGroup.add(inClassificationBtn);
        inOntologyBtn = new JRadioButton();
        buttonGroup.add(inOntologyBtn);
        userSpecifiedBtn = new JRadioButton();
        buttonGroup.add(userSpecifiedBtn);
        browseTfd = new JTextField();
        browseBtn = new JButton();
        endSeparator = compFactory.createSeparator("");//NOI18N
        applyBtn = new JButton();

        CellConstraints cc = new CellConstraints();

        setBorder(Borders.DIALOG_BORDER);

        setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(min;50px)"),//NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC }));

        add(scoreSeparator, cc.xywh(1, 1, 9, 1));

        //Get options
        CESettings options = CESettings.getInstance();

        //---- termCntBtn ----
        termCntBtn.setText(bundle.getString("TXT_TermCntBtn"));//NOI18N
        termCntBtn.setToolTipText(bundle.getString("TIP_TermCntBtn"));//NOI18N
        termCntBtn.setSelected(options.isTermsCountSelected());
        termCntBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateScoreCalculationSettings();
                termCntTfd.setEnabled(termCntBtn.isSelected());
            }
        });
        add(termCntBtn, cc.xywh(1, 3, 5, 1));

        termCntTfd.setText("" + options.getTermsCount());//NOI18N
        termCntTfd.setEnabled(termCntBtn.isSelected());
        add(termCntTfd, cc.xywh(7, 3, 3, 1));

        //---- densityInClusterBtn ----
        densityInClusterBtn
                .setText(bundle.getString("TXT_DensityInClusterBtn"));//NOI18N
        densityInClusterBtn.setToolTipText(bundle
                .getString("TIP_DensityInClusterBtn"));//NOI18N
        densityInClusterBtn.setSelected(options
                .isTermsDensityInClusterSelected());
        densityInClusterBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateScoreCalculationSettings();
                densityInClusterTfd
                        .setEnabled(densityInClusterBtn.isSelected());
            }
        });
        add(densityInClusterBtn, cc.xywh(1, 5, 5, 1));

        densityInClusterTfd.setText("" + options.getDensityInCluster());//NOI18N
        densityInClusterTfd.setEnabled(densityInClusterBtn.isSelected());
        add(densityInClusterTfd, cc.xywh(7, 5, 3, 1));

        //---- densityInPopulationBtn ----
        densityInPopulationBtn.setText(bundle
                .getString("TXT_DensityInPopulationBtn"));//NOI18N
        densityInPopulationBtn.setToolTipText(bundle
                .getString("TIP_DensityInPopulationBtn"));//NOI18N
        densityInPopulationBtn.setSelected(options
                .isTermsDensityInPopulationSelected());
        densityInPopulationBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateScoreCalculationSettings();
                densityInPopulationTfd.setEnabled(densityInPopulationBtn
                        .isSelected());
            }
        });
        add(densityInPopulationBtn, cc.xywh(1, 7, 5, 1));

        densityInPopulationTfd.setText("" + options.getDensityInPopulation());//NOI18N
        densityInPopulationTfd.setEnabled(densityInPopulationBtn.isSelected());
        add(densityInPopulationTfd, cc.xywh(7, 7, 3, 1));

        //---- relativeDensityBtn ----
        relativeDensityBtn.setText(bundle.getString("TXT_RelativeDensityBtn"));//NOI18N
        relativeDensityBtn.setToolTipText(bundle
                .getString("TIP_RelativeDensityBtn"));//NOI18N
        relativeDensityBtn
                .setSelected(options.isTermsRelativeDensitySelected());
        relativeDensityBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateScoreCalculationSettings();
                relativeDensityTfd.setEnabled(relativeDensityBtn.isSelected());
            }
        });
        add(relativeDensityBtn, cc.xywh(1, 9, 5, 1));

        relativeDensityTfd.setText("" + options.getRelativeDensity());//NOI18N
        relativeDensityTfd.setEnabled(relativeDensityBtn.isSelected());
        add(relativeDensityTfd, cc.xywh(7, 9, 3, 1));

        //---- statCalculationBtn ----
        statCalculationBtn.setText(bundle.getString("TXT_StatCalculationBtn"));//NOI18N
        statCalculationBtn.setToolTipText(bundle
                .getString("TIP_StatCalculationBtn"));//NOI18N
        statCalculationBtn.setSelected(options
                .isStatisticalCalculationSelected());
        setPValueWidgetsEnabled(statCalculationBtn.isSelected());
        statCalculationBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateScoreCalculationSettings();
                statCalculationTfd.setEnabled(statCalculationBtn.isSelected());
                setPValueWidgetsEnabled(statCalculationBtn.isSelected());
            }
        });
        add(statCalculationBtn, cc.xywh(1, 11, 5, 1));

        statCalculationTfd.setText("" + options.getStat());//NOI18N
        statCalculationTfd.setEnabled(statCalculationBtn.isSelected());
        add(statCalculationTfd, cc.xywh(7, 11, 3, 1));
        add(statSeparator, cc.xywh(1, 13, 9, 1));

        //---- m1Btn ----
        m1Btn.setText(bundle.getString("TXT_M1Btn"));//NOI18N
        m1Btn.setToolTipText(bundle.getString("TIP_M1Btn"));//NOI18N
        m1Btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setMethod1Selected(m1Btn.isSelected());
                CESettings.getInstance().setMethod2Selected(m2Btn.isSelected());
            }
        });
        add(m1Btn, cc.xy(3, 17));

        //---- m2Btn ----
        m2Btn.setText(bundle.getString("TXT_M2Btn"));//NOI18N
        m2Btn.setToolTipText(bundle.getString("TIP_M2Btn"));//NOI18N
        m2Btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setMethod2Selected(m2Btn.isSelected());
                CESettings.getInstance().setMethod1Selected(m1Btn.isSelected());
            }
        });
        add(m2Btn, cc.xy(5, 17));
        add(pvalueSeparator, cc.xywh(3, 15, 7, 1));

        //---- binLawBtn ----
        binLawBtn.setText(bundle.getString("TXT_BinLawBtn"));//NOI18N
        binLawBtn.setToolTipText(bundle.getString("TIP_BinLawBtn"));//NOI18N
        binLawBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setBinomialLawSelected(
                        binLawBtn.isSelected());
                CESettings.getInstance().setHypergeometricLawSelected(
                        hyperLawBtn.isSelected());
            }
        });
        add(binLawBtn, cc.xy(3, 21));

        //---- hyperLawBtn ----
        hyperLawBtn.setText(bundle.getString("TXT_HyperLawBtn"));//NOI18N
        hyperLawBtn.setToolTipText(bundle.getString("TIP_HyperLawBtn"));//NOI18N
        hyperLawBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setHypergeometricLawSelected(
                        hyperLawBtn.isSelected());
                CESettings.getInstance().setBinomialLawSelected(
                        binLawBtn.isSelected());
            }
        });
        add(hyperLawBtn, cc.xy(5, 21));
        add(lawSeparator, cc.xywh(2, 19, 8, 1));
        add(correctionSeparator, cc.xywh(3, 23, 7, 1));

        //---- bonCorrectionBtn ----
        bonCorrectionBtn.setText(bundle.getString("TXT_BonCorrectionBtn"));//NOI18N
        bonCorrectionBtn.setToolTipText(bundle
                .getString("TIP_BonCorrectionBtn"));//NOI18N
        bonCorrectionBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setBonferonniCorrectionSelected(
                        bonCorrectionBtn.isSelected());
                CESettings.getInstance().setSidakCorrectionSelected(
                        dunCorrectionBtn.isSelected());
                if (bonCorrectionBtn.isSelected()) {
                    dunCorrectionBtn.setSelected(false);
                }
            }
        });
        add(bonCorrectionBtn, cc.xy(3, 25));

        //---- dunCorrectionBtn ----
        dunCorrectionBtn.setText(bundle.getString("TXT_DunCorrectionBtn"));//NOI18N
        dunCorrectionBtn.setToolTipText(bundle
                .getString("TIP_DunCorrectionBtn"));//NOI18N
        dunCorrectionBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setSidakCorrectionSelected(
                        dunCorrectionBtn.isSelected());
                CESettings.getInstance().setBonferonniCorrectionSelected(
                        bonCorrectionBtn.isSelected());
                if (dunCorrectionBtn.isSelected()) {
                    bonCorrectionBtn.setSelected(false);
                }
            }
        });
        add(dunCorrectionBtn, cc.xy(5, 25));

        add(baseCalculationSeparator, cc.xywh(1, 27, 9, 1));

        //---- inClassificationBtn ----
        inClassificationBtn
                .setText(bundle.getString("TXT_InClassificationBtn"));//NOI18N
        inClassificationBtn.setToolTipText(bundle
                .getString("TIP_InClassificationBtn"));//NOI18N
        inClassificationBtn.setSelected(CESettings.getInstance()
                .isClassifBaseSelected());
        inClassificationBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateBaseCalculationSettings();
            }
        });
        add(inClassificationBtn, cc.xywh(1, 29, 9, 1));

        //---- inOntologyBtn ----
        inOntologyBtn.setText(bundle.getString("TXT_InOntologyBtn"));//NOI18N
        inOntologyBtn.setToolTipText(bundle.getString("TIP_InOntologyBtn"));//NOI18N
        inOntologyBtn.setSelected(CESettings.getInstance()
                .isOntologyBaseSelected());
        inOntologyBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateBaseCalculationSettings();
            }
        });
        add(inOntologyBtn, cc.xywh(1, 31, 9, 1));

        //---- userSpecifiedBtn ----
        userSpecifiedBtn.setText(bundle.getString("TXT_UserSpecifiedBtn"));//NOI18N
        userSpecifiedBtn.setToolTipText(bundle
                .getString("TIP_UserSpecifiedBtn"));//NOI18N
        userSpecifiedBtn.setSelected(CESettings.getInstance()
                .isUserSpecifiedBaseSelected());
        userSpecifiedBtn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateBaseCalculationSettings();
                browseBtn.setEnabled(userSpecifiedBtn.isSelected());
                browseTfd.setEnabled(userSpecifiedBtn.isSelected());
            }
        });
        add(userSpecifiedBtn, cc.xywh(1, 33, 3, 1));

        browseTfd.setEnabled(userSpecifiedBtn.isSelected());
        add(browseTfd, cc.xywh(5, 33, 4, 1));

        //---- browseBtn ----
        browseBtn.setBorder(new EtchedBorder());
        browseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(WindowManager
                        .getDefault().getMainWindow());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    browseTfd.setText(chooser.getSelectedFile().toString());
                }
            }
        });
        browseBtn.setEnabled(userSpecifiedBtn.isSelected());
        browseBtn
                .setIcon(new ImageIcon(
                        Utilities
                                .loadImage("fr/unice/bioinfo/thea/ontologyexplorer/resources/BrowseIcon16.gif"))); //NOI18N
        add(browseBtn, cc.xy(9, 33));
        add(endSeparator, cc.xywh(1, 35, 9, 1));

        //---- applyBtn ----
        applyBtn.setText(bundle.getString("TXT_ApplyBtn"));//NOI18N
        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CESettings.getInstance().setTermsCount(
                        Double.parseDouble(termCntTfd.getText()));
                CESettings.getInstance().setDensityInCluster(
                        Double.parseDouble(densityInClusterTfd.getText()));
                CESettings.getInstance().setDensityInPopulation(
                        Double.parseDouble(densityInPopulationTfd.getText()));
                CESettings.getInstance().setRelativeDensity(
                        Double.parseDouble(relativeDensityTfd.getText()));
                CESettings.getInstance().setStat(
                        Double.parseDouble(statCalculationTfd.getText()));
            }
        });
        add(applyBtn, cc.xy(7, 37));

        initFromSettings();
    }

    private void initFromSettings() {
        // Get options
        CESettings options = CESettings.getInstance();

        // initialize widgets
        m1Btn.setSelected(options.isMethod1Selected());
        m2Btn.setSelected(options.isMethod2Selected());
        binLawBtn.setSelected(options.isBinomialLawSelected());
        hyperLawBtn.setSelected(options.isHypergeometricLawSelected());
        dunCorrectionBtn.setSelected(options.isSidakCorrectionSelected());
        bonCorrectionBtn.setSelected(options.isBonferonniCorrectionSelected());
    }

    private void updateScoreCalculationSettings() {
        CESettings.getInstance().setTermsCountSelected(termCntBtn.isSelected());
        CESettings.getInstance().setTermsDensityInClusterSelected(
                densityInClusterBtn.isSelected());
        CESettings.getInstance().setTermsDensityInPopulationSelected(
                densityInPopulationBtn.isSelected());
        CESettings.getInstance().setTermsRelativeDensitySelected(
                relativeDensityBtn.isSelected());
        CESettings.getInstance().setStatisticalCalculationSelected(
                statCalculationBtn.isSelected());
    }

    private void updateBaseCalculationSettings() {
        CESettings.getInstance().setClassifBaseSelected(
                inClassificationBtn.isSelected());
        CESettings.getInstance().setOntologyBaseSelected(
                inOntologyBtn.isSelected());
        CESettings.getInstance().setUserSpecifiedBaseSelected(
                userSpecifiedBtn.isSelected());
    }

    private void setPValueWidgetsEnabled(boolean b) {
        this.m1Btn.setEnabled(b);
        this.m2Btn.setEnabled(b);
        this.binLawBtn.setEnabled(b);
        this.hyperLawBtn.setEnabled(b);
        this.bonCorrectionBtn.setEnabled(b);
        this.dunCorrectionBtn.setEnabled(b);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new AnnotationOptionsPanel());
        f.pack();
        f.setVisible(true);
    }
}