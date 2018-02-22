import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * a simple static http server
 */
public class HTTPServer {

    final boolean condition = true;
    private String numer;
    private Socket client;
    private String allInformations = "Raport z dzialania Serwera/Clienta Grzegorz Wawrzeniecki ";
    private String name;
    private int port;
    private int toSockPort;

    public HTTPServer(String numer,String name,int port,int toSockPort) {
        this.numer = numer;
        this.name = name;
        this.port = port;
        this.toSockPort = toSockPort;
    }

    public void startServerHTTP() throws Exception {
        port +=5;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/" + name + numer, new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        startServerSocket();

    }

    private void startServerSocket() throws IOException {

        ServerSocket server = new ServerSocket(2000 + Integer.parseInt(numer) +toSockPort);
        client = server.accept();
        System.out.println("Polaczono z serwerem WWW");
        collectInformations();
    }

    private void collectInformations() throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String inputLine = "";

        while ((inputLine = in.readLine()) != null) {

            allInformations += " " + "\n" + inputLine;
        }

    }


    public class MyHandler implements HttpHandler {


        public void handle(HttpExchange t) throws IOException {
            while (true) {
                byte[] response = allInformations.getBytes();
                t.sendResponseHeaders(200, response.length);
                OutputStream os = t.getResponseBody();
                os.write(response);
                os.close();
            }
        }
    }
}