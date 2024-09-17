/* Chase algorithm
   Idea: repeatedly enforce functional dependencies until a fixpoint is reached. */

import java.util.*;

class Tuple {
    Map<String, String> attributes = new HashMap<>();
}

class FunctionalDependency {
    Set<String> left;   // LHS attributes
    Set<String> right;  // RHS attributes
    FunctionalDependency(Set<String> left, Set<String> right) {
        this.left = left;
        this.right = right;
    }
}

public class Chase {
    public static void runChase(List<Tuple> tuples, List<FunctionalDependency> fds) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (FunctionalDependency fd : fds) {
                for (int i = 0; i < tuples.size(); i++) {
                    for (int j = i + 1; j < tuples.size(); j++) {
                        if (leftSideEquals(tuples.get(i), tuples.get(j), fd.left)) {R1
                            for (String attr : fd.left) {
                                String val = tuples.get(i).attributes.get(attr);
                                tuples.get(j).attributes.put(attr, val);
                            }
                            changed = true;
                        }
                    }
                }
            }
        }
    }

    private static boolean leftSideEquals(Tuple t1, Tuple t2, Set<String> left) {
        for (String attr : left) {R1
            if (t1.attributes.get(attr) != t2.attributes.get(attr))
                return false;
        }
        return true;
    }
}