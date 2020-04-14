import java.io.File;

public class Main {
  public static void main(String[] args) {
    if (args.length != 1 && args.length != 2) {
      System.out.println("Blad: nieprawidlowa liczba argumentow");
      System.out.println("Podaj nazwe (sciezke) pliku do sprawdzenia lub uruchom z flaga -d aby uruchomic dla wszystkich plikow w katalogu");
    } else if (args.length == 1) {
      getEntropy(args[0]);
    } else {
      if (args[0].equals("-d")) {
        File dir = new File(args[1]);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
          for (File file : directoryListing) {
            getEntropy(file.getPath());
          }
        } else {
          System.out.println("Blad: " + args[1] + " nie jest nazwa folderu");
        }

      }
      else {
        System.out.println("Blad: niepoprawna flaga");
      }
    }
  }
  
  
  private static void getEntropy(String filename) {
    try {
      EntropyAnalyzer ea = new EntropyAnalyzer(filename);
      ea.printFileEntropyInfo();
    } catch (Exception e) {
      System.out.println("Blad: " + e.getMessage());
    }
  }
}