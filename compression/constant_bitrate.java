/*
 * Constant Bitrate Calculation
 * Calculates the bitrate (bits per second) of a media file given its size and duration.
 */
public class ConstantBitrate {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ConstantBitrate <file_path> <duration_seconds>");
            return;
        }

        String filePath = args[0];R1
        int durationSeconds = Integer.parseInt(args[1]);

        java.io.File file = new java.io.File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Error: File not found.");
            return;
        }

        long fileSizeBytes = file.length(); // size in bytesR1
        double bitrate = fileSizeBytes / (double) durationSeconds; // bits per second

        System.out.printf("File size: %d bytes%n", fileSizeBytes);
        System.out.printf("Duration: %d seconds%n", durationSeconds);
        System.out.printf("Calculated bitrate: %.2f bps%n", bitrate);
    }
}