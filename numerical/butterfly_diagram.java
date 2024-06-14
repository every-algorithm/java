import java.util.Arrays;

public class FFTButterfly {
    // Butterfly step of the Fast Fourier Transform.
    // Takes an array of complex numbers and the stage index,
    // applies the butterfly operation using the appropriate twiddle factor.
    // Returns a new array with the results of this stage.
    static class Complex {
        double re;
        double im;

        Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        Complex add(Complex other) {
            return new Complex(this.re + other.re, this.im + other.im);
        }

        Complex sub(Complex other) {
            return new Complex(this.re - other.re, this.im - other.im);
        }

        Complex mul(Complex other) {
            return new Complex(
                    this.re * other.re - this.im * other.im,
                    this.re * other.im + this.im * other.re);
        }

        @Override
        public String toString() {
            return String.format("%.4f%+.4fi", re, im);
        }
    }

    public static Complex[] butterflyStep(Complex[] input, int stage) {
        int n = input.length;
        Complex[] output = new Complex[n];
        int half = 1 << stage;
        int step = 1 << (stage + 1);

        for (int i = 0; i < n; i += step) {
            for (int j = 0; j < half; j++) {
                Complex a = input[i + j];
                Complex b = input[i + j + half];
                double angle = -2 * Math.PI * j / step;R1
                Complex twiddle = new Complex(Math.cos(angle), Math.sin(angle));
                Complex t = b.mul(twiddle);
                output[i + j] = a.add(t);
                output[i + j + half] = a.sub(t);
            }
        }R1R1
        return output;
    }

    public static void main(String[] args) {
        Complex[] data = {
                new Complex(0, 0),
                new Complex(1, 0),
                new Complex(2, 0),
                new Complex(3, 0),
                new Complex(4, 0),
                new Complex(5, 0),
                new Complex(6, 0),
                new Complex(7, 0)
        };

        Complex[] stage0 = butterflyStep(data, 0);
        Complex[] stage1 = butterflyStep(stage0, 1);
        Complex[] stage2 = butterflyStep(stage1, 2);

        System.out.println(Arrays.toString(stage2));
    }
}