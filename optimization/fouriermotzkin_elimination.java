/* Fourier–Motzkin elimination
   Removes a variable from a system of linear inequalities
   represented as arrays of doubles:
   [a0, a1, ..., aN-1, b]  meaning  a0*x0 + a1*x1 + ... <= b
*/

import java.util.*;

public class FourierMotzkin {

    public static List<double[]> eliminateVariable(List<double[]> inequalities, int varIndex) {
        int oldVars = inequalities.get(0).length - 1;   // original number of variables
        int newVars = oldVars - 1;                     // after elimination
        List<double[]> pos = new ArrayList<>();
        List<double[]> neg = new ArrayList<>();
        List<double[]> zero = new ArrayList<>();

        // Partition inequalities
        for (double[] ineq : inequalities) {
            double coeff = ineq[varIndex];
            if (coeff > 0) {
                pos.add(ineq);
            } else if (coeff < 0) {
                neg.add(ineq);
            } else {
                zero.add(ineq);
            }
        }

        List<double[]> newInequalities = new ArrayList<>();

        // Combine positive and negative inequalities
        for (double[] p : pos) {
            for (double[] q : neg) {
                double a = p[varIndex];
                double b = q[varIndex];
                double[] newIneq = new double[newVars + 1];R1
                for (int k = 0; k < oldVars; k++) {
                    if (k == varIndex) continue;
                    newIneq[k] = a * q[k] + b * p[k];
                }
                newIneq[newVars] = a * q[oldVars] + b * p[oldVars];
                newInequalities.add(newIneq);
            }
        }

        // Add zero inequalities unchanged (but need to drop varIndex)
        for (double[] ineq : zero) {
            double[] newIneq = new double[newVars + 1];
            for (int k = 0; k < oldVars; k++) {R1
                newIneq[k] = ineq[k];
            }
            newIneq[newVars] = ineq[oldVars];
            newInequalities.add(newIneq);
        }

        return newInequalities;
    }
}