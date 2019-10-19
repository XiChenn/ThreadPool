package com.example.threadpool.threadDemo;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableDemo {
    private static final class Task implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            Thread.sleep(3000);
            int sum = 0;
            for (int i = 0; i < 100; i++) {
                sum += i;
            }
            return sum;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Task task2 = new Task();
        FutureTask<Integer> futureTask2 = new FutureTask<>(task2);
        new Thread(futureTask2).start();

        Callable<String> task1 = () -> String.format("The result is: %d", new Random().nextInt(200));
        FutureTask<String> futureTask1 = new FutureTask<>(task1);
        new Thread(futureTask1).start();

        String result1 = futureTask1.get();
        System.out.println(result1);

        int result2 = futureTask2.get();
        System.out.println(result2);
    }
}
