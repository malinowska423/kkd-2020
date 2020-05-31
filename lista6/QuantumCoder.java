import java.util.ArrayList;
import java.util.Arrays;

public class QuantumCoder {
  public Pixel[][] filteredLow;
  public Pixel[][] filteredHigh;
  public byte[] bytes;
  public Pixel[][] quantified;
  private TGAAnalyzer tga;
  private int k;
  
  public QuantumCoder(TGAAnalyzer tga, int k) {
    this.tga = tga;
    this.k = k;
    this.filteredLow = null;
    this.filteredHigh = null;
    this.bytes = null;
    this.quantified = null;
  }
  
  public static byte[] differentialCoding(byte[] bytes) {
    byte[] result = new byte[bytes.length];
    result[0] = bytes[0];
    for (int i = 1; i < bytes.length; i++) {
      result[i] = (byte) (bytes[i] - bytes[i - 1]);
    }
    return result;
  }
  
  public static ArrayList<Integer> differentialDecoding(ArrayList<Integer> diffs) {
    ArrayList<Integer> result = new ArrayList<>();
    int a = diffs.get(0);
    result.add(a);
    for (int i = 1; i < diffs.size(); i++) {
      a += diffs.get(i);
      result.add(a);
    }
    return result;
  }
  
  public void encode() {
    filteredLow = new Pixel[tga.height][tga.width];
    filteredHigh = new Pixel[tga.height][tga.width];
    for (int i = 0; i < tga.height; i++) {
      for (int j = 0; j < tga.width; j++) {
        filteredLow[i][j] = filters(i, j, false);
        filteredHigh[i][j] = filters(i, j, true);
      }
    }
    
    byte[] byteArray = differentialCoding(TGAAnalyzer.pixelsToBytes(filteredLow));
    
    for (int i = 0; i < byteArray.length; i++) {
      byte x = byteArray[i];
      byteArray[i] = (byte) (x > 0 ? 2 * x : Math.abs(x) * 2 + 1);
    }
    
    StringBuilder bitStringBuilder = new StringBuilder();
    for (byte x : byteArray) {
      bitStringBuilder.append(EliasGamma.encode(x));
    }
    
    String bitString = bitStringBuilder.toString();
    
    if (bitString.length() % 8 != 0) {
      bitString += "0".repeat(8 - (bitString.length() % 8));
    }
    
    bytes = bitStringToBytes(bitString);
    quantified = quantify(filteredHigh);
  }
  
  public byte[] decode() {
    String content = getBinaryStringFromBytes(tga.bitmapBytes);
    System.out.println("content = " + content.substring(0, 70));
    ArrayList<Integer> codes = EliasGamma.decode(content);
    codes.forEach(x -> x = x % 2 == 0 ? x / 2 : -(x / 2));
    ArrayList<Integer> decoded = differentialDecoding(codes);
    
    byte[] bytes = new byte[decoded.size()];
    for (int i = 0; i < decoded.size(); i++) {
      bytes[i] = decoded.get(i).byteValue();
    }
    
    return tga.getTGABytes(bytes);
  }
  
  private Pixel filters(int x, int y, boolean high) {
    int[][] weightsLow = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    int[][] weightsHigh = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    
    int[][] weights = high ? weightsHigh : weightsLow;
    
    Pixel pixel = new Pixel();
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        pixel = pixel.add(tga.getBitmapItem(x + i, y + j).mul(weights[i + 1][j + 1]));
      }
    }
    
    int weightsSum = 0;
    for (int[] row : weights) {
      for (int weight : row) {
        weightsSum += weight;
      }
    }
    weightsSum = weightsSum < 0 ? 1 : weightsSum;
    
    pixel = pixel.div(weightsSum);
    
    pixel = pixel.max(0);
    pixel = pixel.min(255);
    
    return pixel;
  }
  
  private Pixel[][] quantify(Pixel[][] pixels) {
    Pixel[][] quantifiedPixels = new Pixel[pixels.length][pixels[0].length];
    int step = (int) (256 / Math.pow(2, k));
    for (int i = 0; i < pixels.length; i++) {
      for (int j = 0; j < pixels[0].length; j++) {
        quantifiedPixels[i][j] = pixels[i][j].quantization(step);
      }
    }
    return quantifiedPixels;
  }
  
  byte[] bitStringToBytes(String bitString) {
    byte[] bytes = new byte[bitString.length() / 8];
    for (int i = 0; i < bitString.length(); i += 8) {
      int temp = Integer.parseInt(bitString.substring(i, i + 8), 2);
      bytes[i / 8] = (byte) temp;
    }
    return bytes;
  }
  
  public void stats(String statsTitle, Pixel[][] changed) {
    System.out.println(statsTitle);
    System.out.println("Blad sredniokwadaratowy ");
    double mse = mse(changed);
    System.out.println("\t(ogolny)\t" + mse);
    System.out.println("\t(czerwony)\t" + mse(changed, 0));
    System.out.println("\t(zielony)\t" + mse(changed, 1));
    System.out.println("\t(niebieski)\t" + mse(changed, 2));
    System.out.println("Stosunek sygnalu do szumu: " + snr(mse));
    System.out.println("\n");
  }
  
  public double mse(Pixel[][] changed) {
    double sum = 0;
    for (int i = 0; i < tga.height; i++) {
      for (int j = 0; j < tga.width; j++) {
        sum += euclidSquared(getPixelAsDoubleArray(tga.getBitmap()[i][j]), getPixelAsDoubleArray(changed[i][j]));
      }
    }
    return sum / (tga.width * tga.height);
  }
  
  public double mse(Pixel[][] changed, int color) {
    
    double sum = 0;
    for (int i = 0; i < tga.height; i++) {
      for (int j = 0; j < tga.width; j++) {
        int a, b;
        switch (color) {
          case 0 -> {
            a = tga.getBitmap()[i][j].red;
            b = changed[i][j].red;
          }
          case 1 -> {
            a = tga.getBitmap()[i][j].green;
            b = changed[i][j].green;
          }
          case 2 -> {
            a = tga.getBitmap()[i][j].blue;
            b = changed[i][j].blue;
          }
          default -> {
            a = 0;
            b = 0;
          }
        }
        sum += Math.pow(a - b, 2);
      }
    }
    return sum / (tga.width * tga.height);
  }
  
  public double snr(double MSE) {
    double sum = 0;
    for (Pixel[] row :
        tga.getBitmap()) {
      for (Pixel pixel :
          row) {
        sum += Math.pow(pixel.red, 2) + Math.pow(pixel.green, 2) + Math.pow(pixel.blue, 2);
      }
    }
    return (sum / (tga.width * tga.height)) / MSE;
  }
  
  private double euclidSquared(Double[] a, Double[] b) {
    double sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum += Math.pow((a[i] - b[i]), 2);
    }
    return sum;
  }
  
  private Double[] getPixelAsDoubleArray(Pixel a) {
    Double[] x = new Double[3];
    x[0] = (double) a.red;
    x[1] = ((double) a.green);
    x[2] = ((double) a.blue);
    return x;
  }
  
  private String getBinaryStringFromBytes(byte[] bytes) {
    StringBuilder s = new StringBuilder();
    for (byte aByte : bytes) {
      s.append(String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0'));
    }
    return s.toString();
  }
}
