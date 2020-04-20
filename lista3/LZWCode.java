import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LZWCode {
  private final Encoder encoder;
  private final Decoder decoder;
  
  public LZWCode(CodingType codingType) {
    switch (codingType) {
      case ELIAS_GAMMA -> {
        encoder = new EliasEncoder(CodingType.ELIAS_GAMMA);
        decoder = new EliasDecoder(CodingType.ELIAS_GAMMA);
      }
      case ELIAS_DELTA -> {
        encoder = new EliasEncoder(CodingType.ELIAS_DELTA);
        decoder = new EliasDecoder(CodingType.ELIAS_DELTA);
      }
      case FIBONACCI -> {
        encoder = new FibonacciCode();
        decoder = new FibonacciCode();
      }
      default -> {
        encoder = new EliasEncoder();
        decoder = new EliasDecoder();
      }
    }
  }
  
  public byte[] encode(byte[] content) {
    StringBuilder result = new StringBuilder();
    Map<String, Integer> dict = new HashMap<>();
    for (int i = 0; i < 256; i++) {
      dict.put(String.valueOf((char) i), i);
    }
    String P = String.valueOf((char) content[0]);
    for (int i = 1; i < content.length; i++) {
      String C = String.valueOf((char) content[i]);
      
      if (dict.containsKey(P + C)) {
        P += C;
      } else {
        result.append(encoder.encode(dict.get(P) + 1));
        dict.put(P + C, dict.size());
        P = C;
      }
    }
    result.append(encoder.encode(dict.get(P) + 1));
    
    if (encoder.getCodingType() == CodingType.ELIAS_GAMMA || encoder.getCodingType() == CodingType.ELIAS_DELTA) {
      int lastByteSize = result.length() % 8;
      if (lastByteSize != 0) {
        result.append(getNZeros(8 - lastByteSize));
      }
    } else {
      int padding = (result.length() + 3) % 8;
      if (padding != 0) {
        String offset = String.format("%3s", Integer.toBinaryString(padding)).replaceAll(" ", "0");
        result = new StringBuilder(offset + result + getNZeros(padding));
      } else {
        result.insert(0, "000");
      }
    }
    return getBytesFromBinaryString(result.toString());
  }
  
  public byte[] decode(byte[] input) {
    ArrayList<String> dict = new ArrayList<>();
    for (int i = 0; i < 256; i++) {
      dict.add(String.valueOf((char) i));
    }
    
    String content = getBinaryStringFromBytes(input);
    if (decoder.getCodingType() != CodingType.ELIAS_GAMMA && decoder.getCodingType() != CodingType.ELIAS_DELTA) {
      int num = Integer.parseInt(content.substring(0, 3), 2);
      content = content.substring(3, content.length() - num);
    }
    ArrayList<Integer> codes = decoder.decode(content);
    for (int i = 0; i < codes.size(); i++) {
      codes.set(i, codes.get(i) - 1);
    }
    int index = 0;
    int OLD = codes.get(index);
    String S = dict.get(OLD);
    String C = dict.get(OLD);
    StringBuilder result = new StringBuilder(S);
    index++;
    while (index < codes.size()) {
      int NEW = codes.get(index);
      if (NEW >= dict.size()) {
        S = dict.get(OLD);
        S = S + C;
      } else {
        S = dict.get(NEW);
      }
      result.append(S);
      C = S;
      dict.add(dict.get(OLD) + C);
      OLD = NEW;
      index++;
    }
    return result.toString().getBytes();
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
  
  private String getBinaryStringFromBytes(byte[] bytes) {
    StringBuilder s = new StringBuilder();
    for (byte aByte : bytes) {
      s.append(String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0'));
    }
    return s.toString();
  }
  
  public static double entropy(byte[] content) {
    Map<Byte, Integer> symbolOccur = new HashMap<>();
    symbolOccur.put(content[0], 1);
    for (int i = 1; i < content.length; i++) {
      byte key = content[i];
      symbolOccur.put(key, symbolOccur.get(key) != null ? symbolOccur.get(key) + 1 : 1);
    }
    
    int size = content.length;
    double sizeLog = log2(size);
    final double[] entropy = {0};
    
    symbolOccur.forEach((symbol, occur) -> {
      entropy[0] += (sizeLog - log2(occur)) * occur;
    });
    
    
    return entropy[0] / size;
  }
  
  private static double log2(double x) {
    return Math.log(x) / Math.log(2);
  }
  
}
