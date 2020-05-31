import java.util.ArrayList;

public class EliasGamma {
  
  public static String encode(int number) {
    String code = Integer.toBinaryString(number);
    code = "0".repeat(code.length() - 1) + code;
    return code;
  }
  
  
  public static ArrayList<Integer> decode(String code) {
    ArrayList<Integer> codes = new ArrayList<>();
    int counter = 0;
    int index = 0;
    while (index < code.length()) {
      if (code.charAt(index) == '0') {
        counter++;
        index++;
      } else {
        codes.add(Integer.parseInt(code.substring(index, index + counter + 1), 2));
        index += counter + 1;
        counter = 0;
      }
    }
    return codes;
  }
  
}
