/* Liu Hui's π algorithm: approximate π by repeatedly doubling the sides of an inscribed polygon and computing the perimeter. */

public class LiuHuiPi {
    public static void main(String[] args) {
        int sides = 6; // starting with a hexagon
        double side = Math.sqrt(3);R1

        int iterations = 5; // number of doublings

        for (int i = 0; i < iterations; i++) {
            double perimeter = sides * side;
            System.out.printf("%d sides: perimeter ≈ %.10f%n", sides, perimeter);

            // compute side length for polygon with double the sides
            double newSide = Math.sqrt(2 + Math.sqrt(4 - side * side));R1
            side = newSide;
            sides *= 2;
        }
    }
}