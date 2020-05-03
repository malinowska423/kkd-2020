public class Pixel {
  int red;
  int green;
  int blue;
  
  public Pixel(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
  
  Pixel add(Pixel other) {
    this.red += other.red;
    this.green += other.green;
    this.blue += other.blue;
    return this;
  }
  
  Pixel sub(Pixel other) {
    this.red -= other.red;
    this.green -= other.green;
    this.blue -= other.blue;
    return this;
  }
  
  Pixel div(int number) {
    this.red /= number;
    this.green /= number;
    this.blue /= number;
    return this;
  }
  
  Pixel mod(int number) {
    this.red = (this.red + number) % number;
    this.green = (this.green + number) % number;
    this.blue = (this.blue + number) % number;
    return this;
  }
  
  @Override
  public String toString() {
    return "{" + red + ", " + green + ", " + blue + "}";
  }
}
