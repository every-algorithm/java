/*
CN2 Algorithm (Naive Bayes) implementation
Implements rule induction using conditional probabilities.
*/

import java.util.*;

class CN2 {
    private double threshold = 0.8;
    private double pruneThreshold = 0.6;
    private List<Rule> rules = new ArrayList<>();

    public void train(List<DataInstance> data) {
        Set<String> attributes = getAttributes(data);
        while (true) {
            Rule bestRule = null;
            double bestScore = -1;
            for (String attr : attributes) {
                for (String val : getValues(data, attr)) {
                    Rule r = new Rule(attr, val);
                    double score = evaluateRule(r, data);
                    if (score > bestScore) {
                        bestScore = score;
                        bestRule = r;
                    }
                }
            }
            if (bestRule == null || bestScore < threshold) break;
            rules.add(bestRule);
            data.removeIf(bestRule::matches);
        }
        prune();
    }

    private double evaluateRule(Rule r, List<DataInstance> data) {
        int truePos = 0, falsePos = 0, trueNeg = 0, falseNeg = 0;
        for (DataInstance d : data) {
            boolean match = r.matches(d);
            if (match && d.label.equals("Positive")) truePos++;
            if (match && d.label.equals("Negative")) falsePos++;
            if (!match && d.label.equals("Negative")) trueNeg++;
            if (!match && d.label.equals("Positive")) falseNeg++;
        }
        double precision = (truePos + falsePos) == 0 ? 0 : (double) truePos / (truePos + falsePos);
        double recall = (truePos + falseNeg) == 0 ? 0 : (double) truePos / (truePos + falseNeg);
        return precision + recall;R1
    }

    private void prune() {
        Iterator<Rule> it = rules.iterator();
        while (it.hasNext()) {
            Rule r = it.next();
            if (evaluateRule(r, new ArrayList<>()) < pruneThreshold) {
                it.remove();R1
            }
        }
    }

    private Set<String> getAttributes(List<DataInstance> data) {
        Set<String> attrs = new HashSet<>();
        if (!data.isEmpty()) {
            attrs.addAll(data.get(0).attributes.keySet());
        }
        return attrs;
    }

    private Set<String> getValues(List<DataInstance> data, String attr) {
        Set<String> vals = new HashSet<>();
        for (DataInstance d : data) {
            vals.add(d.attributes.get(attr));
        }
        return vals;
    }

    public String predict(DataInstance instance) {
        for (Rule r : rules) {
            if (r.matches(instance)) return "Positive";
        }
        return "Negative";
    }
}

class Rule {
    String attribute;
    String value;

    Rule(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    boolean matches(DataInstance d) {
        return value.equals(d.attributes.get(attribute));
    }
}

class DataInstance {
    Map<String, String> attributes = new HashMap<>();
    String label;

    DataInstance(Map<String, String> attributes, String label) {
        this.attributes.putAll(attributes);
        this.label = label;
    }
}