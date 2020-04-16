import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class DictServer extends Thread{

    private ServerSocket serverSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    private static Map<String, Integer> registeredLanguageServersMap = new LinkedHashMap<>();

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

            String[] connectionStart = in.readLine().split(" ");

            if (connectionStart[0].equals("LANG")) {
                System.out.println("Language server command: " + Arrays.toString(connectionStart));
                registeredLanguageServersMap.put(connectionStart[1], Integer.parseInt(connectionStart[2]));
                System.out.println("A new language server is available. Server language: " + connectionStart[1]);
                System.out.println("Closing connection with language server...");
                return;
            }

            String serverResponse = "OK";
            out.println(serverResponse);
            System.out.println(serverResponse + " sent.");

            String line = in.readLine();

            if(line != null) {
                String response;
                String[] request = line.split(" ");

                String word = request[0];
                String language = request[1];
                int port = Integer.parseInt(request[2]);

                System.out.println("Request for " + word + " from client listening on port " + port + " for language "
                        + language + " received.");

                System.out.println("Available servers:\n" + registeredLanguageServersMap.toString());

                if (!registeredLanguageServersMap.containsKey(language)) {
                    System.out.println("Language server " + language + " is not available.\n" +
                            "Sending info to the client...\nClosing connection with the client...");
                    Socket responseSocket = new Socket("localhost", port);
                    PrintWriter outClient = new PrintWriter(responseSocket.getOutputStream(), true);
                    outClient.println("SERVER NOT FOUND");
                    try {
                        outClient.close();
                        responseSocket.close();
                    } catch (Exception ignored) { }
                } else {
                    Socket languageServerSocket = new Socket("localhost",
                            registeredLanguageServersMap.get(language));
                    PrintWriter outLang = new PrintWriter(languageServerSocket.getOutputStream(), true);
                    System.out.println("Sending request for " + word + " to the language server " + language + "...");
                    outLang.println("TRANSLATE" + " " + word + " " + port);
                    try {
                        outLang.close();
                        languageServerSocket.close();
                    } catch (Exception ignored) { }
                }

            }
        } catch (Exception exc) {
            System.out.println("Client from " + serverThreadID + " disconnected.");
        } finally {
            try {
                in.close();
                out.close();
                connectionSocket.close();
                System.out.println("Closed connection.");
            } catch (Exception ignored) { }
        }
    }

    public static void main(String[] args) {
        final int SERVERS = 5;
        ServerSocket serverSocket = null;
        try {
            String host = "localhost";
            int port = 4321;
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
            serverSocket = new ServerSocket();
            serverSocket.bind(inetSocketAddress);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        for (int i = 0; i < SERVERS; i++) {
            new DictServer("server thread " + i, serverSocket);
        }
    }
}