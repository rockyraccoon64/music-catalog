package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.SQLItem;
import music.database.fields.DateField;
import music.database.fields.Field;
import music.database.fields.FieldContainer;
import music.database.fields.NStringField;
import music.database.data.items.Band;
import music.database.data.items.Musician;
import music.database.gui.renderers.DataItemComboBoxRenderer;
import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class MusicianEditDialog extends JDialog {

    private final JDialog OWNER;
    private final int BAND_ID;

    public MusicianEditDialog(JDialog owner, int bandID) {
        super(owner, "Редактировать музыканта");
        OWNER = owner;
        BAND_ID = bandID;
        refresh();
    }

    public void refresh() {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, BAND_ID);
        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(MusicApp.BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        Vector<Musician> musicians = band.getMusicians();
        JComboBox<Musician> musicianComboBox = new JComboBox<>(musicians);
        musicianComboBox.setRenderer(new DataItemComboBoxRenderer());
        musicianComboBox.setBackground(Color.WHITE);
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(musicianComboBox, c);

        currentY++;

        JPanel instrumentPanel = new JPanel(new GridBagLayout());
        instrumentPanel.setOpaque(false);
        GridBagConstraints c_instrPanel = new GridBagConstraints();
        c_instrPanel.insets = new Insets(0, 5, 0, 5);

        JButton addInstrumentButton = new JButton("Присоединить инструмент...");
        addInstrumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Musician selectedMusician = musicians.get(musicianComboBox.getSelectedIndex());
                new InstrumentConnectionDialog(MusicianEditDialog.this, selectedMusician.getID());
            }
        });
        c_instrPanel.gridx = 0;
        c_instrPanel.gridy = 0;
        instrumentPanel.add(addInstrumentButton, c_instrPanel);

        JButton removeInstrumentButton = new JButton("Отсоединить инструмент...");
        removeInstrumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Musician selectedMusician = musicians.get(musicianComboBox.getSelectedIndex());
                // TODO showInstrumentDisconnectionDialog(dialog, selectedMusician.getID());
            }
        });
        c_instrPanel.gridx = 1;
        c_instrPanel.gridy = 0;
        instrumentPanel.add(removeInstrumentButton, c_instrPanel);

        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(instrumentPanel, c);

        currentY++;
        c.gridwidth = 1;

        JCheckBox nameCheckBox = new JCheckBox();
        nameCheckBox.setSelected(false);
        nameCheckBox.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameCheckBox, c);

        JLabel newNameLabel = new JLabel("Имя:");
        newNameLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(newNameLabel, c);

        JTextField nameText = new JTextField();
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JCheckBox birthDateCheckBox = new JCheckBox();
        birthDateCheckBox.setSelected(false);
        birthDateCheckBox.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(birthDateCheckBox, c);

        JLabel birthDateLabel = new JLabel("Дата рождения:");
        birthDateLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(birthDateLabel, c);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(Instant.now()));
        Date initDate = calendar.getTime();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -100);
        Date earliestDate = calendar.getTime();
        calendar.setTime(initDate);
        Date latestDate = calendar.getTime();
        SpinnerDateModel birthDateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_YEAR);

        JSpinner birthDateSpinner = new JSpinner(birthDateModel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(birthDateSpinner, c);
        birthDateSpinner.setEditor(new JSpinner.DateEditor(birthDateSpinner, "dd.MM.yyyy"));

        currentY++;

        JCheckBox deathDateCheckBox = new JCheckBox();
        deathDateCheckBox.setSelected(false);
        deathDateCheckBox.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(deathDateCheckBox, c);

        JLabel deathDateLabel = new JLabel("Дата смерти:");
        deathDateLabel.setOpaque(false);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(deathDateLabel, c);

        SpinnerDateModel deathDateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_YEAR);

        JSpinner deathDateSpinner = new JSpinner(deathDateModel);
        c.gridx = 2;
        c.gridy = currentY;
        contentPane.add(deathDateSpinner, c);
        deathDateSpinner.setEditor(new JSpinner.DateEditor(deathDateSpinner, "dd.MM.yyyy"));

        currentY++;

        JButton confirmButton = new JButton("Применить изменения");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();

                if (nameCheckBox.isSelected()) {
                    fields.add(new NStringField("Name", nameText.getText()));
                }
                if (birthDateCheckBox.isSelected()) {
                    fields.add(new DateField("DateOfBirth", birthDateModel.getDate()));
                }
                if (deathDateCheckBox.isSelected()) {
                    fields.add(new DateField("DateOfDeath", deathDateModel.getDate()));
                }
                if (!fields.isEmpty()) {
                    try {
                        DataStorage.update(
                                new FieldContainer(musicians.get(musicianComboBox.getSelectedIndex()), fields)
                        );
                        JOptionPane.showMessageDialog(MusicianEditDialog.this, "Данные обновлены.",
                                "Обновление успешно", JOptionPane.INFORMATION_MESSAGE);
                        MusicApp.MAIN_WINDOW.showBandPage(BAND_ID);
                        dispose();
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(MusicianEditDialog.this, "Обновление не удалось.",
                                "Обновление не удалось", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(MusicianEditDialog.this, "Поля для обновления не выбраны.",
                            "Ошибка обновления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 3;
        contentPane.add(confirmButton, c);

        musicianComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Musician musician = (Musician)e.getItem();
                nameText.setText(musician.getName());
                LocalDate birthDate = musician.getBirthDate();
                if (birthDate != null) {
                    birthDateModel.setValue(Date.from(birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
                LocalDate deathDate = musician.getDeathDate();
                if (deathDate != null) {
                    deathDateModel.setValue(Date.from(deathDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            }
        });

        setPreferredSize(new Dimension(430, 250));
        pack();
        setVisible(true);
    }
}
