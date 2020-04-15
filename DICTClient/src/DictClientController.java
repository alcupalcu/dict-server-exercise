import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DictClientController {
    private DictClientModel model;
    private DictClientGUI view;

    public DictClientController(DictClientModel model, DictClientGUI view) {
        this.model = model;
        this.view = view;

        this.view.addSubmitListener(new SubmitListener());
        this.view.addAnswerListener(new AnswerListener());
    }

    class SubmitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String word, language, port = "";
            String response = "";

            try {
                word = view.getWord();
                language = view.getLanguage();
                port = view.getPort();

                response = model.search(word, language, port);

                view.setTranslationArea(response);

            } catch (Exception exc) {
                view.displayErrorMessage(exc.getMessage());
            }
        }
    }

    class AnswerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String translatedWord = "";

            try {
                translatedWord = model.getAnswer();

                view.setTranslationArea(translatedWord);

            } catch (Exception exc) {
                view.displayErrorMessage(exc.toString());
            }
        }
    }
}
