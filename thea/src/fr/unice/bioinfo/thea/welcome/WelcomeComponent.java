package fr.unice.bioinfo.thea.welcome;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * @author SAÏD, EL KASMI.
 */
public class WelcomeComponent extends TopComponent {

    public WelcomeComponent() {
        super();
        initComponents();
        this.setName(NbBundle.getMessage(WelcomeComponent.class,
                "LBL_WelcomeComponent_Name")); // NOI18N
    }

    // A WelcomeComponent instance to use with the
    // Singleton Pattern.
    private static WelcomeComponent welcomeComponent = null;

    /**
     * Return an instance of the WelcomeComponent class using the Singleton
     * Pattern.
     * @return welcomeComponent - WelcomeComponent Instance.
     */
    public static WelcomeComponent getWelcomeComponent() {
        if (welcomeComponent == null) {
            welcomeComponent = new WelcomeComponent();
        }
        return welcomeComponent;
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
                    .getResourceAsStream("fr/unice/bioinfo/thea/welcome/welcome.htm");

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