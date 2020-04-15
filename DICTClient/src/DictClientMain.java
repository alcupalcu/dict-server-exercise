public class DictClientMain {

    public static void main(String[] args) {

        DictClientModel model = new DictClientModel("localhost");

        DictClientGUI view = new DictClientGUI();

        DictClientController controller = new DictClientController(model, view);

        view.setVisible(true);
    }
}