// import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    final private WeightedQuickUnionUF uf;
    final private int n;
    final private boolean[] grid; // open == true, blocked == false
    private int open;

    // Creates n-by-n grid, with all sites initially blocked. There are two special and encapsulated
    // sites. One site exists at the top of the grid. It is connected to all sites in the first row.
    // An additional site exists at the bottom of the grid. It is connect to all sites in the last
    // row. With these in place, we can much easier ask questions like "isFull" while minimizing
    // complexity. These two sites are always open.
    //
    // Here is an example with n as 3:
    // [   , 0 ,   ] <-- Top site special case
    // [ 1 , 2 , 3 ]
    // [ 4 , 5 , 6 ]
    // [ 7 , 8 , 9 ]
    // [   , 10,   ] <-- Bottom site special case
    //
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("N must be greater than 0");

        int size = (n * n) + 2;
        int topSite = 0;
        int bottomSite = (n * n) + 1;
        int topRowStart = 1;
        int topRowEnd = n;
        int bottomRowStart = Math.max((n * (n - 1)) + 1, 1);
        int bottomRowEnd = n * n;

        this.n = n;
        this.uf = new WeightedQuickUnionUF(size);
        this.grid = new boolean[size];
        this.open = 0;

        // Top site.
        grid[0] = true;
        for (int i = topRowStart; i <= topRowEnd; i++) uf.union(i, topSite);

        // Bottom site.
        grid[(n * n) + 1] = true;
        for (int i = bottomRowStart; i <= bottomRowEnd; i++) uf.union(i, bottomSite);

        // Populate the rest of the grid.
        for (int i = 1; i <= n * n; i++) this.grid[i] = false;
    }

    private int[] plot(int row, int col) {
        int offset = (row * n) - n;
        int self = offset + col;
        int top = self - n;
        int right = self + 1;
        int bottom = self + n;
        int left = self - 1;

        if (row == 1) top = -1;
        if (col == n) right = -1;
        if (row == n) bottom = -1;
        if (col == 1) left = -1;

        return new int[] {self, top, right, bottom, left};
    }

    // Opens the site (row, col) if it is not open already. Specifically, if the neighbor sites
    // are each valid grid addresses (i.e. positive) and they also open, like we are now, establish
    // a connection. We will duplicate work by re-unioning existing unioned sites, however
    // WeightedQuickUnionUF union time complexity is O(lg n) and we favor that over additional
    // space complexity. The alternative is to give up correctness without the open check.
    //
    // Worst case time complexity with WeightedQuickUnionUF is still O(lg n) since we drop constants.
    public void open(int row, int col) {
        if (row <= 0 || row > n) throw new IllegalArgumentException("Row must be in range");
        if (col <= 0 || col > n) throw new IllegalArgumentException("Col must be in range");

        int[] p = plot(row, col);

        grid[p[0]] = true;
        this.open++;
        if (p[1] >= 0 && grid[p[1]]) uf.union(p[0], p[1]);
        if (p[2] >= 0 && grid[p[2]]) uf.union(p[0], p[2]);
        if (p[3] >= 0 && grid[p[3]]) uf.union(p[0], p[3]);
        if (p[4] >= 0 && grid[p[4]]) uf.union(p[0], p[4]);
    }

    // Is the site (row, col) open? Specifically, we do a simple array lookup in O(1) time.
    public boolean isOpen(int row, int col) {
        if (row <= 0 || row > n) throw new IllegalArgumentException("Row must be in range");
        if (col <= 0 || col > n) throw new IllegalArgumentException("Col must be in range");

        int[] p = plot(row, col);

        return grid[p[0]];
    }

    // Is the site (row, col) full? Specifically, we ask if the site at hand is connected to the
    // top site which is has same complexity as the implementing UF connected method. With a
    // WeightedQuickUnionUF this would be O(lg n).
    public boolean isFull(int row, int col) {
        if (row <= 0 || row > n) throw new IllegalArgumentException("Row must be in range");
        if (col <= 0 || col > n) throw new IllegalArgumentException("Col must be in range");

        int[] p = plot(row, col);

        return isOpen(row, col) && uf.connected(p[0], 0);
    }

    // Returns the number of open sites. The time complexity is O(1). We do not count the top site
    // or bottom site special cases since we encapsulate this information.
    public int numberOfOpenSites() {
        return this.open;
    }

    // Does the system percolate? Specfically, we check if the special cases (top and bottom)
    // are connected.
    public boolean percolates() {
        if (n == 1) {
            return numberOfOpenSites() == 1;
        } else {
            return uf.connected(0, (n * n) + 1);
        }
    }

    // test client (optional)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        Percolation perc = new Percolation(n);

        while (!StdIn.isEmpty()) {
            String m = StdIn.readString();
            int r = StdIn.readInt();
            int c = StdIn.readInt();

            StdOut.print(m + "(" + r + ", " + c + "): ");

            if (m.equals("open")) {
                perc.open(r, c);
                StdOut.println("void");
            } else if (m.equals("isOpen")) {
                StdOut.println(perc.isOpen(r, c));
            } else if (m.equals("isFull")) {
                StdOut.println(perc.isFull(r, c));
            } else {
                StdOut.println("unknown");
            }
        }

        StdOut.println("numberOfOpenSites(): " + perc.numberOfOpenSites());
        StdOut.println("percolates(): " + perc.percolates());
    }
}
