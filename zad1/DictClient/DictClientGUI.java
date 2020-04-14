package zad1.DictClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DictClientGUI extends JFrame {
    JPanel panel;
    JLabel wordLabel, languageLabel, portLabel;
    JTextField wordText, languageText, portText;
    JButton submit;
    JTextArea translationArea;
    Container container = getContentPane();

    DictClientGUI() {
        wordLabel = new JLabel();
        wordLabel.setText("Word: ");
        wordText = new JTextField();

        languageLabel = new JLabel();
        languageLabel.setText("Language symbol: ");
        languageText = new JTextField();

        portLabel = new JLabel();
        portLabel.setText("Port number: ");
        portText = new JTextField();

        submit = new JButton("Submit");

        translationArea = new JTextArea(10, 40);
        Font font = new Font("Dialog", Font.BOLD, 14);
        translationArea.setFont(font);

        panel = new JPanel(new GridLayout(4, 1));
        panel.add(wordLabel);
        panel.add(wordText);
        panel.add(languageLabel);
        panel.add(languageText);
        panel.add(portLabel);
        panel.add(portText);
        panel.add(submit);

        container.add(panel);
        container.add(new JScrollPane(translationArea));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 200);

        pack();
    }

    public String getWord() {
        return wordText.getText();
    }

    public String getLanguage() {
        return languageText.getText();
    }

    public String getPort() {
        return portText.getText();
    }

    public void setTranslationArea(String text) {
        translationArea.setText(text);
    }

    void addSubmitListener(ActionListener listenForSubmitButton) {
        submit.addActionListener(listenForSubmitButton);
    }

    void displayErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
