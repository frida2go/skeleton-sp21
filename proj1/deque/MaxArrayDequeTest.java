package deque;

import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;

class MyComparator <T extends Comparable <T>> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }
}

public class MaxArrayDequeTest{
    @Test
    public void firstTest(){
        MaxArrayDeque<Integer> testMax = new MaxArrayDeque <Integer> (new MyComparator<>());
        testMax.addFirst(10);
        testMax.addFirst(15);
        testMax.addFirst(30);

        int a = testMax.max();
        assertEquals(30,a);
    }



}
