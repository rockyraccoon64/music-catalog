package music.database.gui.renderers;

import music.database.data.items.Album;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import javax.imageio.ImageIO;
import javax.swing.*;

public class AlbumRenderer implements ListCellRenderer<Album> {

    private final int HEIGHT = 30;

    public AlbumRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Album> list, Album album,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel albumName = new JLabel(album.getName());
        albumName.setFont(new Font("Arial", Font.ITALIC, 20));
        panel.add(albumName);

        LocalDate releaseDate = album.getReleaseDate();
        if (releaseDate != null) {
            JLabel albumYear = new JLabel("(" + releaseDate.getYear() + ")");
            albumYear.setFont(new Font("Arial", Font.PLAIN, 20));
            panel.add(albumYear);
        }

        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        byte[] imageByteArray = album.getImage();
        if (imageByteArray != null) {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageByteArray));
                int width = image.getWidth();
                int height = image.getHeight();
                int scaledWidth;
                int scaledHeight;
                if (width > height) {
                    scaledWidth = HEIGHT;
                    scaledHeight = (int) (height * ((double) scaledWidth / width));
                } else {
                    scaledHeight = HEIGHT;
                    scaledWidth = (int) (width * ((double) scaledHeight / height));
                }
                albumName.setIcon(new ImageIcon(
                        image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
                ));
            } catch (IOException ex) {
                System.out.println("Error while loading image");
            }
        }

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