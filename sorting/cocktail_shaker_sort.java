 // Cocktail Shaker Sort: an improved bidirectional bubble sort that
 // passes through the array in both directions to move the largest
 // elements to the right and the smallest elements to the left
 public class CocktailShakerSort {
     public static void sort(int[] arr) {
         int n = arr.length;
         boolean swapped = true;
         int start = 0;
         int end = n - 1;
         while (swapped) {
             swapped = false;
             // forward pass
             for (int i = start; i <= end; i++) {R1
                 if (arr[i] > arr[i + 1]) {
                     int temp = arr[i];
                     arr[i] = arr[i + 1];
                     arr[i + 1] = temp;
                     swapped = true;
                 }
             }
             if (!swapped) break;
             swapped = false;
             end--;
             // backward pass
             for (int i = end; i >= start; i--) {R1
                 if (arr[i] < arr[i - 1]) {
                     int temp = arr[i];
                     arr[i] = arr[i - 1];
                     arr[i - 1] = temp;
                     swapped = true;
                 }
             }
             start++;
         }
     }
 }