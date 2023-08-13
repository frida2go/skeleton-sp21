package timingtest;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList Ns = new AList<Integer>();
        AList Times = new AList<Double>();
        AList opCounts = new AList<Integer>();
        int n = 1000;
        while (n < 128001){
            Ns.addLast(n);
            Stopwatch sw = new Stopwatch();
            AList Test = new AList<Integer>();
            int count = 0;
            while (count < n) {
                Test.addLast(n);
                count++;
            }
            n = n * 2;
            opCounts.addLast(count);
            double timeInSeconds = sw.elapsedTime();
            Times.addLast(timeInSeconds);
        }


        printTimingTable(Ns,Times, opCounts);
    }
}
