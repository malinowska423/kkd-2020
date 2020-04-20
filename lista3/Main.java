import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Nalezy uruchomic program z flaga -e lub -d, podac nazwe pliku wejsciowego oraz wyjsciowego i podac flage typu kodowania (-omega, -delta, -gamma, -fib)");
      System.out.println("Np. java Main -e test.txt test.out -omega");
    } else {
      var input = args[1];
      var output = args[2];
      CodingType coding;
      switch (args[3]) {
        case "-gamma" -> {
          coding = CodingType.ELIAS_GAMMA;
        }
        case "-delta" -> {
          coding = CodingType.ELIAS_DELTA;
        }
        case "-fib" -> {
          coding = CodingType.FIBONACCI;
        }
        default -> {
          coding = CodingType.ELIAS_OMEGA;
        }
      }
      switch (args[0]) {
        case "-e" -> {
          try {
            byte[] content = Files.readAllBytes(Paths.get(input));
            var result = (new LZWCode(coding)).encode(content);
            Files.write(Paths.get(output), result);
    
            System.out.println("\n========= Zakodowano plik: " + input + " =========");
            System.out.println("Rozmiar pliku wejsciowego: " + content.length + " B");
            System.out.println("Rozmiar pliku wyjsciowego: " + result.length + " B");
    
            System.out.println("Entropia tekstu: " + LZWCode.entropy(content));
            System.out.println("Entropia kodu: " + LZWCode.entropy(result));
            System.out.println("Stopien kompresji: " + (double) content.length / result.length);
          } catch (IOException ioe) {
            System.out.println("Blad: " + ioe.getMessage());
          }
        }
        case "-d" -> {
          try {
            byte[] content = Files.readAllBytes(Paths.get(input));
            var result = (new LZWCode(coding)).decode(content);
            Files.write(Paths.get(output), result);
    
            System.out.println("\n========= Zdekodowano plik: " + input + " =========");
            System.out.println("Rozmiar pliku wejsciowego: " + content.length + " B");
            System.out.println("Rozmiar pliku wyjsciowego: " + result.length + " B");
          } catch (IOException ioe) {
            System.out.println("Blad: " + ioe.getMessage());
          }
        }
        default -> {
          System.out.println("Blad: niepoprawna flaga");
          System.out.println("Nalezy uruchomic program z flaga -e lub -d");
        }
      }
    }
  }
}