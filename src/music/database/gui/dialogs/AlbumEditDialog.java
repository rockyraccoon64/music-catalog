package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.*;
import music.database.fields.*;
import music.database.gui.ImageButtonListener;
import music.database.gui.MusicApp;
import music.database.gui.renderers.DataItemComboBoxRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

public class AlbumEditDialog extends JDialog {

    private final int ALBUM_ID;

    public AlbumEditDialog(int albumID) {
        super(MusicApp.MAIN_WINDOW, "Редактировать альбом");
        ALBUM_ID = albumID;
        refresh();
    }

    public void refresh() {
        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(MusicApp.BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JPanel songPanel = new JPanel(new GridBagLayout());
        songPanel.setOpaque(false);
        GridBagConstraints c_songPanel = new GridBagConstraints();
        c_songPanel.insets = new Insets(0, 5, 0, 5);

        JButton addSong = new JButton("Добавить песню...");
        addSong.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new SongAdditionDialog(AlbumEditDialog.this, ALBUM_ID);
            }
        });
        JButton removeSong = new JButton("Удалить песню...");
        removeSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SongRemovalDialog(AlbumEditDialog.this, ALBUM_ID);
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

        Album album = (Album) DataStorage.getItemByID(SQLItem.ALBUMS, ALBUM_ID);
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
        ImageButtonListener imageButtonListener = new ImageButtonListener(AlbumEditDialog.this, imagePreview);
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
                        JOptionPane.showMessageDialog(AlbumEditDialog.this, "Данные обновлены.",
                                "Обновление успешно", JOptionPane.INFORMATION_MESSAGE);
                        MusicApp.MAIN_WINDOW.showAlbumPage(album.getID());
                        dispose();
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(AlbumEditDialog.this, "Обновление не удалось.",
                                "Обновление не удалось", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(AlbumEditDialog.this, "Поля для обновления не выбраны.",
                            "Ошибка обновления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 1;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(updateButton, c);

        setPreferredSize(new Dimension(430, 350));
        pack();
        setLocationRelativeTo(MusicApp.MAIN_WINDOW);
        setVisible(true);
    }
}
