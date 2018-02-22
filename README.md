<h4>Projekt na studia PJWSTK<br>
Aplikacja konsolowa "Torrent"</h4>

<b>Instrukcje:</b>

Zakładamy, że użytkownik aplikacji ma założony folder<br>
Dla systemu WINDOWS: C:\\TORrent_$<br>
Dla systemu LINUX: ~\\TORrent_$<br>
Gdzie $ oznaczna numer instacji który podajemy jako argument przy<br>wywowałaniu programu.

W każdym folderze ToRrent_$ powiinien znajdować się plik konfiguracyjny o nazwie conf.txt<br>
Gdzie beda adresy ip oraz porty rozdzielone dwukropkiem.<br>
Na przykład<br>
<code>
localhost:11000<br>
localhost:11001<br>
localhost:11002<br>
</code>
Każda instacja aplikacji spowoduje utworzenie folderu DANE oraz POBRANE w jej folderze.

W celu kompilacji programu można posłużyć się skryptem(Linux)<br>
<code>
#!/bin/bash
javac /Torrent/src/*.java
</code><br>
A w celu uruchomienia 1 instancji<br>
<code>
#!/bin/bash
java -cp /Projekt_SKJ_s15429/bin Main 1
</code><br>

Zaimplementowane wartości:
1. Wymiana listy plików udostępnianych przez hosty
2. Przesyłanie plików typu PULL(Ściągniecie pliku z hosta)
3. Przesyłanie plików typu PUSH(Wysłanie pliku na host)
4. Wznawianie transmisji pliku w przypadku jej przerwania
5. Server HTTP wraz z logami(PO uruchomieniu aplikacji otrzymamy stosowną informację o adresie serwera HTTP)



