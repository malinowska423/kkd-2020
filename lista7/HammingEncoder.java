import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HammingEncoder {
  
  public static byte[] encode(byte[] input) {
    StringBuilder encoded = new StringBuilder();
    String inputBitString = getBitString(input);
    while (inputBitString.length() >= 4) {
      String nibble = inputBitString.substring(0, 4);
      encoded.append(toHamming(nibble));
      inputBitString = inputBitString.substring(4);
    }
    return getBytesFromBinaryString(encoded.toString());
  }
  
  private static String toHamming(String bits) {
    String p1 = parity(bits, new int[]{0, 1, 3});
    String p2 = parity(bits, new int[]{0, 2, 3});
    String p3 = parity(bits, new int[]{1, 2, 3});
    String p = parity(p1 + p2 + bits.charAt(0) + p3 + bits.substring(1), new int[]{0, 1, 2, 3, 4, 5, 6});
    return p1 + p2 + bits.charAt(0) + p3 + bits.substring(1) + p;
  }
  
  private static String parity(String bitString, int[] indices) {
    int sub = 0;
    for (Integer i :
        indices) {
      if (bitString.charAt(i) == '1') {
        sub++;
      }
    }
    return String.valueOf(sub % 2);
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
        byte[] result = HammingEncoder.encode(content);
        Files.write(Paths.get(output), result);
        System.out.println("\nPlik <<" + input + ">> zakodowano do pliku <<" + output + ">>");
      } catch (IOException ioe) {
        System.out.println("Blad: " + ioe.getMessage());
      }
    }
  }
  
  public static String getBitString(byte[] bytes) {
    StringBuilder bitString = new StringBuilder();
    for (Byte b : bytes) {
      bitString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
    }
    return bitString.toString();
  }
  
  public static byte[] getBytesFromBinaryString(String s) {
    int size = (s.length() / 8) + (s.length() % 8 > 0 ? 1 : 0);
    byte[] bytes = new byte[size];
    for (int i = 0, k = 0; i < size; i++, k += 8) {
      try {
        bytes[i] = (byte) Short.parseShort(s.substring(k, k + 8), 2);
      } catch (StringIndexOutOfBoundsException e) {
        bytes[i] = (byte) Short.parseShort(s.substring(k), 2);
      }
    }
    return bytes;
  }
  
}
