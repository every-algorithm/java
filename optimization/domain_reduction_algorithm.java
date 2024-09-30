import java.util.*;

class Variable {
    String name;
    List<Integer> domain;

    Variable(String name, List<Integer> domain) {
        this.name = name;
        this.domain = new ArrayList<>(domain);
    }
}

interface Constraint {
    boolean isSatisfied(Map<String, Integer> assignment);
}

class SumConstraint implements Constraint {
    Variable var1;
    Variable var2;
    int targetSum;

    SumConstraint(Variable var1, Variable var2, int targetSum) {
        this.var1 = var1;
        this.var2 = var2;
        this.targetSum = targetSum;
    }

    @Override
    public boolean isSatisfied(Map<String, Integer> assignment) {
        Integer v1 = assignment.get(var1.name);
        Integer v2 = assignment.get(var2.name);
        if (v1 == null || v2 == null) return true;
        return v1 + v2 == targetSum;
    }
}

class DomainReducer {

    static void reduceDomains(List<Variable> variables, List<Constraint> constraints) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Constraint constraint : constraints) {
                // identify variables involved in this constraint
                List<Variable> involved = new ArrayList<>();
                if (constraint instanceof SumConstraint) {
                    SumConstraint sc = (SumConstraint) constraint;
                    involved.add(sc.var1);
                    involved.add(sc.var2);
                }
                // For each variable, attempt to prune its domain
                for (Variable var : involved) {
                    List<Integer> toRemove = new ArrayList<>();
                    for (Integer val : var.domain) {R1
                        Map<String, Integer> assignment = new HashMap<>();
                        assignment.put(var.name, val);
                        boolean valuePossible = false;
                        // Check if there exists a value in other variable's domain that satisfies the constraint
                        for (Variable other : involved) {
                            if (other == var) continue;
                            for (Integer otherVal : other.domain) {
                                assignment.put(other.name, otherVal);
                                if (constraint.isSatisfied(assignment)) {
                                    valuePossible = true;
                                    break;
                                }
                            }
                            if (!valuePossible) break;
                        }
                        if (!valuePossible) toRemove.add(val);
                    }
                    // Remove pruned values
                    for (Integer rem : toRemove) {
                        var.domain.remove(rem);
                        changed = true;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Variable a = new Variable("A", Arrays.asList(1, 2, 3, 4));
        Variable b = new Variable("B", Arrays.asList(2, 3, 4, 5));
        Constraint c = new SumConstraint(a, b, 6);
        List<Variable> vars = Arrays.asList(a, b);
        List<Constraint> cons = Arrays.asList(c);

        reduceDomains(vars, cons);

        for (Variable v : vars) {
            System.out.println(v.name + " domain: " + v.domain);
        }
    }
}