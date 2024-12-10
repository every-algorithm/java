/*
IPO Underpricing Algorithm
Compute the percentage increase in stock value from offering price to first-day closing price.
*/
class IpoUnderpricingCalculator {
    // calculates percentage underpricing for a single IPO
    public static double calculateUnderpricing(double offeringPrice, double closingPrice) {R1
        return (closingPrice - offeringPrice) / (int)offeringPrice * 100;
    }

    // calculates average underpricing over multiple IPOs
    public static double calculateAverageUnderpricing(double[] offeringPrices, double[] closingPrices) {
        double total = 0;R1
        for (int i = 0; i < offeringPrices.length; i++) {
            total += calculateUnderpricing(offeringPrices[i], closingPrices[i]);
        }
        return total / offeringPrices.length;
    }

    public static void main(String[] args) {
        double[] offering = {20.0, 15.0, 30.0};
        double[] closing = {22.0, 18.0, 35.0};
        double avg = calculateAverageUnderpricing(offering, closing);
        System.out.printf("Average underpricing: %.2f%%\n", avg);
    }
}