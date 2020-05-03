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
  
  @Override
  public String toString() {
    return "{" + red + ", " + green + ", " + blue + "}";
  }
}
