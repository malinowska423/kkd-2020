import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Schemat argumentow: [flaga --e/--d] [plik_wejsciowy] [k]");
    } else {
      var input = args[1];
      int k = 0;
      try {
        k = Integer.parseInt(args[2]);
        if (k < 1 || k > 7) {
          throw new IndexOutOfBoundsException("Blad: k musi wynosic miedzy 1 a 7");
        }
      } catch (NumberFormatException | IndexOutOfBoundsException e) {
        System.out.println("Blad: " + e.getMessage());
      }
      try {
        byte[] content = Files.readAllBytes(Paths.get(input));
        TGAAnalyzer tga = new TGAAnalyzer(content);
        QuantumCoder coder = new QuantumCoder(tga, k);
        
        switch (args[0]) {
          case "--e" -> {
            //encode
            coder.encode();
            Files.write(Paths.get("output_filtered_low.tga"), tga.getTGABytes(TGAAnalyzer.pixelsToBytes(coder.filteredLow)));
            Files.write(Paths.get("output_filtered_high.tga"), tga.getTGABytes(TGAAnalyzer.pixelsToBytes(coder.filteredHigh)));
            Files.write(Paths.get("output_encoded_low.tga"), tga.getTGABytes(coder.bytes));
            Files.write(Paths.get("output_encoded_high.tga"), tga.getTGABytes(TGAAnalyzer.pixelsToBytes(coder.quantified)));
  
            System.out.println("\n=========== Zakodowano plik: " + input + " ============\n\n");
  
            coder.stats("\n============== Filtr dolnoprzepustowy ==============", coder.filteredLow);
            coder.stats("\n============== Filtr gornoprzepustowy ==============", coder.quantified);
          }
          case "--d" -> {
            //decode
            Files.write(Paths.get("output_decoded_low.tga"), coder.decode());
            System.out.println("\n=========== Zdekodowano plik: " + input + " ============\n\n");
          }
          default -> throw new IOException("niepoprawna flaga [--e lub --d]");
        }
      } catch (IOException ioe) {
        System.out.println("Blad: " + ioe.getMessage());
      }
    }
  }
}
