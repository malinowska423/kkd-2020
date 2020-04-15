import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    try(InputStream stream = Files.newInputStream(Paths.get("test.txt"))) {
      byte[] content = stream.readAllBytes();
      stream.close();
      var coder = new AdaptiveHuffmanCode();
      var result = coder.encode(content);
      OutputStream out = Files.newOutputStream(Paths.get("test.out"));
      out.write(result);
      out.close();
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
