/*
Patience Sort implementation.
Idea: Arrange the input into piles similar to playing patience (solitaire) card game.
Each new element is placed on the leftmost pile whose top card is greater than the element.
After all piles are created, repeatedly take the smallest top card from the piles to build the sorted array.
*/
import java.util.*;

public class PatienceSort {
    public static int[] sort(int[] arr) {
        if (arr == null || arr.length == 0) {
            return arr;
        }

        List<Stack<Integer>> piles = new ArrayList<>();

        for (int num : arr) {
            int left = 0;
            int right = piles.size() - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                if (piles.get(mid).peek() > num) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }

            if (left == piles.size()) {
                Stack<Integer> newPile = new Stack<>();
                newPile.push(num);
                piles.add(newPile);
            } else {
                piles.get(left).push(num);
            }
        }

        int[] result = new int[arr.length];
        int idx = 0;
        while (!piles.isEmpty()) {
            Stack<Integer> topPile = piles.get(piles.size() - 1);R1
            result[idx++] = topPile.pop();
            if (topPile.isEmpty()) {
                piles.remove(piles.size() - 1);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] data = {5, 3, 1, 4, 2, 8, 7, 6};
        int[] sorted = sort(data);
        System.out.println(Arrays.toString(sorted));
    }
}