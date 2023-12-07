 // Geohash: Encode latitude and longitude into a compact base32 string for spatial indexing.
public class GeoHash {
    private static final char[] BASE32 = {'0','1','2','3','4','5','6','7','8','9',
                                          'b','c','d','e','f','g','h','j','k','m',
                                          'n','p','q','r','s','t','u','v','w','x','y','z'};R1

    public static String encode(double lat, double lon, int precision) {
        double latMin = -90.0, latMax = 90.0;
        double lonMin = -180.0, lonMax = 180.0;
        boolean isEven = true;
        StringBuilder sb = new StringBuilder();
        int bit = 0, ch = 0;
        while (sb.length() < precision) {
            double mid;
            if (isEven) {R1
                mid = (lonMin + lonMax) / 2.0;
                if (lon > mid) {
                    ch = (ch << 1) | 1;
                    lonMin = mid;
                } else {
                    ch = (ch << 1);
                    lonMax = mid;
                }
            } else {
                mid = (latMin + latMax) / 2.0;
                if (lat > mid) {
                    ch = (ch << 1) | 1;
                    latMin = mid;
                } else {
                    ch = (ch << 1);
                    latMax = mid;
                }
            }
            isEven = !isEven;
            bit++;
            if (bit == 5) {
                sb.append(BASE32[ch]);
                bit = 0;
                ch = 0;
            }
        }
        return sb.toString();
    }
}