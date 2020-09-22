package com.rqma.examples;

/**
 * @Description:
 * @Auther: RQMA
 * @Date: 2020/6/1 8:37
 */
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Arrays.asList;

public class CountDownLatchExample {

    private static abstract class ConverterTask implements Callable<String> {

        private final CountDownLatch latch;
        private String value;

        public ConverterTask(CountDownLatch latch, String value) {
            this.latch = latch;
            this.value = value;
        }

        @Override
        public String call() throws Exception {
            value = convert(value);
            latch.countDown();
            return value;
        }

        protected abstract String convert(String value);
    }

    private static class Lowerer extends ConverterTask {

        public Lowerer(CountDownLatch latch, String value) {
            super(latch, value);
        }

        @Override
        public String convert(String value) {
            return value.toLowerCase();
        }
    }

    private static class Upperer extends ConverterTask {

        public Upperer(CountDownLatch latch, String value) {
            super(latch, value);
        }

        @Override
        public String convert(String value) {
            return value.toUpperCase();
        }
    }

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newCachedThreadPool();

        String message = "Hello, World";
        List<Callable<String>> tasks = asList(new Lowerer(latch, message), new Upperer(latch, message));

        System.out.println("Converting message to lowercase and uppercase:");
        List<Future<String>> results = new LinkedList<>();
        for (Callable<String> task : tasks) {
            results.add(executor.submit(task));
        }

        // wait for tasks to finish:
        latch.await();

        System.out.println("Shutting down the executor.");
        executor.shutdown();

        System.out.println("Getting converted results:");
        for (Future<String> result : results) {
            System.out.printf("Converted message: %s%n", result.get());
        }
    }
}
