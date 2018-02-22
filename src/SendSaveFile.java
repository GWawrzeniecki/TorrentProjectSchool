
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

public class SendSaveFile {
    private ArrayList<File> files;
    private String numer;

    public SendSaveFile(String numer) {
        this.numer = numer;
        files = new ArrayList<File>();
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

    public void sendFileToHost(int fileNumber, Socket client, PrintWriter outHTTP) {

        try {
            final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "POBRANE" + File.separator);

            getFilesRec(folder);
            File fileToSend = files.get(fileNumber);
            InputStream in = Files.newInputStream(fileToSend.toPath());
            OutputStream out = client.getOutputStream();

            int count;
            byte[] buffer = new byte[8192];
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            outHTTP.println("Client numer: " + numer + " Wyslano plik  - " + files.get(fileNumber).getName() + " NA HOST " + client.toString());

        } catch (Exception exc) {
            System.out.println(exc);
        }

        System.out.println("Server: Koniec wysyłania pliku na HOST");

    }

    public void reSendFile(int fileNumber, int sizeOfFile, Socket client, PrintWriter outHTTP) {

        try {
            final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "DANE" + File.separator);
            getFilesRec(folder);
            File fileToSend = files.get(fileNumber);
            InputStream in = Files.newInputStream(fileToSend.toPath());
            OutputStream out = client.getOutputStream();
            in.skip(sizeOfFile);
            int count;
            byte[] buffer = new byte[8192];

            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            outHTTP.println("Server numer: " + numer + " Wysłano ponownie plik - " + files.get(fileNumber).getName() + " DO " + client.toString());
        } catch (Exception exc) {
            System.out.println("BLAD");
            System.out.println(exc);
        }

        System.out.println("Server: Koniec retransmiji");
    }

    public void sendFile(int fileNumber, Socket client, Socket httpSocket, PrintWriter outHTTP) {

        try {
            final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "DANE" + File.separator);
            getFilesRec(folder);
            File fileToSend = files.get(fileNumber);
            InputStream in = Files.newInputStream(fileToSend.toPath());
            OutputStream out = client.getOutputStream();
            int count;
            byte[] buffer = new byte[8192];
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            outHTTP.println("Server numer: " + numer + " Wysłano plik - " + files.get(fileNumber).getName() + " DO " + client.toString());

        } catch (Exception exc) {
            System.out.println(exc);
        }

        System.out.println("Server: Koniec wysyłania");
    }

    protected void saveFile(Socket clientSock, String nameOfFile, Client client, PrintWriter outHTTP) throws IOException {
        InputStream in = null;
        try {
            in = clientSock.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int byteToBeRead = -1;
        final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "POBRANE" + File.separator);

        if (!folder.exists()) {
            folder.mkdir();
        }

        File newFile = new File(folder.getAbsolutePath() + File.separator + nameOfFile);
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(newFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while ((byteToBeRead = in.read()) != -1) {

            try {
                fs.write(byteToBeRead);
                //return;
                    /*

                    Odkomentowac return w celu przetestowania retransmisji pliku
                    MD5 pliku pobranego jest sprawdzane po kazdym pobraniu

                     */
                     /*
                     Tutaj return jest w celu zasymulowania przerwania połączenia
                      */
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fs.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outHTTP.println("Client numer: " + numer + " Zapisano plik - " + nameOfFile);

        System.out.println("Sciagnieto i zapisano plik - " + nameOfFile + " - Na kliencie numer: " + numer);
    }

    protected void saveWithoutInterrupt(Socket clientSock, String nameOfFile, Client client) throws IOException {

        InputStream in = clientSock.getInputStream();
        int byteToBeRead = -1;
        final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "POBRANE" + File.separator);

        if (!folder.exists()) {
            folder.mkdir();
        }

        File newFile = new File(folder.getAbsolutePath() + File.separator + nameOfFile);
        FileOutputStream fs = new FileOutputStream(newFile, true);

        while ((byteToBeRead = in.read()) != -1) {
            fs.write(byteToBeRead);
        }

        fs.flush();
        fs.close();

        System.out.println("Sciagnieto i zapisano plik - " + nameOfFile + " - Na kliencie numer: " + numer);
    }


    protected void saveFileToServer(Socket clientSock, String nameOfFile, PrintWriter outHTTP) throws IOException {
        InputStream in = clientSock.getInputStream();
        int byteToBeRead = -1;
        final File folder = new File(File.separator + "TORrent_" + numer + File.separator + "DANE" + File.separator);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File newFile = new File(folder.getAbsolutePath() + File.separator + nameOfFile);
        if (newFile.exists()) {
            System.out.println("Plik o podanej nazwie " + nameOfFile + " Juz istnieje");
            //client.run();
            return;
        }
        FileOutputStream fs = new FileOutputStream(newFile);
        while ((byteToBeRead = in.read()) != -1) {
            //System.out.println(byteToBeRead);
            fs.write(byteToBeRead);
        }
        outHTTP.println("Server numer: " + numer + " Zapisano plik - " + nameOfFile + " Na hoscie " + clientSock.toString());
        System.out.println("Sciagnieto i zapisano plik - " + nameOfFile + " - Na hoscie numer: " + numer);
        fs.flush();
        fs.close();
    }
}
