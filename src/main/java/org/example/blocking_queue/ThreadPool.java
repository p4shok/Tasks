package org.example.blocking_queue;

import java.util.ArrayList;
import java.util.List;
public class ThreadPool {

    private final BlockingQueue<Runnable> queue;
    private final List<Thread> workers = new ArrayList<>();
    private volatile boolean shutdown = false;

    public ThreadPool(int poolSize, int queueCapacity) {
        if (poolSize <= 0) throw new IllegalArgumentException("poolSize must be > 0");
        this.queue = new BlockingQueue<>(queueCapacity);

        for (int i = 0; i < poolSize; i++) {
            Thread t = new Thread(this::workerLoop, "worker-" + i);
            t.setDaemon(true);
            workers.add(t);
            t.start();
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        if (shutdown) throw new IllegalStateException("pool is shut down");
        queue.enqueue(task);
    }

    private void workerLoop() {
        try {
            while (!shutdown) {
                Runnable task = queue.dequeue();
                if (task == null) {
                    // очередь закрыта и пуста
                    return;
                }
                try {
                    task.run();
                } catch (RuntimeException e) {
                    // Задача не должна ронять воркер.
                    System.err.println(Thread.currentThread().getName()
                            + " task failed: " + e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;
        queue.close();
        for (Thread t : workers) {
            t.interrupt();
        }
        for (Thread t : workers) {
            t.join();
        }
    }

    public int queueSize() {
        return queue.size();
    }
}