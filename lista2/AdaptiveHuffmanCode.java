import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class AdaptiveHuffmanCode {
  private final HuffmanNode nyt;
  private HuffmanNode root;
  private final ArrayList<HuffmanNode> nodes;
  private final HuffmanNode[] seen;
  
  public AdaptiveHuffmanCode() {
    nyt = HuffmanNode.NYT;
    root = nyt;
    nodes = new ArrayList<>();
    seen = new HuffmanNode[256];
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
    return result.toString().getBytes();
  }
  
  public byte[] decode(String content) {
//    System.out.println(content);
//    System.out.println("TRUE NYT " + HuffmanNode.NYT);
//    System.out.println("Rooot " + root);
    var symbol = getASCIIChar(content.substring(0, 8));
    StringBuilder result = new StringBuilder("" + symbol);
    insert((byte) symbol);
//    System.out.println("Rooot " + root);
  
    var node = root;
    System.out.println(result);
    for (int i = 8; i < content.length(); i++) {
//      System.out.println("nodek " + node);
//      System.out.println(content.charAt(i));
      node = content.charAt(i) == '0' ? node.getLeft() : node.getRight();
//      System.out.println("nodjsjdhjses " + nodes);
//      System.out.println(node);
      symbol = (char) node.getSymbol();
      System.out.println("symbool " + symbol);
      
      
      if ((byte) symbol > 0 || node == HuffmanNode.NYT) {
        if (node == HuffmanNode.NYT) {
          symbol = getASCIIChar(content.substring(i +1 , i + 9));
          System.out.println("NYT CHAR " + symbol);
          i += 7;
        }
        result.append(symbol);
        insert((byte) symbol);
        node = root;
//        System.out.println("new node " + node);
      }
      System.out.println(result);
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
  
  private char getASCIIChar(String binaryString) {
    return (char) Integer.parseInt(binaryString, 2);
  }
  
  public static double entropy(String content) {
    Map<Character, Integer> char_occurences = new Hashtable<>();
    
    for (char c : content.toCharArray()) {
      char_occurences.put(c, char_occurences.get(c) != null ? char_occurences.get(c) + 1 : 1);
    }
  
    final int[] result = {0};
    char_occurences.forEach((character, integer) -> result[0] += integer * log2(integer));
  
    double size = content.length();
  
    return log2(size) - (result[0] / size);
  }
  
  
  private static double log2(double x) {
    return Math.log(x) / Math.log(2);
  }
  
}
