package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void test() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addFirst(0);
        lld1.addFirst(1);
        lld1.removeLast();
        lld1.addFirst(3);
        lld1.addFirst(4);
        lld1.addFirst(5);
        lld1.addFirst(6);
        lld1.removeLast();
        lld1.printDeque();

    }
    @Test
    public void testBig() {
        ArrayDeque<Integer> lld2 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            lld2.addLast(i);
        }
        assertEquals(10000,lld2.size());
        int a = lld2.get(9999);
        assertEquals(a,9999);
        lld2.printDeque();
    }

}
