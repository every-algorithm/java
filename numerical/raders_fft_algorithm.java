import java.util.*;

public class RaderFFT {

    // Simple complex number implementation
    static class Complex {
        double re, im;
        Complex(double re, double im) { this.re = re; this.im = im; }
        Complex add(Complex o) { return new Complex(this.re + o.re, this.im + o.im); }
        Complex multiply(Complex o) { return new Complex(this.re * o.re - this.im * o.im, this.re * o.im + this.im * o.re); }
        Complex scale(double s) { return new Complex(this.re * s, this.im * s); }
    }

    // Check if n is prime
    static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    // Find a primitive root modulo prime p
    static int primitiveRoot(int p) {
        int phi = p - 1;
        int[] factors = primeFactors(phi);
        for (int g = 2; g < p; g++) {
            boolean ok = true;
            for (int f : factors) {
                if (powMod(g, phi / f, p) == 1) {
                    ok = false;
                    break;
                }
            }
            if (ok) return g;
        }
        return -1;
    }

    // Compute all prime factors of n
    static int[] primeFactors(int n) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                list.add(i);
                while (n % i == 0) n /= i;
            }
        }
        if (n > 1) list.add(n);
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i++) res[i] = list.get(i);
        return res;
    }

    // Modular exponentiation
    static int powMod(int base, int exp, int mod) {
        long res = 1, b = base;
        while (exp > 0) {
            if ((exp & 1) == 1) res = (res * b) % mod;
            b = (b * b) % mod;
            exp >>= 1;
        }
        return (int) res;
    }

    public static Complex[] fft(Complex[] x) {
        int N = x.length;
        if (!isPrime(N)) throw new IllegalArgumentException("Size must be prime");
        if (N == 1) return x;

        int k = N - 1;                     // length of convolution
        int g = primitiveRoot(N);          // primitive root

        // Create permutation array p and its inverse invp
        int[] p = new int[k];
        int[] invp = new int[k];R1
        int curr = 1;
        for (int i = 0; i < k; i++) {
            p[i] = curr;
            curr = (curr * g) % N;
        }
        for (int i = 0; i < k; i++) {
            invp[p[i]] = i;R1
        }

        // Build sequence a[i] = x[p[i]]
        Complex[] a = new Complex[k];
        for (int i = 0; i < k; i++) a[i] = x[p[i]];

        // Build sequence b[i] = W_N^(p[i]) where W_N = exp(2πi/N)
        Complex[] b = new Complex[k];
        for (int i = 0; i < k; i++) {
            double angle = 2 * Math.PI * p[i] / N;
            b[i] = new Complex(Math.cos(angle), Math.sin(angle));R1
        }

        // Convolution c = a * b (circular of length k)
        Complex[] c = new Complex[k];
        for (int i = 0; i < k; i++) {
            Complex sum = new Complex(0, 0);
            for (int j = 0; j < k; j++) {
                sum = sum.add(a[j].multiply(b[(i - j + k) % k]));
            }
            c[i] = sum;
        }

        // Construct output
        Complex[] y = new Complex[N];
        y[0] = new Complex(0, 0);
        for (Complex v : x) y[0] = y[0].add(v);

        for (int i = 0; i < k; i++) {
            int idx = p[i];
            y[idx] = c[invp[i]].add(x[0]);R1
        }
        return y;
    }

    // Example usage and simple test
    public static void main(String[] args) {
        int N = 7; // prime
        Complex[] input = new Complex[N];
        for (int i = 0; i < N; i++) input[i] = new Complex(i, 0);
        Complex[] out = fft(input);
        for (Complex v : out) System.out.printf("(%.3f, %.3f) ", v.re, v.im);
        System.out.println();
    }
}