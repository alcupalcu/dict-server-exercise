package zad1.DictClient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DictClientModel {
    public final static int port = 4321;
    private Socket clientSocket;
    private Socket receptionSocket;
    private ServerSocket receptionServerSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader receptionIn;
    public boolean running;

    public DictClientModel(String server, int timeout) {
        try {
            clientSocket = new Socket(server, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            running = true;

            String response = in.readLine();
            System.out.println("Server response: " + response);
            //FOR CHANGE WHEN SERVER IS PROGRAMMED
            if (!response.startsWith("BUSY")) {
                System.out.println("Server is too busy. Try again later.");
                running = false;
                cleanExit();
            }

            clientSocket.setSoTimeout(timeout);


        } catch (UnknownHostException exc) {
            running = false;
            System.out.println("Unknown host " + server);
        } catch (Exception exc) {
            running = false;
            exc.printStackTrace();
        }
    }

    public String search(String word, String language, String portNumber) throws NumberFormatException,
            SocketTimeoutException, Exception {

        try {

            if (word.isEmpty()) {
                throw new Exception("Please provide a word to be translated.");
            }

            if (language.isEmpty()) {
                throw new Exception("Please provide a symbol of a language to which the word must be translated.");
            }

            int port = Integer.parseInt(portNumber);

            if (!isPortNumberValid(port)) {
                throw new Exception("The port number provided: " + port + " is invalid.\n" +
                        "It should be in the range from 1024(inclusive) to 49151(inclusive).");
            }

            String response = "";
            StringBuilder translatedWord = new StringBuilder("Word " + word + " translated to " + language + ":\n");

            out.println(word + " " + language + " " + port);

            receptionServerSocket = new ServerSocket(port);

            receptionSocket = receptionServerSocket.accept();

            receptionIn = new BufferedReader(new InputStreamReader(receptionSocket.getInputStream(),
                    StandardCharsets.UTF_8));

            while (response != null && !response.startsWith("DONE")) {
                response = receptionIn.readLine();
                translatedWord.append(response);
                if (response.startsWith("WRONG DATA") || response.startsWith("NOT FOUND")) {
                    break;
                }
            }

            return translatedWord.toString();

        } catch (NumberFormatException exc) {
            throw new NumberFormatException("The port is invalid.");
        } catch (SocketTimeoutException exc) {
            throw new SocketTimeoutException("Timeout exceeded.");
        } catch (Exception exc) {
            throw new Exception(exc.toString());
        } finally {
            cleanExitReception();
        }
    }

    private boolean isPortNumberValid(int port) {
        if (port < 1024 || port > 49152) {
            return false;
        }

        return true;
    }

    private void cleanExit() {
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception ignored) {}
        System.exit(1);
    }

    private void cleanExitReception() {
        try {
            receptionServerSocket.close();
            receptionIn.close();
            receptionSocket.close();
        } catch (Exception ignored) {}
        System.exit(4);
    }
}
