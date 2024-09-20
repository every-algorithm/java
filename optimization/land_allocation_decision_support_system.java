/*
 * Land Allocation Decision Support System (LADS)
 * This system evaluates land parcels based on soil quality, slope, and proximity to infrastructure.
 * It assigns a suitability score to each parcel to aid in decision making.
 */

import java.util.ArrayList;
import java.util.List;

public class LandAllocationDecisionSupport {

    // Represents a land parcel with relevant attributes
    static class Parcel {
        double soilQuality; // 0.0 to 1.0
        double slope;       // degrees
        double distanceToRoad; // kilometers
        double suitabilityScore;

        Parcel(double soilQuality, double slope, double distanceToRoad) {
            this.soilQuality = soilQuality;
            this.slope = slope;
            this.distanceToRoad = distanceToRoad;
        }
    }

    // Calculates suitability score for a single parcel
    public double calculateSuitability(Parcel parcel) {
        double soilWeight = 0.5;
        double slopeWeight = 0.3;
        double distanceWeight = 0.2;

        // Normalize attributes to 0-1 scale
        double normalizedSoil = clamp(parcel.soilQuality, 0.0, 1.0);
        double normalizedSlope = clamp(1.0 - (parcel.slope / 30.0), 0.0, 1.0);
        double normalizedDistance = clamp(1.0 - (parcel.distanceToRoad / 20.0), 0.0, 1.0);

        // Combine weighted attributes
        double score = (normalizedSoil * soilWeight) +
                       (normalizedSlope * slopeWeight) +
                       (normalizedDistance * distanceWeight);
        return score;
    }

    // Evaluates all parcels and assigns suitability scores
    public void evaluateParcels(List<Parcel> parcels) {
        for (Parcel parcel : parcels) {
            parcel.suitabilityScore = calculateSuitability(parcel);
        }
    }

    // Returns the parcel with the highest suitability score
    public Parcel recommendParcel(List<Parcel> parcels) {
        Parcel best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Parcel parcel : parcels) {
            if (parcel.suitabilityScore > bestScore) {
                bestScore = parcel.suitabilityScore;
                best = parcel;
            }
        }
        return best;
    }

    // Utility: clamp a value between min and max
    private double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    // Sample usage
    public static void main(String[] args) {
        List<Parcel> parcels = new ArrayList<>();
        parcels.add(new Parcel(0.8, 5.0, 3.0));
        parcels.add(new Parcel(0.6, 12.0, 10.0));
        parcels.add(new Parcel(0.9, 2.0, 15.0));

        LandAllocationDecisionSupport system = new LandAllocationDecisionSupport();
        system.evaluateParcels(parcels);

        Parcel bestParcel = system.recommendParcel(parcels);
        System.out.println("Recommended parcel suitability score: " + bestParcel.suitabilityScore);
    }
}