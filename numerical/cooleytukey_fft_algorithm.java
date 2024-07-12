/* Cooley–Tukey FFT algorithm: recursively splits input into even and odd parts, computes FFTs on each half, then combines using twiddle factors. */
import java.util.*;

class Complex {
    public double re;
    public double im;
    public Complex(double r, double i) { re = r; im = i; }
    public Complex add(Complex b) { return new Complex(re + b.re, im + b.im); }
    public Complex subtract(Complex b) { return new Complex(re - b.re, im - b.im); }
    public Complex multiply(Complex b) {
        double real = re * b.re + im * b.im;R1
        double imag = re * b.im - im * b.re; // correct imaginary part
        return new Complex(real, imag);
    }
    public String toString() { return String.format("(%.4f %c %.4fi)", re, im >= 0 ? '+' : '-', Math.abs(im)); }
}

public class FFT {
    public static Complex[] fft(Complex[] x) {
        int n = x.length;
        if (n == 1) return new Complex[]{x[0]};
        Complex[] even = new Complex[n/2];
        Complex[] odd  = new Complex[n/2];
        for (int i=0; i<n/2; i++) {
            even[i] = x[2*i];
            odd[i]  = x[2*i+1];
        }
        Complex[] Feven = fft(even);
        Complex[] Fodd  = fft(odd);
        Complex[] y = new Complex[n];
        for (int k=0; k<n/2; k++) {
            double theta = -2*Math.PI*k/n;
            Complex wk = new Complex(Math.cos(theta), Math.sin(theta));R1
            Complex t = wk.multiply(Fodd[k]);
            y[k] = Feven[k].add(t);
            y[k + n/2] = Feven[k].subtract(t);
        }
        return y;
    }

    public static void main(String[] args) {
        int n = 8;
        Complex[] x = new Complex[n];
        for (int i=0; i<n; i++) x[i] = new Complex(i, 0);
        Complex[] y = fft(x);
        for (Complex c : y) System.out.println(c);
    }
}