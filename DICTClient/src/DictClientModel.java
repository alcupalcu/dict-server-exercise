import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DictClientModel {
    public final static int port = 4321;
    public int listeningPort;
    private String server;
    private Socket clientSocket;
    private Socket receptionSocket;
    private ServerSocket receptionServerSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader receptionIn;
    private String response = "No answer received. Try to submit first.";
    private boolean isWrong;

    public DictClientModel(String server) {
        this.server = server;
    }

    public String search(String word, String language, String portNumber) throws Exception {

        if (word.isEmpty()) {
            return "Please provide a word to be translated.";
        }

        if (language.isEmpty()) {
            return "Please provide a symbol of a language to which the word must be translated.";
        }

        if (portNumber.isEmpty()) {
            return "Please provide a port number.";
        }

        int port = 0;

        try {
            port = Integer.parseInt(portNumber);
        } catch (NumberFormatException | NullPointerException exc) {
            throw new Exception("The port number is invalid.");
        }

        if (!isPortNumberValid(port)) {
            return "The port number provided: " + port + " is invalid.\n" +
                    "It should be in the range from 1024(inclusive) to 49151(inclusive).";
        }

        setListeningPort(port);

        try {

            clientSocket = new Socket(server, this.port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8),
                    true);

            out.println("CLIENT");

            String responseFromMain = in.readLine();
            System.out.println("Server connection response: " + responseFromMain);

            if (responseFromMain.startsWith("BUSY")) {
                return "Server is too busy. Try again later.";
            }

            out.println(word + " " + language + " " + port);

            System.out.println("Request for " + word + " translation to " + language + " sent.");

            StringBuilder translatedWord = new StringBuilder();
            response = "";

            if(listeningPort == 0) {
                return "The port number for the previous request was invalid.";
            }

            receptionServerSocket = new ServerSocket(listeningPort);
            receptionServerSocket.setSoTimeout(3000);

            receptionSocket = receptionServerSocket.accept();

            receptionIn = new BufferedReader(new InputStreamReader(receptionSocket.getInputStream(),
                    StandardCharsets.UTF_8));

            isWrong = false;

            while (!response.startsWith("DONE")) {
                response = receptionIn.readLine();
                if (!response.startsWith("DONE"))
                    translatedWord.append(response);
                if (response.startsWith("NOT FOUND")) {
                    response = "Translation not found";
                    isWrong = true;
                    break;
                }
                if (response.startsWith("SERVER NOT FOUND")) {
                    response = "The following language server: " + language + " is not available.";
                    isWrong = true;
                    break;
                }
            }

            if (!isWrong) {
                response = new String(translatedWord);
            }

        } catch (UnknownHostException exc) {
            throw new UnknownHostException("Unknown host: " + server);
        } catch (SocketTimeoutException exc) {
            throw new SocketTimeoutException("Timeout exceeded.");
        } catch (Exception exc) {
            throw new Exception("Unable to connect to the server. Server might not be available.");
        } finally {
            cleanExit();
            cleanExitReception();
        }

        return "Request for translation of word " + word + " has been sent to the server.\n" +
                "Waiting on port " + port + " for the answer.";
    }

    public String getResponse() { return this.response; }

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
        } catch (Exception ignored) {
            System.out.println("Could not close connection with the main server.");
        }
    }

    private void cleanExitReception() {
        try {
            receptionServerSocket.close();
            receptionIn.close();
            receptionSocket.close();
        } catch (Exception ignored) {
            System.out.println("Could not close response connection of the client.");
        }
    }

    private void setListeningPort(int listeningPort) throws Exception { this.listeningPort = listeningPort; }
}
