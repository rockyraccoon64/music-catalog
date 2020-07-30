package music.database.gui.dialogs;

import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstrumentManagementDialog extends JDialog {

    public InstrumentManagementDialog() {
        super(MusicApp.MAIN_WINDOW, "Управление инструментами");
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

        JButton addNewInstrument = new JButton("Добавить новый инструмент...");
        addNewInstrument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InstrumentAdditionDialog(InstrumentManagementDialog.this);
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(addNewInstrument, c);

        JButton removeInstrument = new JButton("Удалить инструмент...");
        removeInstrument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InstrumentRemovalDialog(InstrumentManagementDialog.this);
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
