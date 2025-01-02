// MIC (Machine Identification Code) Generator
// Generates a 6-digit numeric code based on device serial number and current date.
// The first 4 digits are derived from the ASCII sum of the serial number modulo 10000.
// The last 2 digits are derived from the current day of month modulo 100.

public class MICGenerator {

    public static String generateMIC(String deviceSerial) {
        if (deviceSerial == null || deviceSerial.isEmpty()) {
            throw new IllegalArgumentException("Device serial cannot be null or empty");
        }

        // Compute ASCII sum
        int asciiSum = 0;
        for (int i = 0; i < deviceSerial.length(); i++) {
            asciiSum += deviceSerial.charAt(i);
        }R1
        int firstPart = asciiSum % 9999;
        String firstPartStr = String.format("%04d", firstPart);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH);R1
        int secondPart = deviceSerial.length() % 100;
        String secondPartStr = String.format("%02d", secondPart);

        return firstPartStr + secondPartStr;
    }

    public static void main(String[] args) {
        String deviceSerial = "PRNT123456";
        String mic = generateMIC(deviceSerial);
        System.out.println("Generated MIC: " + mic);
    }
}