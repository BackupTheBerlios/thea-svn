package fr.unice.bioinfo.thea.ontologyexplorer.dlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.jar.JarFile;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import fr.unice.bioinfo.thea.ontologyexplorer.db.DbURLClassLoader;
import fr.unice.bioinfo.thea.ontologyexplorer.db.driver.JDBCDriver;
import fr.unice.bioinfo.thea.ontologyexplorer.db.util.DriverListUtil;

/**
 * A swing GUI that exposes necessary entries to make users add a JDBC driver to
 * the list of drivers to be used to connecto to a database.
 * @author Saïd El Kasmi.
 */
public class AddDriverPanel extends JPanel {
    private DefaultListModel dlm;
    private boolean customizer;

    // List of drivers
    private List drvs;
    private JComponent filesSeparator;
    private JList filesList;
    private JScrollPane jsp;
    private JButton addFileBtn;
    private JButton removeFileBtn;
    private JComponent driverSeparator;
    private JLabel classLbl;
    private JComboBox classComboBox;
    private JButton findClassBtn;
    private JLabel nameLbl;
    private JTextField nameField;
    private JProgressBar findBar;

    /** Resource Bundle */
    private ResourceBundle bundle = NbBundle
            .getBundle("fr.unice.bioinfo.thea.ontologyexplorer.dlg.Bundle"); //NOI18N

    public AddDriverPanel() {
        customizer = false;
        initComponents();
        findBar.setBorderPainted(false);

        //initAccessibility();
        dlm = (DefaultListModel) filesList.getModel();
        drvs = new LinkedList();
    }

    public AddDriverPanel(JDBCDriver drv) {
        this();
        customizer = true;

        String fileName;

        for (int i = 0; i < drv.getURLs().length; i++) {
            fileName = (new File(drv.getURLs()[i].getPath())).toString();
            dlm.addElement(fileName);
            drvs.add(drv.getURLs()[i]);
        }

        classComboBox.addItem(drv.getClassName());
        classComboBox.setSelectedItem(drv.getClassName());
        nameField.setText(drv.getName());
    }

