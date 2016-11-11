package bgoug.factorial;

import java.util.Scanner;

import static bgoug.factorial.TailCalls.call;
import static bgoug.factorial.TailCalls.done;

public class FunctionalVersion {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();

        FactorialResult r = functional(n);
        System.out.println("Functional result: " + (r.factorial % 10) + " " + r.zeroCount);
    }

    private static FactorialResult functional(int n) {
        return functional(new FactorialResult(n).multiply(2), 3).invoke();
    }

    private static TailCall<FactorialResult> functional(FactorialResult r, int n) {
        if (r.counter == 1) {
            return done(r);
        }
        return call(() -> functional(r.multiply(n), n + 1));
    }
}
