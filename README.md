# Kodowanie i Kompresja Danych 2020

Repozytorium zawiera rozwiązania zadań z kursu prowadzonego przez <b>dra Macieja Gębalę</b> w semestrze letnim 2019/2020 dla studentów drugiego i trzeciego roku Informatyki na Wydziale Podstawowych Problemów Techniki Politechniki Wrocławskiej.

## Lista 1
Pliki źródłowe do tej listy znajdują się w folderze `lista1`, a są to:
- `EntropyAnalyzer.java` - plik zawierający główną klasę odpowiedzialną za liczenie entropii oraz entropii warunkowej plików
- `Main.java` - plik zawierający klasę obsługującą wejście i wyjście z programu

Aby skompilować pliki do folderu `lista1/out` należy przejść do folderu `lista1` i wywołać polecenie
```
$ javac Main.java -d out
``` 
Następnie, aby uruchomić program dla pojedynczego pliku znajdującego się w folderze `lista1/testy1` należy wywołać polecenie
```
$ java -cp out Main testy1/pan-tadeusz-czyli-ostatni-zajazd-na-litwie.txt
```
Aby uruchomić program dla <b>wszystkich plików w folderze</b> należy uruchomić go z flagą `-d` i ścieżką do folderu
```
$ java -cp out Main -d testy1
```

## Lista 2
Pliki źródłowe do tej listy znajdują się w folderze `lista2`, a są to:
- `AdaptiveHuffmanCode.java` - plik zawierający główną klasę zawierającą funkcje kodującą i dekodującą podany ciąg bajtów
- `HuffmanNode.java` - plik zawierający klasę wierzchołka w drzewie Huffmana
- `Main.java` - plik zawierający klasę obsługującą wejście i wyjście z programu

Kompilacja przebiega analogicznie do listy pierwszej.

Aby zakodować plik `test.txt` do pliku `test.out` należy wywołać polecenie
```
$ java -cp out Main -e test.txt test.out
```

Aby zdekodować plik `test.out` do pliku `test-back.txt` należy wywołać polecenie
```
$ java -cp out Main -d test.out test-back.txt
```

## Lista 3
Pliki źródłowe do tej listy znajdują się w folderze `lista2`, a są to:
- `CodingType.java` - typ wyliczeniowy enum określający typy kodowania obsługiwane przez program
- `Decoder.java` - interfejs definiujący budowę obiektu dekodującego ciąg znaków
- `EliasDecoder.java` - klasa zawierająca metody dekodujące ciąg znaków kodowaniem Eliasa
- `EliasEncoder.java` - klasa zawierająca metody kodujące ciąg znaków kodowaniem Eliasa
- `Encoder.java` - interfejs definiujący budowę obiektu kodującego wartość
- `FibonacciCode.java` - klasa zawierająca metody kodującą i dekodującą kodowaniem Fibonacciego
- `LZWCode.java` - główna klasa zawierająca metody kodującą i dekodującą podany ciąg bajtów według podanego typu kodowania
- `Main.java` - klasa obsługująca wejście i wyjście z programu

Kompilacja przebiega analogicznie do list poprzednich.

Uruchomienie programu przebiega analogicznie do listy poprzedniej z tą różnicą, że należy podać dodatkowy argument w postaci flagi typu kodowania. Są to `-omega`, `-delta`, `-gamma` dla kodowania Eliasa oraz `-fib` dla kodowania Fibonacciego.

<b>Przykład kompilacji i uruchomienia</b>
```
$ javac -d out Main.java && java -cp out Main.java -e testy/test.txt testy/test.out -fib
$ javac -d out Main.java && java -cp out Main.java -d testy/test.out testy/test-back.txt -fib
```