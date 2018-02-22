Projekt na studia PJWSTK
Aplikacja konsolowa "Torrent"
Instrukcje:

Zakładamy, że użytkownik aplikacji ma założony folder
Dla systemu WINDOWS: C:\\TORrent_$
Dla systemu LINUX: ~\\TORrent_$
Gdzie $ oznaczna numer instacji który podajemy jako argument przy wywowałaniu programu.

W każdym folderze ToRrent_$ powiinien znajdować się plik konfiguracyjny o nazwie conf.txt
Gdzie beda adresy ip oraz porty rozdzielone dwukropkiem.
Na przykład
localhost:11000
localhost:11001
localhost:11002

Każda instacja aplikacji spowoduje utworzenie folderu DANE oraz POBRANE w jej folderze.

W celu kompilacji programu można posłużyć się skryptem(Linux)

#!/bin/bash
javac /Torrent/src/*.java

A w celu uruchomienia 1 instancji

#!/bin/bash
java -cp /Projekt_SKJ_s15429/bin Main 1

Zaimplementowane wartości:
1. Wymiana listy plików udostępnianych przez hosty
2. Przesyłanie plików typu PULL(Ściągniecie pliku z hosta)
3. Przesyłanie plików typu PUSH(Wysłanie pliku na host)
4. Wznawianie transmisji pliku w przypadku jej przerwania
5. Server HTTP wraz z logami(PO uruchomieniu aplikacji otrzymamy stosowną informację o adresie serwera HTTP)



