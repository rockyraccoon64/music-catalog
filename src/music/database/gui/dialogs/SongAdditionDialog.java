package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.Album;
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

public class SongAdditionDialog extends JDialog {

    private final AlbumEditDialog OWNER;
    private final int ALBUM_ID;

    public SongAdditionDialog(AlbumEditDialog owner, int albumID) {
        super(owner, "Добавить песню");
        OWNER = owner;
        ALBUM_ID = albumID;
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

        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        JTextField nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(200, 20));
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JLabel trackNoLabel = new JLabel("Позиция в альбоме:");
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(trackNoLabel, c);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 1000, 1);
        JSpinner trackNoSpinner = new JSpinner(spinnerModel);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(trackNoSpinner, c);

        currentY++;

        JButton confirmButton = new JButton("Добавить");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();
                fields.add(new NStringField("Name", nameText.getText()));
                fields.add(new IntField("Album", ALBUM_ID));
                fields.add(new IntField("TrackNo", spinnerModel.getNumber().intValue()));

                try {
                    DataStorage.insert(SQLItem.SONGS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(SongAdditionDialog.this, "Песня добавлена.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    MusicApp.MAIN_WINDOW.showAlbumPage(ALBUM_ID);
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(SongAdditionDialog.this, "Обновление не удалось.",
                            "Добавление не удалось", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(confirmButton, c);

        setPreferredSize(new Dimension(400, 150));
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
