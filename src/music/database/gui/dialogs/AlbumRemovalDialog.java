package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.Album;
import music.database.data.items.Band;
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

public class AlbumRemovalDialog extends JDialog {

    private final BandEditDialog OWNER;
    private final int BAND_ID;

    public AlbumRemovalDialog(BandEditDialog owner, int bandID) {
        super(owner, "Удалить альбом");
        OWNER = owner;
        BAND_ID = bandID;
        refresh();
    }

    public void refresh() {
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, BAND_ID);
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
                    JOptionPane.showMessageDialog(AlbumRemovalDialog.this, "Альбом удалён.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    MusicApp.MAIN_WINDOW.showBandPage(BAND_ID);
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(AlbumRemovalDialog.this, "Удаление не удалось.",
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
        setVisible(true);
    }
}
