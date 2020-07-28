package music.database;

import music.database.fields.Field;
import music.database.fields.FieldContainer;
import music.database.fields.IntField;
import music.database.items.DataItem;
import music.database.items.Instrument;
import music.database.items.Musician;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

public class InstrumentConnectionDialog extends JDialog {

    private final MusicApp MAIN_WINDOW;
    private final JDialog OWNER;
    private final int MUSICIAN_ID;

    public InstrumentConnectionDialog(MusicApp mainWindow, JDialog owner, int musicianID) {
        super(owner, "Добавить инструмент у музыканта");
        MAIN_WINDOW = mainWindow;
        OWNER = owner;
        MUSICIAN_ID = musicianID;

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

        JButton addNewInstrument = new JButton("Добавить новый инструмент");
        addNewInstrument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InstrumentAdditionDialog(InstrumentConnectionDialog.this);
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(addNewInstrument, c);

        currentY++;
        c.gridwidth = 1;

        JLabel nameLabel = new JLabel("Инструмент:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        Collection<DataItem> instrumentCollection = DataStorage.getItems(SQLItem.INSTRUMENTS);
        Vector<Instrument> instruments = new Vector<>();
        for (DataItem item : instrumentCollection) {
            instruments.add((Instrument)item);
        }
        JComboBox<Instrument> instrumentComboBox = new JComboBox<>(instruments);
        instrumentComboBox.setBackground(Color.WHITE);
        instrumentComboBox.setRenderer(new DataItemComboBoxRenderer());
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(instrumentComboBox, c);

        currentY++;

        JButton confirmButton = new JButton("Присоединить инструмент");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();
                fields.add(new IntField("MusicianID", MUSICIAN_ID));
                fields.add(
                        new IntField(
                                "InstrumentID",
                                instruments.get(instrumentComboBox.getSelectedIndex()).getID()
                        )
                );

                try {
                    DataStorage.insert(SQLItem.MUSICIAN_INSTRUMENT, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(InstrumentConnectionDialog.this, "Инструмент добавлен.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    Musician musician = (Musician) DataStorage.getItemByID(SQLItem.MUSICIANS, MUSICIAN_ID);
                    MAIN_WINDOW.showBandPage(musician.getBand().getID());
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(InstrumentConnectionDialog.this, "Обновление не удалось.",
                            "Добавление не удалось", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        setPreferredSize(new Dimension(430, 150));
        pack();
        setVisible(true);
    }
}
