import AES.AES;
import AES.key.KeyGenerator;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JButton iTOpenButton;
    private JButton iTSaveButton;
    private JButton iTClearButton;
    private JButton oTOpenButton;
    private JButton oTSaveButton;
    private JButton oTClearButton;
    private JTextField keyField;
    private JButton keyGenerateButton;
    private JButton keyOpenButton;
    private JButton keySaveButton;
    private JButton keyClearButton;
    private JPanel iTPanel;
    private JPanel iTButtonPanel;
    private JPanel oTPanel;
    private JPanel oTButtonPanel;
    private JPanel keyPanel;
    private JPanel keyFieldPanel;
    private JPanel keyButtonPanel;
    private JButton encryptButton;
    private JButton decryptButton;
    private JPanel encryptButtonPanel;
    private JTextArea iTextArea;
    private JTextArea oTextArea;
    private JScrollPane iTScrollPane;
    private JScrollPane oTScrollPane;
    private JButton swapButton;

    ArrayList<String> plainTextArray, encryptedText, decryptedText;

    public MainWindow() {
        iTOpenButton.addActionListener(e -> {
            try {
                iTextArea.setText(new String(readTextFromFile(readLinkFile()), StandardCharsets.ISO_8859_1));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        iTSaveButton.addActionListener(e -> {
            try {
                saveTextToFile(iTextArea.getText().getBytes(StandardCharsets.ISO_8859_1), readLinkFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        iTClearButton.addActionListener(e -> iTextArea.setText(""));

        oTOpenButton.addActionListener(e -> {
            try {
                oTextArea.setText(new String(readTextFromFile(readLinkFile()), StandardCharsets.ISO_8859_1));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        oTSaveButton.addActionListener(e -> {
            try {
                saveTextToFile(oTextArea.getText().getBytes(StandardCharsets.ISO_8859_1), readLinkFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        oTClearButton.addActionListener(e -> oTextArea.setText(""));

        keyGenerateButton.addActionListener(e -> keyField.setText(KeyGenerator.generate128bitRandomKey()));
        keyOpenButton.addActionListener(e -> {
            try {
                keyField.setText(new String(readTextFromFile(readLinkFile()), StandardCharsets.ISO_8859_1));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        keySaveButton.addActionListener(e -> {
            try {
                saveTextToFile(keyField.getText().getBytes(StandardCharsets.ISO_8859_1), readLinkFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        keyClearButton.addActionListener(e -> keyField.setText(""));

        encryptButton.addActionListener(e -> {
            plainTextArray = divideInto128BitBlocks(iTextArea.getText());
            //System.out.println("size: " + plainTextArray.size() + "\ncontent: " + plainTextArray);

            encryptedText = new ArrayList<>();
            for (String s : plainTextArray) {
                encryptedText.add(AES.encrypt(s, keyField.getText()));
            }
            //System.out.println("size: " + encryptedText.size() + "\ncontent: " + encryptedText);

            StringBuilder result = new StringBuilder();
            for (String element : encryptedText) {
                result.append(element);
            }

            oTextArea.setText(result.toString());
        });
        decryptButton.addActionListener(e -> {
            encryptedText = divideInto128BitBlocks(iTextArea.getText());

            decryptedText = new ArrayList<>();
            for (String s : encryptedText) {
                decryptedText.add(AES.decrypt(s, keyField.getText()));
            }
            //System.out.println("size: " + decryptedText.size() + "\ncontent: " + decryptedText);

            StringBuilder result = new StringBuilder();
            for (String element : decryptedText) {
                result.append(element);
            }

            oTextArea.setText(result.toString());
        });
        swapButton.addActionListener(e -> {
            String buffer = iTextArea.getText();
            iTextArea.setText(oTextArea.getText());
            oTextArea.setText(buffer);
        });

        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }

    public static ArrayList<String> divideInto128BitBlocks(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.ISO_8859_1);
        int blockSize = 16;
        ArrayList<String> blocks = new ArrayList<>();

        for (int i = 0; i < inputBytes.length; i += blockSize) {
            int endIndex = Math.min(i + blockSize, inputBytes.length);
            byte[] blockBytes = new byte[endIndex - i];
            System.arraycopy(inputBytes, i, blockBytes, 0, endIndex - i);
            blocks.add(new String(blockBytes, StandardCharsets.ISO_8859_1));
        }
        return blocks;
    }

    private static String readLinkFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    private static byte[] readTextFromFile(String fileLink) throws IOException {
        FileInputStream fis = new FileInputStream(fileLink);
        byte[] bytes = fis.readAllBytes();
        fis.close();
        return bytes;
    }

    private static void saveTextToFile(byte[] bytes, String fileLink) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileLink);
        fos.write(bytes);
        fos.close();
    }

}
