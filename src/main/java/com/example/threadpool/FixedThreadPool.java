package com.example.threadpool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FixedThreadPool {

    private int poolSize;

    /**
     * The queue used for holding tasks and handing off to worker threads
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * workers contain all worker threads in pool
     */
    private final Set<Worker> workers = Collections.synchronizedSet(new HashSet<>());;

    private volatile boolean isRunning = true;

    private final class Worker extends Thread {
        // Delegates main run loop to outer runWorker
        public void run() {
            runWorker();
        }
    }

    /**
     * Main worker run loop. Repeatedly gets tasks from queue and executes them.
     */
    private final void runWorker() {
        while(isRunning || !workQueue.isEmpty()) {
            try {
                Runnable task = isRunning ? workQueue.take() : workQueue.poll();
                if (task != null) {
                    task.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public FixedThreadPool(int poolSize, BlockingQueue workQueue) {
        if (poolSize <= 0 ) {
            throw new IllegalArgumentException();
        }
        this.poolSize = poolSize;
        this.workQueue = workQueue;
    }

    public void execute (Runnable task) {
        if (isRunning) {
            workQueue.offer(task);

            if (workers.size() < poolSize) {
                Worker worker = new Worker();
                worker.start();
                workers.add(worker);
            }
        }
    }

    /**
     * a. blockingQueue stops receiving task
     * b. finish the existing tasks in the blockingQueue
     * c. worker change to use non-block method (poll) to get the task
     * d. force terminate if workers are already blocked
     */
    public void shutdown() {
        isRunning = false;

        for(Thread thread : workers) {
            if (thread.getState().equals(Thread.State.BLOCKED)) {
                thread.interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int poolSize = 3;
        int taskSize = 12;
        BlockingQueue workQueue = new ArrayBlockingQueue<>(taskSize);
        FixedThreadPool pool = new FixedThreadPool(poolSize, workQueue);

        for (int i = 0; i < taskSize; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Add a new thread...");
                    try {
                        Thread.sleep(2000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        pool.shutdown();
    }
}
