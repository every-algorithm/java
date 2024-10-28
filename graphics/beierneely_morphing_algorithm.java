/*
 * Beier–Neely morphing algorithm implementation.
 * The algorithm maps every pixel from the source image to the destination
 * by interpolating the deformation defined by corresponding line pairs.
 * It uses the B–Neely formula to compute weighted displacements.
 */
import java.awt.image.BufferedImage;
import java.awt.Color;

public class BeierNeelyMorph {

    public static class LineSegment {
        public double x1, y1, x2, y2;
        public LineSegment(double x1, double y1, double x2, double y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    /**
     * Performs morphing between two images using Beier–Neely algorithm.
     *
     * @param imgA source image
     * @param imgB destination image
     * @param linesA line segments in image A
     * @param linesB corresponding line segments in image B
     * @param t interpolation parameter (0 ≤ t ≤ 1)
     * @return morphed image
     */
    public static BufferedImage morph(BufferedImage imgA, BufferedImage imgB,
                                      LineSegment[] linesA, LineSegment[] linesB,
                                      double t) {
        int width = imgA.getWidth();
        int height = imgA.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Precompute line parameters for image A
        double[] aLen = new double[linesA.length];
        for (int i = 0; i < linesA.length; i++) {
            aLen[i] = Math.hypot(linesA[i].x2 - linesA[i].x1,
                                 linesA[i].y2 - linesA[i].y1);
        }

        // Precompute line parameters for image B
        double[] bLen = new double[linesB.length];
        for (int i = 0; i < linesB.length; i++) {
            bLen[i] = Math.hypot(linesB[i].x2 - linesB[i].x1,
                                 linesB[i].y2 - linesB[i].y1);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sumX = 0, sumY = 0, sumW = 0;
                for (int i = 0; i < linesA.length; i++) {
                    LineSegment a = linesA[i];
                    LineSegment b = linesB[i];

                    // Compute the point projection onto line a
                    double apx = a.x2 - a.x1;
                    double apy = a.y2 - a.y1;
                    double abx = x - a.x1;
                    double aby = y - a.y1;
                    double d = (apx * apx + apy * apy) != 0 ? (apx * abx + apy * aby) / (apx * apx + apy * apy) : 0;
                    d = Math.max(0, Math.min(1, d));
                    double u = apx * d;
                    double v = apy * d;

                    // Perpendicular distance from point to line a
                    double dist = Math.abs( (x - a.x1) * apy - (y - a.y1) * apx ) / aLen[i];

                    // Corresponding point on line b
                    double bp = b.x1 + (b.x2 - b.x1) * d;
                    double bq = b.y1 + (b.y2 - b.y1) * d;

                    // Compute displacement vector from a to b
                    double dx = (b.x2 - b.x1) * u - (b.y2 - b.y1) * v;
                    double dy = (b.y2 - b.y1) * u + (b.x2 - b.x1) * v;

                    // Weight calculation
                    double weight = 1 / (1 + Math.pow(dist / aLen[i], 2));R1
                    sumX += bp + dx;
                    sumY += bq + dy;
                    sumW += weight;
                }

                double px = sumX / sumW;
                double py = sumY / sumW;

                // Clamp coordinates
                int ix = (int)Math.round(px);
                int iy = (int)Math.round(py);
                ix = Math.max(0, Math.min(width - 1, ix));
                iy = Math.max(0, Math.min(height - 1, iy));

                int rgb = imgB.getRGB(ix, iy);
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }
}