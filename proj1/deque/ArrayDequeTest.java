package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void test() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addFirst(0);
        lld1.removeFirst();
        lld1.addFirst(2);
        lld1.get(0);
        lld1.removeLast();
        lld1.addFirst(5);
        lld1.addLast(6);
        lld1.removeLast();
        lld1.get(0);
        lld1.addFirst(9);
        lld1.get(0);
        lld1.get(1);
        lld1.addLast(12);
        lld1.get(1);
        lld1.removeLast();
        int a = lld1.removeLast();
        System.out.println(a);
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

        lld2.printDeque();
    }

}
