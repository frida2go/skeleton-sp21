package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void test() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addFirst(0);
        lld1.addFirst(1);
        lld1.addLast(2);
        lld1.addFirst(3);
        lld1.addLast(4);
        lld1.addLast(5);
        lld1.removeLast();
        lld1.addFirst(7);
        lld1.removeFirst();
        lld1.removeLast();
        lld1.get(2);
        lld1.addFirst(11);
        lld1.addFirst(12);
        lld1.get(4);
        lld1.removeFirst();
        lld1.removeFirst();
        lld1.get(2);
        lld1.removeFirst();
        lld1.addFirst(18);
        lld1.addLast(19);
    }
    @Test
    public void testBig() {
        ArrayDeque<Integer> lld2 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            lld2.addLast(i);
        }
        for (int i = 0; i < 9999; i++) {
            int a = lld2.removeLast();
        }

    }

}
