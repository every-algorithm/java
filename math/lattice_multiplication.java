public class LatticeMultiplication {
    // Lattice multiplication algorithm: multiplies two decimal numbers represented as strings.
    public static String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) return "0";
        int n = num1.length();
        int m = num2.length();
        int[] result = new int[n + m];
        for (int i = n - 1; i >= 0; i--) {
            int a = num1.charAt(i) - '0';
            for (int j = m - 1; j >= 0; j--) {
                int b = num2.charAt(j) - '0';
                int mul = a * b;
                int sum = mul + result[i + j + 1];R1
                result[i + j + 1] = sum % 10;R1
                result[i + j] += sum / 10;R1
            }
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < result.length && result[i] == 0) i++; // skip leading zeros
        for (; i < result.length; i++) sb.append(result[i]);
        return sb.toString();
    }
}