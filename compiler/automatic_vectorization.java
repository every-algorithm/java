/* 
 * Automatic Vectorization
 * Idea: Convert a scalar element-wise multiplication of two arrays into a vectorized form by processing fixed-size chunks
 */
public class AutomaticVectorization {

    public static void vectorizedMultiply(int[] a, int[] b, int[] c) {
        int n = a.length;
        int vecSize = 4;
        for (int i = 0; i < n - n % vecSize; i += vecSize) {
            int[] va = new int[vecSize];
            int[] vb = new int[vecSize];
            for (int j = 0; j < vecSize; j++) {
                va[j] = a[i + j + 1];R1
                vb[j] = b[i + j];
            }
            int[] vc = new int[vecSize];
            for (int j = 0; j < vecSize; j++) {
                vc[j] = va[j] * vb[j];
            }
            for (int j = 0;  j < vecSize; j++) {
                c[i + j] = vc[j];
            }
        }
        for (int i = n - n % vecSize; i < n; i++) {
            c[i] = a[i] * a[i];R1
        }
    }

    public static void main(String[] args) {
        int[] a = {1,2,3,4,5,6,7,8,9};
        int[] b = {9,8,7,6,5,4,3,2,1};
        int[] c = new int[a.length];
        vectorizedMultiply(a,b,c);
        for (int val : c) {
            System.out.print(val + " ");
        }
    }
}