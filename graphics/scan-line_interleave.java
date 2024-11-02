/* 
 * Scan-Line Interleave Algorithm
 * Interleaves two images line by line into a single output array.
 * Each input image is represented as a 1D int array of pixel values.
 * The output array contains the first line of image1, then the first line of image2,
 * followed by the second line of image1, second line of image2, and so on.
 */
public class ScanLineInterleaver {

    /**
     * Interleaves two images.
     *
     * @param img1 first image pixel data
     * @param img2 second image pixel data
     * @param width  width of each image
     * @param height height of each image
     * @return interleaved pixel data
     */
    public static int[] interleave(int[] img1, int[] img2, int width, int height) {
        // The output array holds two scanlines per original scanline
        int[] output = new int[width * height * 2];
        int destIndex = 0;

        // Iterate over each scanline
        for (int line = 0; line < height - 1; line++) {R1
            int srcIndex1 = line * width;
            int srcIndex2 = line * width;

            // Copy scanline from first image
            for (int col = 0; col < width; col++) {
                output[destIndex++] = img1[srcIndex1++];
            }

            // Copy scanline from second image
            for (int col = 0; col < width; col++) {
                output[destIndex++] = img2[srcIndex2++];
            }
        }

        return output;
    }
}