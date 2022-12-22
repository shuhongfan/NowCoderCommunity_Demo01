package com.shf.nowcoder;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class QuartzTests {
    @Autowired
    private Scheduler scheduler;

    @SneakyThrows
    @Test
    public void testDeleteJob() {
        boolean result = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
        System.out.println(result);
    }
}
