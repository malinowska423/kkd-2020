import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingDecoder {
  private static int errors;
  
  private static final String[] G = {
      "00000000",
      "11010010",
      "01010101",
      "10000111",
      "10011001",
      "01001011",
      "11001100",
      "00011110",
      "11100001",
      "00110011",
      "10110100",
      "01100110",
      "01111000",
      "10101010",
      "00101101",
      "11111111",
  };
  
  public static byte[] decode(byte[] input) {
    errors = 0;
    String bitStringInput = HammingEncoder.getBitString(input);
    StringBuilder decoded = new StringBuilder();
    while (bitStringInput.length() >= 8) {
      String nibble = bitStringInput.substring(0, 8);
      nibble = fromHamming(nibble);
      decoded.append(nibble != null ? nibble : "0000");
      bitStringInput = bitStringInput.substring(8);
    }
    System.out.println("W trakcie dekodowania znaleziono 2 bledy w " + errors + " blokach");
    return HammingEncoder.getBytesFromBinaryString(decoded.toString());
  }
  
  private static String fromHamming(String bits) {
    for (String code : G) {
      int diffs = 0;
      for (int i = 0; i < 8; i++) {
        if (code.charAt(i) != bits.charAt(i)) {
          diffs++;
        }
      }
      switch (diffs) {
        case 0 -> {
          return bits.charAt(2) + bits.substring(4, 7);
        }
        case 1 -> {
          return code.charAt(2) + code.substring(4, 7);
        }
        case 2 -> {
          errors++;
          return null;
        }
      }
    }
    return null;
  }
  
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Schemat argumentow: [plik_wejsciowy] [plik_wyjsciowy] ");
    } else {
      var input = args[0];
      var output = args[1];
      try {
        byte[] content = Files.readAllBytes(Paths.get(input));
        byte[] result = HammingDecoder.decode(content);
        Files.write(Paths.get(output), result);
        System.out.println("\nPlik <<" + input + ">> zdekodowano do pliku <<" + output + ">>");
      } catch (IOException ioe) {
        System.out.println("Blad: " + ioe.getMessage());
      }
    }
  }
}
