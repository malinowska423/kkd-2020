import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class Noise {
  private final double probability;
  private final SecureRandom random;
  
  public Noise(double probability) {
    this.probability = probability;
    this.random = new SecureRandom();
  }
  
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Blad: niepoprawna liczba argumentow");
      System.out.println("Schemat argumentow: [prawdopodobienstwo] [plik_wejsciowy] [plik_wyjsciowy] ");
    } else {
      var input = args[1];
      var output = args[2];
      double p = 0;
      try {
        p = Double.parseDouble(args[0]);
        if (p < 0.0 || p > 1.0) {
          throw new IndexOutOfBoundsException("prawdpodobienstwo musi byc liczba rzeczywista z przedzialu [0,1]");
        }
      } catch (NumberFormatException | IndexOutOfBoundsException e) {
        System.out.println("Blad: " + e.getMessage());
      }
      try {
        byte[] content = Files.readAllBytes(Paths.get(input));
        var noise = new Noise(p);
//        noise.testProbability(100);
        byte[] result = noise.disruptData(content);
        Files.write(Paths.get(output), result);
        System.out.println("\nZaburzono <<" + input + ">> z prawdopodobienstwem " + p + " do pliku <<" + output + ">>");
      } catch (IOException ioe) {
        System.out.println("Blad: " + ioe.getMessage());
      }
    }
  }
  
  public byte[] disruptData(byte[] input) {
    byte[] output = new byte[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = disruptByte(input[i]);
    }
    return output;
  }
  
  private byte disruptByte(byte b) {
    String bitString = Integer.toBinaryString(b);
    StringBuilder disrupted = new StringBuilder();
    for (int i = 0; i < bitString.length(); i++) {
      char bit = bitString.charAt(i);
      disrupted.append(shouldChange() ? (bit == '0' ? '1' : '0') : bit);
    }
    return Byte.parseByte(disrupted.toString(), 2);
  }
  
  private boolean shouldChange() {
    double d = random.nextDouble();
    return d < probability;
  }
  
}
