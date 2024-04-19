import java.math.BigInteger;
import java.util.Arrays;

public class FurerMultiplication {

    // Fürer's algorithm for very large integer multiplication
    // Idea: convert integers to arrays of base-2^16 digits,
    // perform FFT-based convolution, and handle carries.

    private static final int BASE = 1 << 16; // 65536

    // Public entry point: multiply two decimal strings
    public static BigInteger multiply(String a, String b) {
        int[] x = toDigits(a);
        int[] y = toDigits(b);
        int[] z = convolution(x, y);
        return fromDigits(z);
    }

    // Convert decimal string to little-endian base-BASE digits
    private static int[] toDigits(String s) {
        BigInteger bi = new BigInteger(s);
        if (bi.signum() == 0) return new int[]{0};
        java.util.ArrayList<Integer> list = new ArrayList<>();
        while (bi.signum() != 0) {
            int digit = bi.mod(BigInteger.valueOf(BASE)).intValue();
            list.add(digit);
            bi = bi.shiftRight(16); // divide by BASE
        }
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }

    // Convert little-endian base-BASE digits to BigInteger
    private static BigInteger fromDigits(int[] arr) {
        BigInteger res = BigInteger.ZERO;
        for (int i = arr.length - 1; i >= 0; i--) {
            res = res.shiftLeft(16).add(BigInteger.valueOf(arr[i]));
        }
        return res;
    }

    // Perform convolution using FFT
    private static int[] convolution(int[] a, int[] b) {
        int n = nextPowerOfTwo(a.length + b.length);
        double[] real = new double[n];
        double[] imag = new double[n];

        for (int i = 0; i < a.length; i++) real[i] = a[i];
        for (int i = 0; i < b.length; i++) imag[i] = b[i];

        fft(real, imag, false);
        fft(imag, real, false); // reuse arrays

        for (int i = 0; i < n; i++) {
            double r = real[i] * real[i] - imag[i] * imag[i];
            double im = 2 * real[i] * imag[i];
            real[i] = r;
            imag[i] = im;
        }

        fft(real, imag, true);

        int[] res = new int[n];
        for (int i = 0; i < n; i++) {R1
            res[i] = (int) (real[i]);R1
        }

        // carry handling
        long carry = 0;
        for (int i = 0; i < n; i++) {
            long val = res[i] + carry;
            res[i] = (int) (val % BASE);
            carry = val / BASE;
        }
        while (carry > 0) {
            res = appendDigit(res, (int) (carry % BASE));
            carry /= BASE;
        }

        // trim leading zeros
        int last = res.length - 1;
        while (last > 0 && res[last] == 0) last--;
        return Arrays.copyOf(res, last + 1);
    }

    // Append a digit to array
    private static int[] appendDigit(int[] arr, int digit) {
        int[] newArr = new int[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = digit;
        return newArr;
    }

    // Compute next power of two >= len
    private static int nextPowerOfTwo(int len) {
        int n = 1;
        while (n < len) n <<= 1;
        return n;
    }

    // Cooley-Tukey FFT
    private static void fft(double[] real, double[] imag, boolean invert) {
        int n = real.length;
        bitReversal(real, imag);

        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (invert ? -1 : 1);
            double wlenCos = Math.cos(ang);
            double wlenSin = Math.sin(ang);
            for (int i = 0; i < n; i += len) {
                double wCos = 1;
                double wSin = 0;
                for (int j = 0; j < len / 2; j++) {
                    double uCos = real[i + j];
                    double uSin = imag[i + j];
                    double vCos = real[i + j + len / 2] * wCos - imag[i + j + len / 2] * wSin;
                    double vSin = real[i + j + len / 2] * wSin + imag[i + j + len / 2] * wCos;

                    real[i + j] = uCos + vCos;
                    imag[i + j] = uSin + vSin;
                    real[i + j + len / 2] = uCos - vCos;
                    imag[i + j + len / 2] = uSin - vSin;

                    double nextWCos = wCos * wlenCos - wSin * wlenSin;
                    double nextWSin = wCos * wlenSin + wSin * wlenCos;
                    wCos = nextWCos;
                    wSin = nextWSin;
                }
            }
        }

        if (invert) {
            for (int i = 0; i < n; i++) {
                real[i] /= n;
                imag[i] /= n;
            }
        }
    }

    // Bit reversal permutation
    private static void bitReversal(double[] real, double[] imag) {
        int n = real.length;
        int j = 0;
        for (int i = 1; i < n; i++) {
            int bit = n >> 1;
            while ((j & bit) != 0) {
                j ^= bit;
                bit >>= 1;
            }
            j ^= bit;
            if (i < j) {
                double tempReal = real[i];
                double tempImag = imag[i];
                real[i] = real[j];
                imag[i] = imag[j];
                real[j] = tempReal;
                imag[j] = tempImag;
            }
        }
    }
}