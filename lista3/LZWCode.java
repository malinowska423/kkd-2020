import java.util.HashMap;
import java.util.Map;

public class LZWCode {
  private final Encoder encoder;
  
  public LZWCode(CodingType codingType) {
    switch (codingType) {
      case ELIAS_GAMMA -> {
        encoder = new EliasEncoder(CodingType.ELIAS_GAMMA);
      }
      case ELIAS_DELTA -> {
        encoder = new EliasEncoder(CodingType.ELIAS_DELTA);
      }
//      case FIBONACCI -> {
//        encoder = new FibonacciCode();
//      }
      default -> {
        encoder = new EliasEncoder();
      }
    }
  }
  
  public byte[] encode(byte[] input) {
    StringBuilder output = new StringBuilder();
    Map<String, Integer> dict = new HashMap<>();
    for (int i = 0; i < 256; i++) {
      dict.put(String.valueOf((char) i), i);
    }
    String P = String.valueOf((char) input[0]);
    for (int i = 1; i < input.length; i++) {
      String C = String.valueOf((char) input[i]);
      
      if (dict.containsKey(P + C)) {
        P += C;
      } else {
        output.append(encoder.encode(dict.get(P) + 1));
        dict.put(P + C, dict.size());
        P = C;
      }
    }
    output.append(encoder.encode(dict.get(P) + 1));
    
    if (encoder.getCodingType() == CodingType.ELIAS_GAMMA || encoder.getCodingType() == CodingType.ELIAS_DELTA) {
      int lastByteSize = output.length() % 8;
      if (lastByteSize != 0) {
        output.append(getNZeros(8 - lastByteSize));
      }
    } else {
      int padding = (output.length() + 3) % 8;
      if (padding != 0) {
        String offset = String.format("%3s", Integer.toBinaryString(padding)).replaceAll(" ", "0");
        output = new StringBuilder(offset + output + getNZeros(padding));
      } else {
        output.insert(0, "000");
      }
    }
    return getBytesFromBinaryString(output.toString());
  }
  
  public byte[] decode(byte[] content) {
    
    return null;
  }
  
  private String getNZeros(int n) {
    return String.format("%" + n + "s", " ").replaceAll(" ", "0");
  }
  
  private byte[] getBytesFromBinaryString(String s) {
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
