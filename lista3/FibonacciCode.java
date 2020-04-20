import java.util.ArrayList;

public class FibonacciCode implements Encoder {
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
    return (ArrayList<Integer>) l.subList(2, l.size());
  }
  
  
  @Override
  public CodingType getCodingType() {
    return CodingType.FIBONACCI;
  }
}
