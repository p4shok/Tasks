import org.example.blocking_queue.BlockingQueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

    @Test
    @DisplayName("Конструктор с невалидной capacity бросает IllegalArgumentException")
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new BlockingQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new BlockingQueue<>(-1));
    }

    @Test
    @DisplayName("Новая очередь пуста, capacity возвращается корректно")
    void testInitialState() {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        assertEquals(0, queue.size());
        assertEquals(5, queue.capacity());
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertFalse(queue.isClosed());
    }

    @Test
    @DisplayName("enqueue добавляет элемент, dequeue извлекает его")
    void testEnqueueDequeue() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(2);

        queue.enqueue("a");
        assertEquals(1, queue.size());
        assertFalse(queue.isEmpty());

        String item = queue.dequeue();
        assertEquals("a", item);
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("FIFO порядок сохраняется")
    void testFifoOrder() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
    }

    @Test
    @DisplayName("enqueue(null) бросает NullPointerException")
    void testEnqueueNull() {
        BlockingQueue<String> queue = new BlockingQueue<>(2);
        assertThrows(NullPointerException.class, () -> queue.enqueue(null));
    }

    @Test
    @DisplayName("Кольцевой буфер: повторное использование после заполнения")
    void testCircularBehavior() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(3);

        for (int i = 0; i < 3; i++) queue.enqueue(i);
        assertTrue(queue.isFull());

        assertEquals(0, queue.dequeue());
        assertEquals(1, queue.dequeue());
        assertFalse(queue.isFull());

        queue.enqueue(100);
        queue.enqueue(101);
        assertTrue(queue.isFull());

        assertEquals(2, queue.dequeue());
        assertEquals(100, queue.dequeue());
        assertEquals(101, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("isFull возвращает true при заполнении")
    void testIsFull() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(2);
        assertFalse(queue.isFull());
        queue.enqueue("a");
        assertFalse(queue.isFull());
        queue.enqueue("b");
        assertTrue(queue.isFull());
    }

    @Test
    @DisplayName("close на пустой очереди: dequeue возвращает null")
    void testCloseEmptyQueue() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(2);
        queue.close();
        assertTrue(queue.isClosed());
        assertNull(queue.dequeue());
    }

    @Test
    @DisplayName("close с элементами: dequeue отдает оставшиеся, затем null")
    void testCloseWithElements() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(3);
        queue.enqueue("a");
        queue.enqueue("b");
        queue.close();

        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertNull(queue.dequeue());
    }

    @Test
    @DisplayName("enqueue после close бросает IllegalStateException")
    void testEnqueueAfterClose() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(2);
        queue.close();
        assertThrows(IllegalStateException.class, () -> queue.enqueue("a"));
    }

    @Test
    @DisplayName("close идемпотентен")
    void testCloseIdempotent() {
        BlockingQueue<String> queue = new BlockingQueue<>(2);
        queue.close();
        queue.close();
        assertTrue(queue.isClosed());
    }

    @Test
    @DisplayName("close пробуждает заблокированный dequeue")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testCloseWakesUpDequeue() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(2);
        AtomicReference<String> result = new AtomicReference<>("not-set");
        CountDownLatch started = new CountDownLatch(1);

        Thread consumer = new Thread(() -> {
            try {
                started.countDown();
                result.set(queue.dequeue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        assertTrue(started.await(1, TimeUnit.SECONDS));
        Thread.sleep(100); // даем шанс заблокироваться
        queue.close();
        consumer.join(2000);

        assertFalse(consumer.isAlive());
        assertNull(result.get());
    }

    @Test
    @DisplayName("close пробуждает заблокированный enqueue")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testCloseWakesUpEnqueue() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(1);
        queue.enqueue("a"); // очередь заполнена

        AtomicReference<Throwable> error = new AtomicReference<>();
        Thread producer = new Thread(() -> {
            try {
                queue.enqueue("b");
            } catch (Throwable t) {
                error.set(t);
            }
        });
        producer.start();
        Thread.sleep(100);
        queue.close();
        producer.join(2000);

        assertFalse(producer.isAlive());
        assertInstanceOf(IllegalStateException.class, error.get());
    }

    @Test
    @DisplayName("dequeue блокируется на пустой очереди, пока не появится элемент")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testDequeueBlocksUntilEnqueue() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(2);
        AtomicReference<Integer> result = new AtomicReference<>();
        CountDownLatch ready = new CountDownLatch(1);

        Thread consumer = new Thread(() -> {
            try {
                ready.countDown();
                result.set(queue.dequeue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        assertTrue(ready.await(1, TimeUnit.SECONDS));
        Thread.sleep(100);
        assertEquals(0, queue.size());
        queue.enqueue(42);

        consumer.join(2000);
        assertEquals(42, result.get());
    }

    @Test
    @DisplayName("enqueue блокируется при полной очереди, пока не освободится место")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testEnqueueBlocksWhenFull() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(1);
        queue.enqueue(1);

        Thread producer = new Thread(() -> {
            try {
                queue.enqueue(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        Thread.sleep(100);
        assertTrue(producer.isAlive()); // всё ещё блокирован

        assertEquals(1, queue.dequeue());
        producer.join(2000);
        assertFalse(producer.isAlive());
        assertEquals(2, queue.dequeue());
    }

    @Test
    @DisplayName("Многопоточный producer/consumer: корректный подсчет и порядок")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentProducersConsumers() throws InterruptedException {
        final int producers = 4;
        final int consumers = 4;
        final int itemsPerProducer = 250;
        final int total = producers * itemsPerProducer;

        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        long[] sums = new long[consumers];
        CountDownLatch done = new CountDownLatch(consumers);

        for (int i = 0; i < consumers; i++) {
            final int idx = i;
            new Thread(() -> {
                try {
                    while (true) {
                        Integer item = queue.dequeue();
                        if (item == null) return;
                        sums[idx] += item;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        for (int p = 0; p < producers; p++) {
            final int base = p * itemsPerProducer;
            new Thread(() -> {
                try {
                    for (int i = 0; i < itemsPerProducer; i++) {
                        queue.enqueue(base + i + 1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        // Ждем завершения производителей, потом закрываем очередь
        Thread closer = new Thread(() -> {
            try {
                // грубая синхронизация — ждем, пока очередь опустошится
                while (queue.size() > 0 || !Thread.currentThread().isInterrupted()) {
                    if (queue.size() == 0) {
                        Thread.sleep(50);
                        if (queue.size() == 0) break;
                    }
                }
                queue.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        closer.start();
        closer.join(5000);
        queue.close();

        assertTrue(done.await(5, TimeUnit.SECONDS));

        long expected = (long) total * (total + 1) / 2 / producers; // неверная формула, посчитаем напрямую
        long actual = 0;
        for (long s : sums) actual += s;
        long expectedSum = 0;
        for (int p = 0; p < producers; p++) {
            for (int i = 1; i <= itemsPerProducer; i++) {
                expectedSum += p * itemsPerProducer + i;
            }
        }
        assertEquals(expectedSum, actual);
    }
}