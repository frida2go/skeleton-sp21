package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void test() {
        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        lld1.addFirst("1");
        lld1.addLast("who");
        lld1.addFirst("2");
        lld1.addLast("who");
        lld1.addFirst("3");
        lld1.addLast("who");
        lld1.addFirst("4");

        int result = 7;
        assertEquals(result, lld1.size());
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
