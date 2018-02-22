import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Client implements Runnable {

    private Socket echoSocket;
    private int portNumber;
    final String hostname = "localhost";
    private PrintWriter out;
    private BufferedReader in;
    private String numer;
    private Menu menu;
    private int bytesRead;
    private InputStream is;
    private SendSaveFile saveFile;
    private Reader reader;
    private int knownPort;
    Socket httpSocket;
    private final int min = 9000;
    private final int max = 9999;
    private PrintWriter outHTTP;

    public Client(String numer, Menu menu) {

        this.numer = numer;
        this.menu = menu;
        saveFile = new SendSaveFile(numer);
        reader = new Reader(numer);

    }

    private void getConnect2() {

        try {
            echoSocket = new Socket(hostname, knownPort);// umozliwa polaczenie z jednym hostem
            out = new PrintWriter(echoSocket.getOutputStream(), true); //
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())); //
        } catch (Exception e) {
            System.out.println("Nie udalo się połaczyć");
            System.out.println("Byc moze nikt nie nasluchuje na tym porcie");
            System.exit(0);
        }

    }

    private void getConnect() {

        Scanner sc = new Scanner(System.in);
        readConf();
        System.out.println("Dostepne numery portów: ");
        for (int i = 0; i < Reader.ports.length; i++) {
            if (Reader.ports[i] != null)
                System.out.println("Port numer " + i + " " + Reader.ports[i]);
        }
        int numberOfPort = 100;
        System.out.println("Podaj numer ");
        try {
            numberOfPort = sc.nextInt();
            ;
        } catch (InputMismatchException e) {
            System.out.println("Musisz podac cyfre");
        }
        try {
            portNumber = Integer.parseInt(Reader.ports[numberOfPort]);
            knownPort = portNumber;
        } catch (NumberFormatException e) {
            System.out.println("Podales bledna wartosc");
        }

        try {
            echoSocket = new Socket(hostname, portNumber);// umozliwa polaczenie z jednym hostem
            out = new PrintWriter(echoSocket.getOutputStream(), true); //
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())); //
        } catch (Exception e) {
            System.out.println("Nie udalo się połaczyć");
            System.out.println("Byc moze nikt nie nasluchuje na tym porcie");
            System.exit(0);
        }
    }


    public void readConf() {

        Reader read = new Reader(numer);
        read.readConf();
    }

    public void checkMD5(String numer, String nameOfFile, int numerofFile) throws IOException {

        getConnect2();
        out.println("getMD5" + "," + numer + "," + nameOfFile);
        outHTTP.println("Client numer: " + numer + " Wyslano protokol getMD5 " + nameOfFile + " Do " + echoSocket.toString());
        String checksum = "";
        String inputLine = "";

        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("MD5")) {
                checksum = inputLine.split(",")[1];
                break;
            }
        }

        String path = File.separator + "TORrent_" + numer + File.separator + "POBRANE" + File.separator + nameOfFile + File.separator;
        File fileMD5 = new File(path);
        MessageDigest md5Digest = null;
        String checksumClient = "";

        try {
            md5Digest = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            checksumClient = MD5CheckSum.getFileChecksum(md5Digest, fileMD5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (checksum.equals(checksumClient)) {
            System.out.println("Client: Sciagnieto poprawnie plik");

        } else {
            System.out.println("Client: Plik nie zostal sciagnety poprawnie");
            System.out.println("Client: Rozpocznie sie pobieranie od miejsca przerwania");
            getConnect2();
            out.println("RePULL" + "," + numerofFile + "," + fileMD5.length());
            outHTTP.println("Client numer: " + numer + " Wyslano protokol RePULL" + " Do " + echoSocket.toString());
            saveFile.saveWithoutInterrupt(echoSocket, nameOfFile, this);
            checkMD5(numer, nameOfFile, numerofFile);
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        int randomNumber = random.nextInt(max + 1 - min) + min;
        Thread HTTPServerThread = new Thread(new HTTPServerRunnable(numer, "Client", randomNumber, randomNumber));
        HTTPServerThread.start();
        System.out.println("Server WWW bedzie dostepny na porcie: " + (randomNumber + 5));
        System.out.println("Pod adresem localhost:" + (randomNumber + 5) + "/" + "Client" + numer);
        System.out.println("Trwa wlaczanie servera WWW");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            httpSocket = new Socket("localhost", (2000 + Integer.parseInt(numer) + randomNumber));
        } catch (IOException e) {
            System.out.println("Blad podczas laczenia z serwerem WWW");
        }

        try {
            outHTTP = new PrintWriter(httpSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File folder2 = new File(File.separator + "TORrent_" + numer + File.separator + "POBRANE" + File.separator);

        if (!folder2.exists()) {

            folder2.setExecutable(true, false);
            folder2.setReadable(true, false);
            folder2.setWritable(true, false);
            folder2.mkdir();

        }
        while (true) {
            try {
                System.out.println("Start Client numer: " + numer);

                Scanner sc = new Scanner(System.in);


                System.out.println("==========================================");
                System.out.println("|              MENU CLIENT                |");
                System.out.println("==========================================");
                System.out.println("| Options:                                |");
                System.out.println("|        1. Sprawdz liste plikow          |");
                System.out.println("|        2. Sciagnij plik z hosta         |");
                System.out.println("|        3. Wrzuć plik na host            |");
                System.out.println("|        4. Exit                          |");
                System.out.println("==========================================");

                int choose = 100;
                try {
                    choose = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Musisz  podac cyfre");
                }
                switch (choose) {
                    case 1:
                        getConnect();
                        out.println("getFiles");
                        out.flush();
                        outHTTP.println("Client numer: " + numer + " Wyslano protokol getFiles" + " Do " + echoSocket.toString());

                        String inputLine = null;
                        try {
                            while ((inputLine = in.readLine()) != null) {
                                System.out.println(inputLine); // sczytujemy i wypisujemy odpowiedz z serwera
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case 2:
                        getConnect();
                        System.out.println("Podaj numer i nazwe pliku wraz z rozszerzeniem który chcesz pobrac, odzielajac przecinkiem");
                        System.out.println("Na przyklad");
                        System.out.println("1,nazwa.jpg");
                        System.out.println();
                        String nameOfFile = null;
                        int numerofFile = 0;
                        Scanner sc2 = new Scanner(System.in);
                        nameOfFile = sc2.nextLine();
                        out.println("PULL" + "," + nameOfFile);
                        outHTTP.println("Client numer: " + numer + " Wyslano protokol PULL " + nameOfFile + " Do " + echoSocket.toString());
                        String[] nameoFfiletab = nameOfFile.split(",");

                        try {
                            numerofFile = Integer.parseInt(nameoFfiletab[0]);
                            nameOfFile = nameoFfiletab[1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Podales wartosc ktora nie istnieje");
                            break;
                        }

                        try {
                            saveFile.saveFile(echoSocket, nameOfFile, this, outHTTP);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        checkMD5(numer, nameOfFile, numerofFile);
                        break;

                    case 3:
                        getConnect();
                        File folder = new File("/TORrent_" + numer + File.separator + "POBRANE" + File.separator);
                        reader.getFilesConsole(folder);
                        System.out.println();
                        System.out.println("Podaj numer i nazwe pliku wraz z rozszerzeniem który chcesz wrzucić na host, odzielajac przecinkiem");
                        System.out.println("Na przyklad");
                        System.out.println("1,nazwa.jpg");
                        System.out.println();
                        String nameOfFile2 = null;
                        Scanner sc3 = new Scanner(System.in);
                        nameOfFile2 = sc3.nextLine();
                        out.println("PUSH" + ',' + nameOfFile2);
                        outHTTP.println("Client numer: " + numer + " Wyslano protokol PUSH " + nameOfFile2 + " Do " + echoSocket.toString());
                        String[] nameoFfiletab2 = nameOfFile2.split(",");
                        int numberOfFile = Integer.parseInt(nameoFfiletab2[0]);
                        saveFile.sendFileToHost(numberOfFile, echoSocket, outHTTP);
                        break;

                    case 4:
                        System.out.println("Exit selected");
                        if (echoSocket != null) {
                            echoSocket.close();
                        }
                        menu.chooseOption();
                        return;

                    default:
                        System.out.println("Invalid key selected");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
