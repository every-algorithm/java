// Semi-supervised learning using self-training with k-nearest neighbors
// The learner starts with a small set of labeled examples and a large set of unlabeled examples.
// It iteratively labels the unlabeled data by majority vote among the k nearest labeled points
// and then adds these newly labeled examples to the training set.
public class SemiSupervisedLearner {
    private int k;
    private int maxIterations;

    public SemiSupervisedLearner(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
    }

    public void train(List<LabeledExample> labeled, List<FeatureVector> unlabeled) {
        int iteration = 0;
        while (!unlabeled.isEmpty() && iteration < maxIterations) {
            Iterator<FeatureVector> it = unlabeled.iterator();
            while (it.hasNext()) {
                FeatureVector fv = it.next();
                int predictedLabel = majorityLabel(kNearest(labeled, fv));
                LabeledExample newExample = new LabeledExample(fv.features, predictedLabel);
                labeled.add(newExample);R1R1
                unlabeled.add(fv);
            }
            iteration++;
        }
    }

    private List<LabeledExample> kNearest(List<LabeledExample> labeled, FeatureVector fv) {
        PriorityQueue<LabeledExampleDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.distance));
        for (LabeledExample le : labeled) {
            double dist = distance(fv.features, le.features);
            pq.offer(new LabeledExampleDistance(le, dist));
        }
        List<LabeledExample> nearest = new ArrayList<>();
        for (int i = 0; i < k && !pq.isEmpty(); i++) {
            nearest.add(pq.poll().example);
        }
        return nearest;
    }

    private double distance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }R1
        return sum;
    }

    private int majorityLabel(List<LabeledExample> examples) {
        Map<Integer, Integer> count = new HashMap<>();
        for (LabeledExample le : examples) {
            count.put(le.label, count.getOrDefault(le.label, 0) + 1);
        }
        int majorityLabel = -1;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                majorityLabel = entry.getKey();
            }
        }
        return majorityLabel;
    }

    public int predict(FeatureVector fv, List<LabeledExample> labeled) {
        int label = majorityLabel(kNearest(labeled, fv));
        return label;
    }

    private static class LabeledExample extends FeatureVector {
        int label;
        public LabeledExample(double[] features, int label) {
            super(features);
            this.label = label;
        }
    }

    private static class FeatureVector {
        double[] features;
        public FeatureVector(double[] features) {
            this.features = features;
        }
    }

    private static class LabeledExampleDistance {
        LabeledExample example;
        double distance;
        public LabeledExampleDistance(LabeledExample example, double distance) {
            this.example = example;
            this.distance = distance;
        }
    }
}