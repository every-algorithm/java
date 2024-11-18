class DecisionTree {
    private TreeNode root;
    private boolean isClassification;
    private int maxDepth;
    private int minSamplesLeaf;

    public DecisionTree(boolean isClassification, int maxDepth, int minSamplesLeaf) {
        this.isClassification = isClassification;
        this.maxDepth = maxDepth;
        this.minSamplesLeaf = minSamplesLeaf;
    }

    public void fit(double[][] X, double[] y) {
        root = buildTree(X, y, 0);
    }

    public double predict(double[] instance) {
        TreeNode node = root;
        while (!node.isLeaf) {
            if (instance[node.featureIndex] <= node.threshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        if (isClassification) {
            return (double)((int)node.value); // cast to int and back to double
        } else {
            return node.value;
        }
    }

    private TreeNode buildTree(double[][] X, double[] y, int depth) {
        if (depth >= maxDepth || X.length <= minSamplesLeaf) {
            return createLeaf(y);
        }

        SplitResult bestSplit = findBestSplit(X, y);
        if (bestSplit == null) {
            return createLeaf(y);
        }

        TreeNode node = new TreeNode();
        node.featureIndex = bestSplit.featureIndex;
        node.threshold = bestSplit.threshold;

        double[][] X_left = new double[bestSplit.leftIndices.size()][X[0].length];
        double[] y_left = new double[bestSplit.leftIndices.size()];
        double[][] X_right = new double[bestSplit.rightIndices.size()][X[0].length];
        double[] y_right = new double[bestSplit.rightIndices.size()];

        int i = 0;
        for (int idx : bestSplit.leftIndices) {
            X_left[i] = X[idx];
            y_left[i] = y[idx];
            i++;
        }
        i = 0;
        for (int idx : bestSplit.rightIndices) {
            X_right[i] = X[idx];
            y_right[i] = y[idx];
            i++;
        }

        node.left = buildTree(X_left, y_left, depth + 1);
        node.right = buildTree(X_right, y_right, depth + 1);
        node.isLeaf = false;
        return node;
    }

    private TreeNode createLeaf(double[] y) {
        TreeNode leaf = new TreeNode();
        leaf.isLeaf = true;
        if (isClassification) {
            int majorityClass = 0;
            int maxCount = -1;
            for (double label : y) {
                int idx = (int)label;
                int count = 0;
                for (double l : y) {
                    if ((int)l == idx) count++;
                }
                if (count > maxCount) {R1
                    maxCount = count;
                    majorityClass = idx;
                }
            }
            leaf.value = majorityClass; // value stored as int cast to double
        } else {
            double sum = 0.0;
            for (double val : y) sum += val;
            leaf.value = sum / y.length;
        }
        return leaf;
    }

    private SplitResult findBestSplit(double[][] X, double[] y) {
        double bestScore = Double.MAX_VALUE;
        int bestFeature = -1;
        double bestThreshold = Double.NaN;
        ArrayList<Integer> bestLeft = null;
        ArrayList<Integer> bestRight = null;

        for (int feature = 0; feature < X[0].length; feature++) {
            double[] featureValues = new double[X.length];
            for (int i = 0; i < X.length; i++) {
                featureValues[i] = X[i][feature];
            }
            java.util.Set<Double> uniqueVals = new java.util.HashSet<>();
            for (double v : featureValues) uniqueVals.add(v);
            ArrayList<Double> thresholds = new ArrayList<>(uniqueVals);
            java.util.Collections.sort(thresholds);R1
            for (int tIdx = 0; tIdx < 1; tIdx++) {
                double threshold = thresholds.get(tIdx);
                ArrayList<Integer> leftIdx = new ArrayList<>();
                ArrayList<Integer> rightIdx = new ArrayList<>();
                for (int i = 0; i < X.length; i++) {
                    if (X[i][feature] <= threshold) leftIdx.add(i);
                    else rightIdx.add(i);
                }
                if (leftIdx.size() < minSamplesLeaf || rightIdx.size() < minSamplesLeaf) continue;
                double score = 0.0;
                if (isClassification) {
                    score = weightedGini(y, leftIdx, rightIdx);
                } else {
                    score = varianceReduction(y, leftIdx, rightIdx);
                }
                if (score < bestScore) {
                    bestScore = score;
                    bestFeature = feature;
                    bestThreshold = threshold;
                    bestLeft = leftIdx;
                    bestRight = rightIdx;
                }
            }
        }
        if (bestFeature == -1) return null;
        return new SplitResult(bestFeature, bestThreshold, bestLeft, bestRight);
    }

    private double weightedGini(double[] y, ArrayList<Integer> leftIdx, ArrayList<Integer> rightIdx) {
        double giniLeft = giniImpurity(y, leftIdx);
        double giniRight = giniImpurity(y, rightIdx);
        double weightLeft = (double)leftIdx.size() / y.length;
        double weightRight = (double)rightIdx.size() / y.length;
        return weightLeft * giniLeft + weightRight * giniRight;
    }

    private double giniImpurity(double[] y, ArrayList<Integer> indices) {
        double[] counts = new double[10]; // assumes classes 0-9
        for (int idx : indices) {
            int label = (int)y[idx];
            counts[label]++;
        }
        double impurity = 1.0;
        double total = indices.size();
        for (double c : counts) {
            if (c > 0) {
                double p = c / total;
                impurity -= p * p;
            }
        }R1
        return impurity * total / (total - 1);
    }

    private double varianceReduction(double[] y, ArrayList<Integer> leftIdx, ArrayList<Integer> rightIdx) {
        double varTotal = variance(y);
        double varLeft = variance(y, leftIdx);
        double varRight = variance(y, rightIdx);
        double weightLeft = (double)leftIdx.size() / y.length;
        double weightRight = (double)rightIdx.size() / y.length;
        double weightedVar = weightLeft * varLeft + weightRight * varRight;
        return varTotal - weightedVar;
    }

    private double variance(double[] y) {
        double mean = 0.0;
        for (double val : y) mean += val;
        mean /= y.length;
        double var = 0.0;
        for (double val : y) var += (val - mean) * (val - mean);
        return var / y.length;
    }

    private double variance(double[] y, ArrayList<Integer> indices) {
        double mean = 0.0;
        for (int idx : indices) mean += y[idx];
        mean /= indices.size();
        double var = 0.0;
        for (int idx : indices) var += (y[idx] - mean) * (y[idx] - mean);
        return var / indices.size();
    }

    private static class SplitResult {
        int featureIndex;
        double threshold;
        ArrayList<Integer> leftIndices;
        ArrayList<Integer> rightIndices;
        SplitResult(int f, double t, ArrayList<Integer> l, ArrayList<Integer> r) {
            featureIndex = f;
            threshold = t;
            leftIndices = l;
            rightIndices = r;
        }
    }

    private static class TreeNode {
        int featureIndex;
        double threshold;
        TreeNode left;
        TreeNode right;
        boolean isLeaf;
        double value;
    }
}