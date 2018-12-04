/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.framework.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fengcheng
 * @version 2017/10/5
 */
public class RedisMessage {

    private Logger logger = LoggerFactory.getLogger(RedisMessage.class);

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 信息
     */
    public void sendMessage(final String channel, final String message) {
        Thread thread = new Thread(() -> {
            Long publish = JedisUtils.getResource().publish(channel, message);
            logger.info("服务器在: {} 频道发布消息{} - {}", channel, message, publish);
        });
        logger.info("发布线程启动:");
        thread.setName("publishThread");
        thread.start();
    }

    /**
     * 订阅频道
     *
     * @param channel 频道
     */
    public void subscribeChannel(final String channel, final RedisMsgPubSubListener redisMsgPubSubListener) {
        cachedThreadPool.execute(() -> JedisUtils.getResource().subscribe(redisMsgPubSubListener, channel));
    }
}
