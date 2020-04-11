package zad1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class DictClientModel {
    public final static int port = 4321;
    private String server;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    public boolean running;

    public DictClientModel(String server, int timeout) throws Exception {
        try {
            clientSocket = new Socket(server, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            running =true;

            String response = in.readLine();
            System.out.println(response);
            //FOR CHANGE WHEN SERVER IS PROGRAMMED
            if (!response.startsWith("1")) {
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

    private void cleanExit() {
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception ignored) {}
        System.exit(1);
    }

    public boolean isRunning() {
        return this.running;
    }

}
