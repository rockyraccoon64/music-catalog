package music.database;

import music.database.items.Band;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class BandRenderer extends JLabel implements ListCellRenderer<Band> {

    public BandRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Band> list, Band band,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        //ImageIcon imageIcon = new ImageIcon();

        //setIcon(imageIcon);
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

        return this;
    }

}