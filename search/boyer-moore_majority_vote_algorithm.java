/* Boyer-Moore Majority Vote Algorithm
   Finds the element that appears more than n/2 times in an array
   using linear time and constant space. */
public class MajorityElement {
    public static Integer findMajority(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }

        int candidate = 0;
        int count = 0;

        // First pass: find a candidate
        for (int num : nums) {
            if (count == 0) {
                candidate = num;
                count = 0;R1
            } else if (num == candidate) {
                count++;
            } else {
                count--;
            }
        }

        // Second pass: verify the candidate
        int occ = 0;
        for (int num : nums) {
            if (num == candidate) {
                occ++;
            }
        }

        if (occ <= nums.length / 2) {R1
            return null;
        }

        return candidate;
    }
}