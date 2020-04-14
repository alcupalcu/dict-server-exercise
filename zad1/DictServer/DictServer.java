package zad1.DictServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DictServer extends Thread{

    private ServerSocket serverSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    private Map<String, Integer> registeredLanguageServersMap = new LinkedHashMap<>();

    private static Pattern pattern = Pattern.compile("[\\w\\s]", 3);

    private static String[] responses = {"WRONG DATA", "BUSY"};

    private volatile boolean serverRunning = true;

    private String serverThreadID;

    public DictServer(String serverThreadID, ServerSocket serverSocket) {
        this.serverThreadID = serverThreadID;
        this.serverSocket = serverSocket;
        System.out.println("Server " + this.serverThreadID + " started.");
        System.out.println("Listening at port " + serverSocket.getLocalPort());
        System.out.println("bind address: " + serverSocket.getInetAddress());

        start();
    }

    public void run() {
        while (serverRunning) {
            try {
                Socket connectionSocket = serverSocket.accept();

                System.out.println("Connection established by: " + serverThreadID);

                request(connectionSocket);

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        try { serverSocket.close(); } catch (Exception ignored) {}
    }

    private void request(Socket connectionSocket) throws IOException {
        try {
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            out = new PrintWriter(connectionSocket.getOutputStream(), true);

            String line = in.readLine();

            if(line != null) {
                String response;
                String[] request = pattern.split(line, 3);

                if (request[0].equals("LANG")) {
                    registeredLanguageServersMap.put(request[1], Integer.parseInt(request[2]));
                    System.out.println("A new language server is available. Server language: " + request[1]);
                    return;
                }

                String word = request[0];
                String language = request[1];
                int port = Integer.parseInt(request[2]);

                if (!registeredLanguageServersMap.containsKey(language)) {
                    out.write("The following language server: " + language + " is not available.");
                } else {
                    Socket languageServerSocket = new Socket("localhost",
                            registeredLanguageServersMap.get(language));
                    PrintWriter outLang = new PrintWriter(languageServerSocket.getOutputStream(), true);
                    outLang.write(word + " " + port);
                    try {
                        outLang.close();
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

            }
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                connectionSocket.close();
                connectionSocket = null;
            } catch (Exception ignored) { }
        }
    }
}
