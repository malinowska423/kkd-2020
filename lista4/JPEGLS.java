import java.util.ArrayList;
import java.util.HashMap;

public class JPEGLS {
  
  private ArrayList<Scheme> schemes;
  private ArrayList<ArrayList<Pixel>> bitmap;
  
  public JPEGLS(byte[] input) {
    initSchemes();
    
    int width = input[13] * 256 + input[12] & 0xFF;
    int height = input[15] * 256 + input[14] & 0xFF;
    parseBitmap(input, width, height);
    
  }
  
  private static double log2(double x) {
    return Math.log(x) / Math.log(2);
  }
  
  private void initSchemes() {
    schemes = new ArrayList<>();
    schemes.add((n, w, nw) -> w);
    schemes.add((n, w, nw) -> n);
    schemes.add((n, w, nw) -> nw);
    schemes.add((n, w, nw) -> n.add(w).sub(nw));
    schemes.add((n, w, nw) -> n.add(w.sub(nw)).div(2));
    schemes.add((n, w, nw) -> w.add(n.sub(nw)).div(2));
    schemes.add((n, w, nw) -> n.add(w).div(2));
    schemes.add((n, w, nw) -> {
      int[] _n = new int[]{n.red, n.green, n.blue};
      int[] _w = new int[]{w.red, w.green, w.blue};
      int[] _nw = new int[]{nw.red, nw.green, nw.blue};
      int[] rgb = new int[3];
      for (int i = 0; i < 3; i++) {
        int max = Math.max(_w[i], _n[i]);
        if (_nw[i] >= max) {
          rgb[i] = max;
        } else {
          int min = Math.min(_w[i], _n[i]);
          if (_nw[i] <= min) {
            rgb[i] = min;
          } else {
            rgb[i] = _w[i] + _n[i] - _nw[i];
          }
        }
      }
      return new Pixel(rgb[0], rgb[1], rgb[2]);
    });
  }
  
  private void parseBitmap(byte[] bitmapBytes, int width, int height) {
    bitmap = new ArrayList<>();
    ArrayList<Pixel> row = new ArrayList<>();
    for (int i = 0; i < width * height; i++) {
      row.add(new Pixel(bitmapBytes[i * 3 + 2] & 0xFF, bitmapBytes[i * 3 + 1] & 0xFF, bitmapBytes[i * 3] & 0xFF));
      if (width == row.size()) {
        bitmap.add(0, row);
        row = new ArrayList<>();
      }
    }
  }
  
  public void printEntropyInfo() {
    System.out.println("=================== ENTROPIA (wg koloru) ===================");
    System.out.println("wszystkie:\t" + getEntropy(Color.ALL));
    System.out.println("czerwony:\t" + getEntropy(Color.RED));
    System.out.println("zielony:\t" + getEntropy(Color.GREEN));
    System.out.println("niebieski:\t" + getEntropy(Color.BLUE));
    System.out.println("\n");
  }
  
  private double getEntropy(Color color) {
    HashMap<Integer, Integer> freq = new HashMap<>();
    int size = 0;
    for (ArrayList<Pixel> row :
        bitmap) {
      for (Pixel pixel :
          row) {
        if (color == Color.ALL || color == Color.RED) {
          int key = pixel.red;
          freq.put(key, freq.get(key) != null ? freq.get(key) + 1 : 1);
          size++;
        }
        if (color == Color.ALL || color == Color.GREEN) {
          int key = pixel.green;
          freq.put(key, freq.get(key) != null ? freq.get(key) + 1 : 1);
          size++;
        }
        if (color == Color.ALL || color == Color.BLUE) {
          int key = pixel.blue;
          freq.put(key, freq.get(key) != null ? freq.get(key) + 1 : 1);
          size++;
        }
      }
    }
    return countEntropy(freq, size);
  }
  
  private double countEntropy(HashMap<Integer, Integer> freq, int size) {
    double sizeLog = log2(size);
    final double[] entropy = {0};
    freq.forEach((color, occur) -> {
      entropy[0] += (sizeLog - log2(occur)) * occur;
    });
    return entropy[0] / size;
  }
  
  private enum Color {
    RED,
    GREEN,
    BLUE,
    ALL
  }
  
  
}
