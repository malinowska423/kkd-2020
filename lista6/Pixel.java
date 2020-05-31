public class Pixel {
  int red;
  int green;
  int blue;
  
  public Pixel(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
  
  public Pixel() {
    this.red = 0;
    this.green = 0;
    this.blue = 0;
  }
  
  Pixel add(Pixel other) {
    return new Pixel(red + other.red, green + other.green, blue + other.blue);
  }
  
  Pixel sub(Pixel other) {
    return new Pixel(red - other.red, green - other.green, blue - other.blue);
  }
  
  Pixel div(int number) {
    return new Pixel(red / number, green / number, blue / number);
  }
  
  Pixel mod(int number) {
    return new Pixel((red + number) % number, (green + number) % number, (blue + number) % number);
  }
  
  Pixel mul(int number) {
    return new Pixel(red * number, green * number, blue * number);
  }
  
  Double[] divDouble(int number) {
    return new Double[]{0.0, 0.0, 0.0};
  }
  
  Pixel quantization(int step) {
    return this.div(step).mul(step);
  }
  
  Pixel min(int number) {
    return new Pixel(Math.min(red, number), Math.min(green, number), Math.min(blue, number));
  }
  
  Pixel max(int number) {
    return new Pixel(Math.max(red, number), Math.max(green, number), Math.max(blue, number));
  }
  
  @Override
  public String toString() {
    return "{" + red + ", " + green + ", " + blue + "}";
  }
}
