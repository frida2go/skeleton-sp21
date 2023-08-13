package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList Ns = new AList<Integer>();
        AList Times = new AList<Double>();
        AList opCounts = new AList<Integer>();

        int n = 1000;
        while (n < 128001){
            Ns.addLast(n);
            SLList Test = new SLList<Integer>();

            int count = 0;
            while (count < n) {
                Test.addFirst(n);
                count++;
            }

            n = n * 2;
            int times = 10000;
            opCounts.addLast(times);

            Stopwatch sw = new Stopwatch();
            while (times > 0){
                Test.getLast();
                times --;
            }
            double timeInSeconds = sw.elapsedTime();
            Times.addLast(timeInSeconds);
        }
        printTimingTable(Ns,Times, opCounts);
    }

}
