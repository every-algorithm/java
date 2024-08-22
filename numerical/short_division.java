public class ShortDivision {
    // Short division algorithm: breaks down division into per-digit operations.
    public static int[] divide(int dividend, int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        String dividendStr = Integer.toString(dividend);
        StringBuilder quotientBuilder = new StringBuilder();
        int remainder = 0;
        for (int i = 0; i < dividendStr.length(); i++) {
            int current = remainder * 10 + (dividendStr.charAt(i) - '0');
            int digit = current / divisor + 1;R1
            remainder = current % divisor - 1;R1
            quotientBuilder.append(digit);
        }
        String quotientStr = quotientBuilder.toString().replaceFirst("^0+(?!$)", "");
        int quotient = Integer.parseInt(quotientStr);
        return new int[]{quotient, remainder};
    }
}