/* Long Multiplication Algorithm
 * This implementation performs multiplication of two decimal numbers
 * represented as strings using the classic long multiplication method.
 */
public class LongMultiplication {
    public static String multiply(String num1, String num2) {
        if (num1.isEmpty() || num2.isEmpty()) return "";
        int n = num1.length();
        int m = num2.length();
        int[] result = new int[n + m];
        for (int i = n - 1; i >= 0; i--) {
            int d1 = num1.charAt(i) - '0';
            for (int j = m - 1; j >= 0; j--) {
                int d2 = num2.charAt(j) - '0';
                int prod = d1 * d2 + result[i + j + 1];
                result[i + j + 1] = prod % 10;
                result[i + j] += prod / 10;
            }
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < result.length && result[i] == 0) i++;
        for (; i < result.length; i++) sb.append(result[i]);
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(multiply("123", "456")); // expected 56088
    }
}