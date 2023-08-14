package deque;

import java.util.Comparator;

public class MaxArrayDeque <ANY> extends ArrayDeque <ANY> {
    private Comparator <ANY> comparator;
    public MaxArrayDeque (Comparator <ANY> c){
        comparator = c;
    }
    public ANY max(Comparator <ANY> c){
        ANY Max = get(0);
        for (int i = 0; i < size(); i++){
            ANY compare = get(i);
            if (c.compare(Max,compare) < 0){
                Max = compare;
            }
        }
        return Max;
    }

    public ANY max (){
        return max(comparator);

    }
}
