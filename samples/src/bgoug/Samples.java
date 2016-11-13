package bgoug;

import java.util.*;

public class Samples {
    private static final List<String> names =
            Arrays.asList("Иван", "Петър", "Христо", "Кристиян",
                    "Филип", "Николай", "Ангел", "Димитър", "Камен",
                    "Радко", "Денислав");
    private static final int[] intArray =
            new int[]{5, 10, 22, 45, 101, 90, 200, 35,
                    99, 102, 45, 10, 91};

    public static void main(String[] arg) {
        sumOfNumbersBelow100();

        sumOfDistinctNumbersBelow100();
    }

    private static void sumOfNumbersBelow100() {
        System.out.println(Arrays.stream(intArray).
                filter(n -> n < 100).sum());
    }

    private static void sumOfDistinctNumbersBelow100() {
        System.out.println(Arrays.stream(intArray).
                filter(n -> n < 100).distinct().sum());
    }

    private static void moreExamples() {
        //        Map<String, List<String>> separatedMap = separateByFirstLetter();
//
//        separatedMap.keySet().stream().flatMap(s -> {
//            System.out.println();
//            return separatedMap.get(s).stream();
//        }).forEach(name -> System.out.print(name + ","));
//
//        System.out.println();
//        for (String letter : separatedMap.keySet()) {
//            System.out.println();
//            for (String name : separatedMap.get(letter)) {
//                System.out.print(name + ",");
//            }
//        }
    }

//    private static Map<String, List<String>> separateByFirstLetter() {
//        return names.stream().collect(Collectors.groupingBy(name -> name.substring(0, 1)));
//    }

    private static Map<String, List<String>> separateByFirstLetter() {
        Map<String, List<String>> result = new HashMap<>();
        for (String name : names) {
            String firstLetter = name.substring(0, 1);
            if (!result.containsKey(firstLetter)) {
                result.put(firstLetter, new ArrayList<>());
            }
            result.get(firstLetter).add(name);
        }
        return result;
    }
}
