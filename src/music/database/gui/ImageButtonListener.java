package music.database.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageButtonListener implements ActionListener {

    private Container m_container;
    private byte[] m_imageBytes;
    private JLabel m_imageLabel;

    public ImageButtonListener(Container container, JLabel imageLabel) {
        m_container = container;
        m_imageLabel = imageLabel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(m_container);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(file);
                m_imageBytes = fis.readAllBytes();
                MusicApp.refreshImage(m_imageBytes, m_imageLabel, 50);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(MusicApp.MAIN_WINDOW,
                        "Не удалось получить изображение.",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public byte[] getImageBytes() {
        return m_imageBytes;
    }
}