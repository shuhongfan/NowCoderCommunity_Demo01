package com.shf.nowcoder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.rowset.spi.SyncResolver;
import java.io.File;

@Configuration
public class WKConfig {

    private static final Logger logger = LoggerFactory.getLogger(WKConfig.class);

    @Value("${wk.image.storage}")
    private String storage;

    @PostConstruct
    public void init() {
        File file = new File(storage);
        if (!file.exists()) {
            file.mkdir();
            logger.info("创建WK目录");
        }
    }

}
