
import java.util.InputMismatchException;
import java.util.Scanner;


public class Menu {

    private Thread startClien;
    private Server server;
    private String numer;

    public Menu(String numer) {
        this.numer = numer;
    }

    public void startServer() {

        server = new Server(numer, this);
        Thread serverT = new Thread(server);
        serverT.start();
    }

    void startClient() {
        startClien = new Thread(new Client(numer, this));
        startClien.start();
    }

    void chooseOption() {
        Scanner sc = new Scanner(System.in);

        System.out.println("============================");
        System.out.println("|       MENU TORRENT       |");
        System.out.println("============================");
        System.out.println("| Options:                 |");
        System.out.println("|        1. Start a Client |");
        System.out.println("|        2. Start a Server |");
        System.out.println("|        3. Exit           |");
        System.out.println("============================");

        int choose = 100;
        try {
            choose = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Musisz podac cyfre");
        }
        switch (choose) {
            case 1:
                startClient();
                break;
            case 2:
                startServer();
                break;
            case 3:
                System.out.println("Exit selected");
                System.exit(0);
            default:
                System.out.println("Invalid key selected");
                chooseOption();
        }


    }
}
