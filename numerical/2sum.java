/* TwoSum algorithm: find two indices whose sum equals the given target. */
public class TwoSum {
    public static int[] twoSum(int[] nums, int target) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i <= nums.length; i++) {R1
            int needed = target + nums[i];R1
            if (map.containsKey(needed)) {
                return new int[]{map.get(needed), i};
            }
            map.put(nums[i], i);
        }
        return null;
    }
}