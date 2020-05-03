import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JPEGLS {
  
  private ArrayList<Scheme> schemes;
  private Pixel[][] bitmap;
  
  public JPEGLS(byte[] input) {
    initSchemes();
  
    int width = input[13] * 256 + input[12] & 0xFF;
    int height = input[15] * 256 + input[14] & 0xFF;
    parseBitmap(Arrays.copyOfRange(input, 18, input.length - 26), width, height);
  
  }
  
  private static double log2(double x) {
    return Math.log(x) / Math.log(2);
  }
  
  public void printEncodingStats() {
    ArrayList<Color> colors = new ArrayList<>();
    colors.add(Color.ALL);
    colors.add(Color.RED);
    colors.add(Color.GREEN);
    colors.add(Color.BLUE);
    
    for (Color color :
        colors) {
      int bestScheme = findBestSchemeID(color) + 1;
      System.out.println("\nNajlepszy schemat (kolor: " + color.name() + "):\t" + bestScheme + "\n\n");
    }
  }
  
  public int findBestSchemeID(Color color) {
    System.out.println("=================== ENTROPIA WG SCHEMATU (kolor: " + color.name() + ") ===================");
    double bestEntropy = Double.MAX_VALUE;
    int bestSchemeID = -1;
    for (Scheme scheme :
        schemes) {
      int id = schemes.indexOf(scheme);
      Pixel[][] encoded = encode(id);
      double entropy = getEntropy(encoded, color);
      if (entropy < bestEntropy) {
        bestEntropy = entropy;
        bestSchemeID = id;
      }
      System.out.println((id + 1) + ":\t" + entropy);
    }
    return bestSchemeID;
  }
  
  public Pixel[][] encode(int schemeID) {
    Scheme scheme = schemes.get(schemeID);
    Pixel[][] result = new Pixel[bitmap.length][bitmap[0].length];
    for (int i = 0; i < bitmap.length; i++) {
      for (int j = 0; j < bitmap[0].length; j++) {
        Pixel n = i == 0 ? new Pixel() : bitmap[i - 1][j];
        Pixel w = j == 0 ? new Pixel() : bitmap[i][j - 1];
        Pixel nw = i == 0 || j == 0 ? new Pixel() : bitmap[i - 1][j - 1];
        result[i][j] = bitmap[i][j].sub(scheme.count(n, w, nw)).mod(256);
      }
    }
    return result;
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
    bitmap = new Pixel[height][width];
    for (int i = 0, k = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        bitmap[i][j] = new Pixel(bitmapBytes[k * 3 + 2], bitmapBytes[k * 3 + 1], bitmapBytes[k * 3]);
        k++;
      }
    }
  }
  
  public void printBitmapEntropyInfo() {
    System.out.println("=================== ENTROPIA (wg koloru) ===================");
    System.out.println("wszystkie:\t" + getEntropy(bitmap, Color.ALL));
    System.out.println("czerwony:\t" + getEntropy(bitmap, Color.RED));
    System.out.println("zielony:\t" + getEntropy(bitmap, Color.GREEN));
    System.out.println("niebieski:\t" + getEntropy(bitmap, Color.BLUE));
    System.out.println("\n");
  }
  
  private double getEntropy(Pixel[][] bitmap, Color color) {
    HashMap<Integer, Integer> freq = new HashMap<>();
    int size = 0;
    for (Pixel[] row :
        bitmap) {
      for (Pixel pixel :
          row) {
        if (color == Color.ALL || color == Color.RED) {
          freq.put(pixel.red, freq.getOrDefault(pixel.red, 0) + 1);
          size++;
        }
        if (color == Color.ALL || color == Color.GREEN) {
          freq.put(pixel.green, freq.getOrDefault(pixel.green, 0) + 1);
          size++;
        }
        if (color == Color.ALL || color == Color.BLUE) {
          freq.put(pixel.blue, freq.getOrDefault(pixel.blue, 0) + 1);
          size++;
        }
      }
    }
    return countEntropy(freq, size);
  }
  
  private double countEntropy(HashMap<Integer, Integer> freq, int size) {
    double sizeLog = log2(size);
    final double[] entropy = {0};
    freq.forEach((color, occur) -> entropy[0] += (sizeLog - log2(occur)) * occur);
    return entropy[0] / size;
  }
  
  private enum Color {
    RED,
    GREEN,
    BLUE,
    ALL
  }
  
  private interface Scheme {
    Pixel count(Pixel n, Pixel w, Pixel nw);
  }
}
