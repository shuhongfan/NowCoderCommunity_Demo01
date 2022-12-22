package com.shf.nowcoder.quartz;

import com.shf.nowcoder.entity.DiscussPost;
import com.shf.nowcoder.service.DiscussPostService;
import com.shf.nowcoder.service.ElasticSearchService;
import com.shf.nowcoder.service.LikeService;
import com.shf.nowcoder.util.CommunityConstant;
import com.shf.nowcoder.util.RedisKeyUtil;
import org.checkerframework.checker.units.qual.A;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败！");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("【任务取消】，没有需要刷新的帖子！");
            return;
        }

        logger.info("【任务开始】 正在刷新帖子分数："+operations.size());
        while (operations.size() > 0) {
            refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕！");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            logger.error("该帖子不存在： id=" + postId);
            return;
        }

        boolean wonderful = post.getScore() == 1;
        Integer commentCount = post.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

//        更新帖子分数
        discussPostService.updateScore(postId, score);

//        同步搜索数据
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);
    }
}
