package music.database.gui.dialogs;

import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GenreEditDialog extends JDialog {

    public GenreEditDialog() {
        super(MusicApp.MAIN_WINDOW, "Редактировать жанры");
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

        JButton addNewGenre = new JButton("Добавить новый жанр...");
        addNewGenre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GenreAdditionDialog(GenreEditDialog.this);
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(addNewGenre, c);

        JButton removeGenre = new JButton("Удалить жанр...");
        removeGenre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GenreRemovalDialog(GenreEditDialog.this);
            }
        });
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(removeGenre, c);

        setPreferredSize(new Dimension(430, 100));
        pack();
        setVisible(true);
    }
}
