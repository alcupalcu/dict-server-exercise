package zad1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DictClientModel {
    public final static int port = 4321;
    private String server;
    private Socket clientSocket;
    private Socket receptionSocket;
    private ServerSocket receptionServerSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader receptionIn;
    public boolean running;

    public DictClientModel(String server, int timeout) throws Exception {
        try {
            clientSocket = new Socket(server, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            running = true;

            String response = in.readLine();
            System.out.println(response);
            //FOR CHANGE WHEN SERVER IS PROGRAMMED
            if (!response.startsWith("WHAT?")) {
                System.out.println("Server is too busy. Try again later.");
                running = false;
                cleanExit();
            }

            clientSocket.setSoTimeout(timeout);


        } catch (UnknownHostException exc) {
            running = false;
            throw new UnknownHostException("Unknown host " + server);
        } catch (Exception exc) {
            running = false;
            throw new Exception(exc.toString());
        }
    }

    public String search(String word, String language, int port) {
        try {
            String response = "";
            String translatedWord = "Word " + word + " translated to " + language + ":\n";

            out.println(word + " " + language + " " + port);

            receptionServerSocket = new ServerSocket(port);

            receptionSocket = receptionServerSocket.accept();

            receptionIn = new BufferedReader(new InputStreamReader(receptionSocket.getInputStream(),
                    StandardCharsets.UTF_8));

            while (response != null && !response.startsWith("DONE")) {
                response = receptionIn.readLine();
                translatedWord += response;
                if (response.startsWith("NOTFOUND")) {
                    break;
                }
            }

            return translatedWord;


        } catch (SocketTimeoutException exc) {
            return "Timeout exceeded.";
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            cleanExitReception();
        }

        return "";
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
            receptionIn.close();
            receptionSocket.close();
        } catch (Exception ignored) {}
        System.exit(4);
    }
}
