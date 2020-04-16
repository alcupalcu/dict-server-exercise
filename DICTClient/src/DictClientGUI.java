import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DictClientGUI extends JFrame {
    JPanel panel;
    JLabel wordLabel, languageLabel, portLabel;
    JTextField wordText, languageText, portText;
    JButton submit;
    JButton getAnswer;
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
        getAnswer = new JButton("Get answer");

        translationArea = new JTextArea(3, 40);
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
        panel.add(getAnswer);
        panel.setBorder(BorderFactory.createLineBorder(Color.blue, 2));

        container.add(panel, "Center");
        container.add(new JScrollPane(translationArea), "South");

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

    void addAnswerListener(ActionListener listenForAnswerButton) { getAnswer.addActionListener(listenForAnswerButton); }

    void displayErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
