package music.database;

import music.database.items.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MusicApp extends Frame implements WindowListener, ActionListener {

    public MusicApp() {
        setLayout(new BorderLayout());

        setTitle("Музыкальная база данных");
        setSize(1000, 750);
        setBackground(new Color(255, 244, 161));

        Panel topPanel = new Panel(new BorderLayout());

        Label mainLabel = new Label("Музыкальная база данных");
        mainLabel.setAlignment(Label.CENTER);
        mainLabel.setFont(new Font("Serif", Font.BOLD, 30));
        topPanel.add(mainLabel, BorderLayout.CENTER);

        Panel paddingPanel = new Panel();
        paddingPanel.setSize(0, 50);
        topPanel.add(paddingPanel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        addWindowListener(this);

        setVisible(true);
    }

    public static void main(String[] args) {
        DataStorage.initialize();
        DataStorage.refreshData();
        new MusicApp();
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        System.exit(0);  // Terminate the program
    }

    // Not Used, BUT need to provide an empty body to compile.
    @Override public void windowOpened(WindowEvent evt) { }
    @Override public void windowClosed(WindowEvent evt) { }
    // For Debugging
    @Override public void windowIconified(WindowEvent evt) { System.out.println("Window Iconified"); }
    @Override public void windowDeiconified(WindowEvent evt) { System.out.println("Window Deiconified"); }
    @Override public void windowActivated(WindowEvent evt) { System.out.println("Window Activated"); }
    @Override public void windowDeactivated(WindowEvent evt) { System.out.println("Window Deactivated"); }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
