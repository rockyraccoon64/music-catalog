package music.database.gui.dialogs;

import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BandManagementDialog extends JDialog {

    public BandManagementDialog() {
        super(MusicApp.MAIN_WINDOW, "Управление группами");
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

        JButton addNewInstrument = new JButton("Добавить новую группу...");
        addNewInstrument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BandAdditionDialog(BandManagementDialog.this);
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(addNewInstrument, c);

        JButton removeInstrument = new JButton("Удалить группу...");
        removeInstrument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BandRemovalDialog(BandManagementDialog.this);
            }
        });
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(removeInstrument, c);

        setPreferredSize(new Dimension(430, 100));
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
