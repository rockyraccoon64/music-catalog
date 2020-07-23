package music.database;

import music.database.items.Band;
import music.database.items.DataItem;

import javax.swing.*;
import java.awt.*;

class DataItemComboBoxRenderer extends JLabel implements ListCellRenderer<DataItem> {

    public DataItemComboBoxRenderer() {
        setOpaque(true);
        setVerticalAlignment(CENTER);
        setPreferredSize(new Dimension(200, 20));
    }

    public Component getListCellRendererComponent(
            JList list,
            DataItem value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setText(value.getName());

        return this;
    }
}