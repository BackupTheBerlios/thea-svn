package fr.unice.bioinfo.thea;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openide.WizardDescriptor;


public class UserSettingsPanel extends JPanel implements PropertyChangeListener{
    
    private JLabel usernameLbl;
    private JTextField usernameTxtFld;
    private JLabel passwordLbl;
    private JPasswordField passwordFld;
    
    public UserSettingsPanel(){
        super();
        init(this);
    }
    
    private void init(JPanel self){
        //Bean Properties
        self.setName("User Settings Panel");
        // sets Bounds
        self.setBounds(17,100,376,121);
        // set Layout 
        GridBagLayout gridBagLayout = new GridBagLayout();
        self.setLayout(gridBagLayout);
        GridBagConstraints constraints = new GridBagConstraints();
        Insets defaultInsets = new Insets(5,5,5,5);
        //Adds Children 
        constraints.insets = defaultInsets;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        //usernameLbl
        usernameLbl = new JLabel("Username");
        usernameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        usernameLbl.setToolTipText("login to be used to connect to DataBase");
        usernameLbl.setBounds(138,97,100,22);
        usernameLbl.setPreferredSize(new Dimension(100,22));
        usernameLbl.setMinimumSize(usernameLbl.getPreferredSize());
        gridBagLayout.setConstraints(usernameLbl, constraints);
        self.add(usernameLbl);
        //usernameTxtFld
        usernameTxtFld = new JTextField();
        usernameTxtFld.setBounds(193,97,199,22);
        usernameTxtFld.setPreferredSize(new Dimension(199,22));
        usernameTxtFld.setMinimumSize(usernameTxtFld.getPreferredSize());
        constraints.gridx = 1;
        gridBagLayout.setConstraints(usernameTxtFld, constraints);
        self.add(usernameTxtFld);
        //passwordLbl
        passwordLbl = new JLabel("Password");
        passwordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLbl.setToolTipText("Password requested for connection");
        passwordLbl.setBounds(83,113,100,22);
        passwordLbl.setPreferredSize(new Dimension(100,22));
        passwordLbl.setMinimumSize(passwordLbl.getPreferredSize());
        constraints.gridx = 0;
        constraints.gridy = 1;
        gridBagLayout.setConstraints(passwordLbl, constraints);
        self.add(passwordLbl);
        //passwordFld
        passwordFld = new JPasswordField();
        passwordFld.setBounds(193,112,202,23);
        passwordFld.setPreferredSize(new Dimension(202,23));
        passwordFld.setMinimumSize(passwordFld.getPreferredSize());
        constraints.gridx = 1;
        gridBagLayout.setConstraints(passwordFld, constraints);
        self.add(passwordFld);
    }
    
    /**
     * Initializes this panel fields from existing settings.
     * @param wd
     */
    public void initFromSettings(WizardDescriptor wd){
        wd.addPropertyChangeListener(this);
        //Get the settings instance
        LoadOntologySettings loadOntologySettings = (LoadOntologySettings) LoadOntologySettings
        .findObject(LoadOntologySettings.class, true);
        //Initialize user's settings
        usernameTxtFld.setText(loadOntologySettings.getUsername());
        passwordFld.setText("");
    }
    
    // test panel
    public static void main(String[]args){
        JFrame f = new JFrame();
        f.getContentPane().add(new UserSettingsPanel());
        f.show();
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() instanceof WizardDescriptor){
            LoadOntologySettings loadOntologySettings = (LoadOntologySettings)LoadOntologySettings.findObject(LoadOntologySettings.class,true);
            loadOntologySettings.setUsername(usernameTxtFld.getText());
            loadOntologySettings.setPassword(passwordFld.getPassword());
        }
    }
}
