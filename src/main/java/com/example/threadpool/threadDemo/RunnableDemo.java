package com.example.threadpool.threadDemo;

public class RunnableDemo {
    public static class TestRunnableDemo implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " current thread");
        }
    }

    public static void main(String[] args) {
        TestRunnableDemo t1 = new TestRunnableDemo();
        new Thread(t1).start();
    }
}
