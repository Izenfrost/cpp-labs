import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class JavaSort {

    public static long javaSort() {

        LinkedList<File> filesInFolder = new LinkedList<>();
        LinkedList<Pair<File, Long>> pairs = new LinkedList<>();

        try {

            filesInFolder = Files
                    .walk(Paths.get("./saves/"))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toCollection(LinkedList<File>::new));

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : filesInFolder) {

            pairs.add(new Pair<File, Long>(file, file.length()));

        }

        long previousTime = System.nanoTime();

        mergeSort(pairs);

        long resultTime = System.nanoTime() - previousTime;

        System.out.println("Java time is:  " + resultTime);


        return resultTime;

    }

    public static class TupleComparator {

        public static int compare(Pair<File, Long> a, Pair<File, Long> b) {

            if (a.getValue() < b.getValue()) {

                return -1;
            }

            if (a.getValue() > b.getValue()) {

                return 1;

            }

            return 0;

        }
    }

    public static LinkedList<Pair<File, Long>> mergeSort(LinkedList<Pair<File, Long>> whole) {

        LinkedList<Pair<File, Long>> left = new LinkedList<Pair<File, Long>>();
        LinkedList<Pair<File, Long>> right = new LinkedList<Pair<File, Long>>();

        int center;

        if (whole.size() == 1) {
            return whole;
        } else {
            center = whole.size() / 2;

            for (int i = 0; i < center; i++) {
                left.add(whole.get(i));
            }


            for (int i = center; i < whole.size(); i++) {
                right.add(whole.get(i));
            }


            left = mergeSort(left);
            right = mergeSort(right);


            merge(left, right, whole);
        }
        return whole;
    }

    private static void merge(LinkedList<Pair<File, Long>> left, LinkedList<Pair<File, Long>> right, LinkedList<Pair<File, Long>> whole) {

        int leftIndex = 0;
        int rightIndex = 0;
        int wholeIndex = 0;


        while (leftIndex < left.size() && rightIndex < right.size()) {

            if (TupleComparator.compare(left.get(leftIndex), right.get(rightIndex)) < 0) {


                whole.set(wholeIndex, left.get(leftIndex));
                leftIndex++;

            } else {
                whole.set(wholeIndex, right.get(rightIndex));
                rightIndex++;
            }
            wholeIndex++;
        }

        LinkedList<Pair<File, Long>> rest;
        int restIndex;
        if (leftIndex >= left.size()) {

            rest = right;
            restIndex = rightIndex;
        } else {

            rest = left;
            restIndex = leftIndex;
        }


        for (int i = restIndex; i < rest.size(); i++) {
            whole.set(wholeIndex, rest.get(i));
            wholeIndex++;
        }
    }
}
