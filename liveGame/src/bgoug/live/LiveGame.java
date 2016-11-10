package bgoug.live;

import bgoug.live.model.Cell;
import bgoug.live.model.CellSupplier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LiveGame {

    private final boolean parallelStreams;

    private Cell[][] getCells() {
        return cells;
    }

    private Cell[][] cells;
    private int n, m;

    public LiveGame(int n, int m, boolean parallelStreams) {
        this.n = n;
        this.m = m;
        this.parallelStreams = parallelStreams;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        int m = s.nextInt();
        int cycles = s.nextInt();
        int tmpCycles = cycles;

        LiveGame game = new LiveGame(n, m, false);
        game.initialize(Cell::new);

        s.nextLine();

        printGrid(game.getCells());
        long start = System.currentTimeMillis();

        while (cycles > 0) {
            game.nextIteration();

            // printGrid(game.getCells());

            cycles--;
            // s.nextLine();
        }
        long totalTime = System.currentTimeMillis() - start;
        printGrid(game.getCells());
        System.out.println(String.format("Total time for %1$d iterations is %2$dms",tmpCycles, totalTime));
    }

    /**
     * Run next game iteration
     */
    void nextIteration() {
        constructCellStream(parallelStreams).forEach(this::updateCellState);
        constructCellStream(parallelStreams).forEach(Cell::apply);
    }

    /**
     * Constructs cell stream
     *
     * @param parallel return parallel stream
     * @return {@link Stream} of {@link Cell}
     */
    private Stream<Cell> constructCellStream(boolean parallel) {
        Stream<Cell[]> cellRowsStream = Arrays.stream(cells);
        if (parallel) {
            cellRowsStream = cellRowsStream.parallel();
        }
        Stream<Cell> cellStream = cellRowsStream.flatMap(Arrays::stream);
        if (parallel) {
            cellStream = cellStream.parallel();
        }
        return cellStream;
    }

    /**
     * Initialize cells array and generate random cell status
     */
    void initialize(CellSupplier cellSupplier) {
        cells = new Cell[n][m];

        IntStream.range(0, n).forEach(y ->
                IntStream.range(0, m).forEach(x -> cells[y][x] = cellSupplier.createCell(x, y)));

        Random r = new Random();
        constructCellStream(false).forEach(c -> c.setLive(r.nextBoolean()));
    }

    private void updateCellState(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();

        int startX = x > 0 ? x - 1 : 0;
        int endX = x < n - 1 ? x + 1 : n - 1;

        int startY = y > 0 ? y - 1 : 0;
        int endY = y < m - 1 ? y + 1 : m - 1;

        List<Cell> neighboursList = Arrays.stream(cells, startY, endY + 1)
                                          .flatMap(cellsLine -> Arrays.stream(cellsLine, startX, endX + 1))
                                          .filter(c -> c != cell)
                                          .collect(Collectors.toList());

        Map<Boolean, List<Cell>> liveAndDeadMap = neighboursList.stream()
                                                                .collect(Collectors.groupingBy(Cell::isLive));
        int liveCells = liveAndDeadMap.get(Boolean.TRUE) != null ? liveAndDeadMap.get(Boolean.TRUE).size() : 0;
        if (cell.isLive()) {
            cell.setNextState(!(liveCells < 2 || liveCells > 3));
        } else {
            cell.setNextState(liveCells == 3);
        }
    }

    private static void printGrid(Cell[][] cells) {
        IntStream.range(0, cells.length).forEach(y -> {
            IntStream.range(0, cells[y].length).forEach(x -> System.out.print(cells[y][x].isLive() ? x : " "));
            System.out.println();
        });
        System.out.println("---------------------------------------");
    }

}
