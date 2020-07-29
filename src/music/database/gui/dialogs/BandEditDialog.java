package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.Band;
import music.database.data.items.SQLItem;
import music.database.fields.*;
import music.database.gui.ImageButtonListener;
import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

import static music.database.gui.MusicApp.BACKGROUND_COLOR;

public class BandEditDialog extends JDialog {

    private final int BAND_ID;

    public BandEditDialog(int bandID) {
        super(MusicApp.MAIN_WINDOW, "Редактировать группу");
        BAND_ID = bandID;
        refresh();
    }

    private void refresh() {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, BAND_ID);
        Container contentPane = getContentPane();
        contentPane.removeAll();
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
                new MusicianEditDialog(BandEditDialog.this, BAND_ID);
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
                new MusicianAdditionDialog(BandEditDialog.this, BAND_ID);
            }
        });
        JButton removeMusician = new JButton("Удалить музыканта...");
        removeMusician.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicianRemovalDialog(BandEditDialog.this, BAND_ID);
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
                new AlbumAdditionDialog(BandEditDialog.this, BAND_ID);
            }
        });
        JButton removeAlbum = new JButton("Удалить альбом...");
        removeAlbum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AlbumRemovalDialog(BandEditDialog.this, BAND_ID);
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
        ImageButtonListener imageButtonListener = new ImageButtonListener(BandEditDialog.this, imagePreview);
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
                        JOptionPane.showMessageDialog(BandEditDialog.this, "Данные обновлены.",
                                "Обновление успешно", JOptionPane.INFORMATION_MESSAGE);
                        MusicApp.MAIN_WINDOW.showBandPage(BAND_ID);
                        dispose();
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(BandEditDialog.this, "Обновление не удалось.",
                                "Обновление не удалось", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(BandEditDialog.this, "Поля для обновления не выбраны.",
                            "Ошибка обновления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 1;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(updateButton, c);

        setPreferredSize(new Dimension(430, 375));
        pack();
        setVisible(true);
    }
}
