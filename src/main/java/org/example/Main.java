package org.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static <T> Map<T, Integer> frequencyAnalysis(T[] arr) {
        Map<T, Integer> map  = new HashMap<>();

        if (arr == null) {
            return map;
        }

        for (T a : arr) {
            map.put(a, map.getOrDefault(a, 0) + 1);
        }

        return map;
    }
}