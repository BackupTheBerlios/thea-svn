package fr.unice.bioinfo.thea.core.welcome;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * A simple component used to hold an html file which contains
 * a welcome message and some informations about Thea.
 * @author SAÏD, EL KASMI.
 */
public class WelcomeComponent extends TopComponent {
    
    /** generated Serialized Version UID */
    static final long serialVersionUID = 801132859705717911L;


    public WelcomeComponent() {
        super();
        initComponents();
        this.setName(NbBundle.getMessage(WelcomeComponent.class,
                "LBL_WelcomeComponent")); // NOI18N
    }

    // A WelcomeComponent instance to use with the
    // Singleton Pattern.
    private static WelcomeComponent welcomeComponent = null;
    /** A hint to the window system for generating a unique id */
    private static final String PREFERRED_ID = "welcomepanel"; // NOI18N

    /**
     * Return an instance of the WelcomeComponent class using the Singleton
     * Pattern.
     * @return welcomeComponent - WelcomeComponent Instance.
     */
    public static WelcomeComponent getInstance() {
        //look for an open instance
        Iterator opened = TopComponent.getRegistry().getOpened().iterator();
        while (opened.hasNext()) {
            Object tc = opened.next();
            if (tc instanceof WelcomeComponent) {
                return (WelcomeComponent) tc;
            }
        }
        // none found, make a new one
        return new WelcomeComponent();
    }
    
    /* (non-Javadoc)
     * @see org.openide.windows.TopComponent#preferredID()
     */
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    /* (non-Javadoc)
     * @see org.openide.windows.TopComponent#getPersistenceType()
     */
    public int getPersistenceType() {
        return super.PERSISTENCE_ALWAYS;
    }
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JEditorPane htmlPane = new JEditorPane ();
        htmlPane.setEditable(false);
        htmlPane.setContentType("text/html");
        htmlPane.setEditorKit(new HTMLEditorKit());

        try {
            InputStream stream = getClass().getClassLoader()
                    .getResourceAsStream("fr/unice/bioinfo/thea/core/welcome/welcome.htm");

            StringBuffer buf = new StringBuffer(1000);
            Reader r = new InputStreamReader(stream);

            BufferedReader br = new BufferedReader(r);
            String s = br.readLine();
            while (s != null) {
                buf.append(s);
                s = br.readLine();
            }

            htmlPane.setText(buf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final JScrollPane scrollPane = new JScrollPane(htmlPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(0);
                //scrollRectToVisible(new Rectangle(0,0,1,1));
            }
        });

    }
}