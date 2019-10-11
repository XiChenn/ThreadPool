package com.example.threadpool.threadDemo;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<String> callable = () -> String.format("The result is: %d", new Random().nextInt(200));

        FutureTask<String> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();
        String result = futureTask.get();
        System.out.println(result);
    }
}
