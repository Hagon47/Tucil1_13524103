public class Board {
    private int N;                  // Ukuran Board
    private char[][] startGrid;     // Grid Area Awal
    private char[][] finalGrid;     // Grid Area Final (Udah ada '#' sebagai penanda Queen)
    private long iterations;        // Jumlah iterasi yang dilakukan

    // Visualisasi
    private boolean showVisuals;    
    private BoardListener listener; 
    private int delay = 50;         

    // Interface
    public interface BoardListener {
        void onStep(char[][] currentGrid);
    }

    // Konstruktor
    public Board(char[][] inputGrid){
        this.N = inputGrid.length;
        this.startGrid = inputGrid;
        this.iterations = 0;

        // Duplicate startGrid awal ke finalGrid
        this.finalGrid = new char[N][N];
        for(int i = 0; i < N; i++){
            System.arraycopy(startGrid[i], 0, finalGrid[i], 0, N);
        }
    }

    // GUI 
    public void setListener(BoardListener listener) {
        this.listener = listener;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    // Selector
    public long getIterations() {
        return iterations;
    }

    public char[][] getFinalGrid() {
        return finalGrid;
    }

    // Validator
    private boolean checkQueen(int row, int col) {

        // Cek Kolom
        for (int i = 0; i < row; i++){
            if (finalGrid[i][col] == '#') {
                return false;
            }
        }

        if (row > 0) {
            // Cek Diagonal Kiri Atas
            if (col > 0 && finalGrid[row-1][col-1] == '#') {
                return false;
            }

            // Cek Diagonal Kanan Atas
            if (col < N - 1 && finalGrid[row-1][col+1] == '#') {
                return false;
            }
        }

        // Cek Area
        char currentArea = startGrid[row][col];
        for (int r = 0; r < row; r++) {
            for (int k = 0; k < N; k++) {
                if (finalGrid[r][k] == '#' && startGrid[r][k] == currentArea) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isCorrect() {
        for (int i = 0; i < N; i++) {
            int colFound = -1;
            for (int j = 0; j < N; j++) {
                if (finalGrid[i][j] == '#') {
                    colFound = j;
                    break;
                }
            }

            if (colFound != -1) {
                if (!checkQueen(i, colFound)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Algoritma
    private boolean solveExhaustive(int row) {
        iterations++;

        // Basis
        if (row == N) {
            return isCorrect();
        }

        // Rekurens
        for (int col = 0; col < N; col++) {
            finalGrid[row][col] = '#';
            printStep(row, col, "Placing (Brute Force)");

            if (solveExhaustive(row + 1)) {
                return true;
            }

            finalGrid[row][col] = startGrid[row][col];
        }

        return false;
    }

    public boolean solve(boolean visualizeBrute) {
        this.showVisuals = visualizeBrute;
        this.iterations = 0;
        return solveExhaustive(0);
    }

    // Helper
    public void printStep(int curRow, int curCol, String status) {
        if (listener != null) {
            char[][] snapshot = new char[N][N];

            for(int i=0; i<N; i++) {
                snapshot[i] = finalGrid[i].clone();
            }
            listener.onStep(snapshot); 
            try {
                if (delay > 0) {
                    Thread.sleep(delay); 
                }
            } catch (InterruptedException e) {

            }
            return; 
        }

        if (showVisuals) {
            try { Thread.sleep(200); } catch (Exception e) {}
    
            System.out.println("\n--- Step-" + iterations + " ---");
            System.out.println(status + " at (" + curRow + ", " + curCol + ")");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    System.out.print(finalGrid[i][j] == '#' ? "[#]" : " " + startGrid[i][j] + " ");
                }
                System.out.println();
            }
        }
    }    
}
