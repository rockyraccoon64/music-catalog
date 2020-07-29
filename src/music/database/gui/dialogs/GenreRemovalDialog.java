package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.DataItem;
import music.database.data.items.Genre;
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
import java.util.Collection;
import java.util.Vector;

public class GenreRemovalDialog extends JDialog {

    public GenreRemovalDialog(GenreEditDialog owner) {
        super(owner, "Удалить инструмент");
        refresh();
    }

    public void refresh() {
        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(MusicApp.BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Жанр:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        Vector<Genre> genres = new Vector<>();
        Collection<DataItem> genreCollection = DataStorage.getItems(SQLItem.GENRES);

        for (DataItem item : genreCollection) {
            genres.add((Genre)item);
        }

        JComboBox<Genre> genreComboBox = new JComboBox<>(genres);
        genreComboBox.setBackground(Color.WHITE);
        genreComboBox.setRenderer(new DataItemComboBoxRenderer());
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(genreComboBox, c);

        currentY++;

        JButton confirmButton = new JButton("Удалить жанр");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Vector<Field> fields = new Vector<>();
                    fields.add(new IntField("ID", genres.get(genreComboBox.getSelectedIndex()).getID()));
                    DataStorage.delete(SQLItem.GENRES, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(GenreRemovalDialog.this, "Жанр удалён.",
                            "Удаление успешно", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(GenreRemovalDialog.this, "Удаление не удалось.",
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
