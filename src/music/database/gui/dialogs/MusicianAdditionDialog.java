package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.Band;
import music.database.data.items.SQLItem;
import music.database.fields.Field;
import music.database.fields.FieldContainer;
import music.database.fields.IntField;
import music.database.fields.NStringField;
import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

import static music.database.gui.MusicApp.BACKGROUND_COLOR;

public class MusicianAdditionDialog extends JDialog {

    private final BandEditDialog OWNER;
    private final int BAND_ID;

    public MusicianAdditionDialog(BandEditDialog owner, int bandID) {
        super(owner, "Добавить альбом");
        OWNER = owner;
        BAND_ID = bandID;
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
                fields.add(new IntField("Band", BAND_ID));

                try {
                    DataStorage.insert(SQLItem.MUSICIANS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(MusicianAdditionDialog.this, "Музыкант добавлен.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    MusicApp.MAIN_WINDOW.showBandPage(BAND_ID);
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MusicianAdditionDialog.this, "Обновление не удалось.",
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
