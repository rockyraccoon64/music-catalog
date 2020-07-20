package music.database;

import music.database.items.Album;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class AlbumRenderer implements ListCellRenderer<Album> {

    public AlbumRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Album> list, Album album,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        //ImageIcon imageIcon = new ImageIcon();

        //setIcon(imageIcon);

        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel albumName = new JLabel(album.getName());
        albumName.setFont(new Font("Arial", Font.ITALIC, 20));
        panel.add(albumName);

        JLabel albumYear = new JLabel("(" + album.getReleaseDate().getYear() + ")");
        albumYear.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(albumYear);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            panel.setForeground(list.getSelectionForeground());
        }
        else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
        }

        return panel;
    }

}