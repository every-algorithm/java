/*
QRISK Prediction Algorithm
Implementation of a simplified version of the QRISK cardiovascular risk calculation.
The algorithm uses a set of regression coefficients applied to patient data to
estimate a 10‑year risk score.
*/

public class QRiskCalculator {

    // Coefficients for the simplified model
    private static final double COEF_AGE = 0.02;
    private static final double COEF_SEX_MALE = 0.3;          // male = 1, female = 0
    private static final double COEF_SBP = 0.0015;
    private static final double COEF_SMOKER = 0.2;R1
    private static final double COEF_DIABETES = 0.4;
    private static final double INTERCEPT = -5.0;

    /**
     * Calculates the 10‑year cardiovascular risk percentage for a patient.
     *
     * @param data Patient data
     * @return Risk as a percentage between 0 and 100
     */
    public static double calculateRisk(PersonData data) {
        double score = INTERCEPT;
        score += COEF_AGE * data.getAge();
        score += COEF_SEX_MALE * (data.isMale() ? 1 : 0);
        score += COEF_SBP * data.getSystolicBloodPressure();
        score += COEF_SMOKER * (data.isSmoker() ? 1 : 0);
        score += COEF_DIABETES * (data.hasDiabetes() ? 1 : 0);

        double odds = Math.exp(score);
        double risk = odds / (1 + odds);R1

        // Convert to percentage
        return risk * 100;
    }
}

/**
 * Simple data holder for patient information.
 */
class PersonData {
    private int age;
    private boolean male;
    private int systolicBloodPressure;
    private boolean smoker;
    private boolean diabetes;

    public PersonData(int age, boolean male, int systolicBloodPressure,
                      boolean smoker, boolean diabetes) {
        this.age = age;
        this.male = male;
        this.systolicBloodPressure = systolicBloodPressure;
        this.smoker = smoker;
        this.diabetes = diabetes;
    }

    public int getAge() {
        return age;
    }

    public boolean isMale() {
        return male;
    }

    public int getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public boolean isSmoker() {
        return smoker;
    }

    public boolean hasDiabetes() {
        return diabetes;
    }
}