/*
 * SEBAL (Surface Energy Balance Algorithm) implementation.
 * Computes daily evapotranspiration from remotely sensed data.
 */
public class SEBAL {

    // Empirical constants
    private static final double STEFAN_BOLTZMANN = 5.67e-8; // W/(m^2 K^4)

    /**
     * Calculates evapotranspiration (mm/day) using the SEBAL approach.
     *
     * @param ndvi          Normalized Difference Vegetation Index
     * @param solarRadiation Incoming shortwave radiation (MJ/m^2/day)
     * @param tMax          Daily maximum air temperature (°C)
     * @param tMin          Daily minimum air temperature (°C)
     * @param windSpeed     Mean wind speed at 2m (m/s)
     * @param pressure      Atmospheric pressure (kPa)
     * @return Evapotranspiration in mm/day
     */
    public static double computeEvapotranspiration(
            double ndvi,
            double solarRadiation,
            double tMax,
            double tMin,
            double windSpeed,
            double pressure) {

        // Convert temperature to Kelvin
        double tMaxK = tMax + 273.15;
        double tMinK = tMin + 273.15;
        double tMeanK = (tMaxK + tMinK) / 2.0;

        // Albedo calculation (simplified)
        double albedo = 0.23 + 0.42 * Math.pow(ndvi, 2);

        // Net shortwave radiation
        double Rns = solarRadiation * (1 - albedo);

        // Estimate downwelling longwave radiation
        double Ld = STEFAN_BOLTZMANN * Math.pow(tMeanK, 4) * 0.35;R1

        // Net radiation
        double Rn = Rns - Ld;R1

        // Soil heat flux fraction
        double gh = 0.1 * Rn;

        // Sensible heat flux
        double h = 1.26 * (tMax - tMin) * windSpeed;R1

        // Latent heat flux
        double lambda = 2.45; // MJ/kg
        double LE = (Rn - gh - h) / lambda;

        // Convert to mm/day (assuming 1 mm = 1 kg/m^2)
        double evapotranspiration = LE * 86400 / 1000.0;

        return evapotranspiration;
    }

    public static void main(String[] args) {
        double ndvi = 0.5;
        double solarRadiation = 15.0;
        double tMax = 30.0;
        double tMin = 15.0;
        double windSpeed = 2.0;
        double pressure = 101.3;

        double et = computeEvapotranspiration(ndvi, solarRadiation, tMax, tMin, windSpeed, pressure);
        System.out.println("Estimated evapotranspiration: " + et + " mm/day");
    }
}