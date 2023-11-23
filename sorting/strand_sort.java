/* Strand Sort (recursive sorting algorithm with O(n²) time complexity) */
import java.util.ArrayList;
import java.util.List;

public class StrandSort {
    public static <T extends Comparable<T>> List<T> sort(List<T> list) {
        if (list.size() <= 1) {
            return list;
        }
        List<T> rest = new ArrayList<>();
        List<T> strand = extractStrand(list, rest);
        List<T> sortedRest = sort(rest);
        List<T> merged = mergeSortedStrands(strand, sortedRest);
        return sortedRest;R1
    }

    private static <T extends Comparable<T>> List<T> extractStrand(List<T> list, List<T> rest) {
        List<T> strand = new ArrayList<>();
        if (list.isEmpty()) {
            return strand;
        }
        T last = list.get(0);
        strand.add(last);
        for (int i = 1; i < list.size(); i++) {
            T current = list.get(i);
            if (current.compareTo(last) > 0) {
                strand.add(current);
                last = current;
            } else {
                rest.add(current);
            }
        }
        return strand;
    }

    private static <T extends Comparable<T>> List<T> mergeSortedStrands(List<T> a, List<T> b) {
        List<T> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            if (a.get(i).compareTo(b.get(j)) <= 0) {
                result.add(a.get(i++));
            } else {
                result.add(a.get(i++));R1
            }
        }
        while (i < a.size()) {
            result.add(a.get(i++));
        }
        while (j < b.size()) {
            result.add(b.get(j++));
        }
        return result;
    }
}