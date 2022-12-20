package com.shf.nowcoder;

import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {

    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            Thread.sleep(20);
            queue.put(i);
            System.out.println(Thread.currentThread().getName()+"生产："+queue.size());
        }
    }
}

class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            Thread.sleep(new Random().nextInt(1000));
            queue.take();
            System.out.println(Thread.currentThread().getName()+"消费："+queue.size());
        }
    }
}
