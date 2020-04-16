import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DictLanguageServer {

    public String serverName;
    public static final int mainServerPort = 4321;

    private ServerSocket serverSocket;
    private Socket answerSocket;
    Socket mainServerConnectionSocket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private PrintWriter clientOut = null;
    private int port;
    private String fileName;

    private File sharedCatalog;
    private File[] matchingFiles;

    private Map<String, String> translationMap = new LinkedHashMap<>();

    public DictLanguageServer(String serverName, String port) {
        this.serverName = serverName;
        this.fileName = serverName + ".txt";

        try {
            this.port = Integer.parseInt(port);
        } catch (Exception exc) {
            System.out.println("The port number is invalid.");
            System.exit(1);
        }

        sharedCatalog = new File(System.getProperty("user.dir") + File.separator + "Languages");
        matchingFiles = sharedCatalog.listFiles((dir, name) -> name.equals(fileName));

        if (matchingFiles != null) {
            if (matchingFiles.length == 0) {
                System.out.println("The language server " + serverName + " did not match any translation file." +
                        "\nServer stopped.");
                System.exit(2);
            } else {
                try {
                    FileInputStream fis = new FileInputStream(sharedCatalog + File.separator + fileName);
                    Scanner scanner = new Scanner(fis);
                    while (scanner.hasNextLine()) {
                        String[] nextWord = scanner.nextLine().split(" ");
                        if (nextWord.length == 2) {
                            translationMap.put(nextWord[0], nextWord[1]);
                        }
                    }
                    scanner.close();
                } catch (IOException exc) {
                    System.out.println("The translation file " + fileName + " in path " + sharedCatalog +
                            " is invalid.\nServer stopped.");
                    System.exit(3);
                }

                System.out.println("Language server started.\n");

                connect();
                start();
            }
        } else {
            System.out.println("There is no translation file available.\nServer stopped.");
            System.exit(4);
        }
    }

    public void connect() {
        try {

            mainServerConnectionSocket = new Socket("localhost", mainServerPort);

            out = new PrintWriter(mainServerConnectionSocket.getOutputStream(), true);

            System.out.println("Sending log request to the main server...");

            out.println("LANG " + serverName + " " + this.port);

            System.out.println("Server " + serverName + " log request sent to the main server.");

        } catch (UnknownHostException e) {
            System.out.println("Unknown host.\nServer stopped.");
        } catch (IOException e) {
            System.out.println("The main server is unavailable.\nServer stopped.");
        } finally {
            try {
                out.close();
                mainServerConnectionSocket.close();
            } catch (IOException ignored) { }
        }
    }

    public void start() {

        try {
            serverSocket = new ServerSocket(port);
            Socket connectionSocket = null;

            while (true) {
                try {
                    connectionSocket = serverSocket.accept();

                    in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), StandardCharsets.UTF_8));

                    String command = in.readLine();

                    if (command != null) {
                        String[] request = command.split(" ");

                        if (request[0].equals("TRANSLATE")) {

                            String wordToTranslate = request[1];
                            int portOfDestination = Integer.parseInt(request[2]);
                            String translation = "";

                            System.out.println("Translation for word " + wordToTranslate + " received.\n" +
                                    "Searching for translation...");

                            if (translationMap.containsKey(wordToTranslate)) {
                                System.out.println("Translation for " + wordToTranslate + " found.\nSending...");
                                translation = translationMap.get(wordToTranslate);
                            } else {
                                System.out.println("Translation for " + wordToTranslate + " not found.\n" +
                                        "Sending answer...");
                                translation = "NOT FOUND";
                            }

                            answerSocket = new Socket("localhost", portOfDestination);

                            clientOut = new PrintWriter(answerSocket.getOutputStream(), true);

                            clientOut.println(translation + "\nDONE");

                            System.out.println("Answer sent.\nClosing connection...");

                        } else {
                            System.out.println("Command received from the main server incomprehensible.");
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException exc) {
                    System.out.println("The translation request received is invalid.");
                } catch (NumberFormatException exc) {
                    System.out.println("Port number received from the main server is invalid.");
                } finally {
                    try {
                        connectionSocket.close();
                    } catch (Exception ignored) { }
                }
            }
        }  catch (IOException e) {
            System.out.println("Unknown host: " + serverName);
        } catch (Exception exc) {
            System.out.println(exc.toString() + "\n Server stopped.");
        } finally {
            try {
                clientOut.close();
                answerSocket.close();
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error while closing connection with the client.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            String name = args[0];
            String port = args[1];

            new DictLanguageServer(name, port);

        } catch (Exception exc) { }
    }
}
