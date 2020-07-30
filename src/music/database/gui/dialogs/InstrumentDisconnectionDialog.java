package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.Instrument;
import music.database.data.items.Musician;
import music.database.data.items.SQLItem;
import music.database.fields.Field;
import music.database.fields.FieldContainer;
import music.database.fields.IntField;
import music.database.gui.MusicApp;
import music.database.gui.renderers.DataItemComboBoxRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

import static music.database.gui.MusicApp.BACKGROUND_COLOR;

public class InstrumentDisconnectionDialog extends JDialog {

    private final JDialog OWNER;
    private final int MUSICIAN_ID;

    public InstrumentDisconnectionDialog(JDialog owner, int musicianID) {
        super(owner, "Отсоединить инструмент");
        OWNER = owner;
        MUSICIAN_ID = musicianID;
        refresh();
    }

    public void refresh() {
        Container contentPane = getContentPane();
        contentPane.removeAll();
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

        Musician musician = (Musician) DataStorage.getItemByID(SQLItem.MUSICIANS, MUSICIAN_ID);
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
                    fields.add(new IntField("MusicianID", MUSICIAN_ID));
                    fields.add(
                            new IntField(
                                    "InstrumentID",
                                    instruments.get(
                                            instrumentComboBox.getSelectedIndex()).getID()
                            )
                    );
                    DataStorage.delete(SQLItem.MUSICIAN_INSTRUMENT, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(InstrumentDisconnectionDialog.this, "Инструмент отсоединён.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    Musician musician = (Musician) DataStorage.getItemByID(SQLItem.MUSICIANS, MUSICIAN_ID);
                    MusicApp.MAIN_WINDOW.showBandPage(musician.getBand().getID());
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(InstrumentDisconnectionDialog.this, "Отсоединение не удалось.",
                            "Ошибка при удалении", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        setPreferredSize(new Dimension(430, 150));
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
