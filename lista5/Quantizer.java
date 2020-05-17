import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class Quantizer {
  private final TGAAnalyzer tga;
  private final Pixel[] codebook;
  private final Pixel[][] bitmap;
  
  public Quantizer(byte[] tgaImage, int colors) {
    tga = new TGAAnalyzer(tgaImage);
    codebook = generateCodeBook(colors);
    bitmap = new Pixel[tga.height][tga.width];
  }
  
  public byte[] encode() {
    for (int i = 0; i < tga.height; i++) {
      for (int j = 0; j < tga.width; j++) {
        Double[] diffs = new Double[codebook.length];
        for (int k = 0; k < codebook.length; k++) {
          diffs[k] = euclidSquared(getPixelAsDoubleArray(tga.getBitmap()[i][j]), getPixelAsDoubleArray(codebook[k]));
        }
        bitmap[i][j] = codebook[Arrays.asList(diffs).indexOf(Collections.min(Arrays.asList(diffs)))];
      }
    }
//    for (int i = 0; i < 2; i++) {
//      for (int j = 0; j < 5; j++) {
//        System.out.print(bitmap[i][j] + " ");
//      }
//      System.out.println();
//    }
    return tga.getTGABytes(tga.getBytesFromBitmap(bitmap));
  }
  
  public double mse() {
    return 0;
  }
  
  public double snr(double MSE) {
    return 0;
  }
  
  private Pixel[] generateCodeBook(int codebookSize) {
    double epsilon = 0.00001;
    ArrayList<Double[]> codebook = new ArrayList<>();
    ArrayList<Double[]> data = castBitmapToVectors(tga.getBitmap());
    Double[] c0 = averageVectorOfVectors(data);
    codebook.add(c0);
    
    
    double averageDistortion = averageDistortionC0(c0, data, data.size());
    
    while (codebook.size() < codebookSize) {
      Pair<ArrayList<Double[]>, Double> result = splitCodebook(data, codebook, epsilon, averageDistortion);
      codebook = result.first;
      averageDistortion = result.second;
//      System.out.println("averageDistortion = " + averageDistortion);
//      System.out.println("codebook = " + codebook);
    }
    return castCodebook(codebook);
  }
  
  private ArrayList<Double[]> castBitmapToVectors(Pixel[][] bitmap) {
    ArrayList<Double[]> vectors = new ArrayList<>();
    for (int i = 0; i < tga.height; i++) {
      for (int j = 0; j < tga.width; j++) {
        Pixel pixel = bitmap[i][j];
        vectors.add(new Double[]{((double) pixel.red), ((double) pixel.green), ((double) pixel.blue)});
      }
    }
    return vectors;
  }
  
  private Pixel[] castCodebook(ArrayList<Double[]> vectors) {
    Pixel[] codebook = new Pixel[vectors.size()];
    for (int i = 0; i < vectors.size(); i++) {
      codebook[i] = new Pixel(vectors.get(i)[0].intValue(), vectors.get(i)[1].intValue(), vectors.get(i)[2].intValue());
    }
    return codebook;
  }
  
  private Double[] averageVectorOfVectors(ArrayList<Double[]> vectors) {
    int size = vectors.size();
    Double[] averageVector = new Double[]{0.0, 0.0, 0.0};
    for (Double[] vector : vectors) {
      for (int i = 0; i < 3; i++) {
        averageVector[i] += vector[i] / size;
      }
    }
    return averageVector;
  }
  
  private double averageDistortionC0(Double[] c0, ArrayList<Double[]> vectors, int size) {
    ArrayList<Double> vecs = new ArrayList<>();
    vecs.add(0.0);
    for (Double[] vector :
        vectors) {
      vecs.add(euclidSquared(c0, vector));
    }
    return vecs.stream().reduce((s, d) -> s + d / size).get();
  }
  
  
  private double euclidSquared(Double[] a, Double[] b) {
    double sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum += Math.pow((a[i] - b[i]), 2);
    }
    return sum;
  }
  
  
  private Pair<ArrayList<Double[]>, Double> splitCodebook(ArrayList<Double[]> data, ArrayList<Double[]> codebook, double epsilon, double initialAvgDist) {
    int dataSize = data.size();
    ArrayList<Double[]> new_codevectors = new ArrayList<>();
    for (Double[] c : codebook) {
      new_codevectors.add(new_codevector(c, epsilon));
      new_codevectors.add(new_codevector(c, -epsilon));
    }
    codebook = new_codevectors;
    System.out.println("codevectors");
    codebook.forEach(doubles -> System.out.println(Arrays.toString(doubles)));
    int len_codebook = codebook.size();
    
    double avg_dist = 0.0;
    double err = epsilon + 1;
    while (err > epsilon) {
      ArrayList<Double[]> closest_c_list = new ArrayList<>(dataSize);
      for (int i = 0; i < dataSize; i++) {
        closest_c_list.add(null);
      }
      HashMap<Integer, ArrayList<Double[]>> vecs_near_c = new HashMap<>();
      HashMap<Integer, ArrayList<Integer>> vecs_idxs_near_c = new HashMap<>();
      for (int i = 0; i < data.size(); i++) {
        double min_dist = -1;
        int closest_c_index = -1;
        for (int j = 0; j < codebook.size(); j++) {
          double d = euclidSquared(data.get(i), codebook.get(j));
          if (j == 0 || d < min_dist) {
            min_dist = d;
            closest_c_list.set(i, codebook.get(j));
            closest_c_index = j;
          }
        }
        vecs_near_c.getOrDefault(closest_c_index, new ArrayList<>()).add(data.get(i));
        vecs_idxs_near_c.getOrDefault(closest_c_index, new ArrayList<>()).add(i);
        if (i == 0) {
          System.out.println("vecs_idxs_near_c = " + vecs_idxs_near_c);
        }
      }
      System.out.println("vecs_idxs_near_c = " + vecs_idxs_near_c);
      
      for (int i = 0; i < len_codebook; i++) {
        ArrayList<Double[]> vecs = vecs_near_c.getOrDefault(i, new ArrayList<>());
        int num_vecs_near_c = vecs.size();
        if (num_vecs_near_c > 0) {
          Double[] new_c = averageVectorOfVectors(vecs);
          codebook.set(i, new_c);
          vecs_idxs_near_c.get(i).forEach(integer -> closest_c_list.set(integer, new_c));
        }
      }
      
      double prev_avg_dist = avg_dist > 0.0 ? avg_dist : initialAvgDist;
      avg_dist = averageDistortionCList(closest_c_list, data, dataSize);
      
      err = (prev_avg_dist - avg_dist) / prev_avg_dist;
    }
    return new Pair<>(codebook, avg_dist);
  }
  
  private Double[] new_codevector(Double[] vector, double epsilon) {
    Double[] newCV = new Double[3];
    for (int i = 0; i < vector.length; i++) {
      newCV[i] = vector[i] * (1.0 + epsilon);
    }
    return newCV;
  }
  
  private double averageDistortionCList(ArrayList<Double[]> c_list, ArrayList<Double[]> vectors, int size) {
    ArrayList<Double> vecs = new ArrayList<>();
    vecs.add(0.0);
    for (int i = 0; i < c_list.size(); i++) {
      vecs.add(euclidSquared(c_list.get(i), vectors.get(i)));
    }
    return vecs.stream().reduce((s, d) -> s + d / size).get();
  }
  
  
  private Double[] getPixelAsDoubleArray(Pixel a) {
    Double[] x = new Double[3];
    x[0] = (double) a.red;
    x[1] = ((double) a.green);
    x[2] = ((double) a.blue);
    return x;
  }
  
  class Pair<U, V> {
    U first;
    V second;
    
    public Pair(U first, V second) {
      this.first = first;
      this.second = second;
    }
  }
  
  
}
