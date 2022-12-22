package com.shf.nowcoder.controller;

import com.shf.nowcoder.entity.Event;
import com.shf.nowcoder.event.EventProducer;
import com.shf.nowcoder.util.CommunityConstant;
import com.shf.nowcoder.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String storage;

    @RequestMapping(path = "/share", method = RequestMethod.POST)
    @ResponseBody
    public String share(String htmlUrl) {
        String fileName = CommunityUtil.generateUUID();

        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");

        eventProducer.fireEvent(event);

        HashMap<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);

        return CommunityUtil.getJSONString(0, null, map);
    }

    @RequestMapping(path = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }

        response.setContentType("image/png");
        File file = new File(storage + "/" + fileName + ".png");
        try {
            ServletOutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败："+e.getMessage());
        }
    }
}
