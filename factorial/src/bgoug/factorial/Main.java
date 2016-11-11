package bgoug.factorial;

import java.util.Scanner;

import static bgoug.factorial.TailCalls.call;
import static bgoug.factorial.TailCalls.done;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();

        FactorialResult r = imperative(n);
        System.out.println("Imperative result: " + (r.factorial % 10) + " " + r.zeroCount);

        r = functional(n);
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

    private static FactorialResult imperative(int n) {
        FactorialResult r = new FactorialResult(n);
        for (int i = 2; i <= n; i++) {
            r.multiply(i);
        }
        return r;
    }

    private static class FactorialResult {
        int factorial = 1;
        int zeroCount = 0;
        int counter = 0;

        FactorialResult(int counter) {
            this.counter = counter;
        }

        private static final ThreadLocal<FactorialResult> instanceCache = new ThreadLocal<FactorialResult>() {
            @Override
            protected FactorialResult initialValue() {
                return new FactorialResult(-1);
            }
        };

        private static FactorialResult newInstance(FactorialResult current) {
            FactorialResult cachedInstance = instanceCache.get();
            cachedInstance.counter = current.counter - 1;
            cachedInstance.factorial = current.factorial;
            cachedInstance.zeroCount = current.zeroCount;
            instanceCache.set(current);
            return cachedInstance;
        }

        FactorialResult multiply(int i) {
            factorial *= i;
            while (factorial % 10 == 0) {
                zeroCount++;
                factorial /= 10;
            }
            factorial %= 10;

            //System.out.println(i + " " + factorial + " " + zeroCount);

            return newInstance(this);
        }
    }
}
