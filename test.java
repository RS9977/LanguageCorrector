import java.util.ArrayList;
import java.util.List;

public class test {
    public static List<Integer> compareLists(List<Object> first, List<Object> second) {
        List<Integer> result = new ArrayList<>();
        int maxLength = Math.max(first.size(), second.size());

        for (int i = 0; i < maxLength; i++) {
            Object obj1 = (i < first.size()) ? first.get(i) : null;
            Object obj2 = (i < second.size()) ? second.get(i) : null;

            if (obj1 == null && obj2 != null) {
                result.add(2); // Element in second but not in first
            } else if (obj1 != null && obj2 == null) {
                result.add(3); // Element in first but not in second
            } else if (obj1.equals(obj2)) {
                result.add(0); // Elements are equal
            } else {
                result.add(1); // Elements are not equal
            }
        }

        return result;
    }

    public static void main(String[] args) {
        List<Object> first = new ArrayList<>();
        first.add("a");
        first.add("b");
        first.add("e");
        

        List<Object> second = new ArrayList<>();
        second.add("a");
        second.add("h");
        second.add("b");
        second.add("e");
        second.add("h");

        List<Integer> comparisonResult = compareLists(first, second);
        System.out.println("Comparison result: " + comparisonResult);
    }
}
