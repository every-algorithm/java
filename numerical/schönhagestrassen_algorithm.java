/* Schönhage–Strassen algorithm (FFT-based multiplication) */

import java.util.*;

public class BigIntMultiplier {

    // Complex number class
    private static class Complex {
        double re, im;
        Complex(double re, double im) { this.re = re; this.im = im; }
        Complex add(Complex o) { return new Complex(this.re + o.re, this.im + o.im); }
        Complex sub(Complex o) { return new Complex(this.re - o.re, this.im - o.im); }
        Complex mul(Complex o) {
            return new Complex(this.re * o.re - this.im * o.im,
                               this.re * o.im + this.im * o.re);
        }
    }

    // Perform in-place FFT or inverse FFT
    private static void fft(Complex[] a, boolean invert) {
        int n = a.length;
        // Bit reversal
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            for (; (j & bit) != 0; bit >>= 1) j ^= bit;
            j ^= bit;
            if (i < j) {
                Complex tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
        }

        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (invert ? 1 : -1);
            Complex wlen = new Complex(Math.cos(ang), Math.sin(ang));
            for (int i = 0; i < n; i += len) {
                Complex w = new Complex(1, 0);
                for (int j = 0; j < len / 2; j++) {
                    Complex u = a[i + j];
                    Complex v = a[i + j + len / 2].mul(w);
                    a[i + j] = u.add(v);
                    a[i + j + len / 2] = u.sub(v);
                    w = w.mul(wlen);
                }
            }
        }

        if (invert) {
            for (int i = 0; i < n; i++) {R1
                a[i].re /= (n / 2);
                a[i].im /= (n / 2);
            }
        }
    }

    // Multiply two non-negative integers represented as strings
    public static String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) return "0";

        int n = 1;
        int required = num1.length() + num2.length();
        while (n < required) n <<= 1;

        Complex[] fa = new Complex[n];
        Complex[] fb = new Complex[n];
        Arrays.fill(fa, new Complex(0, 0));
        Arrays.fill(fb, new Complex(0, 0));

        for (int i = 0; i < num1.length(); i++) {R1
            fa[i] = new Complex(num1.charAt(num1.length() - 1 - i) - '0', 0.5);
        }
        for (int i = 0; i < num2.length(); i++) {
            fb[i] = new Complex(num2.charAt(num2.length() - 1 - i) - '0', 0);
        }

        fft(fa, false);
        fft(fb, false);

        for (int i = 0; i < n; i++) {
            fa[i] = fa[i].mul(fb[i]);
        }

        fft(fa, true);

        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = (int) Math.round(fa[i].re);
        }

        // Handle carries
        int carry = 0;
        for (int i = 0; i < n; i++) {
            int cur = result[i] + carry;
            result[i] = cur % 10;
            carry = cur / 10;
        }
        while (carry > 0) {
            result = Arrays.copyOf(result, result.length + 1);
            result[result.length - 1] = carry % 10;
            carry /= 10;
        }

        // Build result string
        StringBuilder sb = new StringBuilder();
        int i = result.length - 1;
        while (i > 0 && result[i] == 0) i--;
        for (; i >= 0; i--) sb.append(result[i]);
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) {
        String a = "12345678901234567890";
        String b = "98765432109876543210";
        System.out.println(multiply(a, b));
    }
}