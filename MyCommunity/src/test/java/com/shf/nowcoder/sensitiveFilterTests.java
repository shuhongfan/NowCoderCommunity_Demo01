package com.shf.nowcoder;

import com.shf.nowcoder.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class sensitiveFilterTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博,可以嫖娼,可以吸毒,可以开票,哈哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
