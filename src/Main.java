public class Main {


    public static void main(String[] args) {

        String numer = "";
        if (args.length > 0) {
            numer = args[0];
        }

        System.out.println("Klient numer " + numer);
        Menu menu = new Menu(numer);
        menu.chooseOption();


       //wraz z lista plikow pobieramy ich wage, dzielimy np na 3 watki, pobieraja one kazda jedna czesc, a potem sie synchronizuja i pakuja wszystko do jednej rzeczy



    }
}

