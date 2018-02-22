<h2>Projekt na studia PJWSTK<br>
Aplikacja konsolowa "Torrent"</h2>

<h4>Instrukcje:</h4>
Zakładamy, że użytkownik aplikacji ma założony folder<br>
<li>Dla systemu WINDOWS: C:\\TORrent_$</li>
<li>Dla systemu LINUX: ~\\TORrent_$</li>

Gdzie $ oznaczna numer instacji który podajemy jako argument przy<br>wywowałaniu programu.<p></p>

W każdym folderze TORrent_$ powiinien znajdować się plik konfiguracyjny o nazwie conf.txt<br>
Gdzie beda adresy ip oraz porty rozdzielone dwukropkiem.<br>
Na przykład<br>
<li>localhost:11000</li>
<li>localhost:11001</li>
<li>localhost:11002</li>
<p></p>
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



