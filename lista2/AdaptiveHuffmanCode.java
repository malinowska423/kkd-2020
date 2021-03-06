import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class AdaptiveHuffmanCode {
  private final HuffmanNode nyt;
  private final ArrayList<HuffmanNode> nodes;
  private final HuffmanNode[] seen;
  private HuffmanNode root;
  
  public AdaptiveHuffmanCode() {
    nyt = HuffmanNode.NYT;
    root = nyt;
    nodes = new ArrayList<>();
    seen = new HuffmanNode[256];
  }
  
  public static double entropy(String content) {
    Map<Character, Integer> symOccur = new Hashtable<>();
    
    for (char c : content.toCharArray()) {
      symOccur.put(c, symOccur.get(c) != null ? symOccur.get(c) + 1 : 1);
    }
    
    final int[] result = {0};
    symOccur.forEach((character, integer) -> result[0] += integer * log2(integer));
    
    double size = content.length();
    
    return log2(size) - (result[0] / size);
  }
  
  private static double log2(double x) {
    return Math.log(x) / Math.log(2);
  }
  
  public byte[] encode(byte[] content) {
    StringBuilder result = new StringBuilder();
    for (byte symbol : content) {
      if (seen[symbol] != null) {
        result.append(getSymbolCode(symbol, root, ""));
      } else {
        result.append(getSymbolCode(HuffmanNode.NYT.getSymbol(), root, ""));
        result.append(String.format("%8s", Integer.toBinaryString(symbol)).replaceAll(" ", "0"));
      }
      insert(symbol);
    }
    return getBytesFromBinaryString(result.toString());
  }
  
  public byte[] decode(byte[] content) {
    String stringContent = getBinaryStringFromBytes(content);
    StringBuilder result = new StringBuilder();
    byte symbol = Byte.parseByte(stringContent.substring(0, 8), 2);
    result.append((char) symbol);
    insert(symbol);
    
    var node = root;
    int i = 8;
    while (i < stringContent.length()) {
      node = stringContent.charAt(i) == '0' ? node.getLeft() : node.getRight();
      symbol = node.getSymbol();
      
      if (symbol != (byte) 0) {
        if (node == HuffmanNode.NYT) {
          try {
            symbol = Byte.parseByte(stringContent.substring(i + 1, i + 9), 2);
          } catch (StringIndexOutOfBoundsException e) {
            symbol = Byte.parseByte(stringContent.substring(i + 1), 2);
          }
          i += 8;
        }
        result.append((char) symbol);
        insert(symbol);
        node = root;
      }
      i++;
    }
    return result.toString().getBytes();
  }
  
  private String getSymbolCode(byte symbol, HuffmanNode node, String code) {
    if (node.getLeft() == null && node.getRight() == null) {
      return node.getSymbol() == symbol ? code : "";
    } else {
      String temp = "";
      if (node.getLeft() != null) {
        temp = getSymbolCode(symbol, node.getLeft(), code + "0");
      }
      
      if (temp.isEmpty() && node.getRight() != null) {
        temp = getSymbolCode(symbol, node.getRight(), code + "1");
      }
      return temp;
    }
  }
  
  private HuffmanNode findNodeOfWeight(int weight) {
    Collections.reverse(nodes);
    for (HuffmanNode node : nodes) {
      if (node.getWeight() == weight) {
        Collections.reverse(nodes);
        return node;
      }
    }
    return null;
  }
  
  private void swapNodes(HuffmanNode a, HuffmanNode b) {
    Collections.swap(nodes, nodes.lastIndexOf(a), nodes.indexOf(b));
    HuffmanNode aParent = a.getParent();
    a.setParent(b.getParent());
    b.setParent(aParent);
    
    
    if (a.getParent().getLeft() == b) {
      a.getParent().setLeft(a);
    } else {
      a.getParent().setRight(a);
    }
    
    if (b.getParent().getLeft() == a) {
      b.getParent().setLeft(b);
    } else {
      b.getParent().setRight(b);
    }
  }
  
  private void insert(byte symbol) {
    HuffmanNode node = seen[symbol];
    if (node == null) {
      var spawn = new HuffmanNode(symbol, 1);
      var internal = new HuffmanNode((byte) 0, 1, this.nyt.getParent(), this.nyt, spawn);
      
      spawn.setParent(internal);
      nyt.setParent(internal);
      
      if (internal.getParent() != null) {
        internal.getParent().setLeft(internal);
      } else {
        root = internal;
      }
      
      nodes.add(0, internal);
      nodes.add(0, spawn);
      
      seen[symbol] = spawn;
      node = internal.getParent();
    }
    
    while (node != null) {
      var largest = this.findNodeOfWeight(node.getWeight());
      if (node != largest && largest != null && node != largest.getParent() && largest != node.getParent()) {
        swapNodes(node, largest);
      }
      node.setWeight(node.getWeight() + 1);
      node = node.getParent();
    }
    
  }
  
  private byte[] getBytesFromBinaryString(String s) {
    int size = (s.length() / 8) + (s.length() % 8 > 0 ? 1 : 0);
    byte[] bytes = new byte[size];
    for (int i = 0, k = 0; i < size; i++, k += 8) {
      try {
        bytes[i] = (byte) Short.parseShort(s.substring(k, k + 8), 2);
      } catch (StringIndexOutOfBoundsException e) {
        bytes[i] = (byte) Short.parseShort(s.substring(k), 2);
      }
    }
    return bytes;
  }
  
  private String getBinaryStringFromBytes(byte[] bytes) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < bytes.length - 1; i++) {
      s.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
    }
    s.append(Integer.toBinaryString(bytes[bytes.length - 1] & 0xFF));
    return s.toString();
  }
}
