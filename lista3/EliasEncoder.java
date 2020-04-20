public class EliasEncoder implements Encoder {
  private CodingType codingType;
  
  public EliasEncoder(CodingType codingType) {
    this.codingType = codingType;
  }
  
  public EliasEncoder() {
    this(CodingType.ELIAS_OMEGA);
  }
  
  private String eliasOmega(int number) {
    StringBuilder code = new StringBuilder("0");
    int k = number;
    while (k > 1) {
      String binary = Integer.toBinaryString(k);
      code.insert(0, binary);
      k = binary.length() - 1;
    }
    return code.toString();
  }
  
  private String eliasGamma(int number) {
    String code = Integer.toBinaryString(number);
    code = String.format("%" + (code.length() + 1) + "s", code).replaceAll(" ", "0");
    return code;
  }
  
  private String eliasDelta(int number) {
    String code = Integer.toBinaryString(number).substring(1);
    code = eliasGamma(code.length() + 1) + code;
    return code;
  }
  
  @Override
  public String encode(int number) {
    switch (codingType) {
      case ELIAS_GAMMA -> {
        return eliasGamma(number);
      }
      case ELIAS_DELTA -> {
        return eliasDelta(number);
      }
      default -> {
        return eliasOmega(number);
      }
    }
  }
  
  @Override
  public CodingType getCodingType() {
    return codingType;
  }
}
