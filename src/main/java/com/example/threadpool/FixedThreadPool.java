package com.example.threadpool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool {

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

    public FixedThreadPool(int poolSize, int taskSize) {
        if (poolSize <= 0 || taskSize <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        workQueue = new LinkedBlockingQueue<>(taskSize);

        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker();
            worker.start();
            workers.add(worker);
        }
    }

    public boolean submit(Runnable task) {
        if (isRunning) {
            return workQueue.offer(task);
        }
        return false;
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
        int taskSize = 6;
        FixedThreadPool pool = new FixedThreadPool(poolSize, taskSize);
        for (int i = 0; i < taskSize; i++) {
            pool.submit(new Runnable() {
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
