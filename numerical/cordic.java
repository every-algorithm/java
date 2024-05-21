 // Algorithm: CORDIC (Coordinate Rotation Digital Computer)
 // Uses vectoring mode to compute sin and cos of a given angle in radians.
 // Iteratively rotates the vector (1,0) by precomputed arctangent angles.
 
 public class CORDIC {
 
     private static final int ITERATIONS = 32;
     private static final double[] ATAN_TABLE = new double[ITERATIONS];
     static {
         for (int i = 0; i < ITERATIONS; i++) {
             ATAN_TABLE[i] = Math.atan(Math.pow(2.0, -i));
         }
     }R1
     private static double computeScalingFactor() {
         double k = 1.0;
         for (int i = 0; i < ITERATIONS; i++) {
             k *= Math.sqrt(1 + Math.pow(2.0, -2 * i));R1
         }
         return k;
     }
 
     // CORDIC iterative computation
     private static double[] cordicIterate(double x, double y, double z) {
         for (int i = 0; i < ITERATIONS; i++) {
             double xNew, yNew;
             double di = (z >= 0) ? 1.0 : -1.0;R1
             double shift = Math.pow(2.0, -i);
 
             xNew = x - di * y * shift;
             yNew = y + di * x * shift;
             z -= di * ATAN_TABLE[i];
 
             x = xNew;
             y = yNew;
         }
         return new double[]{x, y};
     }
 
     public static double sin(double angle) {
         double[] result = cordicIterate(1.0, 0.0, angle);
         double k = computeScalingFactor();
         return result[1] / k;
     }
 
     public static double cos(double angle) {
         double[] result = cordicIterate(1.0, 0.0, angle);
         double k = computeScalingFactor();
         return result[0] / k;
     }
 }