package bgoug.live;

import bgoug.live.model.Cell;
import bgoug.live.model.CellSupplier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LiveGame {

    private final boolean parallelStreams;

    Cell[][] getCells() {
        return cells;
    }

    private Cell[][] cells;
    private int width, height;

    LiveGame(int width, int height, boolean parallelStreams) {
        this.height = height;
        this.width = width;
        this.parallelStreams = parallelStreams;
    }

    /**
     * Run next game iteration
     */
    void nextIterationImperative() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                updateCellState(cells[y][x]);
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x].apply();
            }
        }
    }

    /**
     * Run next game iteration
     */
    void nextIterationFunctional() {
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

    // Imperative style
    void initializeImperative(CellSupplier cellSupplier) {
        cells = new Cell[height][width];
        Random r = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = cellSupplier.createCell(x, y);
                cells[y][x].setLive(r.nextBoolean());
            }
        }
    }

    /**
     * Initialize cells array and generate random cell status
     */
    void initializeFunctional(CellSupplier cellSupplier) {
        cells = new Cell[height][width];

        Random r = new Random();
        IntStream.range(0, height).forEach(y ->
                IntStream.range(0, width)
                         .forEach(x -> cells[y][x] = cellSupplier.createCell(x, y)
                                                                 .setLive(r.nextBoolean())));
    }

    private void updateCellState(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();

        int startX = x > 0 ? x - 1 : 0;
        int endX = x < width - 1 ? x + 1 : width - 1;

        int startY = y > 0 ? y - 1 : 0;
        int endY = y < height - 1 ? y + 1 : height - 1;

        long liveCells = countLiveCellsFunctional(cell, startX, endX, startY, endY);
        if (cell.isLive()) {
            cell.setNextState(!(liveCells < 2 || liveCells > 3));
        } else {
            cell.setNextState(liveCells == 3);
        }
    }

    private long countLiveCellsImperative(Cell cell, int startX, int endX, int startY, int endY) {
        long liveCells = 0;
        for (int y = startY; y <= endY; y++)
            for (int x = startX; x < endX; x++) {
                if (cells[y][x] == cell) continue;
                if (cells[y][x].isLive())
                    liveCells++;
            }

        return liveCells;
    }

    private long countLiveCellsFunctional(Cell cell, int startX, int endX, int startY, int endY) {
        return Arrays.stream(cells, startY, endY + 1)
                     .flatMap(cellsLine -> Arrays.stream(cellsLine, startX, endX + 1))
                     .filter(c -> c != cell)
                     .filter(Cell::isLive)
                     .count();
    }
}
