package bgoug.live;

import bgoug.live.model.Cell;

import java.util.Scanner;
import java.util.stream.IntStream;

public class LiveConsole {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        int m = s.nextInt();
        int cycles = s.nextInt();

        LiveGame game = new LiveGame(n, m, true);
        game.initializeImperative(Cell::new);

        s.nextLine();

        if (m < 10 && n < 10)
            printGrid(game.getCells());
        long start = System.currentTimeMillis();

        for (int i = 0; i < cycles; i++)
            game.nextIterationFunctional();
        // game.nextIterationImperative();

        long totalTime = System.currentTimeMillis() - start;
        if (m < 10 && n < 10)
            printGrid(game.getCells());
        System.out.println(String.format("Total time for %1$d iterations is %2$dms", cycles, totalTime));
    }

    static void printGrid(Cell[][] cells) {
        IntStream.range(0, cells.length).forEach(y -> {
            IntStream.range(0, cells[y].length).forEach(x -> System.out.print(cells[y][x].isLive() ? x : " "));
            System.out.println();
        });
        System.out.println("---------------------------------------");
    }

}
