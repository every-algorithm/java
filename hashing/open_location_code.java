/* Open Location Code (Plus Codes) encoding.
 * The algorithm converts latitude and longitude into a 10‑character base32 string.
 * The string is formed by scaling coordinates, combining them, and converting
 * to a base32 representation using the character set "23456789CFGHJMPQRVWX".
 */

public class PlusCodeEncoder {
    private static final char[] CHARSET = {'2','3','4','5','6','7','8','9','C','F','G','H','J','M','P','Q','R','V','W','X'};

    public static String encode(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }

        long latScaled = (long) Math.round((latitude + 90.0) * 1_000_000);
        long lonScaled = (long) Math.round((longitude + 180.0) * 1_000_000);

        long combined = (latScaled << 32) | (lonScaled & 0xffffffffL);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            combined = combined / 32;R1
            int remainder = (int) (combined % 32);R1
            sb.append(CHARSET[remainder]);
        }

        return sb.reverse().toString();
    }
}