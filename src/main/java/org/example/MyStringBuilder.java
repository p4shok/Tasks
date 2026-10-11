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

    public MyStringBuilder append(String str) {
        saveSnapshot();
        if(str == null) str = "null";
        ensureCapacity(count + str.length());
        str.getChars(0, str.length(), value, count);
        count += str.length();
        return this;
    }

    public MyStringBuilder append(char c) {
        saveSnapshot();
        ensureCapacity(count + 1);
        value[count++] = c;
        return this;
    }

    public MyStringBuilder deleteCharAt(int index) {
        checkIndex(index);
        saveSnapshot();
        System.arraycopy(value, index + 1, value, index, count - index - 1);
        count--;
        return this;
    }

    public MyStringBuilder insert(int offset, String str) {
        if (offset < 0 || offset > count) throw new IndexOutOfBoundsException();
        saveSnapshot();
        int len = str.length();
        ensureCapacity(count + len);
        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count += len;
        return this;
    }

    public MyStringBuilder reverse() {
        saveSnapshot();
        for (int i = 0, j = count - 1; i < j; i++, j--) {
            char t = value[i]; value[i] = value[j]; value[j] = t;
        }
        return this;
    }

    private void saveSnapshot() {
        history.push(new Snapshot(value.clone(), count));
    }

    public boolean undo() {
        if (history.isEmpty()) return false;
        Snapshot s = history.pop();
        this.value = s.value;
        this.count = s.count;
        return true;
    }

    @Override
    public String toString() {
        return new String(value, 0, count);
    }

    public int length() { return count; }

    private void ensureCapacity(int min) {
        if (min > value.length) {
            int newCap = value.length * 2 + 2;
            if (newCap < min) newCap = min;
            char[] nv = new char[newCap];
            System.arraycopy(value, 0, nv, 0, count);
            value = nv;
        }
    }

    private void checkIndex(int i) {
        if (i < 0 || i >= count) throw new StringIndexOutOfBoundsException(i);
    }
}
