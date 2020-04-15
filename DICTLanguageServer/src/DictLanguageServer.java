import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DictLanguageServer {

    public String serverName;

    private ServerSocket serverSocket;
    private BufferedReader in = null;
    private PrintWriter clientOut = null;
    private String server;
    private int port;
    private String fileName;

    private Map<String, String> translationMap = new LinkedHashMap<>();

    private static Pattern pattern = Pattern.compile("[\\w\\s]", 3);

    private boolean serverRunning = true;

    public DictLanguageServer(String server, String port, String fileName) {
        this.server = server;
        this.fileName = fileName;

        try {
            this.port = Integer.parseInt(port);
        } catch (Exception exc) {
            System.out.println("The port number is invalid.");
            System.exit(1);
        }

        //SEEKING FOR THE LOCAL FILE WITH TRANSLATIONS TO FILL MAP...

        start();
    }

    public void start() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Socket connectionSocket = serverSocket.accept();

            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), StandardCharsets.UTF_8));

            String command = in.readLine();

            if (command != null) {
                String request[] = pattern.split(command, 3);

                if (request[0].equals("TRANSLATE")) {

                    // SEEKING FOR THE TRANSLATION...

                }
            }

        } catch (IOException e) {
            System.out.println("Unknown host: " + this.server);
        }

    }
}
