import java.util.concurrent.atomic.AtomicLong;

public class BlockingQueue<T> {
    private final Object[] items;
    private final int capacity;

    private int head;
    private int tail;
    private int count;

    private boolean closed = false;

    private final Object lock = new Object();

    public BlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0, got " + capacity);
        }
        this.capacity = capacity;
        this.items = new Object[capacity];
    }

    public void enqueue(T item) throws InterruptedException {
        if (item == null) {
            throw new NullPointerException("null items are not allowed");
        }
        synchronized (lock) {
            while (count == capacity && !closed) {
                lock.wait();
            }
            if (closed) {
                throw new IllegalStateException("queue is closed");
            }
            items[tail] = item;
            tail = (tail + 1) % capacity;
            count++;
            lock.notifyAll();
        }
    }

    @SuppressWarnings("unchecked")
    public T dequeue() throws InterruptedException {
        synchronized (lock) {
            while (count == 0 && !closed) {
                lock.wait();
            }
            if (count == 0) {
                return null;
            }
            T item = (T) items[head];
            items[head] = null;
            head = (head + 1) % capacity;
            count--;
            lock.notifyAll();
            return item;
        }
    }

    public int size() {
        synchronized (lock) {
            return count;
        }
    }

    public int capacity() {
        return capacity;
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return count == 0;
        }
    }

    public boolean isFull() {
        synchronized (lock) {
            return count == capacity;
        }
    }

    public void close() {
        synchronized (lock) {
            closed = true;
            lock.notifyAll();
        }
    }

    public boolean isClosed() {
        synchronized (lock) {
            return closed;
        }
    }
}