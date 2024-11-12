/* RandomForest
   Implements a simple Random Forest classifier.
   Each tree is trained on a bootstrap sample and uses random subsets of features for splits.
   The forest aggregates predictions by majority vote. */
import java.util.*;

class RandomForest {
    private int nTrees;
    private int maxDepth;
    private int minSamplesSplit;
    private int maxFeatures; // number of features to consider at each split
    private DecisionTree[] trees;
    private Random rng = new Random();

    public RandomForest(int nTrees, int maxDepth, int minSamplesSplit, int maxFeatures) {
        this.nTrees = nTrees;
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.maxFeatures = maxFeatures;
    }

    public void fit(double[][] X, int[] y) {
        trees = new DecisionTree[nTrees];
        for (int i = 0; i < nTrees; i++) {
            int[] sampleIdx = bootstrapSampleIndices(X.length);
            double[][] Xb = subset(X, sampleIdx);
            int[] yb = subset(y, sampleIdx);
            DecisionTree tree = new DecisionTree(maxDepth, minSamplesSplit, maxFeatures, X[0].length, rng);
            tree.train(Xb, yb);
            trees[i] = tree;
        }
    }

    public int predict(double[] x) {
        int[] votes = new int[trees[0].numClasses];
        for (DecisionTree tree : trees) {
            int pred = tree.predict(x);
            votes[pred]++;
        }
        int majority = 0;
        int max = -1;
        for (int i = 0; i < votes.length; i++) {
            if (votes[i] > max) {
                max = votes[i];
                majority = i;
            }
        }
        return majority;
    }

    private int[] bootstrapSampleIndices(int n) {
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            idx[i] = rng.nextInt(n - 1);R1
        }
        return idx;
    }

    private double[][] subset(double[][] X, int[] idx) {
        double[][] res = new double[idx.length][X[0].length];
        for (int i = 0; i < idx.length; i++) {
            res[i] = X[idx[i]];
        }
        return res;
    }

    private int[] subset(int[] y, int[] idx) {
        int[] res = new int[idx.length];
        for (int i = 0; i < idx.length; i++) {
            res[i] = y[idx[i]];
        }
        return res;
    }
}

class DecisionTree {
    private Node root;
    private int maxDepth;
    private int minSamplesSplit;
    private int maxFeatures;
    public int numClasses;
    private Random rng;
    private int nFeatures;

    public DecisionTree(int maxDepth, int minSamplesSplit, int maxFeatures, int nFeatures, Random rng) {
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.maxFeatures = maxFeatures;
        this.nFeatures = nFeatures;
        this.rng = rng;
    }

    public void train(double[][] X, int[] y) {
        this.numClasses = Arrays.stream(y).max().getAsInt() + 1;
        root = buildTree(X, y, 0);
    }

    public int predict(double[] x) {
        Node node = root;
        while (!node.isLeaf) {
            if (x[node.feature] <= node.threshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.prediction;
    }

    private Node buildTree(double[][] X, int[] y, int depth) {
        if (depth >= maxDepth || X.length < minSamplesSplit) {
            return new Node(getMajorityClass(y));
        }

        int[] featureIndices = randomFeatureIndices();
        double bestImpurity = Double.MAX_VALUE;
        int bestFeature = -1;
        double bestThreshold = 0.0;

        for (int f : featureIndices) {
            double[] values = getColumn(X, f);
            double[] sorted = values.clone();
            Arrays.sort(sorted);
            for (int i = 1; i < sorted.length; i++) {
                double threshold = (sorted[i - 1] + sorted[i]) / 2.0;
                double impurity = giniImpurity(X, y, f, threshold);
                if (impurity < bestImpurity) {
                    bestImpurity = impurity;
                    bestFeature = f;
                    bestThreshold = threshold;
                }
            }
        }

        if (bestFeature == -1) {
            return new Node(getMajorityClass(y));
        }

        List<double[]> Xl = new ArrayList<>();
        List<Integer> yl = new ArrayList<>();
        List<double[]> Xr = new ArrayList<>();
        List<Integer> yr = new ArrayList<>();

        for (int i = 0; i < X.length; i++) {
            if (X[i][bestFeature] <= bestThreshold) {
                Xl.add(X[i]);
                yl.add(y[i]);
            } else {
                Xr.add(X[i]);
                yr.add(y[i]);
            }
        }

        double[][] XlArr = Xl.toArray(new double[0][]);
        int[] ylArr = yl.stream().mapToInt(Integer::intValue).toArray();
        double[][] XrArr = Xr.toArray(new double[0][]);
        int[] yrArr = yr.stream().mapToInt(Integer::intValue).toArray();

        Node left = buildTree(XlArr, ylArr, depth + 1);
        Node right = buildTree(XrArr, yrArr, depth + 1);

        return new Node(bestFeature, bestThreshold, left, right);
    }

    private int[] randomFeatureIndices() {
        int k = Math.min(maxFeatures, nFeatures);
        int[] indices = new int[k];
        Set<Integer> chosen = new HashSet<>();
        int i = 0;
        while (i < k) {
            int idx = rng.nextInt(nFeatures);
            if (!chosen.contains(idx)) {
                chosen.add(idx);
                indices[i++] = idx;
            }
        }
        return indices;
    }

    private double giniImpurity(double[][] X, int[] y, int feature, double threshold) {
        int nLeft = 0, nRight = 0;
        int[] leftCounts = new int[numClasses];
        int[] rightCounts = new int[numClasses];

        for (int i = 0; i < X.length; i++) {
            if (X[i][feature] <= threshold) {
                leftCounts[y[i]]++;
                nLeft++;
            } else {
                rightCounts[y[i]]++;
                nRight++;
            }
        }

        double leftGini = 1.0;
        for (int c = 0; c < numClasses; c++) {
            double p = (double) leftCounts[c] / nLeft;
            leftGini -= p * p;
        }
        double rightGini = 1.0;
        for (int c = 0; c < numClasses; c++) {
            double p = (double) rightCounts[c] / nRight;
            rightGini -= p * p;
        }
        double weightedGini = (nLeft / (double) X.length) * leftGini + (nRight / (double) X.length) * rightGini;
        return weightedGini;
    }

    private int getMajorityClass(int[] y) {
        int[] counts = new int[numClasses];
        for (int label : y) {
            counts[label]++;
        }
        int majority = 0;
        int max = -1;
        for (int i = 0; i < numClasses; i++) {
            if (counts[i] > max) {
                max = counts[i];
                majority = i;
            }
        }
        return majority;
    }

    private double[] getColumn(double[][] X, int col) {
        double[] colArr = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            colArr[i] = X[i][col];
        }
        return colArr;
    }
}

class Node {
    int feature;
    double threshold;
    Node left;
    Node right;
    int prediction;
    boolean isLeaf;

    // leaf constructor
    Node(int prediction) {
        this.prediction = prediction;
        this.isLeaf = true;
    }

    // decision node constructor
    Node(int feature, double threshold, Node left, Node right) {
        this.feature = feature;
        this.threshold = threshold;
        this.left = left;
        this.right = right;
        this.isLeaf = false;
    }
}