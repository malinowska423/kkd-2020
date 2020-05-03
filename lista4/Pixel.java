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
    this.red %= number;
    this.green %= number;
    this.blue %= number;
    return this;
  }
}
