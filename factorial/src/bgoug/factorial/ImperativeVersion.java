package bgoug.factorial;

import java.util.*;
import java.util.stream.Collectors;

import static bgoug.factorial.TailCalls.call;
import static bgoug.factorial.TailCalls.done;

public class ImperativeVersion {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();

        FactorialResult r = imperative(n);
        System.out.println("Imperative result: " + (r.factorial % 10) + " " + r.zeroCount);
    }

    private static FactorialResult imperative(int n) {
        return recursive(new FactorialResult(n), 2);
    }

    private static FactorialResult recursive(FactorialResult fr, int n) {
        if (fr.counter == 1) {
            return fr;
        } else {
            return recursive(fr.multiply(n), n + 1);
        }
    }
}
