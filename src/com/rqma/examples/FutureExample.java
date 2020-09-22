package com.rqma.examples;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description:
 * @Auther: RQMA
 * @Date: 2020/6/1 8:50
 */
public class FutureExample {
    static class ConverterTask implements Callable<String> {
        private final CountDownLatch latch;
        private String value;

        public ConverterTask(CountDownLatch latch, String value) {
            this.latch = latch;
            this.value = value;
        }

        @Override
        public String call() throws Exception {
            value.toLowerCase();
            latch.countDown();
            return value;
        }
    }
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(100);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> results = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            ConverterTask task = new ConverterTask(latch,"gh"+i);
            results.add(executor.submit(task));

        }
        // wait for tasks to finish:
        latch.await();
        executor.shutdown();
        for (Future<String> result : results) {
            System.out.printf("Converted message: %s%n", result.get());
        }

    }

}
