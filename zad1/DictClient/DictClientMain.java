package zad1.DictClient;

public class DictClientMain {

    public static void main(String[] args) {
        DictClientModel model = new DictClientModel("127.0.0.1", 200);

        DictClientGUI view = new DictClientGUI();

        DictClientController controller = new DictClientController(model, view);

        view.setVisible(true);
    }
}
