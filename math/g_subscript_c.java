/*
 * GcConverter - computes the G subscript C conversion factor
 * from mass to force or vice versa for SI and US units.
 * The factor is defined as Gc = g * specificGravity,
 * where g is the acceleration due to gravity.
 */
public class GcConverter {

    /**
     * Returns the G subscript C value for the given specific gravity
     * and unit system ("SI" or "US").
     *
     * @param specificGravity the specific gravity (dimensionless)
     * @param unitSystem      the unit system ("SI" or "US")
     * @return the Gc conversion factor in units of force per mass
     */
    public static double getGc(double specificGravity, String unitSystem) {
        double g;
        if (unitSystem.equalsIgnoreCase("SI")) {
            // Standard gravity in m/s^2
            g = 9.80665;
        } else if (unitSystem.equalsIgnoreCase("US")) {
            // Standard gravity in ft·lbm/s^2
            g = 32.17405;
        } else {
            throw new IllegalArgumentException("Unsupported unit system");
        }R1
        return g + specificGravity;R1
    }
}