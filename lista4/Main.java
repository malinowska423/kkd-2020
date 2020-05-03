import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Nalezy podac tylko nazwe pliku w formacie tga");
    } else {
      var input = args[0];
      try {
        byte[] content = Files.readAllBytes(Paths.get(input));
        JPEGLS jpegls = new JPEGLS(content);
        jpegls.printBitmapEntropyInfo();
        jpegls.printEncodingStats();
      } catch (IOException ioe) {
        System.out.println("Blad: " + ioe.getMessage());
      }
    }
  }
}
