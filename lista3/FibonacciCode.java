import java.util.ArrayList;
import java.util.Collections;

public class FibonacciCode implements Encoder, Decoder {
  @Override
  public String encode(int number) {
    ArrayList<Integer> seq = getEncodeSequence(number);
    ArrayList<String> result = new ArrayList<>();
    seq.forEach(integer -> result.add("0"));
    while (number > 0) {
      int index = 0, x = 0;
      for (int i = 0; i < seq.size(); i++) {
        if (seq.get(i) <= number) {
          index = i;
          x = seq.get(i);
        }
      }
      result.set(index, "1");
      number %= x;
    }
    result.add("1");
    StringBuilder res = new StringBuilder();
    for (String s : result) {
      res.append(s);
    }
    return res.toString();
  }
  
  private ArrayList<Integer> getEncodeSequence(int n) {
    ArrayList<Integer> l = new ArrayList<>();
    int a = 0, b = 1;
    while (a <= n) {
      l.add(a);
      int temp = a;
      a = b;
      b = temp + a;
    }
    return new ArrayList<>(l.subList(2, l.size()));
  }
  
  
  private ArrayList<Integer> getDecodeSequence(int n) {
    ArrayList<Integer> l = new ArrayList<>();
    int a = 0, b = 1;
    for (int i = 0; i < n + 2; i++) {
      l.add(a);
      int temp = a;
      a = b;
      b = temp + a;
    }
    return new ArrayList<>(l.subList(2, l.size()));
  }
  
  
  @Override
  public ArrayList<Integer> decode(String code) {
    String[] splitted = code.split("11");
    ArrayList<String> codes = new ArrayList<>();
    int bound = code.endsWith("11") ? splitted.length : splitted.length - 1;
    for (int i = 0; i < bound; i++) {
      codes.add(splitted[i] + "1");
    }
    ArrayList<Integer> len = new ArrayList<>();
    for (String c : codes) {
      len.add(c.length());
    }
    ArrayList<Integer> seq = getDecodeSequence(Collections.max(len));
    ArrayList<Integer> sums = new ArrayList<>();
    for (String c : codes) {
      int sum = 0;
      for (int i = 0; i < c.length(); i++) {
        if (c.charAt(i) == '1') {
          sum += seq.get(i);
        }
      }
      sums.add(sum);
    }
    return sums;
  }
  
  @Override
  public CodingType getCodingType() {
    return CodingType.FIBONACCI;
  }
}
