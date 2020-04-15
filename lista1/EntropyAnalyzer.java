import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EntropyAnalyzer {
  private final String filename;
  private final Map<Byte, Integer> symbolOccur;
  private final Map<String, Integer> symbolPairsOccur;
  private final int size;
  private double entropy;
  private double condEntropy;
  
  public EntropyAnalyzer(String filename) throws IOException {
    this.filename = filename;
    this.symbolOccur = new Hashtable<>();
    this.symbolPairsOccur = new Hashtable<>();
    this.entropy = 0;
    this.condEntropy = 0;
    
    DataInputStream reader = new DataInputStream(new FileInputStream(this.filename));
    this.size = reader.available();
    if (this.size == 0) {
      throw new IOException("Plik jest pusty");
    }
    byte[] content = new byte[this.size];
    if(reader.read(content) == -1) {
      throw new IOException("Plik jest pusty");
    }
    symbolOccur.put(content[0], 1);
    symbolPairsOccur.put(makePair((byte) 0, content[0]), 1);
    for (int i = 1; i < content.length; i++) {
      byte key = content[i];
      symbolOccur.put(key, symbolOccur.get(key) != null ? symbolOccur.get(key) + 1 : 1);
      String pair = makePair(content[i - 1], content[i]);
      symbolPairsOccur.put(pair, symbolPairsOccur.get(pair) != null ? symbolPairsOccur.get(pair) + 1 : 1);
    }
    this.countEntropy();
  }
  
  private void countEntropy() {
    double sizeLog = log2(size);
    
    symbolOccur.forEach((symbol, occur) -> {
      double x = log2(occur);
      entropy += (sizeLog - x) * occur;
      symbolPairsOccur.forEach((pair, pairOccur) -> {
        if (pair.split(" ")[0].equals(symbol.toString())) {
          condEntropy += pairOccur * (x - log2(pairOccur));
        }
      });
    });
    
    entropy /= size;
    condEntropy /= size;
  }
  
  private double log2(double x) {
    return Math.log(x) / Math.log(2);
  }
  
  private String makePair(byte prev, byte sym) {
    return prev + " " + sym;
  }
  
  public void printFileEntropyInfo() {
    System.out.println("=========== " + filename + " (" + size + " B) ===========");
    System.out.println("Entropia = " + entropy);
    System.out.println("Entropia warunkowa = " + condEntropy + "\n");
  }
  
}
