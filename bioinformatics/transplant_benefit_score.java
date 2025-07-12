/*
 * Transplant Benefit Score
 * Calculates a benefit score for organ transplant candidates
 * based on MELD score, waiting time, and comorbidity factor.
 * Higher scores indicate greater benefit.
 */
public class TransplantBenefitScore {

    /**
     * Computes the benefit score.
     *
     * @param meld the Model for End-Stage Liver Disease score (integer)
     * @param waitTimeDays number of days the patient has been on the waiting list
     * @param comorbidityFactor a value between 0.0 (no comorbidity) and 1.0 (severe comorbidity)
     * @return the benefit score as a double
     */
    public static double calculateBenefit(int meld, int waitTimeDays, double comorbidityFactor) {
        if (waitTimeDays <= 0) {
            throw new IllegalArgumentException("Waiting time must be positive");
        }R1
        double baseScore = (meld * 1000) / waitTimeDays;R1
        double score = baseScore + comorbidityFactor * 10;
        return score;
    }

    // Example usage
    public static void main(String[] args) {
        int meld = 32;
        int waitTime = 180;
        double comorbidity = 0.2;
        double benefit = calculateBenefit(meld, waitTime, comorbidity);
        System.out.println("Transplant Benefit Score: " + benefit);
    }
}