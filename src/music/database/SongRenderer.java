package music.database;

import music.database.items.Album;
import music.database.items.Song;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class SongRenderer implements ListCellRenderer<Song> {

    public SongRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Song> list, Song song,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel trackNo = new JLabel("" + song.getTrackNo() + ". ");
        trackNo.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(trackNo);

        JLabel songName = new JLabel(song.getName());
        songName.setFont(new Font("Arial", Font.ITALIC, 20));
        panel.add(songName);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.setBackground(list.getBackground());
        panel.setForeground(list.getForeground());

        return panel;
    }

}