package com.shf.nowcoder;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //    JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //    JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    /**
     * Spring普通任务线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * Spring可执行定时任务的线程池
     */
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;



    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JDK普通线程池
     */
    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(10000);
    }

    /**
     * 2. JDK定时任务
     */
    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello scheduleExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 100, TimeUnit.MILLISECONDS);
    }


    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("testThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(runnable);
        }

        sleep(10000);
    }

    /**
     * 4. spring定时任务线程池
     */
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testThreadPoolTaskScheduler");
            }
        };

        Date startTime = new Date(System.currentTimeMillis() + 10000);
        threadPoolTaskScheduler.scheduleAtFixedRate(task, startTime, 1000);
    }

    @Async
    public void execute1() {
        logger.debug("e1");
    }

    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2() {
        logger.debug("e2");
    }
}
