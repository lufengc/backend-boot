/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.security.shiro.cache;

import com.bdfint.backend.framework.cache.JedisUtils;
import com.bdfint.backend.framework.util.Servlets;
import com.google.common.collect.Sets;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 自定义授权缓存管理类（jedis实现）
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public class JedisCacheManager implements CacheManager {

    private String cacheKeyPrefix = "shiro_cache_";

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new JedisCache<>(cacheKeyPrefix + name);
    }

    public String getCacheKeyPrefix() {
        return cacheKeyPrefix;
    }

    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    public class JedisCache<K, V> implements Cache<K, V> {

        private Logger logger = LoggerFactory.getLogger(getClass());

        private String cacheKeyName = null;

        public JedisCache(String cacheKeyName) {
            this.cacheKeyName = cacheKeyName;
        }

        @SuppressWarnings("unchecked")
        @Override
        public V get(K key) throws CacheException {
            if (key == null) {
                return null;
            }

            V v;
            HttpServletRequest request = Servlets.getRequest();
            if (request != null) {
                v = (V) request.getAttribute(cacheKeyName);
                if (v != null) {
                    return v;
                }
            }

            V value = (V) JedisUtils.toObject(JedisUtils.hget(JedisUtils.getBytesKey(cacheKeyName), JedisUtils.getBytesKey(key)));
            logger.debug("get {} {} {}", cacheKeyName, key, request != null ? request.getRequestURI() : "");

            if (request != null && value != null) {
                request.setAttribute(cacheKeyName, value);
            }

            return value;
        }

        @Override
        public V put(K key, V value) throws CacheException {
            if (key == null) {
                return null;
            }

            JedisUtils.hset(JedisUtils.getBytesKey(cacheKeyName), JedisUtils.getBytesKey(key), JedisUtils.toBytes(value));
            logger.debug("put {} {} = {}", cacheKeyName, key, value);
            return value;
        }

        @SuppressWarnings("unchecked")
        @Override
        public V remove(K key) throws CacheException {
            V value = (V) JedisUtils.toObject(JedisUtils.hget(JedisUtils.getBytesKey(cacheKeyName), JedisUtils.getBytesKey(key)));
            JedisUtils.hdel(JedisUtils.getBytesKey(cacheKeyName), JedisUtils.getBytesKey(key));
            logger.debug("remove {} {}", cacheKeyName, key);
            return value;
        }

        @Override
        public void clear() throws CacheException {
            JedisUtils.hdel(JedisUtils.getBytesKey(cacheKeyName));
        }

        @Override
        public int size() {
            int size = (int) JedisUtils.hlen(JedisUtils.getBytesKey(cacheKeyName));
            logger.debug("size {} {} ", cacheKeyName, size);
            return size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Set<K> keys() {
            Set<K> keys = Sets.newHashSet();
            Set<byte[]> set = JedisUtils.hkeys(JedisUtils.getBytesKey(cacheKeyName));
            for (byte[] key : set) {
                keys.add((K) key);
            }
            logger.debug("keys {} {} ", cacheKeyName, keys);
            return keys;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Collection<V> values() {
            Collection<V> vals = Collections.emptyList();
            Collection<byte[]> col = JedisUtils.hvals(JedisUtils.getBytesKey(cacheKeyName));
            for (byte[] val : col) {
                vals.add((V) val);
            }
            logger.debug("values {} {} ", cacheKeyName, vals);
            return vals;
        }
    }
}

