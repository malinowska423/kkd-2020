import java.util.ArrayList;
import java.util.Arrays;

public class TGAAnalyzer {
  private final byte[] header;
  private final byte[] footer;
  protected int width;
  protected int height;
  protected byte[] bitmapBytes;
  private Pixel[][] bitmap;
  
  public TGAAnalyzer(byte[] tgaImage) {
    header = Arrays.copyOfRange(tgaImage, 0, 18);
    footer = Arrays.copyOfRange(tgaImage, tgaImage.length - 26, tgaImage.length);
    
    width = tgaImage[13] * 256 + (tgaImage[12] & 0xFF);
    height = tgaImage[15] * 256 + (tgaImage[14] & 0xFF);
    bitmapBytes = Arrays.copyOfRange(tgaImage, 18, tgaImage.length - 26);
    parseBitmap(bitmapBytes, width, height);
    
  }
  
  public static byte[] pixelsToBytes(Pixel[][] pixels) {
    ArrayList<Byte> res = new ArrayList<>();
    for (Pixel[] row : pixels) {
      for (Pixel pixel : row) {
        res.add((byte) (pixel.blue));
        res.add((byte) (pixel.green));
        res.add((byte) (pixel.red));
      }
    }
    byte[] bytes = new byte[res.size()];
    for (int i = 0; i < res.size(); i++) {
      bytes[i] = res.get(i);
    }
    return bytes;
  }
  
  private void parseBitmap(byte[] bitmapBytes, int width, int height) {
    bitmap = new Pixel[height][width];
    for (int i = 0, k = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        bitmap[i][j] = new Pixel(bitmapBytes[k * 3 + 2] & 0xff, bitmapBytes[k * 3 + 1] & 0xff, bitmapBytes[k * 3] & 0xff);
        k++;
      }
    }
  }
  
  public Pixel[][] getBitmap() {
    return bitmap;
  }
  
  public Pixel getBitmapItem(int x, int y) {
    int x_res = x, y_res = y;
    if (x < 0) {
      x_res = 0;
    } else if (x >= height) {
      x_res = height - 1;
    }
    
    if (y < 0) {
      y_res = 0;
    } else if (y >= width) {
      y_res = width - 1;
    }
    
    return bitmap[x_res][y_res];
  }
  
  public byte[] getTGABytes(byte[] content) {
    return getTGABytes(header, content, footer);
  }
  
  public byte[] getTGABytes(byte[] header, byte[] content, byte[] footer) {
    int size = header.length + content.length + footer.length;
    byte[] result = new byte[size];
    int i = 0;
    for (int k = 0; k < header.length; k++, i++) {
      result[i] = header[k];
    }
    for (int k = 0; k < content.length; k++, i++) {
      result[i] = content[k];
    }
    for (int k = 0; k < footer.length; k++, i++) {
      result[i] = footer[k];
    }
    return result;
  }
}
