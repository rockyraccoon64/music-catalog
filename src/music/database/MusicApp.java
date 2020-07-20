package music.database;

import music.database.items.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public class MusicApp extends JFrame implements WindowListener, ActionListener {

    public static MusicApp mainFrame;
    private static final int APP_WIDTH = 1000;
    private static final int APP_HEIGHT = 750;
    private static final Color BACKGROUND_COLOR = new Color(255, 244, 161);

    public MusicApp() {
        setLayout(new BorderLayout());

        setTitle("Музыкальная база данных");
        setSize(APP_WIDTH, APP_HEIGHT);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(APP_WIDTH, 75));
        topPanel.setOpaque(false);

        JLabel mainLabel = new JLabel("Музыкальная база данных");
        mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainLabel.setFont(new Font("Arial", Font.BOLD, 30));
        topPanel.add(mainLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        //showBandListPage();
        showAlbumListPage(1);

        addWindowListener(this);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        DataStorage.initialize();
        DataStorage.refreshData();

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MusicApp();
            }
        });
    }

    public JPanel createListPanel(String label) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 30, 30, 30),
                BorderFactory.createLineBorder(Color.BLACK, 3)));
        listPanel.setOpaque(false);

        JLabel bandLabel = createListLabel(label);
        listPanel.add(bandLabel, BorderLayout.NORTH);

        return listPanel;
    }

    public void showBandListPage() {
        DefaultListModel<Band> listModel = new DefaultListModel<>();
        Collection<DataItem> dataItems = DataStorage.getItems(SQLItem.BANDS);
        for (DataItem dataItem : dataItems) {
            Band band = (Band)dataItem;
            listModel.addElement(band);
        }

        JList bandList = new JList(listModel);
        bandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bandList.setLayoutOrientation(JList.VERTICAL);
        bandList.setVisibleRowCount(-1);
        bandList.setCellRenderer(new BandRenderer());

        JPanel listPanel = createListPanel("Группы");

        JScrollPane scrollPane = new JScrollPane(bandList);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(APP_WIDTH, 500));
        listPanel.add(scrollPane);

        add(listPanel, BorderLayout.CENTER);
    }

    public void showAlbumListPage(int bandID) {
        Band thisBand = (Band)DataStorage.getItemByID(SQLItem.BANDS, bandID);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(APP_WIDTH / 3, 500));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        TreeSet<Musician> musicians = thisBand.getMusicians();

        for (Musician musician : musicians) {
            infoPanel.add(createMusicianLabel(musician));
        }
        add(infoPanel, BorderLayout.WEST);

        DefaultListModel<Album> listModel = new DefaultListModel<>();
        //Collection<DataItem> albums = DataStorage.getItems(SQLItem.ALBUMS);
        //albums.removeIf(item -> ((Album)item).getBand().getID() == bandID);

        TreeSet<Album> albums = thisBand.getAlbums();

        for (Album album : albums) {
            listModel.addElement(album);
        }

        JList albumList = new JList(listModel);
        albumList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        albumList.setLayoutOrientation(JList.VERTICAL);
        albumList.setVisibleRowCount(-1);
        albumList.setCellRenderer(new AlbumRenderer());

        JPanel listPanel = createListPanel("Альбомы " + thisBand.getName());

        JScrollPane scrollPane = new JScrollPane(albumList);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(APP_WIDTH - infoPanel.getWidth(), 500));
        listPanel.add(scrollPane);

        add(listPanel, BorderLayout.CENTER);
    }

    public static JLabel createMusicianLabel(Musician musician) {
        StringBuilder sb = new StringBuilder(musician.getName());

        boolean isFirst = true;

        for (Instrument instrument : musician.getInstruments()) {
            if (isFirst) {
                sb.append(" - ");
                isFirst = false;
            }
            else sb.append(", ");

            sb.append(instrument.getName().toLowerCase());
        }

        sb.insert(0, "<html>");
        sb.append("</html>");

        JLabel label = new JLabel(sb.toString());
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        return label;
    }

    public static JLabel createListLabel(String text) {
        JLabel bandLabel = new JLabel(text);
        bandLabel.setHorizontalAlignment(JLabel.CENTER);
        bandLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        bandLabel.setBackground(new Color(171, 255, 156));
        bandLabel.setOpaque(true);
        return bandLabel;
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
