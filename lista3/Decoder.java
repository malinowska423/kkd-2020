import java.util.ArrayList;

public interface Decoder {
  ArrayList<Integer> decode(String code);
  
  CodingType getCodingType();
}
