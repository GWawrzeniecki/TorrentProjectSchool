import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;


public class Reader {
    private String numer;
    protected static String ports[];
    protected static ArrayList<String> alistOfFiles;
    protected static ArrayList<File> files;
    private String checksum;
    protected static int fileN;
    private PrintWriter out;

    public Reader(String numer) {
        this.numer = numer;
        ports = new String[10];
        alistOfFiles = new ArrayList<String>();
        fileN = 0;
        files = new ArrayList<File>();
    }

    public void readConf() {
        try {
            File file = new File("/TORrent_" + numer + "/conf.txt");

            final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "DANE" + File.separator);
            if (!folder.exists()) {
                folder.mkdir();
            }
            Scanner sc = new Scanner(file);
            int i = 0;
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String str[] = line.split(":");
                String strPort = str[1];
                ports[i] = strPort;
                i++;
            }
        } catch (IOException e) {
            System.out.println("Brak pliku conf");
            System.out.println("Dodaj plik konfiguracyjny zawierajacy adresy ip oraz portu odziellone dwukropkiem");
            System.exit(0);
        }
    }

    private void getFilesRec(File folder) {

        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files.add(listOfFiles[i]);
            } else if (listOfFiles[i].isDirectory()) {
                getFilesRec(listOfFiles[i]);
            }


        }

    }

    public void getFiles(File folder, Socket client) {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e);
        }
        getFilesRec(folder);
        out.println("Na serverze numer: " + numer + " Znajduja sie ponizsze pliki ");
        out.println();
        for (int i = 0; i < files.size(); i++) {

            File file = new File(files.get(i).getPath());
            MessageDigest md5Digest = null;
            try {
                md5Digest = MessageDigest.getInstance("MD5");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                checksum = MD5CheckSum.getFileChecksum(md5Digest, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println("NUMER PLIKU: " + fileN++ + " TYP PLIKU " + "FILE " + "NAZWA " + files.get(i).getName() + " " + "SUMA KONTROLNA = " + checksum);
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getFilesConsole(File folder) {

        getFilesRec(folder);
        System.out.println("Na kliencie numer: " + numer + " Znajduja sie ponizsze pliki ");
        System.out.println();

        for (int i = 0; i < files.size(); i++) {

            File file = new File(files.get(i).getPath());
            MessageDigest md5Digest = null;
            try {
                md5Digest = MessageDigest.getInstance("MD5");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                checksum = MD5CheckSum.getFileChecksum(md5Digest, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("NUMER PLIKU: " + fileN++ + " TYP PLIKU " + "FILE " + "NAZWA " + files.get(i).getName() + " " + "SUMA KONTROLNA = " + checksum);
        }
    }
}


