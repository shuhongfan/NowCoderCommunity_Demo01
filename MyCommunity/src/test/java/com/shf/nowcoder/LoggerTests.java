package com.shf.nowcoder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {
        System.out.println(logger.getName());

        logger.debug("debug log");
        logger.info("info log");
    }
}
