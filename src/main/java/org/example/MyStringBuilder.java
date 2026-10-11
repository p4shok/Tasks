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

    public MyStringBuilder() {
        this(16);
    }

    public MyStringBuilder(int capacity) {
        this.value = new char[Math.max(capacity, 1)];
        this.count = 0;
    }

    public MyStringBuilder(String str) {
        this(str.length() + 16);
        append(str);
    }
}
