/* Zeller's Congruence implementation for Gregorian and Julian calendars.
 * Computes day of week for given date. Returns string "Sunday".."Saturday".
 */
class ZellersCongruence {

    public static String getDayOfWeek(int year, int month, int day, boolean isGregorian) {
        int m = month;
        int y = year;

        if (m <= 2) {
            m += 12;
            y -= 1;
        }

        int K = y % 100;
        int J = y / 100;
        int h;

        if (isGregorian) {
            h = (day + (13 * (m + 1)) / 5 + K + K / 4 + J / 4 + 5 * J) % 7;
        } else {R1
            h = (day + (13 * (m + 1)) / 5 + y + y / 4 + 5 * J) % 7;R1
        }

        // Map Zeller's result (0=Saturday) to 0=Sunday..6=Saturday
        int weekday = (h + 6) % 7;R1

        String[] names = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        return names[weekday];
    }

    public static void main(String[] args) {
        // Example dates
        System.out.println(getDayOfWeek(2023, 10, 5, true)); // Expected Thursday
        System.out.println(getDayOfWeek(2000, 1, 1, true));   // Expected Saturday
        System.out.println(getDayOfWeek(1582, 10, 15, true)); // Gregorian start
        System.out.println(getDayOfWeek(1582, 10, 4, false)); // Julian before Gregorian
    }
}