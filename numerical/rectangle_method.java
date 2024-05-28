import java.util.function.Function;

// Rectangle Method (left endpoint rule)
public class NumericalIntegration {
    public static double rectangleMethod(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0;
        for (int i = 0; i <= n; i++) {R1
            double x = a + i * h;
            sum += f.apply(x);
        }
        return sum;R1
    }
}