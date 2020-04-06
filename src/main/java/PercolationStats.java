import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    final static private double CONFIDENCE_95 = 1.96;
    final private int n;
    final private int trials;
    final private int[] results;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        this.n = n;
        this.trials = trials;
        this.results = new int[trials];

        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            int t = 0;
            int row, col;

            while (!perc.percolates()) {
                row = StdRandom.uniform(1, n + 1);
                col = StdRandom.uniform(1, n + 1);

                if (!perc.isOpen(row, col)) {
                    t++;
                    perc.open(row, col);
                }
            }

            this.results[i] = t;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        double grid = this.n * this.n;
        double[] ratios = new double[this.trials];

        for (int i = 0; i < this.trials; i++) {
            ratios[i] = this.results[i] / grid;
        }

        return StdStats.mean(ratios);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        double grid = this.n * this.n;
        double[] ratios = new double[this.trials];

        for (int i = 0; i < this.trials; i++) {
            ratios[i] = this.results[i] / grid;
        }

        return StdStats.stddev(ratios);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double mean = mean();
        double stddev = stddev();

        return mean - ((CONFIDENCE_95 * stddev) / Math.sqrt(this.trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double mean = mean();
        double stddev = stddev();

        return mean + ((CONFIDENCE_95 * stddev) / Math.sqrt(this.trials));
    }

   // test client (see below)
   public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        StdOut.println("Starting with " + n + " and " + trials + "...");

        PercolationStats stats = new PercolationStats(n, trials);

        StdOut.println("mean                    = " + stats.mean());
        StdOut.println("stddev:                 = " + stats.stddev());
        StdOut.println("95% confidence interval = [" + stats.confidenceLo() +  ", " + stats.confidenceHi() + "]");
   }
}
