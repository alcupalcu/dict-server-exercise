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

            String response = in.readLine();
            System.out.println("Server connection response: " + response);

            if (response.startsWith("BUSY")) {
                return "Server is too busy. Try again later.";
            }

            out.println(word + " " + language + " " + port);

        } catch (UnknownHostException exc) {
            throw new UnknownHostException("Unknown host: " + server);
        } catch (SocketTimeoutException exc) {
            throw new SocketTimeoutException("Timeout exceeded.");
        } catch (Exception exc) {
            throw new Exception("Unable to connect to the server. Server might not be available.");
        } finally {
            cleanExit();
        }

        return "Request for translation of word " + word + " has been sent to the server.\n" +
                "Waiting on port " + port + " for the answer.";
    }

    public String getAnswer() {

        StringBuilder translatedWord = new StringBuilder();
        String response = "";

        if(this.listeningPort == 0) {
            return "The port number for the previous request was invalid.";
        }

        try {
            receptionServerSocket = new ServerSocket(this.listeningPort);
            receptionServerSocket.setSoTimeout(3000);

            receptionSocket = receptionServerSocket.accept();

            receptionIn = new BufferedReader(new InputStreamReader(receptionSocket.getInputStream(),
                    StandardCharsets.UTF_8));

            boolean isWrong = false;

            while (!response.startsWith("DONE")) {
                response = receptionIn.readLine();
                translatedWord.append(response);
                if (response.startsWith("WRONG DATA") || response.startsWith("NOT FOUND")) {
                    isWrong = true;
                    break;
                }
            }

            if(isWrong) {
                return response;
            }
        } catch (SocketTimeoutException exc) {
            return "Waited too long for the answer.\nServer not responding.";
        } catch (IOException e) {
            return "Host is not able to receive translation due to IOException.";
        } catch (Exception exc) {
            return exc.toString();
        } finally {
            cleanExitReception();
        }

        return new String(translatedWord);
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

    private void setListeningPort(int listeningPort) throws Exception {
        this.listeningPort = listeningPort;
    }
}
