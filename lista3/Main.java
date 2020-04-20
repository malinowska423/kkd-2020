import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
          try (InputStream stream = Files.newInputStream(Paths.get(input))) {
            byte[] content = stream.readAllBytes();
            stream.close();
            var coder = new LZWCode(coding);
            var result = coder.encode(content);
            OutputStream out = Files.newOutputStream(Paths.get(output));
            out.write(result);
            out.close();
            System.out.println("\n========= Zakodowano plik: " + input + " =========");
            System.out.println("Rozmiar pliku wejsciowego: " + content.length + " B");
            System.out.println("Rozmiar pliku wyjsciowego: " + result.length + " B");
          
//            String stringContent = new String(content, StandardCharsets.UTF_8);
//            var entropy = AdaptiveHuffmanCode.entropy(stringContent);
//            var avg = (double) result.length / stringContent.length();
//            var ratio = (double) content.length / result.length;
//
//            System.out.println("Entropia: " + entropy);
//            System.out.println("Srednia dlugosc kodu: " + avg);
//            System.out.println("Stopien kompresji: " + ratio);
          } catch (IOException ioe) {
            System.out.println("Blad: " + ioe.getMessage());
          }
        }
        case "-d" -> {
          try (InputStream stream = Files.newInputStream(Paths.get(input))) {
            byte [] content = stream.readAllBytes();
            stream.close();
            var coder = new LZWCode(coding);
            var result = coder.decode(content);
            OutputStream out = Files.newOutputStream(Paths.get(output));
            out.write(result);
            out.close();
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