public class HuffmanNode {
  public static HuffmanNode NYT = new HuffmanNode((byte) 0);
  private final byte symbol;
  private int weight;
  private HuffmanNode parent;
  private HuffmanNode left;
  private HuffmanNode right;
  
  public HuffmanNode(byte symbol, int weight, HuffmanNode parent, HuffmanNode left, HuffmanNode right) {
    this.symbol = symbol;
    this.weight = weight;
    this.parent = parent;
    this.left = left;
    this.right = right;
  }
  
  public HuffmanNode(byte symbol) {
    this(symbol, 0, null, null, null);
  }
  
  public HuffmanNode(byte symbol, int weight) {
    this(symbol, weight, null, null, null);
  }
  
  public HuffmanNode getParent() {
    return parent;
  }
  
  public void setParent(HuffmanNode parent) {
    this.parent = parent;
  }
  
  public HuffmanNode getLeft() {
    return left;
  }
  
  public void setLeft(HuffmanNode left) {
    this.left = left;
  }
  
  public HuffmanNode getRight() {
    return right;
  }
  
  public void setRight(HuffmanNode right) {
    this.right = right;
  }
  
  public int getWeight() {
    return weight;
  }
  
  public void setWeight(int weight) {
    this.weight = weight;
  }
  
  public byte getSymbol() {
    return symbol;
  }
  
  @Override
  public String toString() {
    return "{" + symbol + ", " + (parent == null ? "null" : parent.symbol) + "}";
  }
}
