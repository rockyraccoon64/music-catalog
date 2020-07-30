package music.database.gui.dialogs;

import music.database.data.DataStorage;
import music.database.data.items.SQLItem;
import music.database.fields.Field;
import music.database.fields.FieldContainer;
import music.database.fields.IntField;
import music.database.fields.NStringField;
import music.database.gui.MusicApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

public class BandAdditionDialog extends JDialog {

    public BandAdditionDialog() {
        super(MusicApp.MAIN_WINDOW, "Добавить группу");
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

        JLabel formDateLabel = new JLabel("Год формирования:");
        formDateLabel.setOpaque(false);
        c.gridx = 0;
        c.gridy = currentY;
        contentPane.add(formDateLabel, c);

        int currentYear = LocalDate.now().getYear();
        SpinnerNumberModel formDateModel = new SpinnerNumberModel(
                currentYear, 1900, currentYear, 1
        );
        JSpinner formDateSpinner = new JSpinner(formDateModel);
        c.gridx = 1;
        c.gridy = currentY;
        contentPane.add(formDateSpinner, c);

        currentY++;

        JButton addButton = new JButton("Добавить группу");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<Field> fields = new Vector<>();
                fields.add(new NStringField("Name", nameText.getText()));
                fields.add(new IntField("YearOfFormation", formDateModel.getNumber().intValue()));

                try {
                    DataStorage.insert(SQLItem.BANDS, new FieldContainer(null, fields));
                    JOptionPane.showMessageDialog(BandAdditionDialog.this, "Группа добавлена.",
                            "Добавление успешно", JOptionPane.INFORMATION_MESSAGE);
                    MusicApp.MAIN_WINDOW.showBandList();
                    dispose();
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(BandAdditionDialog.this, "Обновление не удалось.",
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
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
