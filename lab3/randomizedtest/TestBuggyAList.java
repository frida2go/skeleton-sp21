package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void Test () {
        AListNoResizing right = new AListNoResizing<>();
        BuggyAList wrong = new BuggyAList();
        right.addLast(5);
        right.addLast(10);
        right.addLast(15);

        wrong.addLast(5);
        wrong.addLast(10);
        wrong.addLast(15);

        assertEquals(right.size(), wrong.size());

        assertEquals(right.removeLast(), wrong.removeLast());
        assertEquals(right.removeLast(), wrong.removeLast());
        assertEquals(right.removeLast(), wrong.removeLast());
        
    }

    @Test
    public void RandomTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> W = new BuggyAList<>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                W.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeW = W.size();
                System.out.println("size: " + size);
            } else if (operationNumber == 2 ){
                // removeLast
                if (L.size() > 0){
                    int randVal = StdRandom.uniform(0, 100);
                    int Last = L.getLast();
                    L.removeLast();
                    W.removeLast();
                    System.out.println("removeLast(" + Last + ")");
                }

            }
        }
    }
    

}
