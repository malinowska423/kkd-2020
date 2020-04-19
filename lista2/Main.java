import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Nalezy uruchomic program z flaga -e lub -d oraz podac nazwe pliku wejsciowego orazy wyjsciowego");
      System.out.println("Np. java Main -e test.txt test.out");
    } else {
      var input = args[1];
      var output = args[2];
      switch (args[0]) {
        case "-e" -> {
          try (InputStream stream = Files.newInputStream(Paths.get(input))) {
            byte[] content = stream.readAllBytes();
            stream.close();
            var coder = new AdaptiveHuffmanCode();
            var result = coder.encode(content);
            OutputStream out = Files.newOutputStream(Paths.get(output));
            out.write(result);
            out.close();
            System.out.println("\n========= Zakodowano plik: " + input + " =========");
            System.out.println("Rozmiar pliku wejsciowego: " + content.length + " B");
            System.out.println("Rozmiar pliku wyjsciowego: " + result.length + " B");
            
            String stringContent = new String(content, StandardCharsets.UTF_8);
            var entropy = AdaptiveHuffmanCode.entropy(stringContent);
            var avg = (double) result.length / stringContent.length();
            var ratio = (double) content.length / result.length;
            
            System.out.println("Entropia: " + entropy);
            System.out.println("Srednia dlugosc kodu: " + avg);
            System.out.println("Stopien kompresji: " + ratio);
          } catch (IOException ioe) {
            System.out.println("Blad: " + ioe.getMessage());
          }
        }
        case "-d" -> {
          try (InputStream stream = Files.newInputStream(Paths.get(input))) {
            byte [] content = stream.readAllBytes();
            stream.close();
            var coder = new AdaptiveHuffmanCode();
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
