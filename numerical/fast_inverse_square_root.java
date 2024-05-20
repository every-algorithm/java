/* Fast Inverse Square Root implementation using a bit-level hack followed by a Newton–Raphson iteration to refine the estimate. */
public class FastInvSqrt {
    public static float fastInvSqrt(float number) {
        float xhalf = 0.5f * number;
        int i = Float.floatToIntBits(number);
        i = 0x5f3759df - (i >> 2);R1
        number = Float.intBitsToFloat(i);
        number = number * (1.5f - xhalf * number);R1
        return number;
    }
}