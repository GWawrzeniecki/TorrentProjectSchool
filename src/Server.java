import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Server implements Runnable {

    private ServerSocket server;
    int portNumber;
    private String numer;
    private Reader reader;
    private Menu menu;
    private ArrayList<File> files;
    private SendSaveFile sendFile;
    private Socket httpSocket;

    public Server(String numer, Menu menu) {
        this.numer = numer;
        reader = new Reader(numer);
        this.menu = menu;
        files = new ArrayList<File>();
        sendFile = new SendSaveFile(numer);
    }

    public void getFiles(Socket client) {

        File folder = new File("/TORrent_" + numer + File.separator + "DANE" + File.separator);
        Reader.fileN = 0;
        Reader.files.clear();
        reader.getFiles(folder, client);

    }

    public void readConf() {
        reader.readConf();
    }


    private void acceptConnectionMethod(Socket client) {

        Runnable acceptConnection = () -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                PrintWriter outHTTP = new PrintWriter(httpSocket.getOutputStream(), true);

                String inputLine = null;
                while (!client.isClosed() && (inputLine = in.readLine()) != null) {

                    if (inputLine.startsWith("getFiles")) {
                        getFiles(client);
                        outHTTP.println("Server numer: " + numer + " Otrzymano protokol getFiles" + " OD " + client.toString());

                    }

                    if (inputLine.startsWith("PULL")) {
                        outHTTP.println("Server numer: " + numer + " Otrzymano protokol PULL" + " OD " + client.toString());
                        String[] nameoFfiletab = inputLine.split(",");
                        int numberofFile = 0;
                        try {
                            numberofFile = Integer.parseInt(nameoFfiletab[1]);
                        } catch (NumberFormatException e) {
                            out.println("Zle podane dane");
                            out.println("Sprobuj jeszcze raz");
                            return;

                        }
                        sendFile.sendFile(numberofFile, client, httpSocket, outHTTP);

                    }
                    if (inputLine.startsWith("PUSH")) {
                        outHTTP.println("Server numer: " + numer + " Otrzymano protokol PUSH" + " OD " + client.toString());
                        String[] nameoFfiletab = inputLine.split(",");
                        String tmp_Name = nameoFfiletab[2];
                        sendFile.saveFileToServer(client, tmp_Name, outHTTP);
                    }
                    if (inputLine.startsWith("getMD5")) {
                        outHTTP.println("Server numer: " + numer + " Otrzymano protokol MD5" + " OD " + client.toString());
                        String strSplit[] = inputLine.split(",");
                        String nameFile = strSplit[2];
                        String path = File.separator + "TORrent_" + numer + File.separator + "DANE" + File.separator + nameFile + File.separator;
                        File fileMD5 = new File(path);
                        MessageDigest md5Digest = null;
                        String checksum = "";
                        try {
                            md5Digest = MessageDigest.getInstance("MD5");

                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        try {
                            checksum = MD5CheckSum.getFileChecksum(md5Digest, fileMD5);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.println("MD5," + checksum);

                    }
                    if (inputLine.startsWith("RePULL")) {
                        outHTTP.println("Server numer: " + numer + " Otrzymano protokol RePULL" + " OD " + client.toString());
                        String[] nameoFfiletab = inputLine.split(",");
                        int numberofFile = 0;
                        int sizeOfFile = 0;

                        try {
                            numberofFile = Integer.parseInt(nameoFfiletab[1]);
                            sizeOfFile = Integer.parseInt(nameoFfiletab[2]);

                        } catch (NumberFormatException e) {
                            out.println("Zle podane dane");
                            out.println("Sprobuj jeszcze raz");
                            client.close();
                            return;

                        }
                        sendFile.reSendFile(numberofFile, sizeOfFile, client, outHTTP);
                    }

                }
            } catch (IOException e) {
                System.out.println("BLAD 2");
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        Thread t = new Thread(acceptConnection);
        t.start();
    }

    Runnable waitForConnect = () -> {
        Socket client = null;
        while (true) {
            System.out.println("Server: Nasłuchuje .....");
            System.out.println();
            try {
                client = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            acceptConnectionMethod(client);
        }
    };

    @Override
    public void run() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Server: Start Server");
            readConf();

            System.out.println("Server: Dostepne numery portów: ");
            for (int i = 0; i < Reader.ports.length; i++) {
                if (Reader.ports[i] != null)
                    System.out.println("Port numer " + i + " " + Reader.ports[i]);
            }

            System.out.println("Server: Podaj numer ");
            int numberOfPort = 100;
            try {
                numberOfPort = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Server: Musisz podac cyfre");
            }

            try {
                portNumber = Integer.parseInt(Reader.ports[numberOfPort]);
            } catch (NumberFormatException e) {
                System.out.println("Server: Podales bledna wartosc");
                System.out.println("Server: Podaj wartosc jeszcze raz");
                numberOfPort = sc.nextInt();
                portNumber = Integer.parseInt(Reader.ports[numberOfPort]);

            }
            try {
                server = new ServerSocket(portNumber);
            } catch (BindException e) {
                System.out.println("Server: Podany port jest już zajety");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread HTTPServerThread = new Thread(new HTTPServerRunnable(numer, "Server", portNumber, 1));
            HTTPServerThread.start();
            System.out.println("Server WWW bedzie dostepny na porcie: " + (portNumber + 5));
            System.out.println("Pod adresem localhost:" + (portNumber + 5) + "/" + "Server" + numer);
            System.out.println("Trwa wlaczanie servera WWW");
            Thread.sleep(2000);
            httpSocket = new Socket("localhost", 2000 + Integer.parseInt(numer) + 1);
            Thread waitForConnectT = new Thread(waitForConnect);
            waitForConnectT.start();
            menu.chooseOption();

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Nie ma takiego numeru");
            System.out.println();
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}