    private void initComponents() {
        // Create separators
        DefaultComponentFactory compFactory = DefaultComponentFactory
                .getInstance();
        filesSeparator = compFactory.createSeparator(bundle
                .getString("LBL_FilesSeparator")); //NOI18N
        driverSeparator = compFactory.createSeparator(bundle
                .getString("LBL_DriverSeparator")); //NOI18N

        // create the list to contains drivers' files
        filesList = new JList();
        filesList.setModel(new DefaultListModel());
        jsp = new JScrollPane();
        jsp.setViewportView(filesList);

        // Instance the Add button and associate and ActionListener to it
        addFileBtn = new JButton();
        addFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFileBtnActionPerformed(e);
            }
        });

        // Remove File button
        removeFileBtn = new JButton();
        removeFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeFileBtnActionPerformed(e);
            }
        });
        classLbl = new JLabel();

        // Driver combo box
        classComboBox = new JComboBox();
        classComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                classComboBoxActionPerformed(e);
            }
        });

        // Find main class button
        findClassBtn = new JButton();
        findClassBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findClassBtnActionPerformed(e);
            }
        });
        nameLbl = new JLabel();
        nameField = new JTextField();
        findBar = new JProgressBar();

        CellConstraints cc = new CellConstraints();

        // set border
        setBorder(Borders.DIALOG_BORDER);

        // layout
        setLayout(new FormLayout(new ColumnSpec[] {
                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT,
                        FormSpec.DEFAULT_GROW),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;150dlu):grow"), //NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(min;10dlu)"), //NOI18N
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                new ColumnSpec("max(default;10dlu)") }, new RowSpec[] { //NOI18N

                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT,
                                FormSpec.DEFAULT_GROW),
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC }));
        add(filesSeparator, cc.xywh(1, 1, 7, 1));

        // Add the files list
        add(jsp, cc
                .xywh(3, 3, 5, 1, CellConstraints.FILL, CellConstraints.FILL));

        //---- addFileBtn ----
        addFileBtn.setText(bundle.getString("LBL_AddBtn")); //NOI18N
        add(addFileBtn, cc.xy(5, 5));

        //---- removeFileBtn ----
        removeFileBtn.setText(bundle.getString("LBL_RemoveBtn")); //NOI18N
        add(removeFileBtn, cc.xy(7, 5));
        add(driverSeparator, cc.xywh(1, 7, 7, 1));

        //---- classLbl ----
        classLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        classLbl.setText(bundle.getString("LBL_Class")); //NOI18N
        add(classLbl, cc.xy(1, 9));
        add(classComboBox, cc.xywh(3, 9, 3, 1));

        //---- findClassBtn ----
        findClassBtn.setText(bundle.getString("LBL_FindBtn")); //NOI18N
        add(findClassBtn, cc.xy(7, 9));

        //---- nameLbl ----
        nameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLbl.setText(bundle.getString("LBL_Name")); //NOI18N
        add(nameLbl, cc.xy(1, 11));
        add(nameField, cc.xywh(3, 11, 5, 1));
        add(findBar, cc.xywh(3, 13, 5, 1));
    }

    public String getName() {
        if (nameField != null) {
            return nameField.getText();
        } else {
            return super.getName();
        }
    }

    public List getDriverLocation() {
        return drvs;
    }

    public String getDriverClass() {
        return (String) classComboBox.getSelectedItem();
    }

    private void addFileBtnActionPerformed(ActionEvent e) {
        hideProgress();

        JFileChooser fc = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(fc, null);
        fc.setDialogTitle(bundle.getString("AddDriver_Chooser_Title")); //NOI18N
        fc.setMultiSelectionEnabled(true);
        fc.setAcceptAllFileFilterUsed(false);

        //.jar and .zip file filter
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".jar") || f
                        .getName().endsWith(".zip")); //NOI18N
            }

            public String getDescription() {
                return bundle.getString("AddDriver_Chooser_Filter"); //NOI18N
            }
        });

        if (fc.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) { //NOI18N

            File[] files = fc.getSelectedFiles();

            for (int i = 0; i < files.length; i++)
                if ((files[i] != null) && files[i].isFile()) {
                    dlm.addElement(files[i].toString());

                    try {
                        drvs.add(files[i].toURL());
                    } catch (MalformedURLException exc) {
                        //PENDING
                    }
                }

            findDriverClass();
        }
    }

    private void removeFileBtnActionPerformed(ActionEvent e) {
        hideProgress();

        ListSelectionModel lsm = filesList.getSelectionModel();
        int count = dlm.getSize();
        int i = 0;

        if (count < 1) {
            return;
        }

        do {
            if (lsm.isSelectedIndex(i)) {
                dlm.remove(i);
                drvs.remove(i);
                count--;

                continue;
            }

            i++;
        } while (count != i);

        findDriverClass();
    }

    private void findClassBtnActionPerformed(ActionEvent evt) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                startProgress();

                JarFile jf;
                Enumeration e;
                String className;
                Class c;
                Class[] cls;
                DbURLClassLoader loader = new DbURLClassLoader((URL[]) drvs
                        .toArray(new URL[drvs.size()]));

                for (int i = 0; i < drvs.size(); i++) {
                    try {
                        jf = new JarFile(
                                new File(((URL) drvs.get(i)).getFile()));
                        e = jf.entries();

                        while (e.hasMoreElements()) {
                            className = e.nextElement().toString();

                            if (className.endsWith(".class")) {
                                className = className.replace('/', '.');
                                className = className.substring(0, className
                                        .length() - 6);

                                try {
                                    c = Class.forName(className, true, loader);
                                    cls = c.getInterfaces();

                                    for (int j = 0; j < cls.length; j++)
                                        if (cls[j]
                                                .equals(java.sql.Driver.class)) {
                                            addDriverClass(className);
                                        }
                                } catch (Exception exc) {
                                    //PENDING
                                } catch (Error err) {
                                    //PENDING
                                }
                            }
                        }

                        jf.close();
                    } catch (IOException exc) {
                        //PENDING
                    }
                }

                stopProgress();
            }
        }, 0);
    }

    private void classComboBoxActionPerformed(ActionEvent evt) {
        if (!customizer) {
            nameField.setText(DriverListUtil.findFreeName(DriverListUtil
                    .getName((String) classComboBox.getSelectedItem())));
        }
    }

    private void findDriverClass() {
        JarFile jf;
        String[] drivers = (String[]) DriverListUtil.getDrivers().toArray(
                new String[DriverListUtil.getDrivers().size()]);

        classComboBox.removeAllItems();

        for (int i = 0; i < drvs.size(); i++) {
            try {
                jf = new JarFile(new File(((URL) drvs.get(i)).getFile()));

                for (int j = 0; j < drivers.length; j++)
                    if (jf.getEntry(drivers[j].replace('.', '/') + ".class") != null) { //NOI18N
                        addDriverClass(drivers[j]);
                    }

                jf.close();
            } catch (IOException exc) {
                //PENDING
            }
        }
    }

    private void addDriverClass(String drv) {
        if (((DefaultComboBoxModel) classComboBox.getModel()).getIndexOf(drv) < 0) {
            classComboBox.addItem(drv);
        }
    }

    private void startProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                findBar.setBorderPainted(true);
                findBar.setIndeterminate(true);
                findBar.setString(bundle.getString("AddDriverProgressStart")); //NOI18N
            }
        });
    }

    private void stopProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                findBar.setValue(findBar.getMaximum());
                findBar.setString(bundle.getString("AddDriverProgressStop")); //NOI18N
                findBar.setIndeterminate(false);
            }
        });
    }

    private void hideProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                findBar.setBorderPainted(false);
                findBar.setIndeterminate(false);
                findBar.setString(""); //NOI18N
                findBar.setValue(findBar.getMinimum());
            }
        });
    }
}