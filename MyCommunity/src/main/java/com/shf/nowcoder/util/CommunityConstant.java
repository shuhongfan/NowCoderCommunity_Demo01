package com.shf.nowcoder.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复过激活
     */
    int ACTIVATION_REPEAT = 0;

    /**
     * 激活失败
     */
    int ACTIVATION_FAIL = 0;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECOUNDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECOUNDS = 3600 * 24 * 100;
}