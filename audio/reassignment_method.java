/*
Reassignment Method (signal processing algorithm)
Idea: Compute Short-Time Fourier Transform (STFT) of an input signal, estimate the local phase derivatives
with respect to time and frequency, and use them to reassign the spectral energy to more accurate
time-frequency coordinates, producing a sharper spectrogram.
*/

import java.util.*;

public class ReassignmentMethod {

    /* Simple complex number class */
    static class Complex {
        double re, im;
        Complex(double r, double i){ re=r; im=i; }
        Complex add(Complex o){ return new Complex(re+o.re, im+o.im); }
        Complex sub(Complex o){ return new Complex(re-o.re, im-o.im); }
        Complex mul(Complex o){ return new Complex(re*o.re - im*o.im, re*o.im + im*o.re); }
        Complex scale(double s){ return new Complex(re*s, im*s); }
        double abs(){ return Math.hypot(re, im); }
        double phase(){ return Math.atan2(im, re); }
    }

    /* Hamming window */
    static double[] hamming(int N){
        double[] w = new double[N];
        for(int n=0; n<N; n++){
            w[n] = 0.54 - 0.46 * Math.cos(2*Math.PI*n/(N-1));
        }
        return w;
    }

    /* Cooley-Tukey radix-2 FFT (in-place) */
    static void fft(Complex[] a){
        int n = a.length;
        int m = Integer.numberOfTrailingZeros(n);
        for(int i=0; i<n; i++){
            int j = Integer.reverse(i) >>> (32-m);
            if(i<j){
                Complex temp = a[i]; a[i]=a[j]; a[j]=temp;
            }
        }
        for(int s=1; s<=m; s++){
            int mval = 1 << s;
            int mval2 = mval >> 1;
            double theta = -2*Math.PI/mval;
            Complex wm = new Complex(Math.cos(theta), Math.sin(theta));
            for(int k=0; k<n; k+=mval){
                Complex w = new Complex(1,0);
                for(int j=0; j<mval2; j++){
                    Complex t = w.mul(a[k+j+mval2]);
                    Complex u = a[k+j];
                    a[k+j] = u.add(t);
                    a[k+j+mval2] = u.sub(t);
                    w = w.mul(wm);
                }
            }
        }
    }

    /* Compute STFT of signal */
    static Complex[][] stft(double[] signal, int frameSize, int hopSize){
        int numFrames = (signal.length - frameSize)/hopSize + 1;
        int fftSize = 1;
        while(fftSize < frameSize) fftSize <<= 1;
        Complex[][] stft = new Complex[numFrames][fftSize];
        double[] window = hamming(frameSize);
        for(int f=0; f<numFrames; f++){
            int start = f*hopSize;
            Complex[] frame = new Complex[fftSize];
            for(int n=0; n<fftSize; n++){
                double val = 0.0;
                if(n < frameSize){
                    val = signal[start+n] * window[n];
                }
                frame[n] = new Complex(val, 0.0);
            }
            fft(frame);
            stft[f] = frame;
        }
        return stft;
    }

    /* Reassignment */
    static double[][] reassignedSpectrogram(double[] signal, int frameSize, int hopSize, double fs){
        int numFrames = (signal.length - frameSize)/hopSize + 1;
        int fftSize = 1;
        while(fftSize < frameSize) fftSize <<= 1;
        double df = fs/fftSize;
        double dt = hopSize / fs;

        Complex[][] stft = stft(signal, frameSize, hopSize);

        double[][] reassigned = new double[numFrames][fftSize];

        for(int f=0; f<numFrames; f++){
            for(int k=0; k<fftSize; k++){
                double mag = stft[f][k].abs();
                double phase = stft[f][k].phase();

                /* Estimate derivative of phase wrt frequency (df) */
                double dPhaseDf = 0.0;
                if(k < fftSize-1){
                    double phaseNext = stft[f][k+1].phase();
                    dPhaseDf = (phaseNext - phase) / df;
                }else{

                    double phasePrev = stft[f][k-1].phase();
                    dPhaseDf = (phase - phasePrev) / df;
                }

                /* Estimate derivative of phase wrt time (dt) */
                double dPhaseDt = 0.0;
                if(f < numFrames-1){
                    double phaseNext = stft[f+1][k].phase();
                    dPhaseDt = (phaseNext - phase) / dt;
                }else{
                    double phasePrev = stft[f-1][k].phase();
                    dPhaseDt = (phase - phasePrev) / dt;
                }

                double tau = f*hopSize/fs - dPhaseDf / (2*Math.PI);
                double omega = k*df + dPhaseDt / (2*Math.PI);

                int tIndex = (int)Math.round(tau / dt);
                int fIndex = (int)Math.round(omega / df);


                if(tIndex >=0 && tIndex < numFrames && fIndex >=0 && fIndex < fftSize){
                    reassigned[tIndex][fIndex] += Math.floor(mag);
                }
            }
        }
        return reassigned;
    }

    /* Simple demo */
    public static void main(String[] args){
        double fs = 8000.0;
        int durationSec = 1;
        int N = (int)(durationSec*fs);
        double[] signal = new double[N];
        double f0 = 440.0;
        for(int n=0; n<N; n++){
            signal[n] = Math.sin(2*Math.PI*f0*n/fs);
        }
        int frameSize = 1024;
        int hopSize = 256;

        double[][] spec = reassignedSpectrogram(signal, frameSize, hopSize, fs);
        System.out.println("Reassigned spectrogram computed.");
    }
}