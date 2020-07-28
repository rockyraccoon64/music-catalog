package music.database;

import music.database.fields.Field;
import music.database.fields.FieldContainer;
import music.database.fields.NStringField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

public class InstrumentAdditionDialog extends JDialog {

    public InstrumentAdditionDialog(InstrumentConnectionDialog mainDialog) {
        super(mainDialog, "Добавить инструмент");
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(MusicApp.BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int currentY = 0;

        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(nameLabel, c);

        JTextField nameText = new JTextField();
        nameText.setPreferredSize(new Dimension(250, 20));
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(nameText, c);

        currentY++;

        JButton addButton = new JButton("Добавить инструмент");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();
                fields.add(new NStringField("Name", nameText.getText()));

                try {
                    DataStorage.insert(SQLItem.INSTRUMENTS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(InstrumentAdditionDialog.this, "Инструмент добавлен.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    mainDialog.refresh();
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(InstrumentAdditionDialog.this, "Обновление не удалось.",
                            "Добавление не удалось", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 0;
        c.gridy = currentY;
        c.gridwidth = 2;
        contentPane.add(addButton, c);

        setPreferredSize(new Dimension(430, 150));
        pack();
        setVisible(true);
    }
}
