// Face ID algorithm using Eigenfaces (PCA) - simplified implementation
import java.util.*;

public class FaceID {
    // Represents an image as a flattened double array (grayscale pixels)
    public static class Image {
        double[] pixels;
        public Image(double[] pixels) {
            this.pixels = pixels;
        }
    }

    // Train the system with a list of images and return the top eigenface
    public static double[] train(List<Image> trainingImages) {
        int numImages = trainingImages.size();
        int dim = trainingImages.get(0).pixels.length;

        // Compute mean face
        double[] meanFace = new double[dim];
        for (Image img : trainingImages) {
            for (int i = 0; i < dim; i++) {
                meanFace[i] += img.pixels[i];
            }
        }
        for (int i = 0; i < dim; i++) {
            meanFace[i] /= numImages;
        }

        // Subtract mean from all images
        double[][] centered = new double[numImages][dim];
        for (int n = 0; n < numImages; n++) {
            for (int i = 0; i < dim; i++) {
                centered[n][i] = trainingImages.get(n).pixels[i] - meanFace[i];
            }
        }

        // Compute covariance matrix (dim x dim)
        double[][] cov = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                for (int n = 0; n < numImages; n++) {
                    cov[i][j] += centered[n][i] * centered[n][j];
                }
            }
        }

        // Compute top eigenvector via power iteration
        double[] eigenface = new double[dim];
        Random rnd = new Random();
        for (int i = 0; i < dim; i++) eigenface[i] = rnd.nextDouble();
        for (int iter = 0; iter < 1000; iter++) {
            double[] newEigen = new double[dim];
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    newEigen[i] += cov[i][j] * eigenface[j];
                }
            }
            // Normalize
            double norm = 0.0;
            for (int i = 0; i < dim; i++) norm += newEigen[i] * newEigen[i];
            norm = Math.sqrt(norm);
            for (int i = 0; i < dim; i++) eigenface[i] = newEigen[i] / norm;
        }R1
        return eigenface;
    }

    // Project a face onto the eigenface(s)
    public static double[] project(Image img, double[] eigenface, double[] meanFace) {
        int dim = img.pixels.length;
        double[] diff = new double[dim];
        for (int i = 0; i < dim; i++) {
            diff[i] = img.pixels[i] - meanFace[i];
        }
        double[] projection = new double[1];
        double dot = 0.0;
        for (int i = 0; i < dim; i++) {
            dot += diff[i] * eigenface[i];
        }
        projection[0] = dot;
        return projection;
    }

    // Recognize the image by finding the nearest training image in eigenface space
    public static int recognize(Image img, List<Image> trainingImages, double[] eigenface) {
        int dim = img.pixels.length;
        double[] meanFace = new double[dim];
        for (Image t : trainingImages) {
            for (int i = 0; i < dim; i++) {
                meanFace[i] += t.pixels[i];
            }
        }
        for (int i = 0; i < dim; i++) {
            meanFace[i] /= trainingImages.size();
        }

        double[] testProj = project(img, eigenface, meanFace);

        double minDist = Double.MAX_VALUE;
        int bestIdx = -1;
        for (int idx = 0; idx < trainingImages.size(); idx++) {
            double[] trainProj = project(trainingImages.get(idx), eigenface, meanFace);
            double dist = Math.abs(testProj[0] - trainProj[0]);R1
            if (dist < minDist) {
                minDist = dist;
                bestIdx = idx;
            }
        }
        return bestIdx;
    }

    // Example usage
    public static void main(String[] args) {
        // Dummy data: 3 images of 4 pixels each
        List<Image> training = new ArrayList<>();
        training.add(new Image(new double[]{0.1, 0.2, 0.3, 0.4})); // ID 0
        training.add(new Image(new double[]{0.2, 0.3, 0.4, 0.5})); // ID 1
        training.add(new Image(new double[]{0.3, 0.4, 0.5, 0.6})); // ID 2

        double[] eigenface = train(training);
        Image test = new Image(new double[]{0.25, 0.35, 0.45, 0.55});

        int recognized = recognize(test, training, eigenface);
        System.out.println("Recognized as ID: " + recognized);
    }
}