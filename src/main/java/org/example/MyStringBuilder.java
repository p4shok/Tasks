package org.example;

public class MyStringBuilder {

    private final class Snapshot {
        private final char[] value;
        private final int count;

        private Snapshot(char[] value, int count) {
            this.value = value;
            this.count = count;
        }
    }
}
