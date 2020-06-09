import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BitComparator {
  
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Schemat argumentow: [plik_wejsciowy1] [plik_wejsciowy2] ");
    } else {
      var input1 = args[0];
      var input2 = args[1];
      try {
        byte[] content1 = Files.readAllBytes(Paths.get(input1));
        byte[] content2 = Files.readAllBytes(Paths.get(input2));
        int blocksDisrupted = BitComparator.getDisruptedBlocksOf4(content1, content2);
        System.out.println("\nPliki <<" + input1 + ">> oraz <<" + input2 + ">> roznia sie w " + blocksDisrupted + " z " + (content1.length * 2) + " wszystkich 4-bitowych blokow");
      } catch (IOException ioe) {
        System.out.println("Blad: " + ioe.getMessage());
      }
    }
  }
  
  public static int getDisruptedBlocksOf4(byte[] input1, byte[] input2) {
    int count = 0;
    for (int i = 0; i < input1.length; i++) {
      String b1 = String.format("%8s", Integer.toBinaryString(input1[i] & 0xFF)).replace(' ', '0');
      String b2 = String.format("%8s", Integer.toBinaryString(input2[i] & 0xFF)).replace(' ', '0');
      boolean found = false;
      for (int j = 0; j < 8; j++) {
        found = found || b1.charAt(j) != b2.charAt(j);
        if (j % 4 == 3 && found) {
          count++;
          found = false;
        }
      }
    }
    return count;
  }
}
