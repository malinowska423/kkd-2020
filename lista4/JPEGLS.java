import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JPEGLS {
  
  private ArrayList<Scheme> schemes;
  private ArrayList<ArrayList<Pixel>> bitmap;
  
  public JPEGLS(byte[] input) {
    initSchemes();
  
    int width = input[13] * 256 + input[12] & 0xFF;
    int height = input[15] * 256 + input[14] & 0xFF;
//    System.out.println("height = " + height);
//    System.out.println("width = " + width);
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
      ArrayList<ArrayList<Pixel>> encoded = encode(id);
      double entropy = getEntropy(encoded, color);
      if (entropy < bestEntropy) {
        bestEntropy = entropy;
        bestSchemeID = id;
      }
      System.out.println((id + 1) + ":\t" + entropy);
    }
    return bestSchemeID;
  }
  
  public ArrayList<ArrayList<Pixel>> encode(int schemeID) {
    Scheme scheme = schemes.get(schemeID);
    ArrayList<ArrayList<Pixel>> result = new ArrayList<>();
    for (int i = 0; i < bitmap.size(); i++) {
      ArrayList<Pixel> encodedRow = new ArrayList<>();
      for (int j = 0; j < bitmap.get(i).size(); j++) {
        Pixel n = i == 0 ? new Pixel(0, 0, 0) : bitmap.get(i - 1).get(j);
        Pixel w = j == 0 ? new Pixel(0, 0, 0) : bitmap.get(i).get(j - 1);
        Pixel nw = i == 0 || j == 0 ? new Pixel(0, 0, 0) : bitmap.get(i - 1).get(j - 1);
//        if (i == 0 && j < 10) {
//          System.out.print("n = " + n);
//          System.out.print("w = " + w);
//          System.out.println("nw = " + nw);
//        }
        encodedRow.add(bitmap.get(i).get(j).sub(scheme.count(n, w, nw)).mod(256));
      }
      result.add(encodedRow);
//      if (i == 0) System.out.println(encodedRow);
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        System.out.println(result.get(i).get(j));
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
    bitmap = new ArrayList<>();
    ArrayList<Pixel> row = new ArrayList<>();
    for (int i = 0; i < width * height; i++) {
      Pixel pixel = new Pixel(bitmapBytes[i * 3 + 2], bitmapBytes[i * 3 + 1], bitmapBytes[i * 3]);
      row.add(pixel);
      if (width == row.size()) {
        bitmap.add(0, row);
        row = new ArrayList<>();
      }
    }
  }
  
  public void printEntropyInfo() {
    System.out.println("=================== ENTROPIA (wg koloru) ===================");
    System.out.println("wszystkie:\t" + getEntropy(bitmap, Color.ALL));
    System.out.println("czerwony:\t" + getEntropy(bitmap, Color.RED));
    System.out.println("zielony:\t" + getEntropy(bitmap, Color.GREEN));
    System.out.println("niebieski:\t" + getEntropy(bitmap, Color.BLUE));
    System.out.println("\n");
  }
  
  private double getEntropy(ArrayList<ArrayList<Pixel>> bitmap, Color color) {
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
