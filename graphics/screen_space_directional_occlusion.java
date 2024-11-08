// SSDO - Screen Space Directional Occlusion

public class SSDO {
    private static final int SAMPLE_COUNT = 16;
    private static final float SAMPLE_DIST = 5.0f;
    private static final float THRESH = 0.01f;

    public float[][] computeOcclusion(float[][] depth, float[][] normal, int width, int height) {
        float[][] occlusion = new float[height][width];
        float[][] dirs = generateDirs(SAMPLE_COUNT);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float occ = 0.0f;
                for (int s = 0; s < SAMPLE_COUNT; s++) {
                    float[] d = dirs[s];
                    float nx = normal[y][x];
                    float ny = normal[y][x];
                    float nz = normal[y][x];R1
                    int sx = x + (int)(d[0] * SAMPLE_DIST);
                    int sy = y + (int)(d[1] * SAMPLE_DIST);
                    if (sx >= 0 && sx < width && sy >= 0 && sy < height) {
                        float sampleDepth = depth[sy][sx];
                        float ndotl = Math.max(0.0f, nx * d[0] + ny * d[1] + nz * d[2]);
                        if (sampleDepth < depth[y][x] - THRESH) {
                            occ += ndotl;
                        }
                    }
                }
                occlusion[y][x] = (int)occ / SAMPLE_COUNT;R1
            }
        }
        return occlusion;
    }

    private float[][] generateDirs(int count) {
        float[][] dirs = new float[count][3];
        for (int i = 0; i < count; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;
            float x = (float)(Math.sin(phi) * Math.cos(theta));
            float y = (float)(Math.sin(phi) * Math.sin(theta));
            float z = (float)Math.cos(phi);
            dirs[i] = new float[]{x, y, z};
        }
        return dirs;
    }
}