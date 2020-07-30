package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.Album;
import music.database.data.items.SQLItem;
import music.database.data.items.Song;
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

public class SongRemovalDialog extends JDialog {

    private final AlbumEditDialog OWNER;
    private final int ALBUM_ID;

    public SongRemovalDialog(AlbumEditDialog owner, int albumID) {
        super(owner, "Удалить песню");
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

        JLabel songLabel = new JLabel("Выберите песню:");
        songLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(songLabel, c);

        Album album = (Album) DataStorage.getItemByID(SQLItem.ALBUMS, ALBUM_ID);
        Vector<Song> songs = album.getSongs();
        JComboBox<Song> songComboBox = new JComboBox<>(songs);
        songComboBox.setBackground(Color.WHITE);
        songComboBox.setRenderer(new DataItemComboBoxRenderer());
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(songComboBox, c);

        currentY++;

        JButton removeButton = new JButton("Удалить песню");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Vector<Field> fields = new Vector<>();
                    fields.add(new IntField("ID", songs.get(songComboBox.getSelectedIndex()).getID()));
                    DataStorage.delete(SQLItem.SONGS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(SongRemovalDialog.this, "Песня удалена.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    MusicApp.MAIN_WINDOW.showAlbumPage(album.getID());
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(SongRemovalDialog.this, "Удаление не удалось.",
                            "Ошибка при удалении", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(removeButton, c);

        setPreferredSize(new Dimension(400, 150));
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
