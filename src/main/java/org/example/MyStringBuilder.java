package org.example;

import java.util.ArrayDeque;
import java.util.Deque;

public class MyStringBuilder {

    private final class Snapshot {
        private final char[] value;
        private final int count;

        private Snapshot(char[] value, int count) {
            this.value = value;
            this.count = count;
        }
    }

    private char[] value;
    private int count;
    private final Deque<Snapshot> history = new ArrayDeque<>();
}
