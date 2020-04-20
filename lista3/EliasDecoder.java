import java.util.ArrayList;

public class EliasDecoder {
  
  public ArrayList<Integer> eliasGamma(String code) {
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
  
  public ArrayList<Integer> eliasDelta(String code) {
    ArrayList<Integer> codes = new ArrayList<>();
    int L = 0;
    int index = 0;
    while (index < code.length()) {
      if (code.charAt(index) == '0') {
        L++;
        index++;
      } else {
        int n = Integer.parseInt(code.substring(index, index + L + 1), 2) - 1;
        index += L + 1;
        codes.add(Integer.parseInt("1" + code.substring(index, index + n), 2));
        index += n;
        L = 0;
      }
    }
    return codes;
  }
  
  public ArrayList<Integer> eliasOmega(String code) {
    ArrayList<Integer> codes = new ArrayList<>();
    int index = 0;
    int n = 1;
    while (index < code.length()) {
      if (code.charAt(index) == '0') {
        codes.add(n);
        n = 1;
        index++;
      } else {
        n = Integer.parseInt(code.substring(index, index + n + 1), 2);
        index += n + 1;
      }
    }
    return codes;
  }
  
}
