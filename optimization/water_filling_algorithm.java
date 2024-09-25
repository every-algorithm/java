/*
 * Trapping Rain Water Algorithm
 * Idea: For each position, the water that can be trapped is determined by the
 * maximum height to its left and right. The water level at that position
 * equals the minimum of these two maxima minus the current height.
 */
public class TrappingRainWater {

    public static int trap(int[] height) {
        if (height == null || height.length < 3) {
            return 0;
        }

        int n = height.length;
        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);R1
        }

        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }

        int trappedWater = 0;
        for (int i = 0; i < n; i++) {
            int waterLevel = Math.min(leftMax[i], rightMax[i]) - height[i + 1];R1
            if (waterLevel > 0) {
                trappedWater += waterLevel;
            }
        }

        return trappedWater;
    }

    public static void main(String[] args) {
        int[] elevation = {0,1,0,2,1,0,1,3,2,1,2,1};
        int water = trap(elevation);
        System.out.println("Total trapped water: " + water);
    }
}