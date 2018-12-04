/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.framework.cache;

import com.cheng.backend.framework.common.SpringContextHolder;
import com.cheng.backend.framework.util.ObjectUtils;
import com.cheng.backend.framework.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * Jedis Cache 工具类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public class JedisUtils {

    private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static JedisPool jedisPool = SpringContextHolder.getBean(JedisPool.class);

    private static JedisCluster cluster = SpringContextHolder.getBean(JedisCluster.class);

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static byte[] hget(byte[] key, byte[] field) {
        byte[] value;
        if (cluster != null) {
            value = cluster.hget(key, field);
        } else {
            Jedis jedis = getResource();
            try {
                value = jedis.hget(key, field);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("hget {} = {}", key, value);
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, String> hgetAll(String key) {
        Map<String, String> value;
        if (cluster != null) {
            value = cluster.hgetAll(key);
        } else {
            Jedis jedis = getResource();
            try {
                value = jedis.hgetAll(key);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("hgetAll {} = {}", key, value);
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        String value;
        if (cluster != null) {
            value = cluster.get(key);
        } else {
            Jedis jedis = getResource();
            try {
                value = jedis.get(key);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("get {} = {}", key, value);
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Object getObject(String key) {
        Object value;
        if (cluster != null) {
            value = toObject(cluster.get(getBytesKey(key)));
        } else {
            Jedis jedis = getResource();
            try {
                value = toObject(jedis.get(getBytesKey(key)));

            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("getObject {} = {}", key, value);
        return value;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return set的信息
     */
    public static String set(String key, String value, int cacheSeconds) {
        String result;
        if (cluster != null) {
            result = cluster.set(key, value);
            cluster.expire(key, cacheSeconds);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.set(key, value);
                jedis.expire(key, cacheSeconds);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("set {} = {}", key, value);
        return result;
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     * @return set的信息
     */
    public static long hset(byte[] key, byte[] field, byte[] value) {
        long result;
        if (cluster != null) {
            result = cluster.hset(key, field, value);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hset(key, field, value);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("hset {} = {}", key, value);
        return result;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return String
     */
    public static String setObject(String key, Object value, int cacheSeconds) {
        String result;
        if (cluster != null) {
            result = cluster.set(getBytesKey(key), getBytesKey(value));
            cluster.expire(key, cacheSeconds);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.set(getBytesKey(key), getBytesKey(value));
                jedis.expire(key, cacheSeconds);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setObject {} = {}", key, value);
        return result;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static List<String> getList(String key) {
        List<String> value;
        if (cluster != null) {
            value = cluster.lrange(key, 0, -1);
        } else {
            Jedis jedis = getResource();
            try {
                value = jedis.lrange(key, 0, -1);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("hset {} = {}", key, value);
        return value;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static List<Object> getObjectList(String key) {
        List<Object> value;
        List<byte[]> list;
        if (cluster != null) {
            list = cluster.lrange(getBytesKey(key), 0, -1);
        } else {
            Jedis jedis = getResource();
            try {
                list = jedis.lrange(getBytesKey(key), 0, -1);
            } finally {
                returnResource(jedis);
            }
        }
        value = Lists.newArrayList();
        for (byte[] bs : list) {
            value.add(toObject(bs));
        }
        logger.debug("getObjectList {} = {}", key, value);
        return value;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return long
     */
    public static long setList(String key, List<String> value, int cacheSeconds) {
        long result;
        if (cluster != null) {
            if (cluster.exists(key)) {
                cluster.del(key);
            }
            result = cluster.rpush(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                result = jedis.rpush(key, (String[]) value.toArray());
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setList {} = {}", key, value);
        return result;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return long
     */
    public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        if (cluster != null) {
            if (cluster.exists(getBytesKey(key))) {
                cluster.del(key);
            }
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = cluster.rpush(getBytesKey(key), (byte[][]) list.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(getBytesKey(key))) {
                    jedis.del(key);
                }
                List<byte[]> list = Lists.newArrayList();
                for (Object o : value) {
                    list.add(toBytes(o));
                }
                result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setObjectList {} = {}", key, value);
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return long
     */
    public static long listAdd(String key, String... value) {
        long result;
        if (cluster != null) {
            result = cluster.rpush(key, value);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.rpush(key, value);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("listAdd {} = {}", key, value);
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return long
     */
    public static long listObjectAdd(String key, Object... value) {
        long result;
        if (cluster != null) {
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = cluster.rpush(getBytesKey(key), (byte[][]) list.toArray());
        } else {
            Jedis jedis = getResource();
            try {
                List<byte[]> list = Lists.newArrayList();
                for (Object o : value) {
                    list.add(toBytes(o));
                }
                result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("listObjectAdd {} = {}", key, value);
        return result;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Set<String> getSet(String key) {
        Set<String> value;
        if (cluster != null) {
            value = cluster.smembers(key);
        } else {
            Jedis jedis = getResource();
            try {
                value = jedis.smembers(key);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("getSet {} = {}", key, value);
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Set<Object> getObjectSet(String key) {
        Set<Object> value;
        if (cluster != null) {
            value = Sets.newHashSet();
            Set<byte[]> set = cluster.smembers(getBytesKey(key));
            for (byte[] bs : set) {
                value.add(toObject(bs));
            }
        } else {
            Jedis jedis = getResource();
            try {
                value = Sets.newHashSet();
                Set<byte[]> set = cluster.smembers(getBytesKey(key));
                for (byte[] bs : set) {
                    value.add(toObject(bs));
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("getObjectSet {} = {}", key, value);
        return value;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return long
     */
    public static long setSet(String key, Set<String> value, int cacheSeconds) {
        long result;
        if (cluster != null) {
            if (cluster.exists(key)) {
                cluster.del(key);
            }
            result = cluster.sadd(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                result = jedis.sadd(key, (String[]) value.toArray());
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setSet {} = {}", key, value);
        return result;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return long
     */
    public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result;
        if (cluster != null) {
            if (cluster.exists(getBytesKey(key))) {
                cluster.del(key);
            }
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = cluster.sadd(getBytesKey(key), (byte[][]) set.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(getBytesKey(key))) {
                    jedis.del(key);
                }
                Set<byte[]> set = Sets.newHashSet();
                for (Object o : value) {
                    set.add(toBytes(o));
                }
                result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setObjectSet {} = {}", key, value);
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return long
     */
    public static long setSetAdd(String key, String... value) {
        long result;
        if (cluster != null) {
            result = cluster.sadd(key, value);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.sadd(key, value);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setSetAdd {} = {}", key, value);
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return long
     */
    public static long setSetObjectAdd(String key, Object... value) {
        long result;
        if (cluster != null) {
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = cluster.rpush(getBytesKey(key), (byte[][]) set.toArray());
        } else {
            Jedis jedis = getResource();
            try {
                Set<byte[]> set = Sets.newHashSet();
                for (Object o : value) {
                    set.add(toBytes(o));
                }
                result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setSetObjectAdd {} = {}", key, value);
        return result;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        if (cluster != null) {
            if (cluster.exists(key)) {
                value = cluster.hgetAll(key);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(key)) {
                    value = jedis.hgetAll(key);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("getMap {} = {}", key, value);
        return value;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;
        if (cluster != null) {
            if (cluster.exists(getBytesKey(key))) {
                value = Maps.newHashMap();
                Map<byte[], byte[]> map = cluster.hgetAll(getBytesKey(key));
                for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
                    value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
                }
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(getBytesKey(key))) {
                    value = Maps.newHashMap();
                    Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
                    for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
                        value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
                    }
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("getObjectMap {} = {}", key, value);
        return value;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return String
     */
    public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result;
        if (cluster != null) {
            if (cluster.exists(key)) {
                cluster.del(key);
            }
            result = cluster.hmset(key, value);
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                result = jedis.hmset(key, value);
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setMap {} = {}", key, value);
        return result;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return String
     */
    public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result;
        if (cluster != null) {
            if (cluster.exists(getBytesKey(key))) {
                cluster.del(key);
            }
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = cluster.hmset(getBytesKey(key), map);
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(getBytesKey(key))) {
                    jedis.del(key);
                }
                Map<byte[], byte[]> map = Maps.newHashMap();
                for (Map.Entry<String, Object> e : value.entrySet()) {
                    map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
                }
                result = jedis.hmset(getBytesKey(key), map);
                if (cacheSeconds != 0) {
                    jedis.expire(key, cacheSeconds);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("setObjectMap {} = {}", key, value);
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return String
     */
    public static String mapPut(String key, Map<String, String> value) {
        String result;
        if (cluster != null) {
            result = cluster.hmset(key, value);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hmset(key, value);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("mapPut {} = {}", key, value);
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return String
     */
    public static String mapObjectPut(String key, Map<String, Object> value) {
        String result;
        if (cluster != null) {
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = cluster.hmset(getBytesKey(key), map);
        } else {
            Jedis jedis = getResource();
            try {
                Map<byte[], byte[]> map = Maps.newHashMap();
                for (Map.Entry<String, Object> e : value.entrySet()) {
                    map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
                }
                result = jedis.hmset(getBytesKey(key), map);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("mapObjectPut {} = {}", key, value);
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return long
     */
    public static long mapRemove(String key, String mapKey) {
        long result;
        if (cluster != null) {
            result = cluster.hdel(key, mapKey);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hdel(key, mapKey);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("mapRemove {} = {}", key, mapKey);
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return long
     */
    public static long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        if (cluster != null) {
            result = cluster.hdel(getBytesKey(key), getBytesKey(mapKey));
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("mapObjectRemove {} = {}", key, mapKey);
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return long
     */
    public static boolean mapExists(String key, String mapKey) {
        boolean result;
        if (cluster != null) {
            result = cluster.hexists(key, mapKey);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hexists(key, mapKey);
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("mapExists {} = {}", key, mapKey);
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return long
     */
    public static boolean mapObjectExists(String key, String mapKey) {
        boolean result;
        if (cluster != null) {
            result = cluster.hexists(getBytesKey(key), getBytesKey(mapKey));
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("mapObjectExists {} = {}", key, mapKey);
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return long
     */
    public static long del(String key) {
        long result = 0;
        if (cluster != null) {
            if (cluster.exists(key)) {
                result = cluster.del(key);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(key)) {
                    result = jedis.del(key);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("del {}", key);
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return long
     */
    public static long hdel(byte[] key, byte[]... field) {
        long result = 0;
        if (cluster != null) {
            if (cluster.exists(key)) {
                result = cluster.hdel(key, field);
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(key)) {
                    result = jedis.hdel(key, field);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("del {}", key);
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return long
     */
    public static long delObject(String key) {
        long result = 0;
        if (cluster != null) {
            if (cluster.exists(getBytesKey(key))) {
                result = cluster.del(getBytesKey(key));
            }
        } else {
            Jedis jedis = getResource();
            try {
                if (jedis.exists(getBytesKey(key))) {
                    result = jedis.del(getBytesKey(key));
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("delObject {}", key);
        return result;
    }

    /**
     * 删除缓存
     *
     * @param likekey 键
     * @return 值
     */
    public static long delKeysLike(String likekey) {
        long result = 0;
        if (cluster != null) {
            Set<String> keys = keys(likekey + "*");
            for (String key : keys) {
                cluster.del(key);
            }
        } else {
            Jedis jedis = getResource();
            try {
                Set<String> keys = jedis.keys(likekey + "*");
                for (String key : keys) {
                    jedis.del(key);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("delKeysLike {}", likekey);
        return result;
    }

    /**
     * 清空所有缓存
     *
     * @return long
     */
    public static long delAll() {
        long result = 0;
        if (cluster != null) {
            Set<String> keys = keys("*");
            for (String key : keys) {
                cluster.del(key);
            }
        } else {
            Jedis jedis = getResource();
            try {
                Set<String> keys = jedis.keys("*");
                for (String key : keys) {
                    jedis.del(key);
                }
            } finally {
                returnResource(jedis);
            }
        }
        logger.debug("delAll");
        return result;
    }

    public static TreeSet<String> keys(String pattern) {
        TreeSet<String> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
        for (String k : clusterNodes.keySet()) {
            JedisPool jp = clusterNodes.get(k);
            try (Jedis connection = jp.getResource()) {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                logger.error("Getting keys error: {}", e);
            } finally {
                logger.debug("Connection closed.");

            }
        }
        return keys;
    }

    /**
     * @param key 键
     * @return boolean
     */
    public static long hlen(byte[] key) {
        long result;
        if (cluster != null) {
            result = cluster.hlen(key);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hlen(key);
            } finally {
                returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * @param key 键
     * @return boolean
     */
    public static Set<byte[]> hkeys(byte[] key) {
        Set<byte[]> result;
        if (cluster != null) {
            result = cluster.hkeys(key);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hkeys(key);
            } finally {
                returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * @param key 键
     * @return boolean
     */
    public static Collection<byte[]> hvals(byte[] key) {
        Collection<byte[]> result = null;
        if (cluster != null) {
            result = cluster.hvals(key);
        } else {
            Jedis jedis = getResource();
            try {
                result = jedis.hvals(key);
            } finally {
                returnResource(jedis);
            }
        }
        return result;
    }

    /**
     * 获取资源
     */
    public static Jedis getResource() {
        return jedisPool.getResource();
    }

    /**
     * 释放资源
     */
    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取byte[]类型Key
     */
    public static byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            return StringUtils.getBytes((String) object);
        } else {
            return ObjectUtils.serialize(object);
        }
    }

    /**
     * Object转换byte[]类型
     */
    public static byte[] toBytes(Object object) {
        return ObjectUtils.serialize(object);
    }

    /**
     * byte[]型转换Object
     */
    public static Object toObject(byte[] bytes) {
        return ObjectUtils.unserialize(bytes);
    }

}
