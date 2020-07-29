package music.database.gui;

import music.database.data.DataStorage;
import music.database.data.items.*;
import music.database.fields.*;
import music.database.gui.dialogs.MusicianEditDialog;
import music.database.gui.dialogs.SongAdditionDialog;
import music.database.gui.dialogs.SongRemovalDialog;
import music.database.gui.renderers.AlbumRenderer;
import music.database.gui.renderers.BandRenderer;
import music.database.gui.renderers.DataItemComboBoxRenderer;
import music.database.gui.renderers.SongRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class MusicApp extends JFrame implements WindowListener, ActionListener {

    public static final Color BACKGROUND_COLOR = new Color(255, 244, 161);
    public static final MusicApp MAIN_WINDOW = new MusicApp();
    private static final int APP_WIDTH = 1000;
    private static final int APP_HEIGHT = 750;
    private static final int INFO_PANEL_WIDTH = APP_WIDTH / 3;
    private static final int INFO_PANEL_BORDER = 30;
    private byte[] IMAGE_PLACEHOLDER;
    private JLabel m_imageLabel;

    public void showWindow() {
        setLayout(new BorderLayout());

        setTitle("Музыкальная база данных");
        setSize(APP_WIDTH, APP_HEIGHT);
        getContentPane().setBackground(BACKGROUND_COLOR);

        showBandList();

        addWindowListener(this);

        File file = new File("images/placeholder.png");
        try {
            FileInputStream input = new FileInputStream(file);
            IMAGE_PLACEHOLDER = input.readAllBytes();
        }
        catch (IOException ex) {
            System.out.println("Image placeholder not found");
            IMAGE_PLACEHOLDER = null;
        }

        pack();
        setVisible(true);
    }

    private void showTopPanel(DataItem item) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        if (item != null) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            JButton backButton = new JButton("< назад");
            backButton.setPreferredSize(new Dimension(150, 20));
            buttonPanel.add(backButton);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);
            backButton.addActionListener(new BackButtonListener(item));
        }

        JLabel mainLabel = new JLabel("Музыкальная база данных");
        mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainLabel.setFont(new Font("Arial", Font.BOLD, 30));
        topPanel.add(mainLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        mainLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    public static void main(String[] args) {
        DataStorage.initialize();
        DataStorage.refreshData();

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MAIN_WINDOW.showWindow();
            }
        });
    }

    private JPanel createListPanel(String label) {
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

    public class ListMouseAdapter extends MouseAdapter {
        private SQLItem m_item;

        public ListMouseAdapter(SQLItem item) {
            m_item = item;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JList list = (JList)e.getSource();
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                int index = list.locationToIndex(e.getPoint());
                Object selectedItem = list.getModel().getElementAt(index);
                switch (m_item) {
                    case BANDS:
                        Band band = (Band)selectedItem;
                        showBandPage(band.getID());
                        revalidate();
                        break;
                    case ALBUMS:
                        Album album = (Album)selectedItem;
                        showAlbumPage(album.getID());
                        revalidate();
                    default:
                        break;
                }
            }
        }
    }

    private class BackButtonListener implements ActionListener {
        private DataItem m_item;

        BackButtonListener(DataItem item) {
            m_item = item;
        }

        public void actionPerformed(ActionEvent e) {
            switch (m_item.getType()) {
                case BANDS:
                    showBandList();
                    break;
                case ALBUMS:
                    showBandPage(((Album)m_item).getBand().getID());
                    break;
                default:
                    break;
            }
        }
    }

    private class EditButtonListener implements ActionListener {
        private final DataItem m_item;

        EditButtonListener(DataItem item) {
            m_item = item;
        }

        public void actionPerformed(ActionEvent e) {
            //TODO
            switch (m_item.getType()) {
                case ALBUMS:
                    showAlbumEditDialog((Album)m_item);
                    break;
                case BANDS:

                    break;
                default:
                    break;
            }
        }
    }

    private class ImageButtonListener implements ActionListener {

        private Container m_container;
        private byte[] m_imageBytes;
        private JLabel m_imageLabel;

        public ImageButtonListener(Container container, JLabel imageLabel) {
            m_container = container;
            m_imageLabel = imageLabel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(m_container);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    m_imageBytes = fis.readAllBytes();
                    refreshImage(m_imageBytes, m_imageLabel, 50);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MusicApp.this,
                            "Не удалось получить изображение.",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        public byte[] getImageBytes() {
            return m_imageBytes;
        }
    }

    private void showAlbumEditDialog(Album album) {
        JDialog dialog = new JDialog(MusicApp.this, "Редактировать альбом");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JPanel songPanel = new JPanel(new GridBagLayout());
        songPanel.setOpaque(false);
        GridBagConstraints c_songPanel = new GridBagConstraints();
        c_songPanel.insets = new Insets(0, 5, 0, 5);

        JButton addSong = new JButton("Добавить песню");
        addSong.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new SongAdditionDialog(dialog, album.getID());
            }
        });
        JButton removeSong = new JButton("Удалить песню...");
        removeSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SongRemovalDialog(dialog, album.getID());
            }
        });

        c_songPanel.gridx = 0;
        c_songPanel.gridy = 0;
        songPanel.add(addSong, c_songPanel);

        c_songPanel.gridx = 1;
        c_songPanel.gridy = 0;
        songPanel.add(removeSong, c_songPanel);

        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(songPanel, c);

        currentY++;
        c.gridwidth = 1;

        JCheckBox bandCheckBox = new JCheckBox();
        bandCheckBox.setOpaque(false);
        bandCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(bandCheckBox, c);

        JLabel bandLabel = new JLabel("Группа:");
        bandLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(bandLabel, c);

        Vector<Band> bands = new Vector<>();
        Collection<DataItem> bandCollection = DataStorage.getItems(SQLItem.BANDS);
        for (DataItem item : bandCollection) {
            bands.add((Band)item);
        }

        JComboBox<Band> bandComboBox = new JComboBox<>(bands);
        bandComboBox.setSelectedItem(album.getBand());
        bandComboBox.setBackground(Color.WHITE);
        bandComboBox.setRenderer(new DataItemComboBoxRenderer());

        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(bandComboBox, c);

        currentY++;

        JCheckBox nameCheckBox = new JCheckBox();
        nameCheckBox.setOpaque(false);
        nameCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameCheckBox, c);

        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        JTextField nameText = new JTextField(album.getName());
        nameText.setColumns(20);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JCheckBox dateCheckBox = new JCheckBox();
        dateCheckBox.setOpaque(false);
        dateCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(dateCheckBox, c);

        JLabel dateLabel = new JLabel("Дата выпуска:");
        dateLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(dateLabel, c);

        LocalDate releaseDate = album.getReleaseDate();
        if (releaseDate == null) {
            releaseDate = LocalDate.now();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(releaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Date initDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -100);
        Date earliestDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 101);
        Date latestDate = calendar.getTime();
        SpinnerDateModel dateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_YEAR);

        JSpinner dateSpinner = new JSpinner(dateModel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(dateSpinner, c);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy"));

        currentY++;

        JCheckBox genreCheckBox = new JCheckBox();
        genreCheckBox.setOpaque(false);
        genreCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(genreCheckBox, c);

        JLabel genreLabel = new JLabel("Жанр:");
        genreLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(genreLabel, c);

        Vector<Genre> genres = new Vector<>();
        Collection<DataItem> genreCollection = DataStorage.getItems(SQLItem.GENRES);
        for (DataItem item : genreCollection) {
            genres.add((Genre)item);
        }

        JComboBox<Genre> genreComboBox = new JComboBox(genres);
        Genre genre = album.getGenre();
        if (genre != null) {
            genreComboBox.setSelectedItem(album.getGenre());
        }
        genreComboBox.setRenderer(new DataItemComboBoxRenderer());
        genreComboBox.setBackground(Color.WHITE);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(genreComboBox, c);

        currentY++;

        JCheckBox imageCheckBox = new JCheckBox();
        imageCheckBox.setOpaque(false);
        imageCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(imageCheckBox, c);

        JLabel imageLabel = new JLabel("Обложка:");
        imageLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(imageLabel, c);

        JLabel imagePreview = new JLabel();
        JButton imageButton = new JButton("Выбрать...");
        ImageButtonListener imageButtonListener = new ImageButtonListener(dialog, imagePreview);
        imageButton.addActionListener(imageButtonListener);

        JPanel imagePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c_imagePanel = new GridBagConstraints();
        imagePanel.setOpaque(false);

        c_imagePanel.insets = new Insets(0, 5, 0, 5);
        c_imagePanel.gridx = 0;
        c_imagePanel.gridy = 0;
        imagePanel.add(imageButton, c_imagePanel);

        c_imagePanel.gridx = 1;
        c_imagePanel.gridy = 0;
        imagePanel.add(imagePreview, c_imagePanel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(imagePanel, c);

        currentY++;

        JButton updateButton = new JButton("Применить изменения");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();

                if (bandCheckBox.isSelected()) {
                    fields.add(new IntField("Band", bands.get(bandComboBox.getSelectedIndex()).getID()));
                }
                if (nameCheckBox.isSelected()) {
                    fields.add(new NStringField("Name", nameText.getText()));
                }
                if (dateCheckBox.isSelected()) {
                    fields.add(new DateField("ReleaseDate", dateModel.getDate()));
                }
                if (genreCheckBox.isSelected()) {
                    fields.add(new IntField("Genre", genres.get(genreComboBox.getSelectedIndex()).getID()));
                }

                byte[] image = imageButtonListener.getImageBytes();
                if (imageCheckBox.isSelected() && image != null) {
                    fields.add(new BlobField("CoverImage", image));
                }
                if (!fields.isEmpty()) {
                    try {
                        DataStorage.update(new FieldContainer(album, fields));
                        JOptionPane.showMessageDialog(dialog, "Данные обновлены.",
                                "Обновление успешно", JOptionPane.INFORMATION_MESSAGE);
                        showAlbumPage(album.getID());
                        dialog.dispose();
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Обновление не удалось.",
                                "Обновление не удалось", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(dialog, "Поля для обновления не выбраны.",
                            "Ошибка обновления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 1;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(updateButton, c);

        dialog.setPreferredSize(new Dimension(430, 350));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showBandList() {
        getContentPane().removeAll();
        showTopPanel(null);

        DefaultListModel<Band> listModel = new DefaultListModel<>();
        Collection<DataItem> dataItems = DataStorage.getItems(SQLItem.BANDS);
        for (DataItem dataItem : dataItems) {
            Band band = (Band)dataItem;
            listModel.addElement(band);
        }

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setCellRenderer(new BandRenderer());
        //m_list.getSelectionModel().addListSelectionListener(MusicApp.this);
        list.addMouseListener(new ListMouseAdapter(SQLItem.BANDS));

        JPanel listPanel = createListPanel("Группы");

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
        listPanel.add(scrollPane);

        add(listPanel, BorderLayout.CENTER);
    }

    public void showBandPage(int bandID) {
        getContentPane().removeAll();
        Band thisBand = (Band)DataStorage.getItemByID(SQLItem.BANDS, bandID);
        showTopPanel(thisBand);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, APP_HEIGHT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, INFO_PANEL_BORDER, INFO_PANEL_BORDER, 0));

        m_imageLabel = new JLabel();
        refreshImage(thisBand.getImage(), m_imageLabel, INFO_PANEL_WIDTH - INFO_PANEL_BORDER);
        infoPanel.add(m_imageLabel);

        JLabel bandDateLabel = new JLabel();
        short formYear = thisBand.getFormYear();
        short disbandYear = thisBand.getDisbandYear();
        StringBuilder sb = new StringBuilder("Годы активности: ");
        sb.append(formYear);
        sb.append("-");
        if (disbandYear > 0) {
            sb.append(disbandYear);
        }
        else {
            sb.append("...");
        }
        bandDateLabel.setText(sb.toString());
        setInfoPanelLabelStyle(bandDateLabel);
        infoPanel.add(bandDateLabel);

        Vector<Musician> musicians = thisBand.getMusicians();
        for (Musician musician : musicians) {
            infoPanel.add(createMusicianLabel(musician));
        }
        add(infoPanel, BorderLayout.WEST);

        DefaultListModel<Album> listModel = new DefaultListModel<>();
        Vector<Album> albums = thisBand.getAlbums();
        for (Album album : albums) {
            listModel.addElement(album);
        }

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setCellRenderer(new AlbumRenderer());
        list.addMouseListener(new ListMouseAdapter(SQLItem.ALBUMS));

        JPanel listPanel = createListPanel("Альбомы " + thisBand.getName());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(APP_WIDTH - INFO_PANEL_WIDTH, APP_HEIGHT));
        listPanel.add(scrollPane);

        JButton editButton = new JButton("Редактировать...");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBandEditDialog(bandID);
            }
        });
        infoPanel.add(editButton);

        add(listPanel, BorderLayout.CENTER);
    }

    private void showAlbumRemovalDialog(JDialog mainDialog, int bandID) {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, bandID);
        JDialog dialog = new JDialog(mainDialog, "Удалить альбом");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        Vector<Album> albums = band.getAlbums();
        JComboBox<Album> albumComboBox = new JComboBox<>(albums);
        albumComboBox.setBackground(Color.WHITE);
        albumComboBox.setRenderer(new DataItemComboBoxRenderer());
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(albumComboBox, c);

        currentY++;

        JButton confirmButton = new JButton("Удалить альбом");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Vector<Field> fields = new Vector<>();
                    fields.add(new IntField("ID", albums.get(albumComboBox.getSelectedIndex()).getID()));
                    DataStorage.delete(SQLItem.ALBUMS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(dialog, "Альбом удалён.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    showBandPage(bandID);
                    dialog.dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Удаление не удалось.",
                            "Ошибка при удалении", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        dialog.setPreferredSize(new Dimension(430, 150));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showAlbumAdditionDialog(JDialog mainDialog, int bandID) {
        JDialog dialog = new JDialog(mainDialog, "Добавить альбом");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        JTextField nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(250, 20));
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JButton confirmButton = new JButton("Добавить альбом");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();
                fields.add(new NStringField("Name", nameText.getText()));
                fields.add(new IntField("Band", bandID));

                try {
                    DataStorage.insert(SQLItem.ALBUMS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(dialog, "Альбом добавлен.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    showBandPage(bandID);
                    dialog.dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Обновление не удалось.",
                            "Добавление не удалось", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        dialog.setPreferredSize(new Dimension(430, 150));
        dialog.pack();
        dialog.setVisible(true);
    }

//    private void showMusicianEditDialog(JDialog mainDialog, int bandID) {
//        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, bandID);
//        JDialog dialog = new JDialog(mainDialog, "Редактировать группу");
//        Container contentPane = dialog.getContentPane();
//        contentPane.setLayout(new GridBagLayout());
//        contentPane.setBackground(BACKGROUND_COLOR);
//        GridBagConstraints c = new GridBagConstraints();
//
//        c.insets = new Insets(5, 5, 5, 5);
//        c.fill = GridBagConstraints.HORIZONTAL;
//
//        int currentY = 0;
//
//        Vector<Musician> musicians = band.getMusicians();
//        JComboBox<Musician> musicianComboBox = new JComboBox<>(musicians);
//        musicianComboBox.setRenderer(new DataItemComboBoxRenderer());
//        musicianComboBox.setBackground(Color.WHITE);
//        c.gridx = 0;
//        c.gridy = currentY;
//        c.gridwidth = 3;
//        contentPane.add(musicianComboBox, c);
//
//        currentY++;
//
//        JPanel instrumentPanel = new JPanel(new GridBagLayout());
//        instrumentPanel.setOpaque(false);
//        GridBagConstraints c_instrPanel = new GridBagConstraints();
//        c_instrPanel.insets = new Insets(0, 5, 0, 5);
//
//        JButton addInstrumentButton = new JButton("Присоединить инструмент...");
//        addInstrumentButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Musician selectedMusician = musicians.get(musicianComboBox.getSelectedIndex());
//                showInstrumentConnectionDialog(dialog, selectedMusician.getID());
//            }
//        });
//        c_instrPanel.gridx = 0;
//        c_instrPanel.gridy = 0;
//        instrumentPanel.add(addInstrumentButton, c_instrPanel);
//
//        JButton removeInstrumentButton = new JButton("Отсоединить инструмент...");
//        removeInstrumentButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Musician selectedMusician = musicians.get(musicianComboBox.getSelectedIndex());
//                showInstrumentDisconnectionDialog(dialog, selectedMusician.getID());
//            }
//        });
//        c_instrPanel.gridx = 1;
//        c_instrPanel.gridy = 0;
//        instrumentPanel.add(removeInstrumentButton, c_instrPanel);
//
//        c.gridx = 0;
//        c.gridy = currentY;
//        c.gridwidth = 3;
//        contentPane.add(instrumentPanel, c);
//
//        currentY++;
//        c.gridwidth = 1;
//
//        JCheckBox nameCheckBox = new JCheckBox();
//        nameCheckBox.setSelected(false);
//        nameCheckBox.setOpaque(false);
//        c.gridx = 0;
//        c.gridy = currentY;
//        contentPane.add(nameCheckBox, c);
//
//        JLabel newNameLabel = new JLabel("Имя:");
//        newNameLabel.setOpaque(false);
//        c.gridx = 1;
//        c.gridy = currentY;
//        contentPane.add(newNameLabel, c);
//
//        JTextField nameText = new JTextField();
//        c.gridx = 2;
//        c.gridy = currentY;
//        contentPane.add(nameText, c);
//
//        currentY++;
//
//        JCheckBox birthDateCheckBox = new JCheckBox();
//        birthDateCheckBox.setSelected(false);
//        birthDateCheckBox.setOpaque(false);
//        c.gridx = 0;
//        c.gridy = currentY;
//        contentPane.add(birthDateCheckBox, c);
//
//        JLabel birthDateLabel = new JLabel("Дата рождения:");
//        birthDateLabel.setOpaque(false);
//        c.gridx = 1;
//        c.gridy = currentY;
//        contentPane.add(birthDateLabel, c);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(Date.from(Instant.now()));
//        Date initDate = calendar.getTime();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.YEAR, -100);
//        Date earliestDate = calendar.getTime();
//        calendar.setTime(initDate);
//        Date latestDate = calendar.getTime();
//        SpinnerDateModel birthDateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_YEAR);
//
//        JSpinner birthDateSpinner = new JSpinner(birthDateModel);
//        c.gridx = 2;
//        c.gridy = currentY;
//        contentPane.add(birthDateSpinner, c);
//        birthDateSpinner.setEditor(new JSpinner.DateEditor(birthDateSpinner, "dd.MM.yyyy"));
//
//        currentY++;
//
//        JCheckBox deathDateCheckBox = new JCheckBox();
//        deathDateCheckBox.setSelected(false);
//        deathDateCheckBox.setOpaque(false);
//        c.gridx = 0;
//        c.gridy = currentY;
//        contentPane.add(deathDateCheckBox, c);
//
//        JLabel deathDateLabel = new JLabel("Дата смерти:");
//        deathDateLabel.setOpaque(false);
//        c.gridx = 1;
//        c.gridy = currentY;
//        contentPane.add(deathDateLabel, c);
//
//        SpinnerDateModel deathDateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_YEAR);
//
//        JSpinner deathDateSpinner = new JSpinner(deathDateModel);
//        c.gridx = 2;
//        c.gridy = currentY;
//        contentPane.add(deathDateSpinner, c);
//        deathDateSpinner.setEditor(new JSpinner.DateEditor(deathDateSpinner, "dd.MM.yyyy"));
//
//        currentY++;
//
//        JButton confirmButton = new JButton("Применить изменения");
//        confirmButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Vector<Field> fields = new Vector<>();
//
//                if (nameCheckBox.isSelected()) {
//                    fields.add(new NStringField("Name", nameText.getText()));
//                }
//                if (birthDateCheckBox.isSelected()) {
//                    fields.add(new DateField("DateOfBirth", birthDateModel.getDate()));
//                }
//                if (deathDateCheckBox.isSelected()) {
//                    fields.add(new DateField("DateOfDeath", deathDateModel.getDate()));
//                }
//                if (!fields.isEmpty()) {
//                    try {
//                        DataStorage.update(
//                                new FieldContainer(musicians.get(musicianComboBox.getSelectedIndex()), fields)
//                        );
//                        JOptionPane.showMessageDialog(dialog, "Данные обновлены.",
//                                "Обновление успешно", JOptionPane.INFORMATION_MESSAGE);
//                        showBandPage(bandID);
//                        dialog.dispose();
//                    }
//                    catch (SQLException ex) {
//                        JOptionPane.showMessageDialog(dialog, "Обновление не удалось.",
//                                "Обновление не удалось", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//                else {
//                    JOptionPane.showMessageDialog(dialog, "Поля для обновления не выбраны.",
//                            "Ошибка обновления", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//        c.gridx = 0;
//        c.gridy = currentY;
//        c.gridwidth = 3;
//        contentPane.add(confirmButton, c);
//
//        musicianComboBox.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                Musician musician = (Musician)e.getItem();
//                nameText.setText(musician.getName());
//                LocalDate birthDate = musician.getBirthDate();
//                if (birthDate != null) {
//                    birthDateModel.setValue(Date.from(birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                }
//                LocalDate deathDate = musician.getDeathDate();
//                if (deathDate != null) {
//                    deathDateModel.setValue(Date.from(deathDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                }
//            }
//        });
//
//        dialog.setPreferredSize(new Dimension(430, 250));
//        dialog.pack();
//        dialog.setVisible(true);
//    }

    private void showInstrumentDisconnectionDialog(JDialog mainDialog, int musicianID) {
        JDialog dialog = new JDialog(mainDialog, "Отсоединить инструмент");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Инструмент:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        Musician musician = (Musician) DataStorage.getItemByID(SQLItem.MUSICIANS, musicianID);
        Vector<Instrument> instruments = musician.getInstruments();
        JComboBox<Instrument> instrumentComboBox = new JComboBox<>(instruments);
        instrumentComboBox.setRenderer(new DataItemComboBoxRenderer());
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(instrumentComboBox, c);

        currentY++;

        JButton confirmButton = new JButton("Отсоединить инструмент");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Vector<Field> fields = new Vector<>();
                    fields.add(new IntField("MusicianID", musicianID));
                    fields.add(
                            new IntField(
                                    "InstrumentID",
                                    instruments.get(
                                            instrumentComboBox.getSelectedIndex()).getID()
                            )
                    );
                    DataStorage.delete(SQLItem.MUSICIAN_INSTRUMENT, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(dialog, "Инструмент отсоединён.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    Musician musician = (Musician) DataStorage.getItemByID(SQLItem.MUSICIANS, musicianID);
                    showBandPage(musician.getBand().getID());
                    dialog.dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Отсоединение не удалось.",
                            "Ошибка при удалении", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        dialog.setPreferredSize(new Dimension(430, 150));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showInstrumentRemovalDialog(JDialog mainDialog) {
        //TODO
    }

    private void showBandAdditionDialog() {
        //TODO
    }

    private void showBandEditDialog(int bandID) {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, bandID);
        JDialog dialog = new JDialog(MusicApp.this, "Редактировать группу");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JButton musicianEditButton = new JButton("Редактировать музыканта...");
        musicianEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicianEditDialog(dialog, bandID);
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(musicianEditButton, c);

        currentY++;

        JPanel musicianPanel = new JPanel(new GridBagLayout());
        musicianPanel.setOpaque(false);
        GridBagConstraints c_musicianPanel = new GridBagConstraints();
        c_musicianPanel.insets = new Insets(0, 5, 0, 5);

        JButton addMusician = new JButton("Добавить музыканта...");
        addMusician.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMusicianAdditionDialog(dialog, bandID);
            }
        });
        JButton removeMusician = new JButton("Удалить музыканта...");
        removeMusician.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMusicianRemovalDialog(dialog, bandID);
            }
        });

        c_musicianPanel.gridx = 0;
        c_musicianPanel.gridy = 0;
        musicianPanel.add(addMusician, c_musicianPanel);

        c_musicianPanel.gridx = 1;
        c_musicianPanel.gridy = 0;
        musicianPanel.add(removeMusician, c_musicianPanel);

        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(musicianPanel, c);

        currentY++;

        JPanel albumPanel = new JPanel(new GridBagLayout());
        albumPanel.setOpaque(false);
        GridBagConstraints c_albumPanel = new GridBagConstraints();
        c_albumPanel.insets = new Insets(0, 5, 0, 5);

        JButton addAlbum = new JButton("Добавить альбом...");
        addAlbum.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showAlbumAdditionDialog(dialog, bandID);
            }
        });
        JButton removeAlbum = new JButton("Удалить альбом...");
        removeAlbum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAlbumRemovalDialog(dialog, bandID);
            }
        });

        c_albumPanel.gridx = 0;
        c_albumPanel.gridy = 0;
        albumPanel.add(addAlbum, c_albumPanel);

        c_albumPanel.gridx = 1;
        c_albumPanel.gridy = 0;
        albumPanel.add(removeAlbum, c_albumPanel);

        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(albumPanel, c);

        currentY++;

        c.gridwidth = 1;

        JCheckBox nameCheckBox = new JCheckBox();
        nameCheckBox.setOpaque(false);
        nameCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameCheckBox, c);

        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        JTextField nameText = new JTextField(band.getName());
        nameText.setColumns(20);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JCheckBox formDateCheckBox = new JCheckBox();
        formDateCheckBox.setOpaque(false);
        formDateCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(formDateCheckBox, c);

        JLabel formDateLabel = new JLabel("Год формирования:");
        formDateLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(formDateLabel, c);

        int currentYear = LocalDate.now().getYear();
        SpinnerNumberModel formDateModel = new SpinnerNumberModel(
                band.getFormYear(), 1900, currentYear, 1
        );
        JSpinner formDateSpinner = new JSpinner(formDateModel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(formDateSpinner, c);

        currentY++;

        JCheckBox disbandDateCheckBox = new JCheckBox();
        disbandDateCheckBox.setOpaque(false);
        disbandDateCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(disbandDateCheckBox, c);

        JLabel disbandDateLabel = new JLabel("Год распада:");
        disbandDateLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(disbandDateLabel, c);

        int disbandYear = band.getDisbandYear() > 0 ? band.getDisbandYear() : currentYear;

        SpinnerNumberModel disbandDateModel = new SpinnerNumberModel(
                disbandYear, 1900, currentYear, 1
        );
        JSpinner disbandDateSpinner = new JSpinner(disbandDateModel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(disbandDateSpinner, c);

        currentY++;

        JCheckBox imageCheckBox = new JCheckBox();
        imageCheckBox.setOpaque(false);
        imageCheckBox.setSelected(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(imageCheckBox, c);

        JLabel imageLabel = new JLabel("Фотография:");
        imageLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(imageLabel, c);

        JLabel imagePreview = new JLabel();
        JButton imageButton = new JButton("Выбрать...");
        ImageButtonListener imageButtonListener = new ImageButtonListener(dialog, imagePreview);
        imageButton.addActionListener(imageButtonListener);

        JPanel imagePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c_imagePanel = new GridBagConstraints();
        imagePanel.setOpaque(false);

        c_imagePanel.insets = new Insets(0, 5, 0, 5);
        c_imagePanel.gridx = 0;
        c_imagePanel.gridy = 0;
        imagePanel.add(imageButton, c_imagePanel);

        c_imagePanel.gridx = 1;
        c_imagePanel.gridy = 0;
        imagePanel.add(imagePreview, c_imagePanel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(imagePanel, c);

        currentY++;

        JButton updateButton = new JButton("Применить изменения");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();

                if (nameCheckBox.isSelected()) {
                    fields.add(new NStringField("Name", nameText.getText()));
                }
                if (formDateCheckBox.isSelected()) {
                    fields.add(new IntField("YearOfFormation", formDateModel.getNumber().intValue()));
                }
                if (disbandDateCheckBox.isSelected()) {
                    fields.add(new IntField("YearOfDisbanding", disbandDateModel.getNumber().intValue()));
                    //TODO добавить возможность удалить дату распада
                }

                byte[] image = imageButtonListener.getImageBytes();
                if (imageCheckBox.isSelected() && image != null) {
                    fields.add(new BlobField("Photo", image));
                }
                if (!fields.isEmpty()) {
                    try {
                        DataStorage.update(new FieldContainer(band, fields));
                        JOptionPane.showMessageDialog(dialog, "Данные обновлены.",
                                "Обновление успешно", JOptionPane.INFORMATION_MESSAGE);
                        showBandPage(bandID);
                        dialog.dispose();
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Обновление не удалось.",
                                "Обновление не удалось", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(dialog, "Поля для обновления не выбраны.",
                            "Ошибка обновления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 1;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(updateButton, c);

        dialog.setPreferredSize(new Dimension(430, 375));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showMusicianAdditionDialog(JDialog mainDialog, int bandID) {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, bandID);
        JDialog dialog = new JDialog(mainDialog, "Добавить альбом");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        JTextField nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(250, 20));
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JButton confirmButton = new JButton("Добавить музыканта");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();
                fields.add(new NStringField("Name", nameText.getText()));
                fields.add(new IntField("Band", bandID));

                try {
                    DataStorage.insert(SQLItem.MUSICIANS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(dialog, "Музыкант добавлен.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    showBandPage(bandID);
                    dialog.dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Обновление не удалось.",
                            "Добавление не удалось", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        dialog.setPreferredSize(new Dimension(430, 150));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void showMusicianRemovalDialog(JDialog mainDialog, int bandID) {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, bandID);
        JDialog dialog = new JDialog(mainDialog, "Удалить альбом");
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        Vector<Musician> musicians = band.getMusicians();
        JComboBox<Musician> musicianComboBox = new JComboBox<>(musicians);
        musicianComboBox.setRenderer(new DataItemComboBoxRenderer());
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(musicianComboBox, c);

        currentY++;

        JButton confirmButton = new JButton("Удалить музыканта");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Vector<Field> fields = new Vector<>();
                    fields.add(new IntField("ID", musicians.get(musicianComboBox.getSelectedIndex()).getID()));
                    DataStorage.delete(SQLItem.MUSICIANS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(dialog, "Музыкант удалён.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    showBandPage(bandID);
                    dialog.dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Удаление не удалось.",
                            "Ошибка при удалении", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        dialog.setPreferredSize(new Dimension(430, 150));
        dialog.pack();
        dialog.setVisible(true);
    }

    public void showAlbumPage(int albumID) {
        getContentPane().removeAll();
        Album album = (Album)DataStorage.getItemByID(SQLItem.ALBUMS, albumID);
        Band band = album.getBand();
        showTopPanel(album);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, APP_HEIGHT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, INFO_PANEL_BORDER, INFO_PANEL_BORDER, 0));

        m_imageLabel = new JLabel();
        refreshImage(album.getImage(), m_imageLabel, INFO_PANEL_WIDTH - INFO_PANEL_BORDER);

        infoPanel.add(m_imageLabel);

        Genre genre = album.getGenre();
        if (genre != null) {
            JLabel genreLabel = new JLabel("Жанр: " + album.getGenre().getName());
            setInfoPanelLabelStyle(genreLabel);
            infoPanel.add(genreLabel);
        }

        LocalDate releaseDate = album.getReleaseDate();
        if (releaseDate != null) {
            JLabel releaseDateLabel = new JLabel("Дата выпуска: " + releaseDate.toString());
            setInfoPanelLabelStyle(releaseDateLabel);
            infoPanel.add(releaseDateLabel);
        }

        add(infoPanel, BorderLayout.WEST);

        DefaultListModel<Song> listModel = new DefaultListModel<>();
        Vector<Song> songs = album.getSongs();
        for (Song song : songs) {
            listModel.addElement(song);
        }

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setCellRenderer(new SongRenderer());

        JPanel listPanel = createListPanel(band.getName() + " - " + album.getName());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(APP_WIDTH - INFO_PANEL_WIDTH, APP_HEIGHT));
        listPanel.add(scrollPane);

        JButton editButton = new JButton("Редактировать...");
        editButton.addActionListener(new EditButtonListener(album));
        infoPanel.add(editButton);

        add(listPanel, BorderLayout.CENTER);
    }

    private void refreshImage(byte[] bytes, JLabel imageLabel, int maxDimension) {
        byte[] imageByteArray = bytes;
        if (imageByteArray == null) {
            imageByteArray = IMAGE_PLACEHOLDER;
        }
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageByteArray));
            int width = image.getWidth();
            int height = image.getHeight();
            int scaledWidth;
            int scaledHeight;
            if (width > height) {
                scaledWidth = maxDimension;
                scaledHeight = (int)(height * ((double)scaledWidth / width));
            }
            else {
                scaledHeight = maxDimension;
                scaledWidth = (int)(width * ((double)scaledHeight / height));
            }
            imageLabel.setIcon(new ImageIcon(
                    image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
            ));
        }
        catch (IOException ex) {
            System.out.println("Error while loading image");
        }
    }

    private static JLabel createMusicianLabel(Musician musician) {
        StringBuilder sb = new StringBuilder(musician.getName());

        LocalDate birthDate = musician.getBirthDate();
        LocalDate deathDate = musician.getDeathDate();

        if (birthDate != null) {
            sb.append(" (");
            sb.append(birthDate.getYear());
            sb.append("-");
            if (deathDate != null) {
                sb.append(deathDate.getYear());
            }
            else {
                sb.append("...");
            }
            sb.append(")");
        }
        else if (deathDate != null) {
            sb.append("(...-");
            sb.append(deathDate.getYear());
            sb.append(")");
        }

        boolean isFirst = true;

        for (Instrument instrument : musician.getInstruments()) {
            if (isFirst) {
                sb.append(" - ");
                isFirst = false;
            }
            else sb.append(", ");

            sb.append(instrument.getName().toLowerCase());
        }

        JLabel label = new JLabel(sb.toString());
        setInfoPanelLabelStyle(label);

        return label;
    }

    private static void setInfoPanelLabelStyle(JLabel label) {
        String text = label.getText();
        StringBuilder sb = new StringBuilder("<html>");
        sb.append(text);
        sb.append("</html>");
        label.setText(sb.toString());
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    }

    private static JLabel createListLabel(String text) {
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