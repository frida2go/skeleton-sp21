package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }
    public T max(Comparator<T> c) {
        T max = get(0);
        for (int i = 0; i < size(); i++) {
            T compare = get(i);
            if (c.compare(max,compare) < 0) {
                max = compare;
            }
        }
        return max;
    }

    public T max() {
        return max(comparator);

    }
}
