package music.database;

import music.database.items.Band;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

public class BandRenderer extends JLabel implements ListCellRenderer<Band> {

    private final int HEIGHT = 30;

    public BandRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Band> list, Band band,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        setText(band.getName());
        setFont(new Font("Arial", Font.PLAIN, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        byte[] imageByteArray = band.getImage();
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
                setIcon(new ImageIcon(
                        image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
                ));
            } catch (IOException ex) {
                System.out.println("Error while loading image");
            }
        }
        else setIcon(null);

        return this;
    }

}