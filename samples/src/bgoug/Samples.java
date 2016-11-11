package bgoug;

import java.util.Arrays;

public class Samples {
    public static void main(String[] arg) {
        sumOfNumbersBelow100();

        sumOfDistinctNumbersBelow100();
    }

    private static void sumOfNumbersBelow100() {
        int[] intArray = new int[]{5, 10, 22, 45, 101, 90, 200, 35, 99, 102, 45, 10, 91};
        System.out.println(Arrays.stream(intArray).filter(n -> n < 100).sum());
    }

    private static void sumOfDistinctNumbersBelow100() {
        int[] intArray = new int[]{5, 10, 22, 45, 101, 90, 200, 35, 99, 102, 45, 10, 91};
        System.out.println(Arrays.stream(intArray).filter(n -> n < 100).distinct().sum());
    }
}
