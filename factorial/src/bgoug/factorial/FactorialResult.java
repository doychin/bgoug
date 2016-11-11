package bgoug.factorial;

//        private static final ThreadLocal<FactorialResult> instanceCache = new ThreadLocal<FactorialResult>() {
//            @Override
//            protected FactorialResult initialValue() {
//                return new FactorialResult(-1);
//            }
//        };

class FactorialResult {
    int factorial = 1;
    int zeroCount = 0;
    int counter = 0;

    FactorialResult(int counter) {
        this.counter = counter;
    }

    static FactorialResult newInstance(FactorialResult current) {
        // FactorialResult cachedInstance = instanceCache.get();
        FactorialResult instance = new FactorialResult(current.counter-1);
        instance.counter = current.counter - 1;
        instance.factorial = current.factorial;
        instance.zeroCount = current.zeroCount;
        // instanceCache.set(current);
        return instance;
    }

    FactorialResult multiply(int i) {
        factorial *= i;
        while (factorial % 10 == 0) {
            zeroCount++;
            factorial /= 10;
        }
        factorial %= 10;

        // System.out.println(i + " " + factorial + " " + zeroCount);

        return newInstance(this);
    }
}
